package org.ruoyi.domain.bo.voice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 语音合成试听请求业务对象（管理端编辑弹窗内即时试听，无需先保存音色档案）
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Data
public class VoiceTtsPreviewBo {

    /**
     * 平台音色ID(voice_platform.id)
     */
    @NotNull(message = "平台音色ID不能为空")
    private Long platformVoiceId;

    /**
     * 关联模型管理ID(chat_model.id，提供平台apiHost/apiKey)
     */
    @NotNull(message = "关联模型ID不能为空")
    private Long modelId;

    /**
     * 试听文本
     */
    @NotBlank(message = "试听文本不能为空")
    private String text;

    /**
     * 语速(0.5-2.0)
     */
    private BigDecimal speed;

    /**
     * 音调(0.5-2.0)
     */
    private BigDecimal pitch;

    /**
     * 音量(0-100)
     */
    private BigDecimal volume;

}
