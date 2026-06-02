package com.medical.auth.service.impl;

import com.medical.auth.mapper.UserCredentialMapper;
import com.medical.auth.model.dto.*;
import com.medical.auth.model.entity.UserCredential;
import com.medical.auth.service.AuthService;
import com.medical.common.constant.SystemConstants;
import com.medical.common.exception.BusinessException;
import com.medical.common.result.R;
import com.medical.common.result.ResultCode;
import com.medical.common.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 * <p>
 * 核心流程：
 * 1. 注册：检查用户名唯一 → BCrypt 加密密码 → 写入数据库 → 签发 Token
 * 2. 登录：查询用户名 → 验证密码 → 签发 Token → 更新最后登录时间
 * 3. 刷新：校验 Refresh Token → 签发新的 Token 对 → 旧 Token 加入黑名单
 * 4. 登出：Access Token 加入 Redis 黑名单
 *
 * @author Architect Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserCredentialMapper userCredentialMapper;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;

    // ==================== 注册 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<TokenDTO> register(RegisterDTO dto) {
        // 1. 检查用户名是否已注册
        UserCredential existUser = userCredentialMapper.selectByUsername(dto.getUsername());
        if (existUser != null) {
            throw new BusinessException(ResultCode.USERNAME_ALREADY_EXISTS);
        }

        // 2. BCrypt 加密密码
        String passwordHash = passwordEncoder.encode(dto.getPassword());

        // 3. 创建用户
        UserCredential newUser = new UserCredential();
        newUser.setUsername(dto.getUsername());
        newUser.setPasswordHash(passwordHash);
        newUser.setStatus(1);
        userCredentialMapper.insert(newUser);

        log.info("[注册成功] userId={}, username={}", newUser.getId(), dto.getUsername());

        // 4. 签发 Token
        String accessToken = JwtUtil.generateAccessToken(newUser.getId(), dto.getUsername());
        String refreshToken = JwtUtil.generateRefreshToken(newUser.getId(), dto.getUsername());

        // 5. 缓存到 Redis
        cacheToken(newUser.getId(), accessToken);

        return R.ok(TokenDTO.of(accessToken, refreshToken,
                newUser.getId(), dto.getUsername()));
    }

    // ==================== 登录 ====================

    @Override
    public R<TokenDTO> login(LoginDTO dto) {
        // 1. 查询用户
        UserCredential user = userCredentialMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "用户名未注册");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        // 2. BCrypt 密码校验
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 3. 签发 Token
        String accessToken = JwtUtil.generateAccessToken(user.getId(), dto.getUsername());
        String refreshToken = JwtUtil.generateRefreshToken(user.getId(), dto.getUsername());

        // 4. 缓存 Token
        cacheToken(user.getId(), accessToken);

        // 5. 更新最后登录时间
        UserCredential updateEntity = new UserCredential();
        updateEntity.setId(user.getId());
        updateEntity.setLastLoginAt(LocalDateTime.now());
        userCredentialMapper.updateById(updateEntity);

        log.info("[登录成功] userId={}, username={}", user.getId(), dto.getUsername());

        return R.ok(TokenDTO.of(accessToken, refreshToken,
                user.getId(), dto.getUsername()));
    }

    // ==================== 刷新 Token ====================

    @Override
    public R<TokenDTO> refreshToken(RefreshTokenDTO dto) {
        // 1. 校验 Refresh Token
        if (!JwtUtil.validateToken(dto.getRefreshToken())) {
            throw new BusinessException(ResultCode.REFRESH_TOKEN_EXPIRED);
        }
        if (!JwtUtil.isRefreshToken(dto.getRefreshToken())) {
            throw new BusinessException(ResultCode.TOKEN_INVALID, "非 Refresh Token");
        }

        // 2. 提取用户信息
        Long userId = JwtUtil.getUserId(dto.getRefreshToken());
        String username = JwtUtil.getUsername(dto.getRefreshToken());

        // 3. 生成新 Token
        String newAccessToken = JwtUtil.generateAccessToken(userId, username);
        String newRefreshToken = JwtUtil.generateRefreshToken(userId, username);

        // 4. 旧的 Refresh Token 加入黑名单
        redisTemplate.opsForValue().set(
                SystemConstants.REDIS_TOKEN_BLACKLIST + dto.getRefreshToken(),
                "1",
                JwtUtil.getRefreshExpireMillis(),
                TimeUnit.MILLISECONDS
        );

        cacheToken(userId, newAccessToken);

        log.info("[Token刷新] userId={}", userId);
        return R.ok(TokenDTO.of(newAccessToken, newRefreshToken, userId, username));
    }

    // ==================== 登出 ====================

    @Override
    public void logout(String accessToken, Long userId) {
        // 将 Access Token 加入黑名单
        redisTemplate.opsForValue().set(
                SystemConstants.REDIS_TOKEN_BLACKLIST + accessToken,
                "1",
                JwtUtil.getAccessExpireMillis(),
                TimeUnit.MILLISECONDS
        );

        // 删除缓存的 Token
        redisTemplate.delete(SystemConstants.REDIS_USER_TOKEN + userId);

        log.info("[登出] userId={}", userId);
    }

    // ==================== 私有方法 ====================

    /**
     * 缓存 Token 到 Redis
     */
    private void cacheToken(Long userId, String accessToken) {
        redisTemplate.opsForValue().set(
                SystemConstants.REDIS_USER_TOKEN + userId,
                accessToken,
                JwtUtil.getAccessExpireMillis(),
                TimeUnit.MILLISECONDS
        );
    }
}
