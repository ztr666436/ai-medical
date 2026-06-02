package com.medical.goal.model.dto;

import com.medical.goal.model.entity.HealthGoal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 目标推荐响应
 *
 * @author Architect Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI 目标推荐响应")
public class GoalRecommendationDTO {

    @Schema(description = "推荐目标")
    private HealthGoalGoalVO recommended;

    @Schema(description = "推荐理由")
    private String reason;

    @Schema(description = "算法来源", example = "AI/规则")
    private String source;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "目标值快照")
    public static class HealthGoalGoalVO {
        private String goalType;
        private BigDecimal targetValue;
        private String unit;
        private String period;
    }
}
