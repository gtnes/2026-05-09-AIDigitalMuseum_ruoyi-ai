package org.ruoyi.domain.bo.voice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 语音合成请求业务对象（按已保存的音色档案合成，C端聊天播报使用）
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Data
public class VoiceTtsBo {

    /**
     * 音色档案ID(voice_profile.id)
     */
    @NotNull(message = "音色ID不能为空")
    private Long voiceId;

    /**
     * 待合成文本
     */
    @NotBlank(message = "合成文本不能为空")
    private String text;

}
