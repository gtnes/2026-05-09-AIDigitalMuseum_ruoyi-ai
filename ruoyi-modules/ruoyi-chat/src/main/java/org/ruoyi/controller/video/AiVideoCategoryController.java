package org.ruoyi.controller.video;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
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
import org.ruoyi.domain.bo.video.AiVideoCategoryBo;
import org.ruoyi.domain.vo.video.AiVideoCategoryVo;
import org.ruoyi.service.video.IAiVideoCategoryService;
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
 * AI视频分类管理
 *
 * @author gtnes
 * @date 2026-09-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/video/category")
public class AiVideoCategoryController extends BaseController {

    private final IAiVideoCategoryService aiVideoCategoryService;

    /**
     * 查询AI视频分类列表
     */
    @SaCheckPermission("video:category:list")
    @GetMapping("/list")
    public TableDataInfo<AiVideoCategoryVo> list(AiVideoCategoryBo bo, PageQuery pageQuery) {
        return aiVideoCategoryService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询启用中的分类下拉选项（AI视频表单分类下拉用）
     * 持有分类列表或视频列表任一权限即可访问
     */
    @SaCheckPermission(value = {"video:category:list", "video:video:list"}, mode = SaMode.OR)
    @GetMapping("/options")
    public R<List<AiVideoCategoryVo>> options() {
        return R.ok(aiVideoCategoryService.queryOptions());
    }

    /**
     * 导出AI视频分类列表
     */
    @SaCheckPermission("video:category:export")
    @Log(title = "AI视频分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiVideoCategoryBo bo, HttpServletResponse response) {
        List<AiVideoCategoryVo> list = aiVideoCategoryService.queryList(bo);
        ExcelUtil.exportExcel(list, "AI视频分类", AiVideoCategoryVo.class, response);
    }

    /**
     * 获取AI视频分类详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("video:category:query")
    @GetMapping("/{id}")
    public R<AiVideoCategoryVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(aiVideoCategoryService.queryById(id));
    }

    /**
     * 新增AI视频分类
     */
    @SaCheckPermission("video:category:add")
    @Log(title = "AI视频分类", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AiVideoCategoryBo bo) {
        return toAjax(aiVideoCategoryService.insertByBo(bo));
    }

    /**
     * 修改AI视频分类
     */
    @SaCheckPermission("video:category:edit")
    @Log(title = "AI视频分类", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiVideoCategoryBo bo) {
        return toAjax(aiVideoCategoryService.updateByBo(bo));
    }

    /**
     * 删除AI视频分类
     *
     * @param ids 主键串
     */
    @SaCheckPermission("video:category:remove")
    @Log(title = "AI视频分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(aiVideoCategoryService.deleteWithValidByIds(List.of(ids), true));
    }

}
