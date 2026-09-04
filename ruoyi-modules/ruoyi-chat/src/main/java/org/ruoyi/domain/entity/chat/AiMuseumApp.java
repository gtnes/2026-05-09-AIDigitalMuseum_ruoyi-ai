package org.ruoyi.domain.entity.chat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI博物馆智能体配置值对象 ai_museum.chatapps（JSON字段元素）
 *
 * @author gtnes
 * @date 2026-09-04
 */
@Data
public class AiMuseumApp implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * chatapp中的id（即appId，与chat_app.id对应）
     */
    @NotNull(message = "智能体不能为空")
    private Long id;

    /**
     * 背景图片（ossId）
     */
    private String bgUrl;

    /**
     * AI形象不说话时的图片，静态图或gif（ossId）
     */
    private String idleImgUrl;

    /**
     * AI形象说话时的gif（ossId）
     */
    private String talkingGifUrl;

    /**
     * 说明
     */
    private String description;

    /**
     * 展示排序
     */
    private Integer sort;

}
