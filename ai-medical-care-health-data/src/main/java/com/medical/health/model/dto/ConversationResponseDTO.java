package com.medical.health.model.dto;

import com.medical.health.model.entity.HealthRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对话式录入响应
 * <p>
 * 包含 AI 解析结果、结构化数据和回复文本
 *
 * @author Architect Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "对话式录入响应")
public class ConversationResponseDTO {

    @Schema(description = "AI 助手的回复文本", example = "收到！已为您记录：今日步数 9000 步，体重 70kg。继续保持哦～")
    private String reply;

    @Schema(description = "解析出的健康数据记录列表")
    private List<HealthRecordVO> records;

    @Schema(description = "是否全部解析成功")
    private boolean allParsed;

    @Schema(description = "是否包含需确认的异常数据")
    private boolean hasWarning;

    // ==================== 内部类 ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "单条健康数据")
    public static class HealthRecordVO {
        private String type;          // step/weight/blood_pressure
        private String value;         // 9000
        private String unit;          // 步/kg
        private Double confidence;    // AI 置信度
        private String note;          // AI 备注
    }
}
