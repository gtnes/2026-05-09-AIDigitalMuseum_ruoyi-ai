package org.ruoyi.domain.entity.chat;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * AI博物馆用量流水对象 ai_museum_usage_log
 * <p>
 * 博物馆C端对话/语音合成接口的计费流水：费用按调用当时的单价固化，后期调价不影响历史账单
 * 表已加入租户排除清单（匿名C端写入、运营侧全局统计）
 *
 * @author gtnes
 * @date 2026-09-18
 */
@Data
@TableName("ai_museum_usage_log")
public class AiMuseumUsageLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 博物馆ID（ai_museum.id）
     */
    private Long museumId;

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
     * 调用时间
     */
    private Date createTime;

}
