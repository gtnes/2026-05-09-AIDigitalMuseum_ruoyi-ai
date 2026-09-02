package org.ruoyi.service.chat.impl.app;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.common.sse.utils.SseMessageUtils;
import org.ruoyi.domain.vo.chat.ChatAppVo;
import org.ruoyi.service.chat.IChatAppService;
import org.ruoyi.service.chat.impl.app.provider.AppCallProvider;
import org.ruoyi.service.chat.impl.app.provider.AppCallProviderFactory;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppCallService {

    private final IChatAppService chatAppService;
    private final AppCallProviderFactory providerFactory;

    @Data
    public static class AppCallRequest {
        private Long appId;
        private String content;
        private String sessionId;
    }

    public void streamCall(AppCallRequest request) {
        ChatAppVo app = chatAppService.queryById(request.getAppId());
        if (app == null) {
            SseMessageUtils.sendError(request.getSessionId(), "应用不存在");
            return;
        }
        if (!"0".equals(app.getStatus())) {
            SseMessageUtils.sendError(request.getSessionId(), "应用已停用");
            return;
        }
        AppCallProvider provider = providerFactory.getProvider(app.getProviderCode());
        if (provider == null) {
            SseMessageUtils.sendError(request.getSessionId(), "不支持的服务商: " + app.getProviderCode());
            return;
        }
        provider.streamCall(app, request.getContent(), request.getSessionId());
    }
}
