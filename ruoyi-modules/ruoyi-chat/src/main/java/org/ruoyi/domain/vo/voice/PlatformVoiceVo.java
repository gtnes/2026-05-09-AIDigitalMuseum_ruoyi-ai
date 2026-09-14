package org.ruoyi.domain.vo.voice;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.ruoyi.domain.entity.voice.PlatformVoice;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 平台音色字典视图对象 voice_platform
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = PlatformVoice.class)
public class PlatformVoiceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 平台标识(aliyun/openai)
     */
    @ExcelProperty(value = "平台标识")
    private String platform;

    /**
     * 平台名称(显示用，如 阿里云)
     */
    @ExcelProperty(value = "平台名称")
    private String platformName;

    /**
     * 平台音色编码(如 longwan/alloy)
     */
    @ExcelProperty(value = "音色编码")
    private String voiceCode;

    /**
     * 音色显示名(如 龙婉)
     */
    @ExcelProperty(value = "音色名称")
    private String voiceName;

    /**
     * 性别(0男 1女 2未知)
     */
    @ExcelProperty(value = "性别")
    private String gender;

    /**
     * 音色描述
     */
    @ExcelProperty(value = "音色描述")
    private String description;

    /**
     * 状态(0正常 1停用)
     */
    @ExcelProperty(value = "状态")
    private String status;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

}
