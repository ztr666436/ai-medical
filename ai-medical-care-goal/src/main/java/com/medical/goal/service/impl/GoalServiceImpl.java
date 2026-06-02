package com.medical.goal.service.impl;

import com.medical.common.result.R;
import com.medical.common.result.ResultCode;
import com.medical.common.exception.BusinessException;
import com.medical.goal.mapper.HealthGoalMapper;
import com.medical.goal.model.dto.GoalDTO;
import com.medical.goal.model.dto.GoalRecommendationDTO;
import com.medical.goal.model.entity.HealthGoal;
import com.medical.goal.service.GoalService;
import com.medical.common.event.HealthDataUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * 健康目标服务实现
 * <p>
 * 核心：
 * 1. AI/规则 推荐目标（基于近期健康数据）
 * 2. 目标 CRUD
 * 3. 监听 HealthDataUpdatedEvent → 自动检查目标进度
 *
 * @author Architect Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final HealthGoalMapper healthGoalMapper;

    // ==================== AI 推荐目标 ====================

    @Override
    public R<GoalRecommendationDTO> recommendGoal(Long userId, String goalType) {
        // 基于规则推荐目标（后续接入 AI Agent 生成个性化目标）
        GoalRecommendationDTO recommendation = buildRecommendation(userId, goalType);

        // 默认不自动保存，由用户确认后调用 createOrUpdateGoal
        return R.ok(recommendation);
    }

    // ==================== 创建/更新目标 ====================

    @Override
    @Transactional
    public R<HealthGoal> createOrUpdateGoal(Long userId, GoalDTO dto) {
        // 检查是否已有同类型活跃目标
        HealthGoal existing = healthGoalMapper.selectActiveByType(userId, dto.getGoalType());
        if (existing != null) {
            // 更新现有目标
            existing.setTargetValue(dto.getTargetValue());
            existing.setUnit(dto.getUnit());
            existing.setUpdatedAt(null); // MyBatis-Plus auto-fill
            healthGoalMapper.updateById(existing);
            log.info("[目标更新] userId={}, type={}, value={}", userId, dto.getGoalType(), dto.getTargetValue());
            return R.ok(existing);
        }

        // 创建新目标
        HealthGoal goal = new HealthGoal();
        goal.setUserId(userId);
        goal.setGoalType(dto.getGoalType());
        goal.setTargetValue(dto.getTargetValue());
        goal.setUnit(dto.getUnit() != null ? dto.getUnit() : getDefaultUnit(dto.getGoalType()));
        goal.setPeriod(dto.getPeriod() != null ? dto.getPeriod() : "daily");
        goal.setStatus("active");
        goal.setAiRecommended(false);
        goal.setStartedAt(LocalDate.now());
        goal.setEndAt(LocalDate.now().plusDays(30)); // 默认30天有效
        healthGoalMapper.insert(goal);

        log.info("[目标创建] userId={}, goalId={}, type={}, target={}",
                userId, goal.getId(), dto.getGoalType(), dto.getTargetValue());
        return R.ok(goal);
    }

    // ==================== 查询 ====================

    @Override
    public R<List<HealthGoal>> getActiveGoals(Long userId) {
        List<HealthGoal> goals = healthGoalMapper.selectActiveByUserId(userId);
        return R.ok(goals);
    }

    // ==================== 完成目标 ====================

    @Override
    @Transactional
    public R<HealthGoal> achieveGoal(Long goalId) {
        HealthGoal goal = healthGoalMapper.selectById(goalId);
        if (goal == null) throw new BusinessException(ResultCode.GOAL_NOT_FOUND);
        goal.setStatus("achieved");
        healthGoalMapper.updateById(goal);
        log.info("[目标达成] goalId={}, type={}", goalId, goal.getGoalType());
        return R.ok(goal);
    }

    // ==================== 进度报告 ====================

    @Override
    public R<String> getProgressReport(Long userId) {
        List<HealthGoal> activeGoals = healthGoalMapper.selectActiveByUserId(userId);
        if (activeGoals.isEmpty()) return R.ok("您还没有设置健康目标，快去设定一个吧～");

        StringBuilder report = new StringBuilder("📊 当前目标进度：\n");
        for (HealthGoal g : activeGoals) {
            report.append(String.format("  %s: %.0f/%d %s\n",
                    getGoalEmoji(g.getGoalType()), g.getTargetValue(), g.getTargetValue().intValue(), g.getUnit()));
        }
        return R.ok(report.toString());
    }

    // ==================== 事件监听：健康数据更新 → 检查目标进度 ====================

    @Async
    @EventListener
    public void onHealthDataUpdated(HealthDataUpdatedEvent event) {
        log.info("[事件监听] 收到健康数据更新: userId={}, type={}, value={}",
                event.getUserId(), event.getRecordType(), event.getRecordValue());

        // 查找同类型活跃目标
        HealthGoal goal = healthGoalMapper.selectActiveByType(event.getUserId(), event.getRecordType());
        if (goal == null) return; // 没有相关目标

        // 检查是否达到目标
        if (event.getRecordValue().compareTo(goal.getTargetValue()) >= 0) {
            goal.setStatus("achieved");
            healthGoalMapper.updateById(goal);
            log.info("[目标自动达成] userId={}, type={}, value={}, target={}",
                    event.getUserId(), event.getRecordType(),
                    event.getRecordValue(), goal.getTargetValue());
        } else {
            // 计算进度百分比
            BigDecimal progress = event.getRecordValue()
                    .divide(goal.getTargetValue(), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            log.info("[目标进度] userId={}, type={}, progress={}%",
                    event.getUserId(), event.getRecordType(), progress);
        }
    }

    // ==================== 私有方法 ====================

    private GoalRecommendationDTO buildRecommendation(Long userId, String goalType) {
        // 基于规则推荐（后续接入 DeepSeek AI）
        GoalRecommendationDTO.HealthGoalGoalVO vo = switch (goalType) {
            case "step" -> GoalRecommendationDTO.HealthGoalGoalVO.builder()
                    .goalType("step").targetValue(new BigDecimal("10000")).unit("步").period("daily").build();
            case "weight" -> GoalRecommendationDTO.HealthGoalGoalVO.builder()
                    .goalType("weight").targetValue(new BigDecimal("65")).unit("kg").period("weekly").build();
            case "blood_pressure" -> GoalRecommendationDTO.HealthGoalGoalVO.builder()
                    .goalType("blood_pressure").targetValue(new BigDecimal("120")).unit("mmHg").period("daily").build();
            default -> GoalRecommendationDTO.HealthGoalGoalVO.builder()
                    .goalType(goalType).targetValue(new BigDecimal("100")).unit("").period("daily").build();
        };

        return GoalRecommendationDTO.builder()
                .recommended(vo)
                .reason(getRecommendationReason(goalType))
                .source("规则引擎（后续接入 AI）")
                .build();
    }

    private String getRecommendationReason(String type) {
        return switch (type) {
            case "step" -> "根据世界卫生组织建议，每日 10000 步有助于保持心血管健康";
            case "weight" -> "基于 BMI 健康范围推荐，65kg 为您的理想体重参考值";
            case "blood_pressure" -> "建议将收缩压控制在 120mmHg 以下，保持心血管健康";
            default -> "基于标准健康指标推荐";
        };
    }

    private String getDefaultUnit(String type) {
        return switch (type) {
            case "step" -> "步"; case "weight" -> "kg";
            case "blood_pressure" -> "mmHg"; case "blood_sugar" -> "mmol/L";
            case "heart_rate" -> "bpm";
            default -> "";
        };
    }

    private String getGoalEmoji(String type) {
        return switch (type) {
            case "step" -> "🚶"; case "weight" -> "⚖️";
            case "blood_pressure" -> "🩺"; case "blood_sugar" -> "🍬";
            default -> "🎯";
        };
    }
}
