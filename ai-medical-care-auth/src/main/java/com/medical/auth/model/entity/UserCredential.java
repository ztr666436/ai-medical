package com.medical.auth.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户认证凭证实体
 *
 * @author Architect Team
 */
@Data
@TableName("user_credential")
public class UserCredential {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 手机号（可选，明文存储） */
    private String phone;

    /** 邮箱（AES 加密存储） */
    private String email;

    /** BCrypt 密码 */
    private String passwordHash;

    /** 状态: 1-正常, 0-禁用 */
    private Integer status;

    /** 最后登录时间 */
    private LocalDateTime lastLoginAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 逻辑删除 */
    @TableLogic
    private Integer deleted;
}
