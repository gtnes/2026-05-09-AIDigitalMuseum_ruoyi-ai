package org.ruoyi.service.voice.tts;

import lombok.Builder;
import lombok.Data;

/**
 * 语音合成统一结果
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Data
@Builder
public class TtsSynthesizeResult {

    /**
     * 音频二进制内容
     */
    private byte[] bytes;

    /**
     * 音频格式(如 mp3)
     */
    private String format;

    /**
     * MIME类型(如 audio/mpeg)
     */
    private String mimeType;

}
