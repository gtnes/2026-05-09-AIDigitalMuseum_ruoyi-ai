package org.ruoyi.domain.vo.chat;

import lombok.Data;
import org.ruoyi.common.translation.annotation.Translation;
import org.ruoyi.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI博物馆前台展示智能体配置视图对象（图片ossId序列化时自动转为URL）
 *
 * @author gtnes
 * @date 2026-09-07
 */
@Data
public class AiMuseumAppFrontVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * chatapp中的id（即appId，与chat_app.id对应）
     */
    private Long id;

    /**
     * 应用名称（来自chatapp）
     */
    private String appName;

    /**
     * 应用描述（来自chat_app.appDescribe，H5展示名称）
     */
    private String appDescribe;

    /**
     * 应用图标（来自chat_app.appShow，ossId，序列化时自动转为URL）
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL)
    private String appShow;

    /**
     * 背景图片（ossId，序列化时自动转为URL）
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL)
    private String bgUrl;

    /**
     * AI形象不说话时的图片，静态图或gif（ossId，序列化时自动转为URL）
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL)
    private String idleImgUrl;

    /**
     * AI形象说话时的gif（ossId，序列化时自动转为URL）
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL)
    private String talkingGifUrl;

    /**
     * 说明
     */
    private String description;

    /**
     * AI语音音色档案id（voice_profile.id，空=不自动播报）
     */
    private Long voiceProfileId;

    /**
     * 职责
     */
    private String duty;

    /**
     * 展示排序
     */
    private Integer sort;

}
