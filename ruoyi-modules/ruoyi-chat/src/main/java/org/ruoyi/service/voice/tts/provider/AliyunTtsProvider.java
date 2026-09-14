package org.ruoyi.service.voice.tts.provider;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.ruoyi.service.voice.tts.TtsProvider;
import org.ruoyi.service.voice.tts.TtsSynthesizeRequest;
import org.ruoyi.service.voice.tts.TtsSynthesizeResult;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 阿里云百炼 CosyVoice 语音合成实现（非流式 HTTP 接口）
 * <p>
 * 接口: POST {apiHost}/api/v1/services/audio/tts/SpeechSynthesizer
 * 鉴权: Authorization: Bearer {apiKey}
 * 请求: model + input{text, voice, format, rate, pitch, volume}
 * 响应: JSON 中 output.audio.url 为音频文件地址（24小时有效），需二次下载音频二进制
 *
 * @author ruoyi
 * @date 2026-09-14
 */
@Slf4j
@Component
public class AliyunTtsProvider implements TtsProvider {

    private static final String PLATFORM = "aliyun";
    private static final String PLATFORM_NAME = "阿里云";
    private static final String DEFAULT_API_HOST = "https://dashscope.aliyuncs.com";
    private static final String TTS_PATH = "/api/v1/services/audio/tts/SpeechSynthesizer";
    private static final String DEFAULT_MODEL = "cosyvoice-v3-flash";
    private static final String DEFAULT_FORMAT = "mp3";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final okhttp3.MediaType JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build();

    @Override
    public String platform() {
        return PLATFORM;
    }

    @Override
    public String platformName() {
        return PLATFORM_NAME;
    }

    @Override
    public TtsSynthesizeResult synthesize(TtsSynthesizeRequest request) {
        String format = StrUtil.blankToDefault(request.getFormat(), DEFAULT_FORMAT);
        ObjectNode payload = buildPayload(request, format);
        String apiHost = StrUtil.blankToDefault(request.getApiHost(), DEFAULT_API_HOST);
        // 第一步：调用合成接口，返回音频文件URL
        String audioUrl = postForAudioUrl(apiHost, request.getApiKey(), payload.toString());
        // 第二步：下载音频二进制
        byte[] bytes = downloadBytes(audioUrl);
        return TtsSynthesizeResult.builder()
            .bytes(bytes)
            .format(format)
            .mimeType(mimeType(format))
            .build();
    }

    /**
     * 请求体：text/voice/format/rate/pitch/volume 均放在 input 对象内
     */
    private ObjectNode buildPayload(TtsSynthesizeRequest request, String format) {
        ObjectNode payload = OBJECT_MAPPER.createObjectNode();
        payload.put("model", StrUtil.blankToDefault(request.getModel(), DEFAULT_MODEL));
        ObjectNode input = payload.putObject("input");
        input.put("text", request.getText());
        input.put("voice", request.getVoice());
        input.put("format", format);
        if (request.getSpeed() != null) {
            input.put("rate", request.getSpeed());
        }
        if (request.getPitch() != null) {
            input.put("pitch", request.getPitch());
        }
        if (request.getVolume() != null) {
            input.put("volume", request.getVolume());
        }
        return payload;
    }

    /**
     * 调用合成接口并解析响应中的音频URL（非流式响应为JSON）
     */
    private String postForAudioUrl(String apiHost, String apiKey, String jsonBody) {
        Request httpRequest = new Request.Builder()
            .url(apiHost + TTS_PATH)
            .addHeader("Authorization", "Bearer " + apiKey)
            .post(RequestBody.create(jsonBody, JSON))
            .build();
        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            ResponseBody body = response.body();
            String text = body == null ? "" : body.string();
            if (!response.isSuccessful()) {
                throw new IllegalArgumentException("阿里云语音合成调用失败: " + response.code() + " - " + extractErrorMessage(text));
            }
            JsonNode root = OBJECT_MAPPER.readTree(text);
            String url = root.path("output").path("audio").path("url").asText("");
            if (StrUtil.isBlank(url)) {
                throw new IllegalArgumentException("阿里云语音合成响应缺少音频URL: " + text);
            }
            return url;
        } catch (IOException e) {
            throw new RuntimeException("阿里云语音合成请求异常: " + e.getMessage(), e);
        }
    }

    /**
     * 下载音频文件二进制
     */
    private byte[] downloadBytes(String url) {
        Request httpRequest = new Request.Builder().url(url).get().build();
        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            ResponseBody body = response.body();
            if (!response.isSuccessful() || body == null) {
                throw new IllegalArgumentException("阿里云语音音频下载失败: " + response.code());
            }
            return body.bytes();
        } catch (IOException e) {
            throw new RuntimeException("阿里云语音音频下载异常: " + e.getMessage(), e);
        }
    }

    /**
     * 从错误响应JSON中提取 message 字段，便于前端直接展示
     */
    private String extractErrorMessage(String text) {
        try {
            JsonNode node = OBJECT_MAPPER.readTree(text);
            String msg = node.path("message").asText("");
            if (StrUtil.isNotBlank(msg)) {
                return msg;
            }
        } catch (Exception ignored) {
            // 非JSON错误响应，原样返回
        }
        return text;
    }

    private String mimeType(String format) {
        return switch (format) {
            case "wav" -> "audio/wav";
            case "pcm" -> "audio/pcm";
            case "opus" -> "audio/opus";
            default -> "audio/mpeg";
        };
    }

}
