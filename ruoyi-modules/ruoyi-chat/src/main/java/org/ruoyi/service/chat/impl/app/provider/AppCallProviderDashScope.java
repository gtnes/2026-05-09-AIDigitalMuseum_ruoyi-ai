package org.ruoyi.service.chat.impl.app.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.sse.utils.SseMessageUtils;
import org.ruoyi.domain.vo.chat.ChatAppVo;
import org.ruoyi.service.chat.IAiMuseumUsageService;
import org.ruoyi.service.chat.impl.app.AppCallService;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Component
public class AppCallProviderDashScope implements AppCallProvider {

    private static final String PROVIDER_CODE = "dashscope";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final IAiMuseumUsageService aiMuseumUsageService;

    @Override
    public String getProviderCode() {
        return PROVIDER_CODE;
    }

    @Override
    public void streamCall(ChatAppVo app, AppCallService.AppCallRequest request) {
        String sessionId = request.getSessionId();
        // 博物馆C端模式：异常不向前端透传原始错误（影响访客体验），仅日志记录并提示稍后重试
        boolean quietMode = request.getMuseumId() != null;
        String baseUrl = app.getApiHost();
        if (StringUtils.isBlank(baseUrl)) {
            SseMessageUtils.sendError(sessionId, quietMode ? "服务繁忙，请稍后再试" : "应用请求地址未配置");
            return;
        }

        // 直接使用用户配置的完整 URL，不拼接任何路径
        String endpoint = baseUrl;
        try {
            String requestBody = buildRequestBody(request.getContent());
            log.info("【DashScope应用调用】endpoint={}, body={}", endpoint, requestBody);

            HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Bearer " + app.getApiKey())
                .header("Content-Type", "application/json")
                .header("X-DashScope-SSE", "enable")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(120))
                .build();

            // usage节点随最后一个数据块返回且仅出现一次，用标记防止重复计费
            AtomicBoolean usageRecorded = new AtomicBoolean(false);
            client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(response -> {
                    log.info("【DashScope响应状态】status={}", response.statusCode());
                    response.body().forEach(line -> {
                        if (StringUtils.isBlank(line)) return;
                        log.debug("【DashScope响应行】line={}", line);
                        try {
                            if (line.startsWith("data:")) {
                                String json = line.substring(5).trim();
                                if ("[DONE]".equals(json)) {
                                    SseMessageUtils.sendDone(sessionId);
                                    return;
                                }
                                parseAndSend(json, request, usageRecorded);
                            }
                        } catch (Exception e) {
                            log.error("解析SSE事件失败: {}", line, e);
                        }
                    });
                })
                .exceptionally(throwable -> {
                    log.error("DashScope应用调用失败", throwable);
                    SseMessageUtils.sendError(sessionId, quietMode ? "服务繁忙，请稍后再试" : "应用调用失败: " + throwable.getMessage());
                    return null;
                });

        } catch (Exception e) {
            log.error("DashScope应用调用异常", e);
            SseMessageUtils.sendError(sessionId, quietMode ? "服务繁忙，请稍后再试" : "应用调用异常: " + e.getMessage());
        }
    }

    private String buildRequestBody(String userInput) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("prompt", userInput);
        body.put("input", input);

        body.put("parameters", new LinkedHashMap<>());

        return OBJECT_MAPPER.writeValueAsString(body);
    }

    private void parseAndSend(String json, AppCallService.AppCallRequest request, AtomicBoolean usageRecorded) {
        String sessionId = request.getSessionId();
        try {
            log.debug("【DashScope解析】json={}", json);
            JsonNode root = OBJECT_MAPPER.readTree(json);

            // 先检查是否有错误
            JsonNode error = root.path("error");
            if (!error.isMissingNode() && !error.isNull()) {
                String errorMsg = error.path("message").asText("未知错误");
                log.error("【DashScope API错误】{}", errorMsg);
                SseMessageUtils.sendError(sessionId, request.getMuseumId() != null
                    ? "服务繁忙，请稍后再试" : "API错误: " + errorMsg);
                return;
            }

            // 博物馆C端计费：usage随最后一个数据块（finish_reason=stop）返回
            // 智能体应用格式为 usage.models[]（可能含多个模型分项），普通模型为 usage.input_tokens/output_tokens
            // 注意：中间数据块可能携带空usage节点，仅在出现真实token数时记账
            JsonNode usage = root.path("usage");
            if (!usage.isMissingNode() && !usage.isNull() && request.getMuseumId() != null) {
                long tokensIn = 0;
                long tokensOut = 0;
                JsonNode models = usage.path("models");
                if (models.isArray() && !models.isEmpty()) {
                    for (JsonNode m : models) {
                        tokensIn += m.path("input_tokens").asLong(0);
                        tokensOut += m.path("output_tokens").asLong(0);
                    }
                } else {
                    tokensIn = usage.path("input_tokens").asLong(0);
                    tokensOut = usage.path("output_tokens").asLong(0);
                }
                if ((tokensIn > 0 || tokensOut > 0) && usageRecorded.compareAndSet(false, true)) {
                    aiMuseumUsageService.recordChat(request.getMuseumId(), request.getAppId(),
                        request.getContent() == null ? 0 : request.getContent().length(), tokensIn, tokensOut);
                }
            }

            // 应用中心API的响应格式: {"output": {"text": "..."}, "usage": {...}}
            JsonNode output = root.path("output");
            if (output.isMissingNode()) {
                log.warn("【DashScope解析】output节点缺失");
                return;
            }

            // 尝试获取text字段
            JsonNode textNode = output.path("text");
            if (!textNode.isMissingNode() && !textNode.isNull()) {
                String text = textNode.asText("");
                if (StringUtils.isNotBlank(text)) {
                    log.debug("【DashScope发送】text={}, sessionId={}", text, sessionId);
                    try {
                        SseMessageUtils.sendContent(sessionId, text);
                        log.debug("【DashScope发送成功】sessionId={}", sessionId);
                    } catch (Exception e) {
                        log.error("【DashScope发送失败】sessionId={}, error={}", sessionId, e.getMessage(), e);
                    }
                }
                // 检查是否完成
                String finishReason = output.path("finish_reason").asText("");
                if ("stop".equals(finishReason)) {
                    log.info("【DashScope完成】sessionId={}", sessionId);
                    SseMessageUtils.sendDone(sessionId);
                }
                return;
            }

            // 尝试获取choices格式 (兼容其他格式)
            JsonNode choices = output.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                JsonNode message = choices.get(0).path("message");
                JsonNode content = message.path("content");
                log.debug("【DashScope解析】content类型={}, content={}", content.getNodeType(), content);

                if (content.isArray()) {
                    for (JsonNode item : content) {
                        String type = item.path("type").asText("");
                        if ("text".equals(type)) {
                            String text = item.path("text").asText("");
                            if (StringUtils.isNotBlank(text)) {
                                log.debug("【DashScope发送】text={}", text);
                                SseMessageUtils.sendContent(sessionId, text);
                            }
                        }
                    }
                } else {
                    String text = content.asText("");
                    if (StringUtils.isNotBlank(text)) {
                        log.debug("【DashScope发送】text={}", text);
                        SseMessageUtils.sendContent(sessionId, text);
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析响应内容失败: {}", json, e);
        }
    }
}
