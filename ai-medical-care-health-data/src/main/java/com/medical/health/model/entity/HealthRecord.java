package com.medical.health.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康数据记录实体
 *
 * @author Architect Team
 */
@Data
@TableName("health_record")
public class HealthRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户 ID */
    private Long userId;

    /** 记录类型: step/weight/blood_pressure/blood_sugar/heart_rate/sleep */
    private String recordType;

    /** 数值 */
    private BigDecimal recordValue;

    /** 单位 */
    private String unit;

    /** 用户声称的记录时间 */
    private LocalDateTime recordedAt;

    /** 用户原始输入文本 */
    private String sourceText;

    /** AI 抽取置信度 */
    private BigDecimal aiConfidence;

    /** AI 备注 */
    private String aiNotes;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
