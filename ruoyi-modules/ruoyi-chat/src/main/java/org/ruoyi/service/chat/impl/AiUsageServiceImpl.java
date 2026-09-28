package org.ruoyi.service.chat.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.core.utils.ip.AddressUtils;
import org.ruoyi.common.mybatis.core.page.PageQuery;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.ruoyi.domain.bo.chat.AiUsageLogBo;
import org.ruoyi.domain.entity.chat.AiUsageLog;
import org.ruoyi.domain.entity.chat.ChatApp;
import org.ruoyi.domain.entity.voice.VoiceProfile;
import org.ruoyi.domain.vo.chat.AiUsageLogVo;
import org.ruoyi.domain.vo.chat.AiUsageSummaryVo;
import org.ruoyi.mapper.chat.AiUsageLogMapper;
import org.ruoyi.mapper.chat.ChatAppMapper;
import org.ruoyi.mapper.voice.VoiceProfileMapper;
import org.ruoyi.service.chat.IAiUsageService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * 通用AI用量计费Service业务层处理
 * <p>
 * 记账失败不影响主流程（对话/合成已发生，仅费用流水缺失，由日志告警人工核对）
 *
 * @author gtnes
 * @date 2026-09-18
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AiUsageServiceImpl implements IAiUsageService {

    /** 当前唯一类别：系统内部/测试调用（预留扩展） */
    private static final String CATEGORY_SYSTEM = "system";
    private static final String BIZ_CHAT = "chat";
    private static final String BIZ_TTS = "tts";
    /** token单价计量单位：千token */
    private static final BigDecimal UNIT_TOKENS = BigDecimal.valueOf(1000);
    /** TTS单价计量单位：万字符 */
    private static final BigDecimal UNIT_TTS_CHARS = BigDecimal.valueOf(10000);

    private final AiUsageLogMapper baseMapper;
    private final ChatAppMapper chatAppMapper;
    private final VoiceProfileMapper voiceProfileMapper;

    @Override
    public void recordChat(Long operId, String operName, Long appId, String appName,
                           int chars, long tokensIn, long tokensOut, String clientIp) {
        try {
            ChatApp app = appId == null ? null : chatAppMapper.selectById(appId);
            BigDecimal cost = BigDecimal.ZERO;
            if (app != null) {
                if (app.getPriceInPer1k() != null) {
                    cost = cost.add(app.getPriceInPer1k()
                        .multiply(BigDecimal.valueOf(tokensIn))
                        .divide(UNIT_TOKENS, 6, RoundingMode.HALF_UP));
                }
                if (app.getPriceOutPer1k() != null) {
                    cost = cost.add(app.getPriceOutPer1k()
                        .multiply(BigDecimal.valueOf(tokensOut))
                        .divide(UNIT_TOKENS, 6, RoundingMode.HALF_UP));
                }
            }
            AiUsageLog logRow = new AiUsageLog();
            logRow.setCategory(CATEGORY_SYSTEM);
            logRow.setBizType(BIZ_CHAT);
            logRow.setAppId(appId);
            logRow.setAppName(app != null ? app.getAppName() : appName);
            logRow.setChars(chars);
            logRow.setTokensIn((int) tokensIn);
            logRow.setTokensOut((int) tokensOut);
            logRow.setCost(cost);
            logRow.setOperId(operId);
            logRow.setOperName(operName);
            fillClientInfo(logRow, clientIp);
            logRow.setCreateTime(new Date());
            baseMapper.insert(logRow);
        } catch (Exception e) {
            log.error("通用对话用量记账失败 operId={}, appId={}, tokensIn={}, tokensOut={}",
                operId, appId, tokensIn, tokensOut, e);
        }
    }

    @Override
    public void recordTts(Long operId, String operName, Long voiceId, String voiceName, int chars, String clientIp) {
        try {
            VoiceProfile profile = voiceId == null ? null : voiceProfileMapper.selectById(voiceId);
            BigDecimal cost = BigDecimal.ZERO;
            if (profile != null && profile.getPricePer10k() != null) {
                cost = profile.getPricePer10k()
                    .multiply(BigDecimal.valueOf(chars))
                    .divide(UNIT_TTS_CHARS, 6, RoundingMode.HALF_UP);
            }
            AiUsageLog logRow = new AiUsageLog();
            logRow.setCategory(CATEGORY_SYSTEM);
            logRow.setBizType(BIZ_TTS);
            logRow.setVoiceId(voiceId);
            logRow.setAppName(profile != null ? profile.getVoiceName() : voiceName);
            logRow.setChars(chars);
            logRow.setTokensIn(0);
            logRow.setTokensOut(0);
            logRow.setCost(cost);
            logRow.setOperId(operId);
            logRow.setOperName(operName);
            fillClientInfo(logRow, clientIp);
            logRow.setCreateTime(new Date());
            baseMapper.insert(logRow);
        } catch (Exception e) {
            log.error("通用语音用量记账失败 operId={}, voiceId={}, chars={}", operId, voiceId, chars, e);
        }
    }

    /**
     * 填充访问者IP与归属地（ip2region离线解析，内网显示"内网IP"；解析失败不阻断记账）
     */
    private void fillClientInfo(AiUsageLog logRow, String clientIp) {
        if (StringUtils.isBlank(clientIp)) {
            return;
        }
        logRow.setClientIp(clientIp);
        try {
            logRow.setLocation(AddressUtils.getRealAddressByIP(clientIp));
        } catch (Exception e) {
            log.warn("IP归属地解析失败 ip={}", clientIp, e);
        }
    }

    @Override
    public TableDataInfo<AiUsageLogVo> queryPageList(AiUsageLogBo bo, PageQuery pageQuery) {
        IPage<AiUsageLogVo> result = baseMapper.selectUsageLogPage(pageQuery.build(), bo);
        return TableDataInfo.build(result);
    }

    @Override
    public List<AiUsageLogVo> queryList(AiUsageLogBo bo) {
        return baseMapper.selectUsageLogList(bo);
    }

    @Override
    public List<AiUsageSummaryVo> summary(AiUsageLogBo bo) {
        return baseMapper.selectUsageSummary(bo);
    }

}
