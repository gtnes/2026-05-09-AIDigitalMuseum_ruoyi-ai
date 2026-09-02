package org.ruoyi.service.chat.impl.app.provider;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AppCallProviderFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private final Map<String, AppCallProvider> providerMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        Map<String, AppCallProvider> beans = applicationContext.getBeansOfType(AppCallProvider.class);
        for (AppCallProvider provider : beans.values()) {
            providerMap.put(provider.getProviderCode(), provider);
            log.info("注册应用调用Provider: {} -> {}", provider.getProviderCode(), provider.getClass().getSimpleName());
        }
    }

    public AppCallProvider getProvider(String providerCode) {
        return providerMap.get(providerCode);
    }
}
