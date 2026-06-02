package com.medical.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康数据更新事件
 * <p>
 * 当用户录入新的健康数据后，发布此事件。
 * goal-service 监听此事件以检查目标进度。
 *
 * @author Architect Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataUpdatedEvent {

    /** 用户 ID */
    private Long userId;

    /** 数据类型 */
    private String recordType;

    /** 数值 */
    private BigDecimal recordValue;

    /** 记录时间 */
    private LocalDateTime recordedAt;

    /** 事件时间戳 */
    private LocalDateTime eventTime;
}
