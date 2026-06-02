package com.medical.goal.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 目标创建/更新请求
 *
 * @author Architect Team
 */
@Data
@Schema(description = "健康目标请求")
public class GoalDTO {

    @NotBlank @Schema(description = "目标类型", example = "step")
    private String goalType;

    @NotNull @Schema(description = "目标值", example = "10000")
    private BigDecimal targetValue;

    @Schema(description = "单位", example = "步")
    private String unit;

    @Schema(description = "周期", example = "daily")
    private String period;
}
