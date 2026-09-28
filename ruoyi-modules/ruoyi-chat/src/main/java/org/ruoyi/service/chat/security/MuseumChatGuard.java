package org.ruoyi.service.chat.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.common.core.exception.ServiceException;
import org.ruoyi.common.core.utils.ServletUtils;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.redis.utils.RedisUtils;
import org.ruoyi.common.tenant.helper.TenantHelper;
import org.ruoyi.mapper.chat.SysConfigQueryMapper;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 博物馆C端对话防滥用守卫（/chat/museumSend 免登录，token是主要成本项）
 * <p>
 * 单IP双桶限流（Redis计数，超限拒绝，全局异常处理转R.fail，C端静默复位loading）：
 * <ol>
 *   <li>IP分钟频率：单IP每分钟对话请求上限（一轮问答含打字与AI思考，正常远低于此值）</li>
 *   <li>IP日配额：单IP每日对话次数上限，限制单日最大刷量成本</li>
 * </ol>
 * 阈值与开关为系统参数（sys_config，后台"系统管理→参数设置"可改，改完即时生效，无需重启）：
 * <ul>
 *   <li>museum.chat.guard.enabled —— 总开关（true/false，缺省true）</li>
 *   <li>museum.chat.guard.rate.limit —— 单IP每分钟上限（缺省20）</li>
 *   <li>museum.chat.guard.daily.limit —— 单IP每日上限（缺省500）</li>
 * </ul>
 * 参数缺失或值非法时回退内置默认值，保证守卫永不因配置问题失效。
 *
 * @author gtnes
 * @date 2026-09-28
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MuseumChatGuard {

    private static final String KEY_RATE = "museum:chat:guard:rate:";
    private static final String KEY_QUOTA = "museum:chat:guard:quota:";

    private static final String CONFIG_ENABLED = "museum.chat.guard.enabled";
    private static final String CONFIG_RATE_LIMIT = "museum.chat.guard.rate.limit";
    private static final String CONFIG_DAILY_LIMIT = "museum.chat.guard.daily.limit";

    /** 参数缺失/非法时的兜底默认值 */
    private static final boolean DEFAULT_ENABLED = true;
    private static final int DEFAULT_RATE_LIMIT_PER_MINUTE = 20;
    private static final int DEFAULT_DAILY_LIMIT = 500;

    private final SysConfigQueryMapper sysConfigQueryMapper;

    /**
     * 对话请求入口校验：超限抛ServiceException
     */
    public void check() {
        if (!readEnabled()) {
            return;
        }
        String ip = ServletUtils.getClientIP();
        checkRate(ip);
        checkDailyQuota(ip);
    }

    /**
     * 单IP分钟级频率限制（滑动起点：首次请求时设置60秒过期）
     */
    private void checkRate(String ip) {
        String key = KEY_RATE + ip;
        long count = RedisUtils.incrAtomicValue(key);
        if (count == 1) {
            RedisUtils.expire(key, Duration.ofSeconds(60));
        }
        if (count > readIntConfig(CONFIG_RATE_LIMIT, DEFAULT_RATE_LIMIT_PER_MINUTE)) {
            throw new ServiceException("访问过于频繁，请稍后再试");
        }
    }

    /**
     * 单IP日配额：按日累计对话次数，跨日自动失效
     */
    private void checkDailyQuota(String ip) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String key = KEY_QUOTA + today + ":" + ip;
        long count = RedisUtils.incrAtomicValue(key);
        if (count == 1) {
            RedisUtils.expire(key, Duration.ofDays(2));
        }
        if (count > readIntConfig(CONFIG_DAILY_LIMIT, DEFAULT_DAILY_LIMIT)) {
            throw new ServiceException("今日访问次数已达上限，请明天再来");
        }
    }

    /**
     * 读取总开关：默认开启，显式false才关闭
     */
    private boolean readEnabled() {
        String value = readConfig(CONFIG_ENABLED);
        return !"false".equalsIgnoreCase(StringUtils.trim(value));
    }

    /**
     * 读取整数参数：缺失或非法回退默认值（保证守卫不因配置问题失效）
     */
    private int readIntConfig(String key, int defaultValue) {
        String value = StringUtils.trim(readConfig(key));
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : defaultValue;
        } catch (NumberFormatException e) {
            log.warn("系统参数{}值非法：{}，回退默认值{}", key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * 直查sys_config（sys_config不在租户排除表，需绕过租户过滤；每次直读保证后台改参数即时生效）
     */
    private String readConfig(String key) {
        try {
            return TenantHelper.ignore(() -> sysConfigQueryMapper.selectValueByKey(key));
        } catch (Exception e) {
            log.warn("读取系统参数{}失败，使用默认值", key, e);
            return null;
        }
    }

}
