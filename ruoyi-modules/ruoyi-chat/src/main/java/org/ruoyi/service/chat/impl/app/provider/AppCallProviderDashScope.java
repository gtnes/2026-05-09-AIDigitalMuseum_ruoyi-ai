package org.ruoyi.service.chat.impl.app.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.sse.utils.SseMessageUtils;
import org.ruoyi.domain.vo.chat.ChatAppVo;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class AppCallProviderDashScope implements AppCallProvider {

    private static final String PROVIDER_CODE = "dashscope";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String getProviderCode() {
        return PROVIDER_CODE;
    }

    @Override
    public void streamCall(ChatAppVo app, String userInput, String sessionId) {
        String baseUrl = app.getApiHost();
        if (StringUtils.isBlank(baseUrl)) {
            SseMessageUtils.sendError(sessionId, "应用请求地址未配置");
            return;
        }

        // 直接使用用户配置的完整 URL，不拼接任何路径
        String endpoint = baseUrl;
        try {
            String requestBody = buildRequestBody(userInput);
            log.info("【DashScope应用调用】endpoint={}, body={}", endpoint, requestBody);

            HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Bearer " + app.getApiKey())
                .header("Content-Type", "application/json")
                .header("X-DashScope-SSE", "enable")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(120))
                .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(response -> {
                    log.info("【DashScope响应状态】status={}", response.statusCode());
                    response.body().forEach(line -> {
                        if (StringUtils.isBlank(line)) return;
                        log.info("【DashScope响应行】line={}", line);
                        try {
                            if (line.startsWith("data:")) {
                                String json = line.substring(5).trim();
                                if ("[DONE]".equals(json)) {
                                    SseMessageUtils.sendDone(sessionId);
                                    return;
                                }
                                parseAndSend(json, sessionId);
                            }
                        } catch (Exception e) {
                            log.error("解析SSE事件失败: {}", line, e);
                        }
                    });
                })
                .exceptionally(throwable -> {
                    log.error("DashScope应用调用失败", throwable);
                    SseMessageUtils.sendError(sessionId, "应用调用失败: " + throwable.getMessage());
                    return null;
                });

        } catch (Exception e) {
            log.error("DashScope应用调用异常", e);
            SseMessageUtils.sendError(sessionId, "应用调用异常: " + e.getMessage());
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

    private void parseAndSend(String json, String sessionId) {
        try {
            log.info("【DashScope解析】json={}", json);
            JsonNode root = OBJECT_MAPPER.readTree(json);

            // 先检查是否有错误
            JsonNode error = root.path("error");
            if (!error.isMissingNode() && !error.isNull()) {
                String errorMsg = error.path("message").asText("未知错误");
                log.error("【DashScope API错误】{}", errorMsg);
                SseMessageUtils.sendError(sessionId, "API错误: " + errorMsg);
                return;
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
                    log.info("【DashScope发送】text={}, sessionId={}", text, sessionId);
                    try {
                        SseMessageUtils.sendContent(sessionId, text);
                        log.info("【DashScope发送成功】sessionId={}", sessionId);
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
                log.info("【DashScope解析】content类型={}, content={}", content.getNodeType(), content);

                if (content.isArray()) {
                    for (JsonNode item : content) {
                        String type = item.path("type").asText("");
                        if ("text".equals(type)) {
                            String text = item.path("text").asText("");
                            if (StringUtils.isNotBlank(text)) {
                                log.info("【DashScope发送】text={}", text);
                                SseMessageUtils.sendContent(sessionId, text);
                            }
                        }
                    }
                } else {
                    String text = content.asText("");
                    if (StringUtils.isNotBlank(text)) {
                        log.info("【DashScope发送】text={}", text);
                        SseMessageUtils.sendContent(sessionId, text);
                    }
                }
            }
        } catch (Exception e) {
            log.error("解析响应内容失败: {}", json, e);
        }
    }
}
