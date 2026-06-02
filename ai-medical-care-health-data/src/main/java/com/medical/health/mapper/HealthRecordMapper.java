package com.medical.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.health.model.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康数据 Mapper
 *
 * @author Architect Team
 */
@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {

    /** 查询用户某天的记录 */
    @Select("SELECT * FROM health_record WHERE user_id = #{userId} AND DATE(recorded_at) = DATE(#{date}) AND deleted = 0 ORDER BY recorded_at DESC")
    List<HealthRecord> selectByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDateTime date);

    /** 统计近7天某类型数据的平均值 */
    @Select("SELECT AVG(record_value) FROM health_record WHERE user_id = #{userId} AND record_type = #{type} AND recorded_at >= #{startDate} AND deleted = 0")
    BigDecimal selectAvgByType(@Param("userId") Long userId, @Param("type") String type, @Param("startDate") LocalDateTime startDate);

    /** 查询用户最新一条某类型记录 */
    @Select("SELECT * FROM health_record WHERE user_id = #{userId} AND record_type = #{type} AND deleted = 0 ORDER BY recorded_at DESC LIMIT 1")
    HealthRecord selectLatestByType(@Param("userId") Long userId, @Param("type") String type);
}
