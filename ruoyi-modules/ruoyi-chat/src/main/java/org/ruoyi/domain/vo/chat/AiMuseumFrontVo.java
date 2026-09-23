package org.ruoyi.domain.vo.chat;

import lombok.Data;
import org.ruoyi.common.translation.annotation.Translation;
import org.ruoyi.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * AI博物馆前台展示视图对象（H5页面专用接口返回，不包含管理端字段）
 *
 * @author gtnes
 * @date 2026-09-07
 */
@Data
public class AiMuseumFrontVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 展示标题
     */
    private String title;

    /**
     * Logo地址（ossId，序列化时自动转为URL）
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL)
    private String logoUrl;

    /**
     * banner背景图地址（ossId，序列化时自动转为URL）
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL)
    private String bannerUrl;

    /**
     * 是否已过期：0未过期，1已过期
     */
    private Integer isExpire;

    /**
     * 过期提示文案
     */
    private String expireTips;

    /**
     * 是否开启VR（0关闭 1开启）
     */
    private Integer vrEnable;

    /**
     * VR地址
     */
    private String vrUrl;

    /**
     * 是否开启AI视频（0关闭 1开启）
     */
    private Integer videoEnable;

    /**
     * AI视频分类id（ai_video_category.id）
     */
    private Long videoCategoryId;

    /**
     * AI视频讲解员（chat_app.id，从本博物馆智能体配置中选择）
     */
    private Long videoChatappId;

    /**
     * 智能体配置列表
     */
    private List<AiMuseumAppFrontVo> chatapps;

}
