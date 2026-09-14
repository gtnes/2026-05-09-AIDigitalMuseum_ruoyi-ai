package org.ruoyi.controller.voice;

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
import org.ruoyi.domain.bo.voice.PlatformVoiceBo;
import org.ruoyi.domain.vo.voice.PlatformVoiceVo;
import org.ruoyi.service.voice.IPlatformVoiceService;
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
 * 平台音色字典管理
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/voice/platform")
public class PlatformVoiceController extends BaseController {

    private final IPlatformVoiceService platformVoiceService;

    /**
     * 查询平台音色列表（platform 参数过滤某平台下可选音色）
     */
    @SaCheckPermission("voice:platform:list")
    @GetMapping("/list")
    public TableDataInfo<PlatformVoiceVo> list(PlatformVoiceBo bo, PageQuery pageQuery) {
        return platformVoiceService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询平台音色下拉选项（不分页）
     */
    @SaCheckPermission("voice:platform:list")
    @GetMapping("/options")
    public R<List<PlatformVoiceVo>> options(PlatformVoiceBo bo) {
        return R.ok(platformVoiceService.queryList(bo));
    }

    /**
     * 导出平台音色列表
     */
    @SaCheckPermission("voice:platform:export")
    @Log(title = "平台音色字典", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(PlatformVoiceBo bo, HttpServletResponse response) {
        List<PlatformVoiceVo> list = platformVoiceService.queryList(bo);
        ExcelUtil.exportExcel(list, "平台音色字典", PlatformVoiceVo.class, response);
    }

    /**
     * 获取平台音色详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("voice:platform:query")
    @GetMapping("/{id}")
    public R<PlatformVoiceVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(platformVoiceService.queryById(id));
    }

    /**
     * 新增平台音色（可用于自定义克隆音色编码等）
     */
    @SaCheckPermission("voice:platform:add")
    @Log(title = "平台音色字典", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody PlatformVoiceBo bo) {
        return toAjax(platformVoiceService.insertByBo(bo));
    }

    /**
     * 修改平台音色
     */
    @SaCheckPermission("voice:platform:edit")
    @Log(title = "平台音色字典", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody PlatformVoiceBo bo) {
        return toAjax(platformVoiceService.updateByBo(bo));
    }

    /**
     * 删除平台音色
     *
     * @param ids 主键串
     */
    @SaCheckPermission("voice:platform:remove")
    @Log(title = "平台音色字典", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(platformVoiceService.deleteWithValidByIds(List.of(ids), true));
    }

}
