package com.medical.common.utils;

/**
 * 用户上下文（ThreadLocal）
 * <p>
 * 在请求链路中传递当前用户信息，网关校验 Token 后注入，
 * 后续微服务通过 Feign 拦截器传递。
 *
 * @author Architect Team
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME_HOLDER = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }

    public static void setUsername(String username) {
        USERNAME_HOLDER.set(username);
    }

    public static String getUsername() {
        return USERNAME_HOLDER.get();
    }

    /**
     * 清理上下文（每个请求结束后必须调用）
     */
    public static void clear() {
        USER_ID_HOLDER.remove();
        USERNAME_HOLDER.remove();
    }
}
