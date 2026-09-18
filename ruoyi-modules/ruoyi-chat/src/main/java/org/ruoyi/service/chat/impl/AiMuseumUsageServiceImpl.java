package org.ruoyi.service.chat.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.domain.bo.chat.MuseumUsageQueryBo;
import org.ruoyi.domain.entity.chat.AiMuseumUsageLog;
import org.ruoyi.domain.entity.chat.ChatApp;
import org.ruoyi.domain.entity.voice.VoiceProfile;
import org.ruoyi.domain.vo.chat.MuseumUsageSummaryVo;
import org.ruoyi.mapper.chat.AiMuseumUsageLogMapper;
import org.ruoyi.mapper.chat.ChatAppMapper;
import org.ruoyi.mapper.voice.VoiceProfileMapper;
import org.ruoyi.service.chat.IAiMuseumUsageService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * AI博物馆用量计费Service业务层处理
 * <p>
 * 记账失败不影响主流程（对话/合成已发生，仅费用流水缺失，由日志告警人工核对）
 *
 * @author gtnes
 * @date 2026-09-18
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AiMuseumUsageServiceImpl implements IAiMuseumUsageService {

    private static final String BIZ_CHAT = "chat";
    private static final String BIZ_TTS = "tts";
    /** token单价计量单位：千token */
    private static final BigDecimal UNIT_TOKENS = BigDecimal.valueOf(1000);
    /** TTS单价计量单位：万字符 */
    private static final BigDecimal UNIT_TTS_CHARS = BigDecimal.valueOf(10000);

    private final AiMuseumUsageLogMapper baseMapper;
    private final ChatAppMapper chatAppMapper;
    private final VoiceProfileMapper voiceProfileMapper;

    @Override
    public void recordChat(Long museumId, Long appId, int chars, long tokensIn, long tokensOut) {
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
            AiMuseumUsageLog logRow = new AiMuseumUsageLog();
            logRow.setMuseumId(museumId);
            logRow.setBizType(BIZ_CHAT);
            logRow.setAppId(appId);
            logRow.setChars(chars);
            logRow.setTokensIn((int) tokensIn);
            logRow.setTokensOut((int) tokensOut);
            logRow.setCost(cost);
            logRow.setCreateTime(new Date());
            baseMapper.insert(logRow);
        } catch (Exception e) {
            log.error("博物馆对话用量记账失败 museumId={}, appId={}, tokensIn={}, tokensOut={}",
                museumId, appId, tokensIn, tokensOut, e);
        }
    }

    @Override
    public void recordTts(Long museumId, Long voiceId, int chars) {
        try {
            VoiceProfile profile = voiceId == null ? null : voiceProfileMapper.selectById(voiceId);
            BigDecimal cost = BigDecimal.ZERO;
            if (profile != null && profile.getPricePer10k() != null) {
                cost = profile.getPricePer10k()
                    .multiply(BigDecimal.valueOf(chars))
                    .divide(UNIT_TTS_CHARS, 6, RoundingMode.HALF_UP);
            }
            AiMuseumUsageLog logRow = new AiMuseumUsageLog();
            logRow.setMuseumId(museumId);
            logRow.setBizType(BIZ_TTS);
            logRow.setVoiceId(voiceId);
            logRow.setChars(chars);
            logRow.setTokensIn(0);
            logRow.setTokensOut(0);
            logRow.setCost(cost);
            logRow.setCreateTime(new Date());
            baseMapper.insert(logRow);
        } catch (Exception e) {
            log.error("博物馆语音用量记账失败 museumId={}, voiceId={}, chars={}", museumId, voiceId, chars, e);
        }
    }

    @Override
    public List<MuseumUsageSummaryVo> summary(MuseumUsageQueryBo bo) {
        return baseMapper.selectUsageSummary(bo);
    }

}
