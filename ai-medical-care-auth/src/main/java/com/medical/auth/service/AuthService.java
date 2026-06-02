package com.medical.auth.service;

import com.medical.auth.model.dto.*;
import com.medical.common.result.R;

/**
 * 认证服务接口
 *
 * @author Architect Team
 */
public interface AuthService {

    /**
     * 用户注册
     * @param dto 注册信息（用户名、密码）
     * @return 注册结果
     */
    R<TokenDTO> register(RegisterDTO dto);

    /**
     * 用户登录
     * @param dto 登录信息（用户名、密码）
     * @return Token 信息
     */
    R<TokenDTO> login(LoginDTO dto);

    /**
     * 刷新 Token
     * @param dto Refresh Token
     * @return 新的 Token 对
     */
    R<TokenDTO> refreshToken(RefreshTokenDTO dto);

    /**
     * 用户登出（将 Token 加入黑名单）
     * @param accessToken Access Token
     * @param userId 用户 ID
     */
    void logout(String accessToken, Long userId);
}
