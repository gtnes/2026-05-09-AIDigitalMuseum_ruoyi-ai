package org.ruoyi.domain.bo.voice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 语音合成请求业务对象（按已保存的音色档案合成，C端聊天播报使用）
 * <p>
 * 公开接口防滥用：sign/timestamp/nonce 为HMAC签名三件套，由 TtsRequestGuard 校验
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
     * 待合成文本（前端按句分段后逐段请求，单段远小于上限）
     */
    @NotBlank(message = "合成文本不能为空")
    @Size(max = 200, message = "单次合成文本过长")
    private String text;

    /**
     * 签名时间戳（毫秒），与服务器时差须在±5分钟内
     */
    private Long timestamp;

    /**
     * 随机串（防重放，同一nonce 5分钟内仅允许提交一次）
     */
    private String nonce;

    /**
     * HMAC-SHA256签名（hex）：hmac(secret, timestamp + "\n" + nonce + "\n" + text)
     */
    private String sign;

}
