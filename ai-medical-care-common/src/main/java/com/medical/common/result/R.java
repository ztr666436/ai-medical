package com.medical.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应体
 * <p>
 * 所有微服务接口统一使用此格式返回数据，
 * 前端根据 code 字段判断请求是否成功。
 *
 * @author Architect Team
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码：200-成功, 400-参数错误, 401-未认证, 403-无权限, 500-系统异常 */
    private int code;

    /** 提示信息 */
    private String message;

    /** 响应数据 */
    private T data;

    /** 时间戳 */
    private long timestamp;

    // ==================== 成功响应 ====================

    public static <T> R<T> ok() {
        return new R<>(ResultCode.SUCCESS.getCode(), "success", null, System.currentTimeMillis());
    }

    public static <T> R<T> ok(T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), "success", data, System.currentTimeMillis());
    }

    public static <T> R<T> ok(String message, T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), message, data, System.currentTimeMillis());
    }

    // ==================== 失败响应 ====================

    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null, System.currentTimeMillis());
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return new R<>(resultCode.getCode(), resultCode.getMessage(), null, System.currentTimeMillis());
    }

    public static <T> R<T> fail(ResultCode resultCode, String message) {
        return new R<>(resultCode.getCode(), message, null, System.currentTimeMillis());
    }

    // ==================== 常用快捷方法 ====================

    /** 参数错误 */
    public static <T> R<T> paramError(String message) {
        return fail(ResultCode.PARAM_ERROR, message);
    }

    /** 未登录 */
    public static <T> R<T> unauthorized() {
        return fail(ResultCode.UNAUTHORIZED);
    }

    /** 无权限 */
    public static <T> R<T> forbidden() {
        return fail(ResultCode.FORBIDDEN);
    }

    /** 系统异常 */
    public static <T> R<T> systemError(String message) {
        return fail(ResultCode.SYSTEM_ERROR, message);
    }
}
