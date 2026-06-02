package com.medical.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * DeepSeek AI 配置属性
 *
 * @author Architect Team
 */
@Data
@ConfigurationProperties(prefix = "ai.deepseek")
public class DeepSeekProperties {

    /** API 密钥 */
    private String apiKey = "";

    /** API 基础地址 */
    private String baseUrl = "https://api.deepseek.com";

    /** 模型名称 */
    private String model = "deepseek-chat";

    /** 最大 Token 数 */
    private int maxTokens = 2048;

    /** 温度参数（0-1，越高越随机） */
    private double temperature = 0.7;
}
