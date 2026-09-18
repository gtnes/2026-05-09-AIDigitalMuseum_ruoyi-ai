package org.ruoyi.controller.chat;

import java.util.List;
import java.util.Objects;

import cn.hutool.core.collection.CollUtil;
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
import org.ruoyi.domain.bo.chat.MuseumChatSendBo;
import org.ruoyi.domain.vo.chat.AiMuseumVo;
import org.ruoyi.domain.vo.chat.ChatAppSimpleVo;
import org.ruoyi.domain.vo.chat.ChatAppVo;
import org.ruoyi.domain.bo.chat.ChatAppBo;
import org.ruoyi.service.chat.IAiMuseumService;
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
    private final IAiMuseumService aiMuseumService;
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
     * 查询启用的应用列表（下拉选择器用，不包含敏感字段）
     */
    @GetMapping("/appList")
    public R<List<ChatAppSimpleVo>> appList() {
        return R.ok(chatAppService.queryEnableList());
    }

    /**
     * 按ID查询启用的应用信息（公开接口，仅返回该应用精简信息，不包含敏感字段）
     *
     * @param id 主键
     */
    @GetMapping("/appInfo/{id}")
    public R<ChatAppSimpleVo> appInfo(@NotNull(message = "主键不能为空")
                                      @PathVariable Long id) {
        ChatAppSimpleVo vo = chatAppService.queryEnableById(id);
        if (vo == null) {
            return R.fail("应用不存在或未启用");
        }
        return R.ok(vo);
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

    /**
     * 博物馆C端应用对话发送（公开接口，需museumId）
     * 校验：博物馆存在且启用、服务未到期、智能体已在该博物馆开通
     */
    @PostMapping("/chat/museumSend")
    public R<Void> museumChatSend(@Validated @RequestBody MuseumChatSendBo bo) {
        AiMuseumVo museum = aiMuseumService.checkServiceValid(bo.getMuseumId());
        boolean bound = CollUtil.isNotEmpty(museum.getChatapps())
            && museum.getChatapps().stream()
                .anyMatch(app -> Objects.equals(app.getId(), bo.getAppId()));
        if (!bound) {
            return R.fail("该智能体未在此博物馆开通");
        }
        AppCallService.AppCallRequest request = new AppCallService.AppCallRequest();
        request.setAppId(bo.getAppId());
        request.setContent(bo.getContent());
        request.setSessionId(bo.getSessionId());
        appCallService.streamCall(request);
        return R.ok();
    }
}
