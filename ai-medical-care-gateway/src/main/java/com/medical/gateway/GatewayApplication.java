package com.medical.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API 网关启动类
 * <p>
 * 统一入口（端口 8080），负责：
 * - 路由转发到各微服务
 * - Token 校验（JWT）
 * - CORS 跨域处理
 * - 请求限流
 *
 * @author Architect Team
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("""
                
                ╔══════════════════════════════════════╗
                ║   API Gateway Started! (port 8080)   ║
                ║   路由转发 · Token校验 · CORS · 限流  ║
                ╚══════════════════════════════════════╝
                """);
    }
}
