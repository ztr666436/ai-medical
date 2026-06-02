package com.medical.goal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 健康目标微服务启动类
 * <p>
 * 负责：目标推荐、目标 CRUD、进度追踪、
 * 监听 HealthDataUpdatedEvent 自动检查目标进度
 *
 * @author Architect Team
 */
@SpringBootApplication
@EnableAsync
@MapperScan("com.medical.goal.mapper")
public class GoalApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoalApplication.class, args);
        System.out.println("""
                
                ========================================
                  AI Medical Care Goal Service Started!
                  健康目标微服务启动成功
                ========================================
                """);
    }
}
