package org.ruoyi.domain.vo.voice;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 语音合成结果视图对象
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Data
@Builder
public class VoiceTtsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 音频格式(如 mp3)
     */
    private String format;

    /**
     * MIME类型(如 audio/mpeg)
     */
    private String mimeType;

    /**
     * base64音频数据
     */
    private String b64Json;

    /**
     * data:协议音频地址，前端可直接 new Audio(dataUrl) 播放
     */
    private String dataUrl;

    /**
     * 合成文本长度
     */
    private Integer textLength;

}
