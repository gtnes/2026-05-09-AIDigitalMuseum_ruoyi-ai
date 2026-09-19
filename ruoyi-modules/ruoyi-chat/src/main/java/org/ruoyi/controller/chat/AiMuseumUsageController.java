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
import org.ruoyi.domain.bo.chat.MuseumUsageLogBo;
import org.ruoyi.domain.bo.chat.MuseumUsageQueryBo;
import org.ruoyi.domain.vo.chat.MuseumUsageLogVo;
import org.ruoyi.domain.vo.chat.MuseumUsageSummaryVo;
import org.ruoyi.service.chat.IAiMuseumUsageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI博物馆用量统计
 * <p>
 * 数据来源：博物馆C端对话（token计费）与语音合成（字符计费）流水
 *
 * @author gtnes
 * @date 2026-09-18
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/museumUsage")
public class AiMuseumUsageController extends BaseController {

    private final IAiMuseumUsageService aiMuseumUsageService;

    /**
     * 按博物馆+业务类型汇总用量与费用（支持博物馆/业务类型/日期范围筛选）
     */
    @SaCheckPermission("system:museumUsage:query")
    @GetMapping("/summary")
    public R<List<MuseumUsageSummaryVo>> summary(MuseumUsageQueryBo bo) {
        return R.ok(aiMuseumUsageService.summary(bo));
    }

    /**
     * 查询博物馆用量流水明细列表（分页，支持博物馆/业务类型/日期范围筛选）
     */
    @SaCheckPermission("system:museumUsage:list")
    @GetMapping("/list")
    public TableDataInfo<MuseumUsageLogVo> list(MuseumUsageLogBo bo, PageQuery pageQuery) {
        return aiMuseumUsageService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出博物馆用量流水明细列表
     */
    @SaCheckPermission("system:museumUsage:export")
    @Log(title = "AI博物馆调用记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MuseumUsageLogBo bo, HttpServletResponse response) {
        List<MuseumUsageLogVo> list = aiMuseumUsageService.queryList(bo);
        ExcelUtil.exportExcel(list, "AI博物馆调用记录", MuseumUsageLogVo.class, response);
    }

}
