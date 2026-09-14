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
import org.ruoyi.domain.bo.voice.VoiceProfileBo;
import org.ruoyi.domain.vo.voice.VoiceProfileVo;
import org.ruoyi.service.voice.IVoiceProfileService;
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
 * AI语音音色档案管理
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/voice/profile")
public class VoiceProfileController extends BaseController {

    private final IVoiceProfileService voiceProfileService;

    /**
     * 查询AI语音音色档案列表
     */
    @SaCheckPermission("voice:profile:list")
    @GetMapping("/list")
    public TableDataInfo<VoiceProfileVo> list(VoiceProfileBo bo, PageQuery pageQuery) {
        return voiceProfileService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出AI语音音色档案列表
     */
    @SaCheckPermission("voice:profile:export")
    @Log(title = "AI语音音色档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(VoiceProfileBo bo, HttpServletResponse response) {
        List<VoiceProfileVo> list = voiceProfileService.queryList(bo);
        ExcelUtil.exportExcel(list, "AI语音音色档案", VoiceProfileVo.class, response);
    }

    /**
     * 获取AI语音音色档案详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("voice:profile:query")
    @GetMapping("/{id}")
    public R<VoiceProfileVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long id) {
        return R.ok(voiceProfileService.queryById(id));
    }

    /**
     * 新增AI语音音色档案
     */
    @SaCheckPermission("voice:profile:add")
    @Log(title = "AI语音音色档案", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody VoiceProfileBo bo) {
        return toAjax(voiceProfileService.insertByBo(bo));
    }

    /**
     * 修改AI语音音色档案
     */
    @SaCheckPermission("voice:profile:edit")
    @Log(title = "AI语音音色档案", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody VoiceProfileBo bo) {
        return toAjax(voiceProfileService.updateByBo(bo));
    }

    /**
     * 删除AI语音音色档案
     *
     * @param ids 主键串
     */
    @SaCheckPermission("voice:profile:remove")
    @Log(title = "AI语音音色档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(voiceProfileService.deleteWithValidByIds(List.of(ids), true));
    }

}
