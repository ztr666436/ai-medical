package com.medical.health.feign;

import com.medical.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * AI Agent 服务 Feign 客户端
 * <p>
 * 负责将用户自然语言输入发送给 ai-agent-service，
 * 获取解析后的结构化健康数据。
 *
 * @author Architect Team
 */
@FeignClient(name = "ai-medical-care-ai-agent", url = "http://localhost:8085", path = "/api/agent")
public interface AiAgentClient {

    /**
     * 解析健康数据
     * @param body { "message": "今天走了9000步", "userId": 1 }
     * @return 解析结果
     */
    @PostMapping("/parse-health")
    R<Map<String, Object>> parseHealthData(@RequestBody Map<String, Object> body);

    /**
     * 生成健康建议
     * @param body { "records": [...], "userId": 1 }
     * @return AI 建议文本
     */
    @PostMapping("/health-advice")
    R<Map<String, Object>> generateHealthAdvice(@RequestBody Map<String, Object> body);
}
