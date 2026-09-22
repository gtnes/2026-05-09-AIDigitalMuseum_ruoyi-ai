package org.ruoyi.domain.vo.video;

import org.ruoyi.domain.entity.video.AiVideo;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.ruoyi.common.excel.annotation.ExcelDictFormat;
import org.ruoyi.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * AI视频视图对象 ai_video
 *
 * @author gtnes
 * @date 2026-09-22
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiVideo.class)
public class AiVideoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 标题
     */
    @ExcelProperty(value = "标题")
    private String title;

    /**
     * 封面图片（sys_oss.ossId，前端显示时实时换取访问URL）
     */
    private String coverUrl;

    /**
     * 视频文件（sys_oss.ossId，前端显示时实时换取访问URL）
     */
    private String videoUrl;

    /**
     * 所属分类ID(ai_video_category.id，仅内部筛选用，不对外)
     */
    private Long categoryId;

    /**
     * 所属分类名称(冗余显示)
     */
    @ExcelProperty(value = "所属分类")
    private String categoryName;

    /**
     * 展示类别(对外展示：宣传片/文物/历史等)
     */
    @ExcelProperty(value = "展示类别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "ai_video_show_category")
    private String showCategory;

    /**
     * 描述
     */
    @ExcelProperty(value = "描述")
    private String description;

    /**
     * 预设问题列表
     */
    private List<String> presetQuestions;

    /**
     * 视频时长(秒)
     */
    @ExcelProperty(value = "视频时长(秒)")
    private Integer duration;

    /**
     * 播放次数
     */
    @ExcelProperty(value = "播放次数")
    private Long playCount;

    /**
     * 显示顺序
     */
    @ExcelProperty(value = "显示顺序")
    private Integer sort;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

}
