package com.medical.auth.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 响应
 *
 * @author Architect Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token 响应")
public class TokenDTO {

    @Schema(description = "Access Token（2小时有效）")
    private String accessToken;

    @Schema(description = "Refresh Token（7天有效）")
    private String refreshToken;

    @Schema(description = "Access Token 过期时间（秒）", example = "7200")
    private long expiresIn;

    @Schema(description = "Token 类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    public static TokenDTO of(String accessToken, String refreshToken, Long userId, String username) {
        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(7200L)
                .tokenType("Bearer")
                .userId(userId)
                .username(username)
                .build();
    }
}
