package org.ruoyi.domain.bo.chat;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * AI博物馆用量汇总查询条件
 *
 * @author gtnes
 * @date 2026-09-18
 */
@Data
public class MuseumUsageQueryBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 博物馆ID（空=全部）
     */
    private Long museumId;

    /**
     * 业务类型（chat对话 tts语音合成，空=全部）
     */
    private String bizType;

    /**
     * 开始日期（含当天，空=不限）
     */
    private LocalDate beginTime;

    /**
     * 结束日期（含当天，空=不限）
     */
    private LocalDate endTime;

}
