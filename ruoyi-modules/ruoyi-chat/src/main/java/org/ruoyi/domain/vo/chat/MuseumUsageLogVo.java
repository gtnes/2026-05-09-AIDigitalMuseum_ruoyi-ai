package org.ruoyi.domain.vo.chat;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * AI博物馆用量流水视图对象 ai_museum_usage_log
 *
 * @author gtnes
 * @date 2026-09-18
 */
@Data
@ExcelIgnoreUnannotated
public class MuseumUsageLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 博物馆ID
     */
    private Long museumId;

    /**
     * 博物馆名称（关联ai_museum.title）
     */
    @ExcelProperty(value = "博物馆")
    private String museumTitle;

    /**
     * 业务类型（chat对话 tts语音合成）
     */
    @ExcelProperty(value = "业务类型")
    private String bizType;

    /**
     * 智能体ID（对话类记录）
     */
    private Long appId;

    /**
     * 智能体名称（关联chat_app.app_name，对话类记录）
     */
    @ExcelProperty(value = "智能体名称")
    private String appName;

    /**
     * 音色档案ID（语音类记录）
     */
    private Long voiceId;

    /**
     * 音色名称（关联voice_profile.voice_name，语音类记录）
     */
    @ExcelProperty(value = "音色名称")
    private String voiceName;

    /**
     * 字符数
     */
    @ExcelProperty(value = "字符数")
    private Integer chars;

    /**
     * 输入token数
     */
    @ExcelProperty(value = "输入token")
    private Integer tokensIn;

    /**
     * 输出token数
     */
    @ExcelProperty(value = "输出token")
    private Integer tokensOut;

    /**
     * 费用（元）
     */
    @ExcelProperty(value = "费用(元)")
    private BigDecimal cost;

    /**
     * 调用时间
     */
    @ExcelProperty(value = "调用时间")
    private Date createTime;

}
