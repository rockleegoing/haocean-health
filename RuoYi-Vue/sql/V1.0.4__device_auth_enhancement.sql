-- ============================================================
-- 设备与认证模块 - 功能增强
-- 版本：V1.0.4
-- 日期：2026-04-23
-- 说明：添加心跳机制、一码多设备支持
-- ============================================================

-- 1. 激活码表新增字段
ALTER TABLE sys_activation_code
ADD COLUMN max_device_count INT DEFAULT 1 COMMENT '最大允许设备数' AFTER bind_user_id,
ADD COLUMN activated_count INT DEFAULT 0 COMMENT '已激活设备数' AFTER max_device_count;

-- 2. 设备表新增字段
ALTER TABLE sys_device
ADD COLUMN heartbeat_time DATETIME COMMENT '最后心跳时间' AFTER last_sync_time,
ADD COLUMN heartbeat_interval INT DEFAULT 300 COMMENT '心跳间隔 (秒)' AFTER heartbeat_time;

-- 3. 更新现有数据（如果有）
UPDATE sys_activation_code SET max_device_count = 1, activated_count = 0 WHERE max_device_count IS NULL;
UPDATE sys_device SET heartbeat_interval = 300 WHERE heartbeat_interval IS NULL;
