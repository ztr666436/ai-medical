package com.medical.health;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 健康数据微服务启动类
 * <p>
 * 负责：健康数据 CRUD、对话式记录、AI 解析协调、数据统计与事件发布
 *
 * @author Architect Team
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.medical.health.feign")
@MapperScan("com.medical.health.mapper")
public class HealthDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthDataApplication.class, args);
        System.out.println("""
                
                ========================================
                  AI Medical Care Health Data Started!
                  健康数据微服务启动成功
                ========================================
                """);
    }
}
