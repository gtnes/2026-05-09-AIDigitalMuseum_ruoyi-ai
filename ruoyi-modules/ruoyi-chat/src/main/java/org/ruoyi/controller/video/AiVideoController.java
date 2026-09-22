package org.ruoyi.controller.video;

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
import org.ruoyi.domain.bo.video.AiVideoBo;
import org.ruoyi.domain.vo.video.AiVideoVo;
import org.ruoyi.service.video.IAiVideoService;
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
 * AI视频管理
 *
 * @author gtnes
 * @date 2026-09-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/video/video")
public class AiVideoController extends BaseController {

    private final IAiVideoService aiVideoService;

    /**
     * 查询AI视频列表
     */
    @SaCheckPermission("video:video:list")
    @GetMapping("/list")
    public TableDataInfo<AiVideoVo> list(AiVideoBo bo, PageQuery pageQuery) {
        return aiVideoService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出AI视频列表
     */
    @SaCheckPermission("video:video:export")
    @Log(title = "AI视频", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiVideoBo bo, HttpServletResponse response) {
        List<AiVideoVo> list = aiVideoService.queryList(bo);
        ExcelUtil.exportExcel(list, "AI视频", AiVideoVo.class, response);
    }

    /**
     * 获取AI视频详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("video:video:query")
    @GetMapping("/{id}")
    public R<AiVideoVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(aiVideoService.queryById(id));
    }

    /**
     * 新增AI视频
     */
    @SaCheckPermission("video:video:add")
    @Log(title = "AI视频", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AiVideoBo bo) {
        return toAjax(aiVideoService.insertByBo(bo));
    }

    /**
     * 修改AI视频
     */
    @SaCheckPermission("video:video:edit")
    @Log(title = "AI视频", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiVideoBo bo) {
        return toAjax(aiVideoService.updateByBo(bo));
    }

    /**
     * 删除AI视频
     *
     * @param ids 主键串
     */
    @SaCheckPermission("video:video:remove")
    @Log(title = "AI视频", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(aiVideoService.deleteWithValidByIds(List.of(ids), true));
    }

}
