package org.ruoyi.controller.voice;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ruoyi.common.core.domain.R;
import org.ruoyi.domain.bo.voice.VoiceProfileBo;
import org.ruoyi.domain.bo.voice.VoiceTtsBo;
import org.ruoyi.domain.bo.voice.VoiceTtsPreviewBo;
import org.ruoyi.domain.vo.voice.VoiceProfileVo;
import org.ruoyi.domain.vo.voice.VoiceTtsVo;
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

    /**
     * 按音色档案合成语音
     * C端聊天页播放按钮/自动播报调用：{ voiceId, text, timestamp, nonce, sign } -> dataUrl(mp3)
     * 公开接口，先过防滥用守卫（签名防直刷 + nonce防重放 + IP频率/日配额）
     */
    @PostMapping
    public R<VoiceTtsVo> synthesize(@Valid @RequestBody VoiceTtsBo bo) {
        ttsRequestGuard.check(bo);
        return R.ok(voiceTtsService.synthesize(bo));
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
     * 启用中的音色档案列表（管理端下拉选项，需登录）
     */
    @SaCheckPermission("voice:profile:list")
    @GetMapping("/voices")
    public R<List<VoiceProfileVo>> voices() {
        VoiceProfileBo bo = new VoiceProfileBo();
        bo.setStatus("0");
        return R.ok(voiceProfileService.queryList(bo));
    }

}
