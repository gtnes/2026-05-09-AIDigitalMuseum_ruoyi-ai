package org.ruoyi.service.chat.impl.app.provider;

import org.ruoyi.domain.vo.chat.ChatAppVo;

public interface AppCallProvider {

    void streamCall(ChatAppVo app, String userInput, String sessionId);

    String getProviderCode();
}
