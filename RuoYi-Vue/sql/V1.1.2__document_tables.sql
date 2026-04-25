-- ============================================
-- 脚本：V1.1.2__document_tables.sql
-- 描述：文书模块数据库表
-- 日期：2026-04-25
-- ============================================

-- 文书模板表
DROP TABLE IF EXISTS `sys_document_template`;
CREATE TABLE `sys_document_template` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `template_code` VARCHAR(64) NOT NULL COMMENT '模板编码',
  `template_name` VARCHAR(128) NOT NULL COMMENT '模板名称',
  `template_type` VARCHAR(32) COMMENT '模板类型',
  `category` VARCHAR(32) COMMENT '分类',
  `file_path` VARCHAR(256) COMMENT '模板文件路径',
  `file_url` VARCHAR(256) COMMENT '模板文件URL',
  `version` INT DEFAULT 1 COMMENT '版本号',
  `is_active` CHAR(1) DEFAULT '1' COMMENT '是否启用（0否 1是）',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` VARCHAR(500) COMMENT '备注',
  UNIQUE KEY `uk_template_code` (`template_code`),
  INDEX `idx_template_type` (`template_type`),
  INDEX `idx_category` (`category`),
  INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书模板表';

-- 文书模板变量表
DROP TABLE IF EXISTS `sys_document_variable`;
CREATE TABLE `sys_document_variable` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `template_id` BIGINT NOT NULL COMMENT '关联模板ID',
  `variable_name` VARCHAR(64) NOT NULL COMMENT '变量名',
  `variable_label` VARCHAR(128) COMMENT '变量标签',
  `variable_type` VARCHAR(32) DEFAULT 'TEXT' COMMENT '变量类型（TEXT/DATE/DATETIME/SELECT/RADIO/CHECKBOX/SIGNATURE/PHOTO）',
  `required` CHAR(1) DEFAULT '1' COMMENT '是否必填（0否 1是）',
  `default_value` VARCHAR(256) COMMENT '默认值',
  `options` TEXT COMMENT '选项JSON',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `max_length` INT COMMENT '最大长度',
  INDEX `idx_template_id` (`template_id`),
  INDEX `idx_variable_name` (`variable_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书模板变量表';

-- 文书套组表
DROP TABLE IF EXISTS `sys_document_group`;
CREATE TABLE `sys_document_group` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `group_code` VARCHAR(64) NOT NULL COMMENT '套组编码',
  `group_name` VARCHAR(128) NOT NULL COMMENT '套组名称',
  `group_type` VARCHAR(32) COMMENT '套组类型',
  `templates` TEXT COMMENT '模板列表JSON',
  `is_active` CHAR(1) DEFAULT '1' COMMENT '是否启用（0否 1是）',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` VARCHAR(500) COMMENT '备注',
  UNIQUE KEY `uk_group_code` (`group_code`),
  INDEX `idx_group_type` (`group_type`),
  INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书套组表';

-- 文书记录表
DROP TABLE IF EXISTS `sys_document_record`;
CREATE TABLE `sys_document_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `document_no` VARCHAR(64) COMMENT '文书编号',
  `template_id` BIGINT NOT NULL COMMENT '关联模板ID',
  `template_code` VARCHAR(64) COMMENT '模板编码',
  `record_id` BIGINT COMMENT '关联执法记录ID',
  `unit_id` BIGINT COMMENT '关联单位ID',
  `variables` TEXT COMMENT '变量值JSON',
  `file_path` VARCHAR(256) COMMENT '生成文件路径',
  `file_url` VARCHAR(256) COMMENT '生成文件URL',
  `signatures` TEXT COMMENT '签名图片JSON',
  `status` VARCHAR(32) DEFAULT 'DRAFT' COMMENT '状态（DRAFT草稿/GENERATED已生成/PRINTED已打印）',
  `sync_status` CHAR(1) DEFAULT '0' COMMENT '同步状态（0未同步 1已同步）',
  `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_document_no` (`document_no`),
  INDEX `idx_template_id` (`template_id`),
  INDEX `idx_template_code` (`template_code`),
  INDEX `idx_record_id` (`record_id`),
  INDEX `idx_unit_id` (`unit_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_sync_status` (`sync_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书记录表';

-- ============================================
-- 初始化数据：示例文书模板
-- ============================================
INSERT INTO `sys_document_template` (`template_code`, `template_name`, `template_type`, `category`, `file_url`, `version`, `is_active`, `del_flag`, `remark`) VALUES
('WS001', '现场笔录', 'CHECK_RECORD', '日常监督', '/templates/ws001.docx', 1, '1', '0', '公共场所日常监督检查现场笔录'),
('WS002', '卫生监督意见书', 'OPINION', '执法文书', '/templates/ws002.docx', 1, '1', '0', '卫生监督意见书模板'),
('WS003', '行政处罚决定书', 'PUNISHMENT', '处罚文书', '/templates/ws003.docx', 1, '1', '0', '行政处罚决定书模板'),
('WS004', '当场行政处罚决定书', 'PUNISHMENT_QUICK', '处罚文书', '/templates/ws004.docx', 1, '1', '0', '当场行政处罚决定书模板'),
('WS005', '证据先行登记保存决定书', 'EVIDENCE', '证据文书', '/templates/ws005.docx', 1, '1', '0', '证据先行登记保存决定书模板');

-- ============================================
-- 初始化数据：示例文书套组
-- ============================================
INSERT INTO `sys_document_group` (`group_code`, `group_name`, `group_type`, `templates`, `is_active`, `del_flag`, `remark`) VALUES
('GROUP001', '日常监督检查套组', 'DAILY_CHECK', '["WS001", "WS002"]', '1', '0', '日常监督检查常用文书套组'),
('GROUP002', '行政处罚套组', 'PUNISHMENT', '["WS001", "WS003", "WS005"]', '1', '0', '行政处罚流程文书套组');
