package com.medical.auth.controller;

import com.medical.auth.model.dto.*;
import com.medical.auth.service.AuthService;
import com.medical.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证授权接口
 *
 * @author Architect Team
 */
@Tag(name = "认证授权", description = "注册、登录、Token 管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<TokenDTO> register(@Valid @RequestBody RegisterDTO dto) {
        return authService.register(dto);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<TokenDTO> login(@Valid @RequestBody LoginDTO dto) {
        return authService.login(dto);
    }

    @Operation(summary = "刷新 Token")
    @PostMapping("/refresh")
    public R<TokenDTO> refreshToken(@Valid @RequestBody RefreshTokenDTO dto) {
        return authService.refreshToken(dto);
    }

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public R<String> health() {
        return R.ok("Auth Service is running - 认证服务运行正常");
    }
}
