package com.medical.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一返回码枚举
 *
 * @author Architect Team
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或 Token 已过期"),
    FORBIDDEN(403, "无访问权限"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    CONFLICT(409, "数据冲突"),
    SYSTEM_ERROR(500, "系统内部异常"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    // 业务错误码 (1xxxx)
    USER_NOT_FOUND(10001, "用户不存在"),
    USER_PASSWORD_ERROR(10002, "密码错误"),
    USER_ALREADY_EXISTS(10003, "用户已存在"),
    USERNAME_ALREADY_EXISTS(10004, "用户名已注册"),
    EMAIL_ALREADY_EXISTS(10005, "邮箱已注册"),

    TOKEN_EXPIRED(10008, "Token 已过期，请刷新"),
    TOKEN_INVALID(10009, "Token 无效"),
    REFRESH_TOKEN_EXPIRED(10010, "Refresh Token 已过期，请重新登录"),

    // 健康数据 (2xxxx)
    HEALTH_DATA_NOT_FOUND(20001, "健康数据不存在"),
    HEALTH_RECORD_CONFLICT(20002, "该时段已有记录"),
    AI_PARSE_FAILED(20003, "AI 解析健康数据失败，请重新输入"),

    // 目标管理 (3xxxx)
    GOAL_NOT_FOUND(30001, "目标不存在"),
    GOAL_ALREADY_ACHIEVED(30002, "目标已达成"),
    GOAL_CONFLICT(30003, "该类型已有活跃目标"),

    // AI Agent (4xxxx)
    AI_TIMEOUT(40001, "AI 响应超时，请稍后重试"),
    AI_SERVICE_ERROR(40002, "AI 服务异常"),
    AI_CONTEXT_TOO_LONG(40003, "对话上下文过长，请开启新对话");

    private final int code;
    private final String message;
}
