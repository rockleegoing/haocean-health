-- ============================================
-- 脚本：V1.2.6__legal_basis_content_tables.sql
-- 版本：1.2.6
-- 日期：2026-04-28
-- 描述：创建定性依据和处理依据内容表
-- ============================================

-- 1. 创建定性依据内容表
CREATE TABLE IF NOT EXISTS `sys_legal_basis_content` (
    `content_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '内容ID',
    `basis_id` bigint(20) NOT NULL COMMENT '关联定性依据ID',
    `label` varchar(100) NOT NULL COMMENT '标签（如：条款内容、法律责任、裁量标准）',
    `content` text COMMENT '内容',
    `sort_order` int(11) DEFAULT 0 COMMENT '排序',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`content_id`),
    KEY `idx_basis_id` (`basis_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定性依据内容表';

-- 添加外键约束（级联删除）
ALTER TABLE `sys_legal_basis_content`
    ADD CONSTRAINT `fk_legal_basis_content_basis`
    FOREIGN KEY (`basis_id`) REFERENCES `sys_legal_basis` (`basis_id`)
    ON DELETE CASCADE;

-- 2. 创建处理依据内容表
CREATE TABLE IF NOT EXISTS `sys_processing_basis_content` (
    `content_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '内容ID',
    `basis_id` bigint(20) NOT NULL COMMENT '关联处理依据ID',
    `label` varchar(100) NOT NULL COMMENT '标签',
    `content` text COMMENT '内容',
    `sort_order` int(11) DEFAULT 0 COMMENT '排序',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`content_id`),
    KEY `idx_basis_id` (`basis_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处理依据内容表';

-- 添加外键约束（级联删除）
ALTER TABLE `sys_processing_basis_content`
    ADD CONSTRAINT `fk_processing_basis_content_basis`
    FOREIGN KEY (`basis_id`) REFERENCES `sys_processing_basis` (`basis_id`)
    ON DELETE CASCADE;

-- 3. 修改定性依据主表 - 删除冗余字段（MySQL 5.7 不支持 DROP COLUMN IF EXISTS，逐列删除）
ALTER TABLE `sys_legal_basis` DROP COLUMN IF EXISTS `basis_no`;
ALTER TABLE `sys_legal_basis` DROP COLUMN IF EXISTS `violation_type`;
ALTER TABLE `sys_legal_basis` DROP COLUMN IF EXISTS `issuing_authority`;
ALTER TABLE `sys_legal_basis` DROP COLUMN IF EXISTS `effective_date`;
ALTER TABLE `sys_legal_basis` DROP COLUMN IF EXISTS `legal_level`;
ALTER TABLE `sys_legal_basis` DROP COLUMN IF EXISTS `clauses`;
ALTER TABLE `sys_legal_basis` DROP COLUMN IF EXISTS `legal_liability`;
ALTER TABLE `sys_legal_basis` DROP COLUMN IF EXISTS `discretion_standard`;

-- 4. 修改处理依据主表 - 删除冗余字段
ALTER TABLE `sys_processing_basis` DROP COLUMN IF EXISTS `basis_no`;
ALTER TABLE `sys_processing_basis` DROP COLUMN IF EXISTS `violation_type`;
ALTER TABLE `sys_processing_basis` DROP COLUMN IF EXISTS `issuing_authority`;
ALTER TABLE `sys_processing_basis` DROP COLUMN IF EXISTS `effective_date`;
ALTER TABLE `sys_processing_basis` DROP COLUMN IF EXISTS `legal_level`;
ALTER TABLE `sys_processing_basis` DROP COLUMN IF EXISTS `clauses`;
ALTER TABLE `sys_processing_basis` DROP COLUMN IF EXISTS `legal_liability`;
ALTER TABLE `sys_processing_basis` DROP COLUMN IF EXISTS `discretion_standard`;
