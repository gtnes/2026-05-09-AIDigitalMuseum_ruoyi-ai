package org.ruoyi.service.chat.impl.app.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ruoyi.common.core.utils.StringUtils;
import org.ruoyi.common.sse.utils.SseMessageUtils;
import org.ruoyi.domain.vo.chat.ChatAppVo;
import org.ruoyi.service.chat.IAiMuseumUsageService;
import org.ruoyi.service.chat.IAiUsageService;
import org.ruoyi.service.chat.impl.app.AppCallService;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
    private final IAiUsageService aiUsageService;

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
        // agentId非空 → 百炼知识问答v2接口；为空 → 旧版应用调用v1接口
        boolean v2KnowledgeChat = StringUtils.isNotBlank(app.getAgentId());
        try {
            String requestBody = buildRequestBody(app, request.getContent());
            log.info("【DashScope应用调用】endpoint={}, v2KnowledgeChat={}, body={}", endpoint, v2KnowledgeChat, requestBody);

            HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

            // v2知识问答接口用Accept头开启SSE；v1应用调用用X-DashScope-SSE头
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Bearer " + app.getApiKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(120));
            if (v2KnowledgeChat) {
                requestBuilder.header("Accept", "text/event-stream");
            } else {
                requestBuilder.header("X-DashScope-SSE", "enable");
            }
            HttpRequest httpRequest = requestBuilder.build();

            // usage节点随最后一个数据块返回且仅出现一次，用标记防止重复计费
            AtomicBoolean usageRecorded = new AtomicBoolean(false);
            // v2知识问答：每帧content为增量片段，前端气泡按最新全量覆盖渲染（与v1行为一致），需累积拼接后发送
            StringBuilder v2ContentBuffer = new StringBuilder();
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
                                parseAndSend(json, app, request, usageRecorded, v2ContentBuffer);
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

    private String buildRequestBody(ChatAppVo app, String userInput) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();

        Map<String, Object> input = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(app.getAgentId())) {
            // 知识问答v2接口：input.messages + parameters.agent_options.agent_id，stream必填true（当前版本仅支持流式）
            List<Map<String, Object>> messages = new ArrayList<>();
            Map<String, Object> message = new LinkedHashMap<>();
            message.put("role", "user");
            message.put("content", userInput);
            messages.add(message);
            input.put("messages", messages);
            body.put("input", input);

            Map<String, Object> agentOptions = new LinkedHashMap<>();
            agentOptions.put("agent_id", app.getAgentId());
            Map<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("agent_options", agentOptions);
            body.put("parameters", parameters);
            body.put("stream", true);
        } else {
            // 旧版应用调用v1接口
            input.put("prompt", userInput);
            body.put("input", input);

            body.put("parameters", new LinkedHashMap<>());
        }

        return OBJECT_MAPPER.writeValueAsString(body);
    }

    private void parseAndSend(String json, ChatAppVo app, AppCallService.AppCallRequest request,
                              AtomicBoolean usageRecorded, StringBuilder v2ContentBuffer) {
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

            // 知识问答v2接口：走独立解析（记账延迟到finish帧，思考/检索内容不透出）
            if (StringUtils.isNotBlank(app.getAgentId())) {
                JsonNode outputV2 = root.path("output");
                if (outputV2.isMissingNode()) {
                    log.warn("【DashScope解析】output节点缺失");
                    return;
                }
                parseV2KnowledgeChat(root, outputV2, app, request, usageRecorded, v2ContentBuffer);
                return;
            }

            // v1：计费记账（usage随最后一个数据块返回，中间数据块可能携带空usage节点，仅在出现真实token数时记账）
            recordUsage(root.path("usage"), app, request, usageRecorded);

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

    /**
     * 知识问答v2接口响应解析
     * 响应分阶段：planning（规划思考）→ tool_calling（知识检索）→ generating（最终回答）
     * 采用反向排除：仅跳过明确的 planning/tool 帧，其余帧内容均作为回答输出
     * （文档说明空包/边界帧的 step、step_change、group 可能缺失，按 step==generating 正向匹配会漏发内容）
     */
    private void parseV2KnowledgeChat(JsonNode root, JsonNode output, ChatAppVo app,
                                       AppCallService.AppCallRequest request, AtomicBoolean usageRecorded,
                                       StringBuilder contentBuffer) {
        String sessionId = request.getSessionId();
        JsonNode choices = output.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            return;
        }
        JsonNode choice = choices.get(0);
        JsonNode message = choice.path("message");
        // 仅尾包（generation_end帧）finish_reason为stop，流式片段为空串
        boolean finished = "stop".equals(choice.path("finish_reason").asText(""));

        // 计费记账：generation_end帧携带最终usage；中间tool_calling帧的usage为检索用量，不记账
        if (finished) {
            recordUsage(root.path("usage"), app, request, usageRecorded);
        }

        // 规划思考帧（step=planning）、工具调用帧（step=tool_calling）、工具返回帧（type=tool）不透出给前端
        String messageType = message.path("type").asText("");
        String step = message.path("extra").path("step").asText("");
        boolean thinkingOrTool = "tool".equals(messageType)
            || "planning".equals(step) || "tool_calling".equals(step);

        if (!thinkingOrTool) {
            // 增量片段累积到缓冲区，发送当前全量（前端气泡按最新全量覆盖渲染）
            appendV2Content(contentBuffer, message.path("content"));
            if (contentBuffer.length() > 0) {
                SseMessageUtils.sendContent(sessionId, contentBuffer.toString());
            }
        }

        if (finished) {
            log.info("【DashScope完成】sessionId={}", sessionId);
            SseMessageUtils.sendDone(sessionId);
        }
    }

    /**
     * 拼接v2回答内容到缓冲区：content 可为纯文本字符串或多模态数组（取其中text部分）
     */
    private void appendV2Content(StringBuilder buffer, JsonNode contentNode) {
        if (contentNode.isTextual()) {
            buffer.append(contentNode.asText(""));
            return;
        }
        if (contentNode.isArray()) {
            for (JsonNode item : contentNode) {
                if ("text".equals(item.path("type").asText(""))) {
                    buffer.append(item.path("text").asText(""));
                }
            }
        }
    }

    /**
     * 计费记账：智能体应用格式为 usage.models[]（可能含多个模型分项），知识问答v2/普通模型为 usage.input_tokens/output_tokens
     * museumId非空记博物馆流水，否则记通用流水（系统类别，apps-chat等登录测试调用）
     */
    private void recordUsage(JsonNode usage, ChatAppVo app, AppCallService.AppCallRequest request, AtomicBoolean usageRecorded) {
        if (usage.isMissingNode() || usage.isNull()) {
            return;
        }
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
        if (tokensIn <= 0 && tokensOut <= 0) {
            return;
        }
        if (!usageRecorded.compareAndSet(false, true)) {
            return;
        }
        int chars = request.getContent() == null ? 0 : request.getContent().length();
        if (request.getMuseumId() != null) {
            aiMuseumUsageService.recordChat(request.getMuseumId(), request.getAppId(),
                chars, tokensIn, tokensOut, request.getClientIp());
        } else {
            aiUsageService.recordChat(request.getOperId(), request.getOperName(),
                request.getAppId(), app == null ? null : app.getAppName(),
                chars, tokensIn, tokensOut, request.getClientIp());
        }
    }
}
