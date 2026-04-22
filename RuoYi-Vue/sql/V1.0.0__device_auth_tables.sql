-- --------------------------------------------------------------
-- 脚本：V1.0.0__device_auth_tables.sql
-- 版本：1.0.0
-- 日期：2026-04-22
-- 描述：设备与认证模块 - 表结构初始化
-- 作者：系统开发团队
-- --------------------------------------------------------------

-- ----------------------------
-- 1. 激活码表
-- ----------------------------
DROP TABLE IF EXISTS `sys_activation_code`;
CREATE TABLE `sys_activation_code` (
    `code_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `code_value` VARCHAR(20) NOT NULL COMMENT '激活码值',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态 (0 未使用 1 已使用 2 已过期 3 已禁用)',
    `expire_time` DATETIME COMMENT '有效期',
    `bind_device_id` VARCHAR(64) COMMENT '绑定设备 ID',
    `bind_user_id` BIGINT COMMENT '绑定用户 ID',
    `remark` VARCHAR(500) COMMENT '备注',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`code_id`),
    UNIQUE KEY `uk_code_value` (`code_value`),
    KEY `idx_status` (`status`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='激活码表';

-- ----------------------------
-- 2. 设备表
-- ----------------------------
DROP TABLE IF EXISTS `sys_device`;
CREATE TABLE `sys_device` (
    `device_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `device_uuid` VARCHAR(64) NOT NULL COMMENT '设备唯一标识',
    `device_name` VARCHAR(100) COMMENT '设备名称',
    `device_model` VARCHAR(100) COMMENT '设备型号',
    `device_os` VARCHAR(50) COMMENT '操作系统',
    `app_version` VARCHAR(20) COMMENT 'App 版本',
    `current_user_id` BIGINT COMMENT '当前登录用户 ID',
    `current_user_name` VARCHAR(64) COMMENT '当前登录用户名 (冗余字段，便于查询)',
    `activation_code_id` BIGINT COMMENT '激活码 ID',
    `last_sync_time` DATETIME COMMENT '最后同步时间',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(64) COMMENT '最后登录 IP',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态 (0 离线 1 在线)',
    `remark` VARCHAR(500) COMMENT '备注',
    `create_by` VARCHAR(64) COMMENT '创建人',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) COMMENT '更新人',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`device_id`),
    UNIQUE KEY `uk_device_uuid` (`device_uuid`),
    KEY `idx_current_user` (`current_user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';
