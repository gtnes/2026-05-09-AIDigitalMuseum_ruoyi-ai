package org.ruoyi.domain.bo.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 博物馆C端对话发送请求业务对象（公开接口，无需登录）
 * <p>
 * 后端校验：博物馆存在且启用、服务未到期、智能体已在该博物馆开通
 *
 * @author gtnes
 * @date 2026-09-18
 */
@Data
public class MuseumChatSendBo {

    /**
     * 博物馆ID（ai_museum.id）
     */
    @NotNull(message = "博物馆ID不能为空")
    private Long museumId;

    /**
     * 智能体ID（chat_app.id）
     */
    @NotNull(message = "智能体ID不能为空")
    private Long appId;

    /**
     * 对话内容
     */
    @NotBlank(message = "对话内容不能为空")
    private String content;

    /**
     * SSE会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

}
