package com.medical.agent.service;

import com.medical.common.result.R;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * AI Agent 服务接口
 *
 * @author Architect Team
 */
public interface AgentService {

    /** 解析健康数据（供 health-data-service 调用） */
    R<Map<String, Object>> parseHealthData(String message, Long userId);

    /** 多轮对话（阿福风格） */
    R<Map<String, Object>> chat(String message, Long userId);

    /** 流式对话（SSE） */
    Flux<String> chatStream(String message, Long userId);

    /** 生成健康建议 */
    R<Map<String, Object>> generateHealthAdvice(String records, Long userId);

    /** 意图识别 */
    R<Map<String, Object>> detectIntent(String message);
}
