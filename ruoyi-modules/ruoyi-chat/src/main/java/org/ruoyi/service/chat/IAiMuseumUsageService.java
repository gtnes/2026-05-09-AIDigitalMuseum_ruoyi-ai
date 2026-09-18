package org.ruoyi.service.chat;

import org.ruoyi.domain.bo.chat.MuseumUsageQueryBo;
import org.ruoyi.domain.vo.chat.MuseumUsageSummaryVo;

import java.util.List;

/**
 * AI博物馆用量计费Service接口
 * <p>
 * 对话按token计费（chat_app单价：元/千token），语音合成按字符计费（voice_profile单价：元/万字符）
 *
 * @author gtnes
 * @date 2026-09-18
 */
public interface IAiMuseumUsageService {

    /**
     * 记录博物馆对话用量（token计费，单价取智能体配置，按当时单价固化费用）
     *
     * @param museumId  博物馆ID
     * @param appId     智能体ID
     * @param chars     输入内容字符数
     * @param tokensIn  输入token数（百炼usage）
     * @param tokensOut 输出token数（百炼usage）
     */
    void recordChat(Long museumId, Long appId, int chars, long tokensIn, long tokensOut);

    /**
     * 记录博物馆语音合成用量（字符计费，单价取音色档案配置，按当时单价固化费用）
     *
     * @param museumId 博物馆ID
     * @param voiceId  音色档案ID
     * @param chars    合成文本长度
     */
    void recordTts(Long museumId, Long voiceId, int chars);

    /**
     * 按博物馆+业务类型汇总用量与费用
     *
     * @param bo 查询条件（博物馆/业务类型/日期范围均可选）
     * @return 汇总列表（按费用降序）
     */
    List<MuseumUsageSummaryVo> summary(MuseumUsageQueryBo bo);

}
