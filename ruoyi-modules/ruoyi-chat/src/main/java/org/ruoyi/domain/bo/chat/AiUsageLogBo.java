package org.ruoyi.domain.bo.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ruoyi.common.mybatis.core.domain.BaseEntity;

/**
 * 通用AI用量流水查询对象 ai_usage_log
 *
 * @author gtnes
 * @date 2026-09-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiUsageLogBo extends BaseEntity {

    /**
     * 类别（system系统内部/测试调用）
     */
    private String category;

    /**
     * 业务类型（chat对话 tts语音合成）
     */
    private String bizType;

    /**
     * 应用名称（模糊查询）
     */
    private String appName;

}
