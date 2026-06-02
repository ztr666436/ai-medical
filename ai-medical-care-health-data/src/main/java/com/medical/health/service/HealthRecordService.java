package com.medical.health.service;

import com.medical.health.model.dto.ConversationInputDTO;
import com.medical.health.model.dto.ConversationResponseDTO;
import com.medical.health.model.entity.HealthRecord;
import com.medical.common.result.R;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康数据服务接口
 *
 * @author Architect Team
 */
public interface HealthRecordService {

    /**
     * 对话式录入健康数据
     * <p>
     * 用户输入自然语言 → AI Agent 解析 → 写入数据库 → 发布事件
     */
    R<ConversationResponseDTO> recordByConversation(ConversationInputDTO input);

    /**
     * 直接插入健康数据（非对话式）
     */
    R<HealthRecord> recordDirectly(HealthRecord record);

    /**
     * 查询用户某天的健康数据
     */
    R<List<HealthRecord>> queryByDate(Long userId, LocalDateTime date);

    /**
     * 查询用户近 N 天某类型数据
     */
    R<List<HealthRecord>> queryByType(Long userId, String recordType, int days);

    /**
     * 获取近 7 天趋势数据（供前端图表）
     */
    R<List<HealthRecord>> getWeeklyTrend(Long userId, String recordType);
}
