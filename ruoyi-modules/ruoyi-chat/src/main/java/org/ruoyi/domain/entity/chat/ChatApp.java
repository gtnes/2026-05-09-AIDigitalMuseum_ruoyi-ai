package org.ruoyi.domain.entity.chat;

import org.ruoyi.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * 应用管理对象 chat_app
 *
 * @author gtnes
 * @date 2026-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "chat_app", autoResultMap = true)
public class ChatApp extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用类型（agent智能体 workflow工作流）
     */
    private String appType;

    /**
     * 服务商编码（dashscope dify coze）
     */
    private String providerCode;

    /**
     * 请求地址
     */
    private String apiHost;

    /**
     * 密钥
     */
    private String apiKey;

    /**
     * 应用描述
     */
    private String appDescribe;

    /**
     * 应用图标
     */
    private String appShow;

    /**
     * 欢迎语
     */
    private String welcomeMsg;

    /**
     * 预设问题列表（JSON数组存储）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> presetQuestions;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;


}
