package com.medical.common.config;

import com.medical.common.exception.GlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Common 模块自动配置
 * <p>
 * 当其他服务引入 ai-medical-care-common 依赖时，
 * Spring Boot 自动装配以下组件：
 * - GlobalExceptionHandler: 全局异常处理
 *
 * @author Architect Team
 */
@Slf4j
@AutoConfiguration
@Import(GlobalExceptionHandler.class)
public class CommonAutoConfiguration {

    static {
        log.info("AI Medical Care Common 模块已加载");
    }
}
