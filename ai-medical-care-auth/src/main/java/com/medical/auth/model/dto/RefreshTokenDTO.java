package com.medical.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Token 刷新请求
 *
 * @author Architect Team
 */
@Data
@Schema(description = "Token 刷新请求")
public class RefreshTokenDTO {

    @NotBlank(message = "Refresh Token 不能为空")
    @Schema(description = "Refresh Token", example = "eyJhbGciOi...")
    private String refreshToken;
}
