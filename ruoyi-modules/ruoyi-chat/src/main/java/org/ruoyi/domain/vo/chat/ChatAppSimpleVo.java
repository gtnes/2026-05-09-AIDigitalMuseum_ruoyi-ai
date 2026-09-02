package org.ruoyi.domain.vo.chat;

import org.ruoyi.domain.entity.chat.ChatApp;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 应用管理精简视图对象（前端下拉选择器用，不包含敏感字段）
 *
 * @author gtnes
 * @date 2026-08-19
 */
@Data
@AutoMapper(target = ChatApp.class)
public class ChatAppSimpleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String appName;

    private String appType;

    private String providerCode;

    private String appDescribe;

    private String appShow;
}
