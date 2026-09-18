package org.ruoyi.service.chat.impl.app.provider;

import org.ruoyi.domain.vo.chat.ChatAppVo;
import org.ruoyi.service.chat.impl.app.AppCallService;

public interface AppCallProvider {

    /**
     * 流式调用应用
     *
     * @param app     应用信息
     * @param request 调用请求（content/sessionId/appId，museumId非空时按token计费记账）
     */
    void streamCall(ChatAppVo app, AppCallService.AppCallRequest request);

    String getProviderCode();
}
