package com.medical.common.exception;

import com.medical.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 * <p>
 * 所有业务层异常统一使用此类抛出，
 * 由 GlobalExceptionHandler 统一捕获并返回前端。
 *
 * @author Architect Team
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 异常码 */
    private final int code;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 快捷创建：参数错误
     */
    public static BusinessException paramError(String message) {
        return new BusinessException(ResultCode.PARAM_ERROR, message);
    }

    /**
     * 快捷创建：用户不存在
     */
    public static BusinessException userNotFound() {
        return new BusinessException(ResultCode.USER_NOT_FOUND);
    }

    /**
     * 快捷创建：未登录
     */
    public static BusinessException unauthorized() {
        return new BusinessException(ResultCode.UNAUTHORIZED);
    }
}
