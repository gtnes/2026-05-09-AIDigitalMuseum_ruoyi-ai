package org.ruoyi.controller.chat;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.ruoyi.common.idempotent.annotation.RepeatSubmit;
import org.ruoyi.common.log.annotation.Log;
import org.ruoyi.common.web.core.BaseController;
import org.ruoyi.common.mybatis.core.page.PageQuery;
import org.ruoyi.common.core.domain.R;
import org.ruoyi.common.core.validate.AddGroup;
import org.ruoyi.common.core.validate.EditGroup;
import org.ruoyi.common.log.enums.BusinessType;
import org.ruoyi.common.excel.utils.ExcelUtil;
import org.ruoyi.common.sse.core.SseEmitterManager;
import org.ruoyi.domain.vo.chat.ChatAppVo;
import org.ruoyi.domain.bo.chat.ChatAppBo;
import org.ruoyi.service.chat.IChatAppService;
import org.ruoyi.service.chat.impl.app.AppCallService;
import org.ruoyi.common.mybatis.core.page.TableDataInfo;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 应用管理
 *
 * @author gtnes
 * @date 2026-08-19
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/chatapp")
public class ChatAppController extends BaseController {

    private final IChatAppService chatAppService;
    private final AppCallService appCallService;
    private final SseEmitterManager sseEmitterManager;

    /**
     * 查询应用管理列表
     */
    @SaCheckPermission("system:chatapp:list")
    @GetMapping("/list")
    public TableDataInfo<ChatAppVo> list(ChatAppBo bo, PageQuery pageQuery) {
        return chatAppService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询启用的应用列表（下拉选择器用）
     */
    @GetMapping("/appList")
    public R<List<ChatAppVo>> appList() {
        return R.ok(chatAppService.queryEnableList());
    }

    /**
     * 导出应用管理列表
     */
    @SaCheckPermission("system:chatapp:export")
    @Log(title = "应用管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ChatAppBo bo, HttpServletResponse response) {
        List<ChatAppVo> list = chatAppService.queryList(bo);
        ExcelUtil.exportExcel(list, "应用管理", ChatAppVo.class, response);
    }

    /**
     * 获取应用管理详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:chatapp:query")
    @GetMapping("/{id}")
    public R<ChatAppVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(chatAppService.queryById(id));
    }

    /**
     * 新增应用管理
     */
    @SaCheckPermission("system:chatapp:add")
    @Log(title = "应用管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ChatAppBo bo) {
        return toAjax(chatAppService.insertByBo(bo));
    }

    /**
     * 修改应用管理
     */
    @SaCheckPermission("system:chatapp:edit")
    @Log(title = "应用管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ChatAppBo bo) {
        return toAjax(chatAppService.updateByBo(bo));
    }

    /**
     * 删除应用管理
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:chatapp:remove")
    @Log(title = "应用管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(chatAppService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * SSE连接
     */
    @GetMapping("/connect/{sessionId}")
    public SseEmitter connect(@PathVariable String sessionId) {
        return sseEmitterManager.connect(sessionId);
    }

    /**
     * 应用对话发送
     */
    @PostMapping("/chat/send")
    public R<Void> chatSend(@RequestBody AppCallService.AppCallRequest request) {
        appCallService.streamCall(request);
        return R.ok();
    }
}
