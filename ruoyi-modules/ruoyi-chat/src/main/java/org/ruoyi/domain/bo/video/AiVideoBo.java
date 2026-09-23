package org.ruoyi.domain.bo.video;

import org.ruoyi.domain.entity.video.AiVideo;
import org.ruoyi.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;

import java.util.List;

/**
 * AI视频业务对象 ai_video
 *
 * @author gtnes
 * @date 2026-09-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiVideo.class, reverseConvertGenerate = false)
public class AiVideoBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * 封面图片（sys_oss.ossId）
     */
    private String coverUrl;

    /**
     * 视频文件（sys_oss.ossId）
     */
    @NotBlank(message = "视频不能为空", groups = { AddGroup.class, EditGroup.class })
    private String videoUrl;

    /**
     * 所属分类ID(ai_video_category.id，仅内部筛选用，不对外)
     */
    private Long categoryId;

    /**
     * 展示类别(对外展示：宣传片/文物/历史等，字典 ai_video_show_category)
     */
    private String showCategory;

    /**
     * 描述
     */
    private String description;

    /**
     * 预设问题列表
     */
    private List<String> presetQuestions;

    /**
     * 视频时长(秒)
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

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

}
