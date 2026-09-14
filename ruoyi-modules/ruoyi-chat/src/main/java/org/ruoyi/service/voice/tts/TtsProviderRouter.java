package org.ruoyi.service.voice.tts;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 语音合成平台路由器：按平台标识路由到对应 Provider（Spring 自动收集全部实现）
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Component
public class TtsProviderRouter {

    private final Map<String, TtsProvider> providerMap;

    public TtsProviderRouter(List<TtsProvider> providers) {
        this.providerMap = providers.stream()
            .collect(Collectors.toMap(TtsProvider::platform, Function.identity()));
    }

    /**
     * 按平台标识获取 Provider
     */
    public TtsProvider getProvider(String platform) {
        TtsProvider provider = providerMap.get(platform);
        if (provider == null) {
            throw new IllegalArgumentException("暂不支持的语音平台: " + platform
                + "，当前已支持: " + String.join("/", supportedPlatforms()));
        }
        return provider;
    }

    /**
     * 平台显示名（未实现的平台原样返回平台标识）
     */
    public String platformName(String platform) {
        TtsProvider provider = providerMap.get(platform);
        return provider == null ? platform : provider.platformName();
    }

    /**
     * 当前已实现的所有平台标识
     */
    public List<String> supportedPlatforms() {
        return new ArrayList<>(providerMap.keySet());
    }

}
