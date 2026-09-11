package org.ruoyi.controller.chat;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.ruoyi.common.core.domain.R;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.common.excel.utils.ExcelUtil;
import org.ruoyi.common.idempotent.annotation.RepeatSubmit;
import org.ruoyi.common.log.annotation.Log;
import org.ruoyi.common.log.enums.BusinessType;
import org.ruoyi.common.mybatis.core.page.PageQuery;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.ruoyi.common.web.core.BaseController;
import org.ruoyi.domain.bo.chat.AiMuseumBo;
import org.ruoyi.domain.vo.chat.AiMuseumFrontVo;
import org.ruoyi.domain.vo.chat.AiMuseumVo;
import org.ruoyi.service.chat.IAiMuseumService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AI博物馆实例管理
 *
 * @author gtnes
 * @date 2026-09-04
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/aimuseum")
public class AiMuseumController extends BaseController {

    private final IAiMuseumService aiMuseumService;

    /**
     * 查询AI博物馆列表
     */
    @SaCheckPermission("system:aimuseum:list")
    @GetMapping("/list")
    public TableDataInfo<AiMuseumVo> list(AiMuseumBo bo, PageQuery pageQuery) {
        return aiMuseumService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出AI博物馆列表
     */
    @SaCheckPermission("system:aimuseum:export")
    @Log(title = "AI博物馆", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiMuseumBo bo, HttpServletResponse response) {
        List<AiMuseumVo> list = aiMuseumService.queryList(bo);
        ExcelUtil.exportExcel(list, "AI博物馆", AiMuseumVo.class, response);
    }

    /**
     * 获取AI博物馆详细信息（含智能体配置列表）
     *
     * @param id 主键
     */
    @SaCheckPermission("system:aimuseum:query")
    @GetMapping("/{id}")
    public R<AiMuseumVo> getInfo(@NotNull(message = "主键不能为空")
                                 @PathVariable Long id) {
        return R.ok(aiMuseumService.queryById(id));
    }

    /**
     * 前台获取AI博物馆展示信息（公开接口，无需登录，含服务到期检查）
     *
     * @param id 主键
     */
    @GetMapping("/front/{id}")
    public R<AiMuseumFrontVo> frontInfo(@NotNull(message = "主键不能为空")
                                        @PathVariable Long id) {
        return R.ok(aiMuseumService.frontQueryById(id));
    }

    /**
     * 新增AI博物馆
     */
    @SaCheckPermission("system:aimuseum:add")
    @Log(title = "AI博物馆", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AiMuseumBo bo) {
        return toAjax(aiMuseumService.insertByBo(bo));
    }

    /**
     * 修改AI博物馆
     */
    @SaCheckPermission("system:aimuseum:edit")
    @Log(title = "AI博物馆", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiMuseumBo bo) {
        return toAjax(aiMuseumService.updateByBo(bo));
    }

    /**
     * 删除AI博物馆
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:aimuseum:remove")
    @Log(title = "AI博物馆", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(aiMuseumService.deleteWithValidByIds(List.of(ids), true));
    }
}
