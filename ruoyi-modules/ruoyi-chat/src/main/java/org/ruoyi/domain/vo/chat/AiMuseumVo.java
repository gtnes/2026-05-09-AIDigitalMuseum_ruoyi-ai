package org.ruoyi.domain.vo.chat;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.ruoyi.domain.entity.chat.AiMuseum;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * AI博物馆实例视图对象 ai_museum
 *
 * @author gtnes
 * @date 2026-09-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiMuseum.class)
public class AiMuseumVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 实例状态（1正常 0停用）
     */
    @ExcelProperty(value = "实例状态(1正常 0停用)")
    private Integer status;

    /**
     * 原始机构名称
     */
    @ExcelProperty(value = "原始机构名称")
    private String orgTitle;

    /**
     * 展示标题
     */
    @ExcelProperty(value = "展示标题")
    private String title;

    /**
     * Logo地址（ossId）
     */
    private String logoUrl;

    /**
     * banner背景图地址（ossId）
     */
    private String bannerUrl;

    /**
     * 服务开始时间戳（秒）
     */
    private Long startTime;

    /**
     * 服务到期时间戳（秒）
     */
    private Long endTime;

    /**
     * 是否已过期：0未过期，1已过期
     */
    @ExcelProperty(value = "是否已过期(1已过期 0未过期)")
    private Integer isExpire;

    /**
     * 过期提示文案
     */
    @ExcelProperty(value = "过期提示文案")
    private String expireTips;

    /**
     * 自定义名称
     */
    @ExcelProperty(value = "自定义名称")
    private String customName;

    /**
     * 是否开启VR（0关闭 1开启）
     */
    @ExcelProperty(value = "是否开启VR(1开启 0关闭)")
    private Integer vrEnable;

    /**
     * VR地址
     */
    @ExcelProperty(value = "VR地址")
    private String vrUrl;

    /**
     * 是否开启AI视频（0关闭 1开启）
     */
    @ExcelProperty(value = "是否开启AI视频(1开启 0关闭)")
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
     * 权限操作列表（保留字段，暂未启用）
     */
    private List<Map<String, Object>> operationList;

    /**
     * 智能体配置列表
     */
    private List<AiMuseumAppVo> chatapps;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

}
