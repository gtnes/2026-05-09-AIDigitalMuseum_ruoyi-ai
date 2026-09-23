package org.ruoyi.domain.vo.video;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.ruoyi.common.translation.annotation.Translation;
import org.ruoyi.common.translation.constant.TransConstant;
import org.ruoyi.domain.entity.video.AiVideo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * AI视频前台展示视图对象（博物馆C端公开接口专用，不包含管理端字段）
 *
 * @author gtnes
 * @date 2026-09-23
 */
@Data
@AutoMapper(target = AiVideo.class)
public class AiVideoFrontVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 视频名称
     */
    private String title;

    /**
     * 封面地址（ossId，序列化时自动转为URL）
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL)
    private String coverUrl;

    /**
     * 视频地址（ossId，序列化时自动转为URL）
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL)
    private String videoUrl;

    /**
     * 展示类别（对外展示：宣传片/文物/历史等）
     */
    private String showCategory;

    /**
     * 视频介绍
     */
    private String description;

    /**
     * 预设问题列表（播放页"你可以试着问我"模块）
     */
    private List<String> presetQuestions;

    /**
     * 视频时长（秒）
     */
    private Integer duration;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 是否置顶（0否 1是）
     */
    private Integer topFlag;

}
