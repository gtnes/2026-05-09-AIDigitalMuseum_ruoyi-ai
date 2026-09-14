package org.ruoyi.domain.entity.voice;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ruoyi.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * 平台音色字典对象 voice_platform
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("voice_platform")
public class PlatformVoice extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 平台标识(aliyun/openai)
     */
    private String platform;

    /**
     * 平台音色编码(如 longwan/alloy)
     */
    private String voiceCode;

    /**
     * 音色显示名(如 龙婉)
     */
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

}
