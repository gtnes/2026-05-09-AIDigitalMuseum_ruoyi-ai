package org.ruoyi.domain.vo.chat;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.ruoyi.domain.entity.chat.AiMuseumApp;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI博物馆智能体配置视图对象 ai_museum.chatapps（JSON字段元素）
 *
 * @author gtnes
 * @date 2026-09-04
 */
@Data
@AutoMapper(target = AiMuseumApp.class)
public class AiMuseumAppVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * chatapp中的id（即appId，与chat_app.id对应）
     */
    private Long id;

    /**
     * 应用名称（来自chatapp，不可修改）
     */
    private String appName;

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
     * AI语音音色档案id（voice_profile.id，空=不播报）
     */
    private Long voiceProfileId;

    /**
     * 语音开关（关闭时C端不显示播报按钮、不调用语音合成接口）
     */
    private Boolean voiceEnabled;

    /**
     * 语音自动播报（开启时C端回复完成自动朗读）
     */
    private Boolean voiceAutoPlay;

    /**
     * 职责
     */
    private String duty;

    /**
     * 展示排序
     */
    private Integer sort;

}
