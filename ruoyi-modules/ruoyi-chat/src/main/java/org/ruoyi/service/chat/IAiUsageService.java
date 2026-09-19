package org.ruoyi.service.chat;

import org.ruoyi.common.mybatis.core.page.PageQuery;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.ruoyi.domain.bo.chat.AiUsageLogBo;
import org.ruoyi.domain.vo.chat.AiUsageLogVo;
import org.ruoyi.domain.vo.chat.AiUsageSummaryVo;

import java.util.List;

/**
 * 通用AI用量计费Service接口
 * <p>
 * 记录登录通用接口（chat/send、voice/tts）的对话/语音合成用量流水
 *
 * @author gtnes
 * @date 2026-09-18
 */
public interface IAiUsageService {

    /**
     * 记录通用对话用量（按智能体token单价计费）
     *
     * @param operId     操作人ID（登录用户）
     * @param operName   操作人昵称快照
     * @param appId      智能体ID
     * @param appName    智能体名称快照
     * @param chars      输入内容字符数
     * @param tokensIn   输入token数
     * @param tokensOut  输出token数
     */
    void recordChat(Long operId, String operName, Long appId, String appName, int chars, long tokensIn, long tokensOut);

    /**
     * 记录通用语音合成用量（按音色字符单价计费）
     *
     * @param operId    操作人ID（登录用户）
     * @param operName  操作人昵称快照
     * @param voiceId   音色档案ID
     * @param voiceName 音色名称快照
     * @param chars     合成文本字符数
     */
    void recordTts(Long operId, String operName, Long voiceId, String voiceName, int chars);

    /**
     * 分页查询通用AI用量流水列表
     */
    TableDataInfo<AiUsageLogVo> queryPageList(AiUsageLogBo bo, PageQuery pageQuery);

    /**
     * 查询通用AI用量流水列表（导出用）
     */
    List<AiUsageLogVo> queryList(AiUsageLogBo bo);

    /**
     * 按类别+应用+业务类型汇总用量与费用（支持类别/业务类型/应用名称/日期范围筛选）
     */
    List<AiUsageSummaryVo> summary(AiUsageLogBo bo);

}
