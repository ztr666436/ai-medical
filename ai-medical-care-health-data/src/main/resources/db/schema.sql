-- =============================================
-- 健康数据表
-- =============================================
DROP TABLE IF EXISTS health_record;
CREATE TABLE health_record (
    id              BIGINT          AUTO_INCREMENT PRIMARY KEY  COMMENT '主键ID',
    user_id         BIGINT          NOT NULL                    COMMENT '用户ID',
    record_type     VARCHAR(20)     NOT NULL                    COMMENT '类型: step/weight/blood_pressure/blood_sugar/heart_rate/sleep',
    record_value    DECIMAL(10,2)   NOT NULL                    COMMENT '数值',
    unit            VARCHAR(20)                                 COMMENT '单位: step/kg/mmHg/mmol/L/bpm/h',
    recorded_at     DATETIME                                    COMMENT '用户声称的记录时间',
    source_text     TEXT                                        COMMENT '用户原始输入文本',
    ai_confidence   DECIMAL(3,2)                                COMMENT 'AI 抽取置信度 (0.00-1.00)',
    ai_notes        VARCHAR(500)                                COMMENT 'AI 备注（如异常提示）',
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    updated_at      DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除',
    INDEX idx_user_time (user_id, recorded_at),
    INDEX idx_user_type (user_id, record_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康数据记录表';
