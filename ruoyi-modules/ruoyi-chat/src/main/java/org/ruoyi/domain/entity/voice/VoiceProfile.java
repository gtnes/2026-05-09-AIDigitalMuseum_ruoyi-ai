package org.ruoyi.domain.entity.voice;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ruoyi.common.tenant.core.TenantEntity;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * AI语音音色档案对象 voice_profile
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("voice_profile")
public class VoiceProfile extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 音色名称(如 西西)
     */
    private String voiceName;

    /**
     * 音色头像URL
     */
    private String avatar;

    /**
     * 平台标识(aliyun/openai)
     */
    private String platform;

    /**
     * 平台音色ID(voice_platform.id)
     */
    private Long platformVoiceId;

    /**
     * 关联模型管理ID(chat_model.id，提供平台apiHost/apiKey)
     */
    private Long modelId;

    /**
     * 语速(0.5-2.0，默认1.0)
     */
    private BigDecimal speed;

    /**
     * 音调(0.5-2.0，默认1.0)
     */
    private BigDecimal pitch;

    /**
     * 音量(0-100，默认50)
     */
    private BigDecimal volume;

    /**
     * 试听文本
     */
    private String sampleText;

    /**
     * 状态(0正常 1停用)
     */
    private String status;

    /**
     * 显示顺序
     */
    private Integer sort;

}
