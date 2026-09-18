package org.ruoyi.domain.bo.chat;

import org.ruoyi.domain.entity.chat.ChatApp;
import org.ruoyi.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;

import java.math.BigDecimal;
import java.util.List;

/**
 * 应用管理业务对象 chat_app
 *
 * @author gtnes
 * @date 2026-08-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ChatApp.class, reverseConvertGenerate = false)
public class ChatAppBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 应用名称
     */
    @NotBlank(message = "应用名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String appName;

    /**
     * 应用类型（agent智能体 workflow工作流）
     */
    private String appType;

    /**
     * 服务商编码（dashscope dify coze）
     */
    @NotBlank(message = "服务商编码（dashscope dify coze）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String providerCode;

    /**
     * 请求地址
     */
    @NotBlank(message = "请求地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String apiHost;

    /**
     * 密钥
     */
    @NotBlank(message = "密钥不能为空", groups = { AddGroup.class, EditGroup.class })
    private String apiKey;

    /**
     * 输入token单价（元/千token，博物馆对话用量计费依据，空=不计费）
     */
    private BigDecimal priceInPer1k;

    /**
     * 输出token单价（元/千token，博物馆对话用量计费依据，空=不计费）
     */
    private BigDecimal priceOutPer1k;

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
     * 预设问题列表
     */
    private List<String> presetQuestions;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;


}
