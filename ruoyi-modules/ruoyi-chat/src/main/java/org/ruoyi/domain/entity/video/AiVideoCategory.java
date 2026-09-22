package org.ruoyi.domain.entity.video;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ruoyi.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * AI视频分类对象 ai_video_category
 *
 * @author gtnes
 * @date 2026-09-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_video_category")
public class AiVideoCategory extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 分类名称
     */
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
