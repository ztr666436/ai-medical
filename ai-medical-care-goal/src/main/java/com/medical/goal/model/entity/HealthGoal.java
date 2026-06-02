package com.medical.goal.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("health_goal")
public class HealthGoal {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String goalType;
    private BigDecimal targetValue;
    private String unit;
    private String period;
    private String status;
    private Boolean aiRecommended;
    private LocalDate startedAt;
    private LocalDate endAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private Integer deleted;
}
