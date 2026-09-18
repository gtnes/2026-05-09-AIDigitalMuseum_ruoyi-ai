package org.ruoyi.domain.vo.voice;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.ruoyi.domain.entity.voice.VoiceProfile;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * AI语音音色档案视图对象 voice_profile
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = VoiceProfile.class)
public class VoiceProfileVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 音色名称(如 西西)
     */
    @ExcelProperty(value = "音色名称")
    private String voiceName;

    /**
     * 音色头像URL
     */
    private String avatar;

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
     * 平台音色ID(voice_platform.id)
     */
    private Long platformVoiceId;

    /**
     * 平台音色编码(冗余显示，如 longwan)
     */
    @ExcelProperty(value = "平台音色编码")
    private String platformVoiceCode;

    /**
     * 平台音色显示名(冗余显示，如 龙婉)
     */
    @ExcelProperty(value = "平台音色")
    private String platformVoiceName;

    /**
     * 关联模型管理ID(chat_model.id)
     */
    private Long modelId;

    /**
     * 关联模型名称(冗余显示，如 cosyvoice-v2)
     */
    @ExcelProperty(value = "关联模型")
    private String modelName;

    /**
     * 语速(0.5-2.0)
     */
    @ExcelProperty(value = "语速")
    private BigDecimal speed;

    /**
     * 音调(0.5-2.0)
     */
    @ExcelProperty(value = "音调")
    private BigDecimal pitch;

    /**
     * 音量(0-100)
     */
    @ExcelProperty(value = "音量")
    private BigDecimal volume;

    /**
     * 试听文本
     */
    private String sampleText;

    /**
     * 计费单价（元/万字符，博物馆TTS用量计费依据，空=不计费）
     */
    @ExcelProperty(value = "计费单价(元/万字符)")
    private BigDecimal pricePer10k;

    /**
     * 状态(0正常 1停用)
     */
    @ExcelProperty(value = "状态")
    private String status;

    /**
     * 显示顺序
     */
    @ExcelProperty(value = "显示顺序")
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

}
