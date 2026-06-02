package com.medical.health.controller;

import com.medical.health.model.dto.ConversationInputDTO;
import com.medical.health.model.dto.ConversationResponseDTO;
import com.medical.health.model.entity.HealthRecord;
import com.medical.health.service.HealthRecordService;
import com.medical.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康数据接口
 *
 * @author Architect Team
 */
@Tag(name = "健康数据", description = "对话式录入、CRUD、统计查询")
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    @Operation(summary = "对话式录入健康数据", description = "输入自然语言，AI 自动解析并记录")
    @PostMapping("/conversation")
    public R<ConversationResponseDTO> recordByConversation(
            @Valid @RequestBody ConversationInputDTO input,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        input.setUserId(userId);
        return healthRecordService.recordByConversation(input);
    }

    @Operation(summary = "直接录入健康数据")
    @PostMapping("/record")
    public R<HealthRecord> recordDirectly(@RequestBody HealthRecord record,
                                           @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        record.setUserId(userId);
        return healthRecordService.recordDirectly(record);
    }

    @Operation(summary = "查询当日健康数据")
    @GetMapping("/today")
    public R<List<HealthRecord>> queryToday(@RequestHeader("X-User-Id") Long userId) {
        return healthRecordService.queryByDate(userId, LocalDateTime.now());
    }

    @Operation(summary = "查询指定日期数据")
    @GetMapping("/by-date")
    public R<List<HealthRecord>> queryByDate(@RequestHeader("X-User-Id") Long userId,
                                              @RequestParam String date) {
        LocalDateTime dateTime = LocalDate.parse(date).atStartOfDay();
        return healthRecordService.queryByDate(userId, dateTime);
    }

    @Operation(summary = "获取近7天趋势数据")
    @GetMapping("/trend")
    public R<List<HealthRecord>> getWeeklyTrend(@RequestHeader("X-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "step") String type) {
        return healthRecordService.getWeeklyTrend(userId, type);
    }

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public R<String> health() {
        return R.ok("Health Data Service is running");
    }
}
