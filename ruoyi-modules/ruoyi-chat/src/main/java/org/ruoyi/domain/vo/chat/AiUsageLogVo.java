package org.ruoyi.domain.vo.chat;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.ruoyi.domain.entity.chat.AiUsageLog;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 通用AI用量流水视图对象 ai_usage_log
 *
 * @author gtnes
 * @date 2026-09-18
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiUsageLog.class)
public class AiUsageLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 类别（system系统内部/测试调用）
     */
    @ExcelProperty(value = "类别")
    private String category;

    /**
     * 业务类型（chat对话 tts语音合成）
     */
    @ExcelProperty(value = "业务类型")
    private String bizType;

    /**
     * 智能体ID（chat_app.id，对话类记录）
     */
    private Long appId;

    /**
     * 音色档案ID（voice_profile.id，语音类记录）
     */
    private Long voiceId;

    /**
     * 应用名称快照（chat=智能体名称，tts=音色名称）
     */
    @ExcelProperty(value = "应用名称")
    private String appName;

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
     * 操作人ID
     */
    private Long operId;

    /**
     * 操作人昵称
     */
    @ExcelProperty(value = "操作人")
    private String operName;

    /**
     * 调用时间
     */
    @ExcelProperty(value = "调用时间")
    private Date createTime;

}
