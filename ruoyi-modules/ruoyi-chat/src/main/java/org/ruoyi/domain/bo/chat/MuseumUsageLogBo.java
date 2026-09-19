package org.ruoyi.domain.bo.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ruoyi.common.mybatis.core.domain.BaseEntity;

/**
 * AI博物馆用量流水查询对象 ai_museum_usage_log
 *
 * @author gtnes
 * @date 2026-09-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MuseumUsageLogBo extends BaseEntity {

    /**
     * 博物馆ID（ai_museum.id）
     */
    private Long museumId;

    /**
     * 业务类型（chat对话 tts语音合成）
     */
    private String bizType;

}
