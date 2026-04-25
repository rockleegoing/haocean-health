-- ============================================
-- 脚本：supervision.sql
-- 描述：监管事项模块数据库表
-- 日期：2026-04-25
-- ============================================

-- 监管类型表（字典表）
DROP TABLE IF EXISTS `sys_supervision_category`;
CREATE TABLE `sys_supervision_category` (
  `category_id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '类型ID',
  `category_name` VARCHAR(100) NOT NULL COMMENT '类型名称',
  `category_code` VARCHAR(50) COMMENT '类型编码',
  `icon` VARCHAR(100) COMMENT '图标',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监管类型表';

-- 监管事项表
DROP TABLE IF EXISTS `sys_supervision_item`;
CREATE TABLE `sys_supervision_item` (
  `item_id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '事项ID',
  `item_no` VARCHAR(50) UNIQUE COMMENT '事项编码',
  `name` VARCHAR(200) NOT NULL COMMENT '事项名称',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父级事项ID（0为一级）',
  `category_id` BIGINT COMMENT '监管类型ID',
  `description` TEXT COMMENT '监管要求描述',
  `legal_basis` TEXT COMMENT '法律依据',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  INDEX `idx_no` (`item_no`),
  INDEX `idx_category` (`category_id`),
  INDEX `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监管事项表';

-- 监管事项关联规范用语表
DROP TABLE IF EXISTS `sys_supervision_language_link`;
CREATE TABLE `sys_supervision_language_link` (
  `link_id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
  `item_id` BIGINT NOT NULL COMMENT '监管事项ID',
  `language_id` BIGINT NOT NULL COMMENT '规范用语ID',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_item` (`item_id`),
  INDEX `idx_language` (`language_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监管事项关联规范用语表';

-- 监管事项关联法律法规表
DROP TABLE IF EXISTS `sys_supervision_regulation_link`;
CREATE TABLE `sys_supervision_regulation_link` (
  `link_id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
  `item_id` BIGINT NOT NULL COMMENT '监管事项ID',
  `regulation_id` BIGINT NOT NULL COMMENT '法律法规ID',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX `idx_item` (`item_id`),
  INDEX `idx_regulation` (`regulation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监管事项关联法律法规表';

-- ============================================
-- 初始化数据：监管类型（10类）
-- ============================================
INSERT INTO `sys_supervision_category` (`category_name`, `category_code`, `icon`, `sort_order`, `status`) VALUES
('住宿场所', 'ACCOMMODATION', 'ic_accommodation', 1, '0'),
('公共场所', 'PUBLIC_PLACE', 'ic_public', 2, '0'),
('消毒产品', 'DISINFECT_PRODUCT', 'ic_disinfect', 3, '0'),
('生活饮用水', 'DRINKING_WATER', 'ic_water', 4, '0'),
('职业卫生', 'OCCUPATIONAL', 'ic_occupational', 5, '0'),
('医疗服务', 'MEDICAL', 'ic_medical', 6, '0'),
('学校卫生', 'SCHOOL', 'ic_school', 7, '0'),
('放射卫生', 'RADIATION', 'ic_radiation', 8, '0'),
('血液安全', 'BLOOD', 'ic_blood', 9, '0'),
('计划生育', 'FAMILY_PLANNING', 'ic_family', 10, '0');

-- ============================================
-- 初始化数据：示例监管事项
-- ============================================
INSERT INTO `sys_supervision_item` (`item_no`, `name`, `parent_id`, `category_id`, `description`, `legal_basis`, `sort_order`, `status`) VALUES
('JY001', '对医师的监管', 0, 6, '对医师执业资格的监督检查', '《中华人民共和国执业医师法》', 1, '0'),
('JY002', '对医疗机构执业的监管', 0, 6, '对医疗机构执业资格的监督检查', '《医疗机构管理条例》', 2, '0'),
('GY001', '对住宿场所的监管', 0, 1, '对住宿场所卫生状况的监督检查', '《公共场所卫生管理条例》', 1, '0'),
('GY002', '对公共场所的监管', 0, 2, '对公共场所卫生状况的监督检查', '《公共场所卫生管理条例》', 1, '0');
