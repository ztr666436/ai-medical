package com.medical.goal.service;

import com.medical.common.result.R;
import com.medical.goal.model.dto.GoalDTO;
import com.medical.goal.model.dto.GoalRecommendationDTO;
import com.medical.goal.model.entity.HealthGoal;

import java.util.List;

/**
 * 健康目标服务接口
 *
 * @author Architect Team
 */
public interface GoalService {

    /** AI 推荐健康目标 */
    R<GoalRecommendationDTO> recommendGoal(Long userId, String goalType);

    /** 创建/更新目标 */
    R<HealthGoal> createOrUpdateGoal(Long userId, GoalDTO dto);

    /** 查询活跃目标 */
    R<List<HealthGoal>> getActiveGoals(Long userId);

    /** 完成目标 */
    R<HealthGoal> achieveGoal(Long goalId);

    /** 获取进度报告 */
    R<String> getProgressReport(Long userId);
}
