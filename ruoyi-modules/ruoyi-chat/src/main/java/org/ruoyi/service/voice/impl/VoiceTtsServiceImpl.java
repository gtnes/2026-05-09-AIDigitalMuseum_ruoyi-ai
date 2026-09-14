package org.ruoyi.service.voice.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.common.chat.domain.vo.chat.ChatModelVo;
import org.ruoyi.common.chat.service.chat.IChatModelService;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.domain.bo.voice.VoiceTtsBo;
import org.ruoyi.domain.bo.voice.VoiceTtsPreviewBo;
import org.ruoyi.domain.vo.voice.PlatformVoiceVo;
import org.ruoyi.domain.vo.voice.VoiceProfileVo;
import org.ruoyi.domain.vo.voice.VoiceTtsVo;
import org.ruoyi.enums.ModelType;
import org.ruoyi.mapper.voice.PlatformVoiceMapper;
import org.ruoyi.mapper.voice.VoiceProfileMapper;
import org.ruoyi.service.voice.IVoiceTtsService;
import org.ruoyi.service.voice.tts.TtsProvider;
import org.ruoyi.service.voice.tts.TtsProviderRouter;
import org.ruoyi.service.voice.tts.TtsSynthesizeRequest;
import org.ruoyi.service.voice.tts.TtsSynthesizeResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 语音合成Service业务层处理
 * <p>
 * 链路：音色档案(voice_profile) -> 平台音色(voice_platform取voice_code)
 * -> 关联模型(chat_model取apiHost/apiKey) -> 按平台路由 Provider 合成
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VoiceTtsServiceImpl implements IVoiceTtsService {

    private final VoiceProfileMapper voiceProfileMapper;
    private final PlatformVoiceMapper platformVoiceMapper;
    private final IChatModelService chatModelService;
    private final TtsProviderRouter ttsProviderRouter;

    @Override
    public VoiceTtsVo synthesize(VoiceTtsBo bo) {
        VoiceProfileVo profile = loadEnabledProfile(bo.getVoiceId());
        ChatModelVo model = loadAudioModel(profile.getModelId());
        PlatformVoiceVo platformVoice = platformVoiceMapper.selectVoById(profile.getPlatformVoiceId());
        if (platformVoice == null) {
            throw new IllegalArgumentException("音色档案关联的平台音色不存在，请重新编辑音色");
        }
        return doSynthesize(profile.getPlatform(), platformVoice.getVoiceCode(), model,
            bo.getText(), profile.getSpeed(), profile.getPitch(), profile.getVolume());
    }

    @Override
    public VoiceTtsVo preview(VoiceTtsPreviewBo bo) {
        PlatformVoiceVo platformVoice = platformVoiceMapper.selectVoById(bo.getPlatformVoiceId());
        if (platformVoice == null) {
            throw new IllegalArgumentException("所选平台音色不存在");
        }
        ChatModelVo model = loadAudioModel(bo.getModelId());
        return doSynthesize(platformVoice.getPlatform(), platformVoice.getVoiceCode(), model,
            bo.getText(), bo.getSpeed(), bo.getPitch(), bo.getVolume());
    }

    @Override
    public List<LinkedHashMap<String, String>> platformOptions() {
        return ttsProviderRouter.supportedPlatforms().stream()
            .map(platform -> {
                LinkedHashMap<String, String> item = new LinkedHashMap<>();
                item.put("label", ttsProviderRouter.platformName(platform));
                item.put("value", platform);
                return item;
            })
            .toList();
    }

    /**
     * 加载并校验音色档案
     */
    private VoiceProfileVo loadEnabledProfile(Long voiceId) {
        VoiceProfileVo profile = voiceProfileMapper.selectVoById(voiceId);
        if (profile == null) {
            throw new IllegalArgumentException("音色档案不存在: " + voiceId);
        }
        if (!"0".equals(profile.getStatus())) {
            throw new IllegalArgumentException("音色已停用: " + profile.getVoiceName());
        }
        return profile;
    }

    /**
     * 加载并校验语音生成模型凭证
     */
    private ChatModelVo loadAudioModel(Long modelId) {
        if (modelId == null) {
            throw new IllegalArgumentException("未关联语音生成模型，请先编辑音色关联模型");
        }
        ChatModelVo model = chatModelService.queryById(modelId);
        if (model == null) {
            throw new IllegalArgumentException("关联的语音生成模型不存在: " + modelId);
        }
        if (!ModelType.AUDIO.getKey().equals(model.getCategory())) {
            throw new IllegalArgumentException("关联模型不是语音生成分类，请在模型管理中调整: " + model.getModelName());
        }
        if (StringUtils.isBlank(model.getApiKey())) {
            throw new IllegalArgumentException("关联模型未配置密钥，请在模型管理中补充: " + model.getModelName());
        }
        return model;
    }

    /**
     * 执行合成：路由平台 Provider 并组装结果
     */
    private VoiceTtsVo doSynthesize(String platform, String voiceCode, ChatModelVo model,
                                    String text, BigDecimal speed, BigDecimal pitch, BigDecimal volume) {
        TtsProvider provider = ttsProviderRouter.getProvider(platform);
        TtsSynthesizeResult result = provider.synthesize(TtsSynthesizeRequest.builder()
            .text(text)
            .voice(voiceCode)
            .model(model.getModelName())
            .format("mp3")
            .speed(speed)
            .pitch(pitch)
            .volume(volume)
            .apiHost(model.getApiHost())
            .apiKey(model.getApiKey())
            .build());
        String b64 = Base64.getEncoder().encodeToString(result.getBytes());
        String dataUrl = "data:" + result.getMimeType() + ";base64," + b64;
        return VoiceTtsVo.builder()
            .format(result.getFormat())
            .mimeType(result.getMimeType())
            .b64Json(b64)
            .dataUrl(dataUrl)
            .textLength(text == null ? 0 : text.length())
            .build();
    }

}
