package org.ruoyi.domain.bo.voice;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.common.mybatis.core.domain.BaseEntity;
import org.ruoyi.domain.entity.voice.PlatformVoice;

/**
 * 平台音色字典业务对象 voice_platform
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = PlatformVoice.class, reverseConvertGenerate = false)
public class PlatformVoiceBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 平台标识(aliyun/openai)
     */
    @NotBlank(message = "平台标识不能为空", groups = { AddGroup.class, EditGroup.class })
    private String platform;

    /**
     * 平台音色编码(如 longwan/alloy)
     */
    @NotBlank(message = "音色编码不能为空", groups = { AddGroup.class, EditGroup.class })
    private String voiceCode;

    /**
     * 音色显示名(如 龙婉)
     */
    @NotBlank(message = "音色名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String voiceName;

    /**
     * 性别(0男 1女 2未知)
     */
    private String gender;

    /**
     * 音色描述
     */
    private String description;

    /**
     * 状态(0正常 1停用)
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

}
