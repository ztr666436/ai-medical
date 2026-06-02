package com.medical.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 认证授权微服务启动类
 * <p>
 * 负责：注册、登录、Token 签发、Token 刷新
 *
 * @author Architect Team
 */
@SpringBootApplication
@MapperScan("com.medical.auth.mapper")
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
        System.out.println("""
                
                ========================================
                  AI Medical Care Auth Service Started!
                  认证授权微服务启动成功
                ========================================
                """);
    }
}
