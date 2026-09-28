package org.ruoyi.domain.entity.chat;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI博物馆域名配置值对象 ai_museum.domains（JSON字段元素）
 *
 * @author gtnes
 * @date 2026-09-28
 */
@Data
public class AiMuseumDomain implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 域名（如 localhost:5173 或 example.com）
     */
    private String domain;

    /**
     * 环境类型（dev本地开发 prod生产环境）
     */
    private String envType;

}
