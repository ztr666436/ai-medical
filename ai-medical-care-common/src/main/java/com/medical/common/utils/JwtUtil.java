package com.medical.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 * <p>
 * 负责 Token 的生成、解析与校验。
 * Access Token 有效期 2 小时，Refresh Token 有效期 7 天。
 *
 * @author Architect Team
 */
@Slf4j
public class JwtUtil {

    /** 签名密钥（生产环境应放在配置中心） */
    private static final String SECRET = "ai-medical-care-agents-jwt-secret-key-must-be-256-bits-long!!";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    /** Access Token 有效期：2 小时 */
    private static final long ACCESS_EXPIRE = 2 * 60 * 60 * 1000L;

    /** Refresh Token 有效期：7 天 */
    private static final long REFRESH_EXPIRE = 7 * 24 * 60 * 60 * 1000L;

    /** Token 签发人 */
    private static final String ISSUER = "ai-medical-care";

    // ==================== 生成 Token ====================

    /**
     * 生成 Access Token
     */
    public static String generateAccessToken(Long userId, String username) {
        return buildToken(userId, username, ACCESS_EXPIRE, "access");
    }

    /**
     * 生成 Refresh Token
     */
    public static String generateRefreshToken(Long userId, String username) {
        return buildToken(userId, username, REFRESH_EXPIRE, "refresh");
    }

    /**
     * 构建 Token
     */
    private static String buildToken(Long userId, String username, long expire, String type) {
        Date now = new Date();
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expire))
                .signWith(SECRET_KEY)
                .compact();
    }

    // ==================== 解析 Token ====================

    /**
     * 解析 Token，返回 Claims
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token 已过期: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("Token 解析失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 从 Token 中获取用户 ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从 Token 中获取用户名
     */
    public static String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 校验 Token 是否有效
     */
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 判断是否为 Refresh Token
     */
    public static boolean isRefreshToken(String token) {
        Claims claims = parseToken(token);
        return "refresh".equals(claims.get("type"));
    }

    // ==================== 快捷方法 ====================

    /**
     * 获取 Access Token 有效期（毫秒）
     */
    public static long getAccessExpireMillis() {
        return ACCESS_EXPIRE;
    }

    /**
     * 获取 Refresh Token 有效期（毫秒）
     */
    public static long getRefreshExpireMillis() {
        return REFRESH_EXPIRE;
    }
}
