package com.medical.common.constant;

/**
 * 系统常量
 *
 * @author Architect Team
 */
public final class SystemConstants {

    private SystemConstants() {}

    // ==================== Token 相关 ====================

    /** 请求头中 Token 的 key */
    public static final String TOKEN_HEADER = "Authorization";

    /** Token 前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** 用户 ID 上下文 key */
    public static final String USER_ID_CONTEXT = "userId";

    /** 用户名上下文 key */
    public static final String USER_USERNAME_CONTEXT = "username";

    // ==================== Redis Key 前缀 ====================

    /** Token 黑名单前缀 */
    public static final String REDIS_TOKEN_BLACKLIST = "token:blacklist:";

    /** 用户 Token 缓存前缀 */
    public static final String REDIS_USER_TOKEN = "user:token:";

    // ==================== 健康数据类型 ====================

    /** 步数 */
    public static final String HEALTH_TYPE_STEP = "step";

    /** 体重 */
    public static final String HEALTH_TYPE_WEIGHT = "weight";

    /** 血压 */
    public static final String HEALTH_TYPE_BLOOD_PRESSURE = "blood_pressure";

    /** 血糖 */
    public static final String HEALTH_TYPE_BLOOD_SUGAR = "blood_sugar";

    /** 心率 */
    public static final String HEALTH_TYPE_HEART_RATE = "heart_rate";

    /** 睡眠时长 */
    public static final String HEALTH_TYPE_SLEEP = "sleep";

    // ==================== 目标周期 ====================

    /** 每日目标 */
    public static final String GOAL_PERIOD_DAILY = "daily";

    /** 每周目标 */
    public static final String GOAL_PERIOD_WEEKLY = "weekly";

    /** 每月目标 */
    public static final String GOAL_PERIOD_MONTHLY = "monthly";

    // ==================== 目标状态 ====================

    /** 活跃 */
    public static final String GOAL_STATUS_ACTIVE = "active";

    /** 已达成 */
    public static final String GOAL_STATUS_ACHIEVED = "achieved";

    /** 已过期 */
    public static final String GOAL_STATUS_EXPIRED = "expired";

    // ==================== AI Agent ====================

    /** AI 单次解析超时（秒） */
    public static final int AI_PARSE_TIMEOUT = 2;

    /** AI 复杂推理超时（秒） */
    public static final int AI_REASONING_TIMEOUT = 15;

    /** 对话上下文最大轮数 */
    public static final int MAX_CONVERSATION_ROUNDS = 10;
}
