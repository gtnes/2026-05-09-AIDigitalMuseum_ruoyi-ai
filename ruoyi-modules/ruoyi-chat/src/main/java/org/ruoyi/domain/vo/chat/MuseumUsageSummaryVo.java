package org.ruoyi.domain.vo.chat;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * AI博物馆用量汇总视图对象（按博物馆+业务类型聚合）
 *
 * @author gtnes
 * @date 2026-09-18
 */
@Data
public class MuseumUsageSummaryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 博物馆ID
     */
    private Long museumId;

    /**
     * 博物馆标题
     */
    private String museumTitle;

    /**
     * 业务类型（chat对话 tts语音合成）
     */
    private String bizType;

    /**
     * 调用次数
     */
    private Long calls;

    /**
     * 字符总量
     */
    private Long chars;

    /**
     * 输入token总量
     */
    private Long tokensIn;

    /**
     * 输出token总量
     */
    private Long tokensOut;

    /**
     * 费用合计（元）
     */
    private BigDecimal cost;

}
