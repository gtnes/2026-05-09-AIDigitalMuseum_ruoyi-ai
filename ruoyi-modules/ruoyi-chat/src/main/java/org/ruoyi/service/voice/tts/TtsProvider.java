package org.ruoyi.service.voice.tts;

/**
 * 语音合成平台 SPI
 * 新增平台支持：实现本接口并注册为 Spring Bean 即可，无需改动既有代码。
 *
 * @author ruoyi
 * @date 2026-09-14
 */
public interface TtsProvider {

    /**
     * 平台标识(小写，如 aliyun/openai)，与 voice_platform.platform / voice_profile.platform 对应
     */
    String platform();

    /**
     * 平台显示名(如 阿里云/OpenAI)
     */
    String platformName();

    /**
     * 执行语音合成
     *
     * @param request 统一合成请求
     * @return 合成结果(音频二进制+格式信息)
     */
    TtsSynthesizeResult synthesize(TtsSynthesizeRequest request);

}
