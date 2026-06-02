package com.medical.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.medical.common.config.DeepSeekProperties;

/**
 * AI Agent 智能对话微服务
 * <p>
 * 接入 DeepSeek API，提供：
 * - 健康数据解析（parse-health）
 * - 多轮对话（chat）
 * - 健康建议生成（health-advice）
 * - 意图识别（intent）
 *
 * @author Architect Team
 */
@SpringBootApplication
@EnableConfigurationProperties(DeepSeekProperties.class)
public class AgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
        System.out.println("""
                
                ╔══════════════════════════════════════╗
                ║   AI Agent Service Started!          ║
                ║   DeepSeek API 已就绪                 ║
                ╚══════════════════════════════════════╝
                """);
    }
}
