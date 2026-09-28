package org.ruoyi.service.voice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.common.core.exception.ServiceException;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.redis.utils.RedisUtils;
import org.ruoyi.common.core.utils.ServletUtils;
import org.ruoyi.common.tenant.helper.TenantHelper;
import org.ruoyi.domain.bo.voice.VoiceTtsBo;
import org.ruoyi.mapper.chat.SysConfigQueryMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * TTS公开接口防滥用守卫（/voice/tts 免登录，需防脚本刷量烧钱）
 * <p>
 * 校验顺序（全部通过才放行合成）：
 * <ol>
 *   <li>HMAC-SHA256签名：sign = hex(hmac(secret, timestamp + "\n" + nonce + "\n" + text))，
 *       时间窗±5分钟，防脱离页面直接curl调用</li>
 *   <li>nonce防重放：同一签名5分钟内只允许提交一次</li>
 *   <li>IP分钟频率：单IP每分钟合成请求上限（分段流水线会并发请求，需留余量）</li>
 *   <li>IP日配额：单IP每日合成次数上限，限制单日最大盗刷成本</li>
 * </ol>
 * 密钥在前端JS中可见，签名仅能抬高直刷门槛，真正的兜底是3/4两层的频率与配额。
 * <p>
 * 频率与日配额为系统参数（sys_config，后台"系统管理→参数设置"可改，改完即时生效）：
 * <ul>
 *   <li>tts.guard.rate.limit —— 单IP每分钟上限（缺省30）</li>
 *   <li>tts.guard.daily.limit —— 单IP每日上限（缺省500）</li>
 * </ul>
 * 签名开关与密钥仍走yml（需与C端前端常量两端同步，不宜后台随意改动）。
 *
 * @author ruoyi
 * @date 2026-09-15
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TtsRequestGuard {

    private static final String KEY_RATE = "tts:guard:rate:";
    private static final String KEY_QUOTA = "tts:guard:quota:";
    private static final String KEY_NONCE = "tts:guard:nonce:";

    private static final String CONFIG_RATE_LIMIT = "tts.guard.rate.limit";
    private static final String CONFIG_DAILY_LIMIT = "tts.guard.daily.limit";

    /** 参数缺失/非法时的兜底默认值 */
    private static final int DEFAULT_RATE_LIMIT_PER_MINUTE = 30;
    private static final int DEFAULT_DAILY_LIMIT = 500;
    /** 签名时间窗口（毫秒） */
    private static final long SIGN_WINDOW_MILLIS = 5 * 60 * 1000L;

    /**
     * 签名开关+密钥（与C端前端常量保持一致；更换时需两端同步改）
     */
    @Value("${tts.sign-enabled:true}")
    private boolean signEnabled;

    @Value("${tts.sign-secret:tts_9f3b7c2a4e8d1f6b0a5c3e7d9f2b4a6c}")
    private String signSecret;

    private final SysConfigQueryMapper sysConfigQueryMapper;

    /**
     * 合成请求入口校验
     */
    public void check(VoiceTtsBo bo) {
        String ip = ServletUtils.getClientIP();
        if (signEnabled) {
            verifySign(bo);
            registerNonce(bo.getNonce());
        }
        checkRate(ip);
        checkDailyQuota(ip);
    }

    /**
     * 验证签名：时间窗内 + HMAC一致（常量时间比较防时序探测）
     */
    private void verifySign(VoiceTtsBo bo) {
        if (bo.getTimestamp() == null || StringUtils.isBlank(bo.getNonce()) || StringUtils.isBlank(bo.getSign())) {
            throw new ServiceException("语音请求签名参数缺失");
        }
        long now = System.currentTimeMillis();
        if (Math.abs(now - bo.getTimestamp()) > SIGN_WINDOW_MILLIS) {
            throw new ServiceException("语音请求已过期，请刷新页面重试");
        }
        String expected = hmacHex(bo.getTimestamp(), bo.getNonce(), bo.getText());
        if (!MessageDigest.isEqual(
            expected.getBytes(StandardCharsets.UTF_8),
            bo.getSign().getBytes(StandardCharsets.UTF_8))) {
            throw new ServiceException("语音请求签名校验失败");
        }
    }

    /**
     * nonce登记：验签通过后记录，同一nonce 5分钟内仅可提交一次
     */
    private void registerNonce(String nonce) {
        if (!RedisUtils.setObjectIfAbsent(KEY_NONCE + nonce, "1", Duration.ofMinutes(5))) {
            throw new ServiceException("请勿重复提交语音请求");
        }
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
            throw new ServiceException("语音合成请求过于频繁，请稍后再试");
        }
    }

    /**
     * 单IP日配额：按日累计合成次数，跨日自动失效
     */
    private void checkDailyQuota(String ip) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String key = KEY_QUOTA + today + ":" + ip;
        long count = RedisUtils.incrAtomicValue(key);
        if (count == 1) {
            RedisUtils.expire(key, Duration.ofDays(2));
        }
        if (count > readIntConfig(CONFIG_DAILY_LIMIT, DEFAULT_DAILY_LIMIT)) {
            throw new ServiceException("今日语音合成次数已达上限，请明天再试");
        }
    }

    /**
     * 读取整数参数：缺失或非法回退默认值（保证守卫不因配置问题失效）
     */
    private int readIntConfig(String key, int defaultValue) {
        String value;
        try {
            value = StringUtils.trim(TenantHelper.ignore(() -> sysConfigQueryMapper.selectValueByKey(key)));
        } catch (Exception e) {
            log.warn("读取系统参数{}失败，使用默认值{}", key, defaultValue, e);
            return defaultValue;
        }
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
     * 计算HMAC-SHA256十六进制签名（算法与C端前端保持严格一致）：
     * message = timestamp + "\n" + nonce + "\n" + text
     */
    private String hmacHex(Long timestamp, String nonce, String text) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(
                signSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String message = timestamp + "\n" + nonce + "\n" + (text == null ? "" : text);
            byte[] bytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16))
                    .append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString();
        } catch (Exception e) {
            log.error("TTS签名计算异常", e);
            throw new ServiceException("语音请求校验异常，请重试");
        }
    }
}
