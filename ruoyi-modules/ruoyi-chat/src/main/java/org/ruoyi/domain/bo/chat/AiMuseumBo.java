package org.ruoyi.domain.bo.chat;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.common.mybatis.core.domain.BaseEntity;
import org.ruoyi.domain.entity.chat.AiMuseum;
import org.ruoyi.domain.entity.chat.AiMuseumApp;

import java.util.List;
import java.util.Map;

/**
 * AI博物馆实例业务对象 ai_museum
 *
 * @author gtnes
 * @date 2026-09-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiMuseum.class, reverseConvertGenerate = false)
public class AiMuseumBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 实例状态（1正常 0停用）
     */
    @NotNull(message = "实例状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer status;

    /**
     * 原始机构名称
     */
    private String orgTitle;

    /**
     * 展示标题
     */
    @NotBlank(message = "展示标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * Logo地址（ossId）
     */
    private String logoUrl;

    /**
     * banner背景图地址（ossId）
     */
    private String bannerUrl;

    /**
     * 服务开始时间戳（秒）
     */
    private Long startTime;

    /**
     * 服务到期时间戳（秒）
     */
    @NotNull(message = "服务到期时间不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long endTime;

    /**
     * 过期提示文案
     */
    private String expireTips;

    /**
     * 自定义名称
     */
    private String customName;

    /**
     * 是否开启VR（0关闭 1开启）
     */
    private Integer vrEnable;

    /**
     * VR地址
     */
    private String vrUrl;

    /**
     * 权限操作列表（保留字段，暂未启用）
     */
    private List<Map<String, Object>> operationList;

    /**
     * 智能体配置列表
     */
    @Valid
    private List<AiMuseumApp> chatapps;

    /**
     * 备注
     */
    private String remark;

}
