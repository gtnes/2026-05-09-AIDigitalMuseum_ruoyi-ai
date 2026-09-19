package org.ruoyi.domain.vo.chat;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 通用AI用量汇总视图对象（按类别+应用+业务类型聚合）
 *
 * @author gtnes
 * @date 2026-09-18
 */
@Data
public class AiUsageSummaryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类别（system系统内部/测试调用）
     */
    private String category;

    /**
     * 业务类型（chat对话 tts语音合成）
     */
    private String bizType;

    /**
     * 应用名称快照（chat=智能体名称，tts=音色名称）
     */
    private String appName;

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
