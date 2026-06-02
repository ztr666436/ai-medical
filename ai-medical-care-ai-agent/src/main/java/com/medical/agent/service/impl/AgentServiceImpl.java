package com.medical.agent.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.medical.agent.llm.DeepSeekClient;
import com.medical.agent.llm.DeepSeekClient.ReasoningResponse;
import com.medical.agent.prompt.PromptTemplates;
import com.medical.agent.service.AgentService;
import com.medical.common.result.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI Agent 服务实现
 * <p>
 * 核心能力：
 * 1. 健康数据解析 — DeepSeek 提取结构化指标
 * 2. 多轮对话 — 蚂蚁阿福风格萌趣对话
 * 3. 流式输出 — SSE 实时传输
 * 4. 健康建议 — 个性化 AI 建议
 * 5. 意图识别 — 路由到不同处理逻辑
 * <p>
 * 降级策略：AI 不可用时返回友好提示
 *
 * @author Architect Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final DeepSeekClient deepSeekClient;

    /** 用户对话上下文（生产环境应使用 Redis） */
    private final Map<Long, StringBuilder> conversationContext = new ConcurrentHashMap<>();

    // ==================== 健康数据解析 ====================

    @Override
    public R<Map<String, Object>> parseHealthData(String message, Long userId) {
        log.info("[健康解析] userId={}, message={}", userId, message);
        try {
            ReasoningResponse aiResponse = deepSeekClient.chatSync(PromptTemplates.HEALTH_PARSE, message);
            JSONObject json = JSON.parseObject(cleanJsonResponse(aiResponse.content()));
            return R.ok(json);
        } catch (Exception e) {
            log.error("[健康解析] AI 调用失败: {}", e.getMessage());
            // 降级：返回空结果，由 health-data-service 的规则引擎兜底
            return R.ok(Map.of("records", java.util.Collections.emptyList(),
                    "summary", "AI 暂时不可用，已切换至本地解析"));
        }
    }

    // ==================== 多轮对话 ====================

    @Override
    public R<Map<String, Object>> chat(String message, Long userId) {
        log.info("[对话] userId={}, message={}...", userId, message.substring(0, Math.min(30, message.length())));

        try {
            // 获取上下文
            String context = getContext(userId);

            // 构建增强 prompt
            String enhancedPrompt = context.isEmpty() ? PromptTemplates.CONVERSATION
                    : PromptTemplates.CONVERSATION + "\n\n对话历史：\n" + context;

            ReasoningResponse aiResponse = deepSeekClient.chatSync(enhancedPrompt, message);

            // 保存上下文（保留最近 10 轮）
            appendContext(userId, message, aiResponse.content());

            return R.ok(Map.of("reply", aiResponse.content(),
                    "reasoning", aiResponse.reasoning(),
                    "role", "assistant"));
        } catch (Exception e) {
            log.error("[对话] AI 调用失败: {}", e.getMessage());
            return R.ok(Map.of("reply", "🤖 阿福正在休息，请稍后再和我聊天吧～", "role", "assistant"));
        }
    }

    // ==================== 流式对话 ====================

    @Override
    public Flux<String> chatStream(String message, Long userId) {
        log.info("[流式对话] userId={}", userId);
        try {
            return deepSeekClient.chatStream(PromptTemplates.CONVERSATION, message)
                    .doOnComplete(() -> {
                        // 流式结束后保存上下文
                        // 简化处理，后续优化
                    })
                    .doOnError(e -> log.error("[流式对话] 异常: {}", e.getMessage()));
        } catch (Exception e) {
            return Flux.just("🤖 流式服务暂时不可用");
        }
    }

    // ==================== 健康建议 ====================

    @Override
    public R<Map<String, Object>> generateHealthAdvice(String records, Long userId) {
        log.info("[健康建议] userId={}", userId);
        try {
            String prompt = PromptTemplates.HEALTH_ADVICE + "\n\n用户健康数据：" + records;
            ReasoningResponse aiResponse = deepSeekClient.chatSync(prompt, "请根据以上数据给出健康建议");
            return R.ok(Map.of("advice", aiResponse.content(),
                    "reasoning", aiResponse.reasoning()));
        } catch (Exception e) {
            log.error("[健康建议] AI 调用失败: {}", e.getMessage());
            return R.ok(Map.of("advice", "💪 坚持记录健康数据就是好习惯！请定期监测，保持健康生活方式。"));
        }
    }

    // ==================== 意图识别 ====================

    @Override
    public R<Map<String, Object>> detectIntent(String message) {
        try {
            ReasoningResponse aiResponse = deepSeekClient.chatSync(PromptTemplates.INTENT, message);
            JSONObject json = JSON.parseObject(cleanJsonResponse(aiResponse.content()));
            return R.ok(json);
        } catch (Exception e) {
            log.error("[意图识别] AI 调用失败: {}", e.getMessage());
            // 降级：简单规则判断
            String intent = message.contains("步") || message.contains("kg") || message.contains("血压")
                    ? "record_health" : "casual_chat";
            return R.ok(Map.of("intent", intent, "confidence", 0.5, "source", "rule_fallback"));
        }
    }

    // ==================== 上下文管理 ====================

    private String getContext(Long userId) {
        StringBuilder ctx = conversationContext.get(userId);
        return ctx != null ? ctx.toString() : "";
    }

    private void appendContext(Long userId, String userMessage, String aiResponse) {
        conversationContext.compute(userId, (k, v) -> {
            if (v == null) v = new StringBuilder();
            v.append("用户: ").append(userMessage.substring(0, Math.min(100, userMessage.length()))).append("\n");
            v.append("阿福: ").append(aiResponse.substring(0, Math.min(100, aiResponse.length()))).append("\n");

            // 保留最近 10 轮
            String[] lines = v.toString().split("\n");
            if (lines.length > 20) {
                v = new StringBuilder();
                for (int i = lines.length - 20; i < lines.length; i++) {
                    v.append(lines[i]).append("\n");
                }
            }
            return v;
        });
    }

    /**
     * 清洗 AI 返回的 JSON（去除 markdown 代码块标记）
     */
    private String cleanJsonResponse(String response) {
        if (response == null) return "{}";
        return response
                .replaceAll("```json\\s*", "")
                .replaceAll("```\\s*", "")
                .trim();
    }
}
