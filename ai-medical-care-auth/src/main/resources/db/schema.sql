-- =============================================
-- ai-medical-care-agents 数据库初始化脚本
-- 认证授权模块 (auth-service)
-- =============================================

CREATE DATABASE IF NOT EXISTS ai_medical_care
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE ai_medical_care;

-- ==================== 用户认证凭证表 ====================
DROP TABLE IF EXISTS user_credential;
CREATE TABLE user_credential (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY  COMMENT '主键ID',
    username        VARCHAR(50)     UNIQUE NOT NULL             COMMENT '用户名',
    phone           VARCHAR(20)     DEFAULT NULL                COMMENT '手机号（可选）',
    email           VARCHAR(200)    UNIQUE                      COMMENT '邮箱（可选）',
    password_hash   VARCHAR(255)    NOT NULL                    COMMENT 'BCrypt 加密密码',
    status          TINYINT         DEFAULT 1                   COMMENT '状态: 1-正常, 0-禁用',
    last_login_at   DATETIME                                    COMMENT '最后登录时间',
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除: 1-已删除',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户认证凭证表';
