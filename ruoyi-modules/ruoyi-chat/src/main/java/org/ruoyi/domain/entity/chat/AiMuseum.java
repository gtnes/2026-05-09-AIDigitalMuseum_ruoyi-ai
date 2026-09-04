package org.ruoyi.domain.entity.chat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.ruoyi.common.tenant.core.TenantEntity;

import java.io.Serial;
import java.util.List;
import java.util.Map;

/**
 * AI博物馆实例对象 ai_museum
 *
 * @author gtnes
 * @date 2026-09-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "ai_museum", autoResultMap = true)
public class AiMuseum extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 实例状态（1正常 0停用）
     */
    private Integer status;

    /**
     * 原始机构名称
     */
    private String orgTitle;

    /**
     * 展示标题
     */
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
     * 智能体配置列表（JSON存储，元素id为chat_app.id，appName不落库）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<AiMuseumApp> chatapps;

    /**
     * 权限操作列表（保留字段，暂未启用）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Map<String, Object>> operationList;

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
