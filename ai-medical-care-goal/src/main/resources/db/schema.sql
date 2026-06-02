-- =============================================
-- 健康目标相关表
-- =============================================
DROP TABLE IF EXISTS goal_progress;
DROP TABLE IF EXISTS health_goal;

-- 健康目标表
CREATE TABLE health_goal (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY  COMMENT '主键ID',
    user_id         BIGINT          NOT NULL                    COMMENT '用户ID',
    goal_type       VARCHAR(20)     NOT NULL                    COMMENT '类型: step/weight/blood_pressure',
    target_value    DECIMAL(10,2)   NOT NULL                    COMMENT '目标值',
    unit            VARCHAR(10)                                 COMMENT '单位',
    period          VARCHAR(10)     DEFAULT 'daily'             COMMENT '周期: daily/weekly/monthly',
    status          VARCHAR(10)     DEFAULT 'active'            COMMENT '状态: active/achieved/expired',
    ai_recommended  TINYINT         DEFAULT 0                   COMMENT '是否 AI 推荐',
    started_at      DATE                                        COMMENT '开始日期',
    end_at          DATE                                        COMMENT '结束日期',
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT         DEFAULT 0,
    INDEX idx_user_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康目标表';

-- 目标进度表
CREATE TABLE goal_progress (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY  COMMENT '主键ID',
    goal_id         BIGINT          NOT NULL                    COMMENT '关联目标ID',
    user_id         BIGINT          NOT NULL                    COMMENT '用户ID',
    progress_value  DECIMAL(10,2)   NOT NULL                    COMMENT '当前进度值',
    percentage      DECIMAL(5,2)                                COMMENT '完成百分比',
    recorded_date   DATE            NOT NULL                    COMMENT '记录日期',
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_goal_date (goal_id, recorded_date),
    INDEX idx_user_date (user_id, recorded_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='目标进度表';
