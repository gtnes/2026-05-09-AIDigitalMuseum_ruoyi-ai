package org.ruoyi.domain.vo.chat;

import org.ruoyi.domain.entity.chat.ChatApp;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.ruoyi.common.excel.annotation.ExcelDictFormat;
import org.ruoyi.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;



/**
 * 应用管理视图对象 chat_app
 *
 * @author gtnes
 * @date 2026-08-19
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ChatApp.class)
public class ChatAppVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 应用名称
     */
    @ExcelProperty(value = "应用名称")
    private String appName;

    /**
     * 应用类型（agent智能体 workflow工作流）
     */
    @ExcelProperty(value = "应用类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "app_type")
    private String appType;

    /**
     * 服务商编码（dashscope dify coze）
     */
    @ExcelProperty(value = "服务商编码", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "app_provider")
    private String providerCode;

    /**
     * 请求地址
     */
    @ExcelProperty(value = "请求地址")
    private String apiHost;

    /**
     * 密钥
     */
    @ExcelProperty(value = "密钥")
    private String apiKey;

    /**
     * 输入token单价（元/千token，博物馆对话用量计费依据，空=不计费）
     */
    @ExcelProperty(value = "输入token单价(元/千token)")
    private BigDecimal priceInPer1k;

    /**
     * 输出token单价（元/千token，博物馆对话用量计费依据，空=不计费）
     */
    @ExcelProperty(value = "输出token单价(元/千token)")
    private BigDecimal priceOutPer1k;

    /**
     * 应用描述
     */
    @ExcelProperty(value = "应用描述")
    private String appDescribe;

    /**
     * 应用图标
     */
    @ExcelProperty(value = "应用图标")
    private String appShow;

    /**
     * 欢迎语
     */
    @ExcelProperty(value = "欢迎语")
    private String welcomeMsg;

    /**
     * 预设问题列表
     */
    private List<String> presetQuestions;

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
