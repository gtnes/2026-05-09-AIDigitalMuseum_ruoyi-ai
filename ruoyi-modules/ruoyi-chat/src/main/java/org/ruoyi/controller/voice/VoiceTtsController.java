package org.ruoyi.controller.voice;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.CollUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ruoyi.common.core.domain.R;
import org.ruoyi.common.core.domain.model.LoginUser;
import org.ruoyi.common.satoken.utils.LoginHelper;
import org.ruoyi.domain.bo.voice.MuseumVoiceTtsBo;
import org.ruoyi.domain.bo.voice.VoiceProfileBo;
import org.ruoyi.domain.bo.voice.VoiceTtsBo;
import org.ruoyi.domain.bo.voice.VoiceTtsPreviewBo;
import org.ruoyi.domain.vo.chat.AiMuseumVo;
import org.ruoyi.domain.vo.voice.VoiceProfileVo;
import org.ruoyi.domain.vo.voice.VoiceTtsVo;
import org.ruoyi.service.chat.IAiMuseumService;
import org.ruoyi.service.chat.IAiMuseumUsageService;
import org.ruoyi.service.chat.IAiUsageService;
import org.ruoyi.service.voice.IVoiceProfileService;
import org.ruoyi.service.voice.IVoiceTtsService;
import org.ruoyi.service.voice.security.TtsRequestGuard;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * 语音合成接口（智能体输出文字转语音）
 * <p>
 * 合成/音色列表接口登录即可调用（C端聊天播报使用），试听接口需音色管理权限。
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/voice/tts")
public class VoiceTtsController {

    private final IVoiceTtsService voiceTtsService;
    private final IVoiceProfileService voiceProfileService;
    private final TtsRequestGuard ttsRequestGuard;
    private final IAiMuseumService aiMuseumService;
    private final IAiMuseumUsageService aiMuseumUsageService;
    private final IAiUsageService aiUsageService;

    /**
     * 按音色档案合成语音
     * C端聊天页播放按钮/自动播报调用：{ voiceId, text, timestamp, nonce, sign } -> dataUrl(mp3)
     * 需登录，先过防滥用守卫（签名防直刷 + nonce防重放 + IP频率/日配额）；用量记入通用流水（系统类别）
     */
    @PostMapping
    public R<VoiceTtsVo> synthesize(@Valid @RequestBody VoiceTtsBo bo) {
        ttsRequestGuard.check(bo);
        VoiceTtsVo vo = voiceTtsService.synthesize(bo);
        // 通用接口记账：记入通用用量流水，失败不影响合成结果
        VoiceProfileVo profile = voiceProfileService.queryById(bo.getVoiceId());
        LoginUser loginUser = LoginHelper.getLoginUser();
        aiUsageService.recordTts(
            loginUser == null ? null : loginUser.getUserId(),
            loginUser == null ? null : loginUser.getNickname(),
            bo.getVoiceId(),
            profile == null ? null : profile.getVoiceName(),
            vo.getTextLength());
        return R.ok(vo);
    }

    /**
     * 博物馆C端按音色档案合成语音（公开接口，需museumId）
     * 校验：博物馆存在且启用、服务未到期、音色已在该博物馆智能体中配置，再过防滥用守卫
     * C端博物馆聊天页调用：{ museumId, voiceId, text, timestamp, nonce, sign } -> dataUrl(mp3)
     */
    @PostMapping("/museum")
    public R<VoiceTtsVo> museumSynthesize(@Valid @RequestBody MuseumVoiceTtsBo bo) {
        AiMuseumVo museum = aiMuseumService.checkServiceValid(bo.getMuseumId());
        boolean bound = CollUtil.isNotEmpty(museum.getChatapps())
            && museum.getChatapps().stream()
                .anyMatch(app -> Objects.equals(app.getVoiceProfileId(), bo.getVoiceId()));
        if (!bound) {
            return R.fail("该音色未在此博物馆开通");
        }
        ttsRequestGuard.check(bo);
        VoiceTtsVo vo = voiceTtsService.synthesize(bo);
        // 按音色单价（元/万字符）记账，失败不影响合成结果
        aiMuseumUsageService.recordTts(bo.getMuseumId(), bo.getVoiceId(), vo.getTextLength());
        return R.ok(vo);
    }

    /**
     * 试听合成（管理端编辑弹窗内即时试听，无需先保存音色档案）
     */
    @SaCheckPermission("voice:profile:list")
    @PostMapping("/preview")
    public R<VoiceTtsVo> preview(@Valid @RequestBody VoiceTtsPreviewBo bo) {
        return R.ok(voiceTtsService.preview(bo));
    }

    /**
     * 已支持的平台下拉选项 [{label, value}]
     */
    @GetMapping("/platformOptions")
    public R<List<LinkedHashMap<String, String>>> platformOptions() {
        return R.ok(voiceTtsService.platformOptions());
    }

    /**
     * 启用中的音色档案列表（管理端下拉选项 / C端测试页音色选择，需登录+音色列表权限）
     */
    @SaCheckPermission("voice:profile:list")
    @GetMapping("/voices")
    public R<List<VoiceProfileVo>> voices() {
        VoiceProfileBo bo = new VoiceProfileBo();
        bo.setStatus("0");
        return R.ok(voiceProfileService.queryList(bo));
    }

}
