package com.medical.health.service.impl;

import com.alibaba.fastjson2.JSON;
import com.medical.common.exception.BusinessException;
import com.medical.common.result.R;
import com.medical.common.result.ResultCode;
import com.medical.health.feign.AiAgentClient;
import com.medical.health.mapper.HealthRecordMapper;
import com.medical.health.model.dto.ConversationInputDTO;
import com.medical.health.model.dto.ConversationResponseDTO;
import com.medical.health.model.entity.HealthRecord;
import com.medical.common.event.HealthDataUpdatedEvent;
import com.medical.health.service.HealthRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 健康数据服务实现
 * <p>
 * 核心流程：
 * 对话式录入 → AI Agent 解析 → 结构化入库 → 发布事件 → 返回 AI 回复
 * <p>
 * 注：当前 AI Agent 服务尚未实现，使用规则引擎作为兜底方案，
 * 后续直接切换 Feign 调用即可。
 *
 * @author Architect Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthRecordServiceImpl implements HealthRecordService {

    private final HealthRecordMapper healthRecordMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final AiAgentClient aiAgentClient;

    // ==================== 对话式录入（核心） ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<ConversationResponseDTO> recordByConversation(ConversationInputDTO input) {
        String message = input.getMessage();
        Long userId = input.getUserId();
        if (userId == null) {
            throw BusinessException.unauthorized();
        }
        log.info("[对话录入] userId={}, message={}", userId, message);

        // ====== 调用 AI Agent 解析健康数据 ======
        List<ConversationResponseDTO.HealthRecordVO> parsedRecords;
        try {
            // TODO: 切换到 Feign 调用（ai-agent-service 实现后启用）
            // R<Map<String, Object>> aiResult = aiAgentClient.parseHealthData(Map.of(
            //         "message", message,
            //         "userId", userId
            // ));
            // parsedRecords = extractFromAiResult(aiResult);

            // 当前：使用规则引擎兜底
            parsedRecords = parseWithRuleEngine(message);
        } catch (Exception e) {
            log.error("[AI解析失败] {}", e.getMessage());
            throw new BusinessException(ResultCode.AI_PARSE_FAILED);
        }

        // ====== 写入数据库 ======
        List<HealthRecord> savedRecords = new ArrayList<>();
        for (ConversationResponseDTO.HealthRecordVO vo : parsedRecords) {
            HealthRecord record = new HealthRecord();
            record.setUserId(userId);
            record.setRecordType(vo.getType());
            record.setRecordValue(new BigDecimal(vo.getValue()));
            record.setUnit(vo.getUnit());
            record.setRecordedAt(LocalDateTime.now());
            record.setSourceText(message);
            record.setAiConfidence(BigDecimal.valueOf(vo.getConfidence()));
            record.setAiNotes(vo.getNote());
            healthRecordMapper.insert(record);
            savedRecords.add(record);

            // ====== 发布事件（通知 goal-service 检查目标进度） ======
            eventPublisher.publishEvent(HealthDataUpdatedEvent.builder()
                    .userId(userId)
                    .recordType(vo.getType())
                    .recordValue(new BigDecimal(vo.getValue()))
                    .recordedAt(LocalDateTime.now())
                    .eventTime(LocalDateTime.now())
                    .build());
        }

        // ====== 构建响应 ======
        String reply = buildReply(savedRecords);
        boolean hasWarning = parsedRecords.stream()
                .anyMatch(r -> r.getConfidence() < 0.6);

        ConversationResponseDTO response = ConversationResponseDTO.builder()
                .reply(reply)
                .records(parsedRecords)
                .allParsed(!parsedRecords.isEmpty())
                .hasWarning(hasWarning)
                .build();

        return R.ok(response);
    }

    // ==================== 直接录入 ====================

    @Override
    public R<HealthRecord> recordDirectly(HealthRecord record) {
        healthRecordMapper.insert(record);
        eventPublisher.publishEvent(HealthDataUpdatedEvent.builder()
                .userId(record.getUserId())
                .recordType(record.getRecordType())
                .recordValue(record.getRecordValue())
                .recordedAt(record.getRecordedAt())
                .eventTime(LocalDateTime.now())
                .build());
        return R.ok(record);
    }

    // ==================== 查询 ====================

    @Override
    public R<List<HealthRecord>> queryByDate(Long userId, LocalDateTime date) {
        List<HealthRecord> records = healthRecordMapper.selectByUserIdAndDate(userId, date);
        return R.ok(records);
    }

    @Override
    public R<List<HealthRecord>> queryByType(Long userId, String recordType, int days) {
        LocalDateTime start = LocalDateTime.now().minusDays(days);
        // 简化实现：查询全部后用 Java 过滤
        List<HealthRecord> all = healthRecordMapper.selectByUserIdAndDate(userId, LocalDateTime.now());
        List<HealthRecord> filtered = all.stream()
                .filter(r -> r.getRecordType().equals(recordType) && r.getRecordedAt().isAfter(start))
                .collect(Collectors.toList());
        return R.ok(filtered);
    }

    @Override
    public R<List<HealthRecord>> getWeeklyTrend(Long userId, String recordType) {
        return queryByType(userId, recordType, 7);
    }

    // ==================== 私有方法：规则引擎解析（兜底方案） ====================

    /**
     * 规则引擎兜底解析
     * <p>
     * 使用正则从自然语言中提取：步数、体重、血压、血糖、心率
     * 后续接入 AI Agent 后此方法作为降级方案
     */
    private List<ConversationResponseDTO.HealthRecordVO> parseWithRuleEngine(String message) {
        List<ConversationResponseDTO.HealthRecordVO> records = new ArrayList<>();

        // 步数：xxx步 / xxx 步
        var stepMatcher = java.util.regex.Pattern.compile("(\\d+)\\s*步").matcher(message);
        if (stepMatcher.find()) {
            records.add(ConversationResponseDTO.HealthRecordVO.builder()
                    .type("step").value(stepMatcher.group(1)).unit("步")
                    .confidence(0.85).note("识别步数").build());
        }

        // 体重：xxkg / xx公斤 / xx千克
        var weightMatcher = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(kg|公斤|千克)").matcher(message);
        if (weightMatcher.find()) {
            records.add(ConversationResponseDTO.HealthRecordVO.builder()
                    .type("weight").value(weightMatcher.group(1)).unit("kg")
                    .confidence(0.90).note("识别体重").build());
        }

        // 血压：xxx/yy (mmHg)
        var bpMatcher = java.util.regex.Pattern.compile("(\\d+)\\s*/\\s*(\\d+)\\s*(?:mmHg|毫米汞柱|血压)?").matcher(message);
        if (bpMatcher.find()) {
            records.add(ConversationResponseDTO.HealthRecordVO.builder()
                    .type("blood_pressure").value(bpMatcher.group(1)).unit("mmHg")
                    .confidence(0.80).note("收缩压").build());
            records.add(ConversationResponseDTO.HealthRecordVO.builder()
                    .type("blood_pressure").value(bpMatcher.group(2)).unit("mmHg")
                    .confidence(0.80).note("舒张压").build());
        }

        // 血糖：x.x mmol/L
        var sugarMatcher = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(?:mmol/L|血糖)").matcher(message);
        if (sugarMatcher.find()) {
            records.add(ConversationResponseDTO.HealthRecordVO.builder()
                    .type("blood_sugar").value(sugarMatcher.group(1)).unit("mmol/L")
                    .confidence(0.85).note("识别血糖").build());
        }

        // 心率：xx (bpm/次/分钟/心率)
        var hrMatcher = java.util.regex.Pattern.compile("(\\d+)\\s*(?:bpm|次/分钟|次|心率)").matcher(message);
        if (hrMatcher.find()) {
            records.add(ConversationResponseDTO.HealthRecordVO.builder()
                    .type("heart_rate").value(hrMatcher.group(1)).unit("bpm")
                    .confidence(0.85).note("识别心率").build());
        }

        if (records.isEmpty()) {
            records.add(ConversationResponseDTO.HealthRecordVO.builder()
                    .type("unknown").value("0").unit("")
                    .confidence(0.0).note("未识别到有效的健康数据，请重新输入")
                    .build());
        }

        return records;
    }

    // ==================== 私有方法：构建 AI 回复 ====================

    /**
     * 构建温馨的 AI 回复文本
     */
    private String buildReply(List<HealthRecord> records) {
        if (records.isEmpty()) {
            return "抱歉，我没有识别到有效的健康数据，请再试一次吧～例如：「今天走了8000步，体重65kg」";
        }

        StringBuilder sb = new StringBuilder("收到！已为您记录：\n");
        List<String> encouragements = new ArrayList<>();

        for (HealthRecord r : records) {
            sb.append(String.format("  • %s: %s %s\n", getTypeName(r.getRecordType()), r.getRecordValue(), r.getUnit()));
            encouragements.add(getEncouragement(r.getRecordType(), r.getRecordValue()));
        }

        sb.append("\n").append(encouragements.get(new Random().nextInt(encouragements.size())));
        return sb.toString();
    }

    private String getTypeName(String type) {
        return switch (type) {
            case "step" -> "今日步数"; case "weight" -> "体重";
            case "blood_pressure" -> "血压"; case "blood_sugar" -> "血糖";
            case "heart_rate" -> "心率"; case "sleep" -> "睡眠";
            default -> type;
        };
    }

    private String getEncouragement(String type, BigDecimal value) {
        return switch (type) {
            case "step" -> value.intValue() >= 8000 ? "🚶 步数达标，太棒了！继续保持哦～" : "🚶 还差一点就达到推荐步数了，加油！";
            case "weight" -> "⚖️ 体重记录成功，健康管理从记录开始！";
            case "blood_pressure" -> "🩺 血压数据已记录，记得定期监测哦～";
            case "blood_sugar" -> "🍬 血糖值已保存，饮食上多注意呀～";
            default -> "💪 每一次记录都是对健康的投资！";
        };
    }
}
