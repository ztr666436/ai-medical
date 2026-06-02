package com.medical.health.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 对话式录入请求
 * <p>
 * 用户通过自然语言输入，由 AI Agent 解析为结构化数据
 *
 * @author Architect Team
 */
@Data
@Schema(description = "对话式健康数据录入请求")
public class ConversationInputDTO {

    @NotBlank(message = "请输入健康数据")
    @Schema(description = "用户自然语言输入", example = "今天走了9000步，体重70kg")
    private String message;

    @Schema(description = "用户ID（网关注入）", hidden = true)
    private Long userId;
}
