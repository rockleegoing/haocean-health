-- ============================================
-- 脚本：V1.3.0__phrase_category_table.sql
-- 版本：1.3.0
-- 日期：2026-04-28
-- 描述：创建用语分类表
-- ============================================

CREATE TABLE sys_phrase_category (
    category_id    BIGINT(20)      NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    category_name  VARCHAR(100)    NOT NULL                  COMMENT '分类名称',
    sort           INT(11)         NOT NULL DEFAULT 0        COMMENT '排序',
    status         CHAR(1)         NOT NULL DEFAULT '0'      COMMENT '状态（0正常 1停用）',
    create_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用语分类表';
