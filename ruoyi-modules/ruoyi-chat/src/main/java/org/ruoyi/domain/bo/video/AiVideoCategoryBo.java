package org.ruoyi.domain.bo.video;

import org.ruoyi.domain.entity.video.AiVideoCategory;
import org.ruoyi.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;

/**
 * AI视频分类业务对象 ai_video_category
 *
 * @author gtnes
 * @date 2026-09-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiVideoCategory.class, reverseConvertGenerate = false)
public class AiVideoCategoryBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String categoryName;

    /**
     * 状态(0正常 1停用)
     */
    private String status;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

}
