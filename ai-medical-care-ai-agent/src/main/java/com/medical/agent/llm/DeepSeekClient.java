package com.medical.agent.llm;

import com.medical.common.config.DeepSeekProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek API 客户端
 * <p>
 * 封装 DeepSeek Chat Completion API 调用，
 * 支持同步请求和流式输出（SSE）。
 *
 * @author Architect Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeepSeekClient {

    private final DeepSeekProperties properties;
    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Authorization", "Bearer " + properties.getApiKey())
                .defaultHeader("Content-Type", "application/json")
                .codecs(config -> config.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                .build();
        log.info("DeepSeek 客户端初始化完成: model={}", properties.getModel());
    }

    /**
     * 同步调用（非流式）
     * @param systemPrompt 系统提示词
     * @param userMessage 用户消息
     * @return AI 回复，包含思考和最终回答
     */
    public ReasoningResponse chatSync(String systemPrompt, String userMessage) {
        Map<String, Object> body = buildRequestBody(systemPrompt, userMessage, false);

        log.debug("[DeepSeek] 发起请求: {}...", userMessage.substring(0, Math.min(50, userMessage.length())));

        Map<String, Object> response = webClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(60))
                .block();

        if (response == null) {
            log.error("[DeepSeek] 响应为空");
            return new ReasoningResponse("", "AI 服务暂时无响应，请稍后重试");
        }

        return extractReasoningResponse(response);
    }

    /**
     * 思考+回答结果
     */
    public record ReasoningResponse(String reasoning, String content) {}

    /**
     * 流式调用（SSE）
     * @param systemPrompt 系统提示词
     * @param userMessage 用户消息
     * @return 流式 Flux
     */
    public Flux<String> chatStream(String systemPrompt, String userMessage) {
        Map<String, Object> body = buildRequestBody(systemPrompt, userMessage, true);

        log.debug("[DeepSeek-Stream] 发起请求");

        return webClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(Map.class)
                .timeout(Duration.ofSeconds(60))
                .mapNotNull(this::extractStreamChunk)
                .filter(s -> !s.isEmpty());
    }

    /**
     * 构建请求体
     */
    private Map<String, Object> buildRequestBody(String systemPrompt, String userMessage, boolean stream) {
        return Map.of(
                "model", properties.getModel(),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ),
                "max_tokens", properties.getMaxTokens(),
                "temperature", properties.getTemperature(),
                "stream", stream
        );
    }

    /**
     * 从同步响应中提取思考内容和最终回答
     */
    @SuppressWarnings("unchecked")
    private ReasoningResponse extractReasoningResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) return new ReasoningResponse("", "");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.getOrDefault("content", "");
            String reasoning = (String) message.getOrDefault("reasoning_content", "");
            return new ReasoningResponse(reasoning, content);
        } catch (Exception e) {
            log.error("[DeepSeek] 解析响应失败: {}", e.getMessage());
            return new ReasoningResponse("", "");
        }
    }

    /**
     * 从同步响应中提取内容（兼容旧调用）
     */
    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> response) {
        return extractReasoningResponse(response).content();
    }

    /**
     * 从流式响应中提取文本片段
     */
    @SuppressWarnings("unchecked")
    private String extractStreamChunk(Map<String, Object> chunk) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) chunk.get("choices");
            if (choices == null || choices.isEmpty()) return "";
            Map<String, Object> delta = (Map<String, Object>) choices.get(0).get("delta");
            if (delta == null) return "";
            Object content = delta.get("content");
            return content != null ? content.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
