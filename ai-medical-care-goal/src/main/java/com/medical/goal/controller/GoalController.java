package com.medical.goal.controller;

import com.medical.common.result.R;
import com.medical.goal.model.dto.GoalDTO;
import com.medical.goal.model.dto.GoalRecommendationDTO;
import com.medical.goal.model.entity.HealthGoal;
import com.medical.goal.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 健康目标接口
 *
 * @author Architect Team
 */
@Tag(name = "健康目标", description = "目标推荐、CRUD、进度")
@RestController
@RequestMapping("/api/goal")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @Operation(summary = "AI推荐健康目标")
    @GetMapping("/recommend")
    public R<GoalRecommendationDTO> recommend(@RequestHeader("X-User-Id") Long userId,
                                               @RequestParam(defaultValue = "step") String type) {
        return goalService.recommendGoal(userId, type);
    }

    @Operation(summary = "创建/更新目标")
    @PostMapping
    public R<HealthGoal> saveGoal(@RequestHeader("X-User-Id") Long userId,
                                   @Valid @RequestBody GoalDTO dto) {
        return goalService.createOrUpdateGoal(userId, dto);
    }

    @Operation(summary = "获取活跃目标列表")
    @GetMapping
    public R<List<HealthGoal>> getActiveGoals(@RequestHeader("X-User-Id") Long userId) {
        return goalService.getActiveGoals(userId);
    }

    @Operation(summary = "手动完成目标")
    @PutMapping("/{goalId}/achieve")
    public R<HealthGoal> achieveGoal(@PathVariable Long goalId) {
        return goalService.achieveGoal(goalId);
    }

    @Operation(summary = "获取进度报告")
    @GetMapping("/report")
    public R<String> getReport(@RequestHeader("X-User-Id") Long userId) {
        return goalService.getProgressReport(userId);
    }
}
