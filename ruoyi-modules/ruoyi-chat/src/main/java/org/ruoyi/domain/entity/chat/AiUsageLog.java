package org.ruoyi.domain.entity.chat;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 通用AI用量流水对象 ai_usage_log
 * <p>
 * 登录通用接口（chat/send、voice/tts，apps-chat测试页等系统内部调用）的计费流水：
 * 应用名称/操作人按调用当时快照固化，费用按当时单价固化，后期调价或改名不影响历史记录
 * 表已加入租户排除清单（运营侧全局统计）
 *
 * @author gtnes
 * @date 2026-09-18
 */
@Data
@TableName("ai_usage_log")
public class AiUsageLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 类别（system系统内部/测试调用，预留扩展）
     */
    private String category;

    /**
     * 业务类型（chat对话 tts语音合成）
     */
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
    private String appName;

    /**
     * 字符数（对话=输入内容长度，TTS=合成文本长度）
     */
    private Integer chars;

    /**
     * 输入token数（对话类记录，来自百炼usage）
     */
    private Integer tokensIn;

    /**
     * 输出token数（对话类记录，来自百炼usage）
     */
    private Integer tokensOut;

    /**
     * 费用（元，按当时单价固化）
     */
    private BigDecimal cost;

    /**
     * 操作人ID（登录用户）
     */
    private Long operId;

    /**
     * 操作人昵称快照
     */
    private String operName;

    /**
     * 调用时间
     */
    private Date createTime;

}
