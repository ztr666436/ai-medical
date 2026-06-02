package com.medical.goal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.goal.model.entity.HealthGoal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 健康目标 Mapper
 *
 * @author Architect Team
 */
@Mapper
public interface HealthGoalMapper extends BaseMapper<HealthGoal> {

    @Select("SELECT * FROM health_goal WHERE user_id = #{userId} AND status = 'active' AND deleted = 0")
    List<HealthGoal> selectActiveByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM health_goal WHERE user_id = #{userId} AND goal_type = #{type} AND status = 'active' AND deleted = 0 LIMIT 1")
    HealthGoal selectActiveByType(@Param("userId") Long userId, @Param("type") String type);
}
