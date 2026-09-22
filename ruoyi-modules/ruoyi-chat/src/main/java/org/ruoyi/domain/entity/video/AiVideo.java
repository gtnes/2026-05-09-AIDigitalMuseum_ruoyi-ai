package org.ruoyi.domain.entity.video;

import org.ruoyi.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * AI视频对象 ai_video
 *
 * @author gtnes
 * @date 2026-09-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "ai_video", autoResultMap = true)
public class AiVideo extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面图片（sys_oss.ossId，显示时实时换取访问URL，不存签名URL）
     */
    private String coverUrl;

    /**
     * 视频文件（sys_oss.ossId，显示时实时换取访问URL，不存签名URL）
     */
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
     * 预设问题列表（JSON数组存储）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> presetQuestions;

    /**
     * 视频时长(秒)
     */
    private Integer duration;

    /**
     * 播放次数
     */
    private Long playCount;

    /**
     * 显示顺序
     */
    private Integer sort;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;

}
