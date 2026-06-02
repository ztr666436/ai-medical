package com.medical.agent.controller;

import com.medical.agent.service.AgentService;
import com.medical.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * AI Agent 对话接口
 * <p>
 * 核心入口，所有 AI 相关能力通过此控制器暴露
 *
 * @author Architect Team
 */
@Tag(name = "AI Agent", description = "DeepSeek 对话、健康解析、意图识别")
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @Operation(summary = "解析健康数据", description = "从自然语言中提取结构化健康指标")
    @PostMapping("/parse-health")
    public R<Map<String, Object>> parseHealth(@RequestBody Map<String, Object> body) {
        String message = (String) body.get("message");
        Long userId = body.get("userId") != null
                ? Long.valueOf(body.get("userId").toString()) : 0L;
        return agentService.parseHealthData(message, userId);
    }

    @Operation(summary = "多轮对话", description = "蚂蚁阿福风格智能对话")
    @PostMapping("/chat")
    public R<Map<String, Object>> chat(@RequestBody Map<String, Object> body) {
        String message = (String) body.get("message");
        Long userId = body.get("userId") != null
                ? Long.valueOf(body.get("userId").toString()) : 0L;
        return agentService.chat(message, userId);
    }

    @Operation(summary = "流式对话（SSE）", description = "实时流式输出 AI 回复")
    @PostMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody Map<String, Object> body) {
        String message = (String) body.get("message");
        Long userId = body.get("userId") != null
                ? Long.valueOf(body.get("userId").toString()) : 0L;
        return agentService.chatStream(message, userId);
    }

    @Operation(summary = "健康建议", description = "根据健康数据生成个性化建议")
    @PostMapping("/health-advice")
    public R<Map<String, Object>> healthAdvice(@RequestBody Map<String, Object> body) {
        String records = body.get("records") != null ? body.get("records").toString() : "";
        Long userId = body.get("userId") != null
                ? Long.valueOf(body.get("userId").toString()) : 0L;
        return agentService.generateHealthAdvice(records, userId);
    }

    @Operation(summary = "意图识别", description = "分析用户消息意图")
    @PostMapping("/intent")
    public R<Map<String, Object>> intent(@RequestBody Map<String, Object> body) {
        String message = (String) body.get("message");
        return agentService.detectIntent(message);
    }

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public R<String> health() {
        return R.ok("AI Agent Service is running - DeepSeek 已就绪");
    }
}
