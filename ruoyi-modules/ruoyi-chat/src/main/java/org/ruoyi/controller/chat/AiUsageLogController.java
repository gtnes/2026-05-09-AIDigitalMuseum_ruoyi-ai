package org.ruoyi.controller.chat;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.ruoyi.common.core.domain.R;
import org.ruoyi.common.excel.utils.ExcelUtil;
import org.ruoyi.common.log.annotation.Log;
import org.ruoyi.common.log.enums.BusinessType;
import org.ruoyi.common.mybatis.core.page.PageQuery;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.ruoyi.common.web.core.BaseController;
import org.ruoyi.domain.bo.chat.AiUsageLogBo;
import org.ruoyi.domain.vo.chat.AiUsageLogVo;
import org.ruoyi.domain.vo.chat.AiUsageSummaryVo;
import org.ruoyi.service.chat.IAiUsageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 通用AI用量记录
 * <p>
 * 数据来源：登录通用接口（chat/send、voice/tts，apps-chat测试页等系统内部调用）的对话/语音合成流水
 *
 * @author gtnes
 * @date 2026-09-18
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/usage")
public class AiUsageLogController extends BaseController {

    private final IAiUsageService aiUsageService;

    /**
     * 查询通用AI用量流水列表
     */
    @SaCheckPermission("system:usage:list")
    @GetMapping("/list")
    public TableDataInfo<AiUsageLogVo> list(AiUsageLogBo bo, PageQuery pageQuery) {
        return aiUsageService.queryPageList(bo, pageQuery);
    }

    /**
     * 按类别+应用+业务类型汇总用量与费用（支持类别/业务类型/应用名称/日期范围筛选）
     */
    @SaCheckPermission("system:usage:query")
    @GetMapping("/summary")
    public R<List<AiUsageSummaryVo>> summary(AiUsageLogBo bo) {
        return R.ok(aiUsageService.summary(bo));
    }

    /**
     * 导出通用AI用量流水列表
     */
    @SaCheckPermission("system:usage:export")
    @Log(title = "通用AI用量记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiUsageLogBo bo, HttpServletResponse response) {
        List<AiUsageLogVo> list = aiUsageService.queryList(bo);
        ExcelUtil.exportExcel(list, "通用AI用量记录", AiUsageLogVo.class, response);
    }

}
