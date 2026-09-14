package org.ruoyi.service.voice.tts;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 语音合成统一请求（与具体平台无关，由各平台 Provider 自行映射参数）
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Data
@Builder
public class TtsSynthesizeRequest {

    /**
     * 待合成文本
     */
    private String text;

    /**
     * 平台音色编码(如 longwan/alloy)
     */
    private String voice;

    /**
     * 平台模型名(如 cosyvoice-v2/tts-1)
     */
    private String model;

    /**
     * 音频格式(默认 mp3)
     */
    private String format;

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

    /**
     * 平台请求地址(可空，Provider 使用自身默认地址)
     */
    private String apiHost;

    /**
     * 平台密钥
     */
    private String apiKey;

}
