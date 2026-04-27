-- ============================================
-- 脚本：V1.2.0__dict_tables.sql
-- 版本：1.2.0
-- 日期：2026-04-27
-- 描述：创建法律类型和监管类型字典表
-- ============================================

-- 法律类型表
CREATE TABLE IF NOT EXISTS `sys_legal_type` (
    `type_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '类型ID',
    `type_code` varchar(50) NOT NULL COMMENT '类型编码（唯一）',
    `type_name` varchar(100) NOT NULL COMMENT '类型名称',
    `sort_order` int(11) DEFAULT 0 COMMENT '排序',
    `status` char(1) DEFAULT '0' COMMENT '状态：0正常 1停用',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`type_id`),
    UNIQUE KEY `uk_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='法律类型表';

-- 监管类型表
CREATE TABLE IF NOT EXISTS `sys_supervision_type` (
    `type_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '类型ID',
    `type_code` varchar(50) NOT NULL COMMENT '类型编码（唯一）',
    `type_name` varchar(100) NOT NULL COMMENT '类型名称',
    `sort_order` int(11) DEFAULT 0 COMMENT '排序',
    `status` char(1) DEFAULT '0' COMMENT '状态：0正常 1停用',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`type_id`),
    UNIQUE KEY `uk_type_code` (`type_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监管类型表';

-- 初始化法律类型数据
INSERT INTO `sys_legal_type` (`type_code`, `type_name`, `sort_order`, `status`, `create_by`, `create_time`) VALUES
('law', '法律', 1, '0', 'admin', NOW()),
('regulation', '法规', 2, '0', 'admin', NOW()),
('rule', '规章', 3, '0', 'admin', NOW()),
('normative_document', '规范性文件', 4, '0', 'admin', NOW()),
('approval_document', '批复文件', 5, '0', 'admin', NOW()),
('standard', '标准', 6, '0', 'admin', NOW());

-- 初始化监管类型数据
INSERT INTO `sys_supervision_type` (`type_code`, `type_name`, `sort_order`, `status`, `create_by`, `create_time`) VALUES
('food_production', '食品生产', 1, '0', 'admin', NOW()),
('food_sales', '食品销售', 2, '0', 'admin', NOW()),
('catering_service', '餐饮服务', 3, '0', 'admin', NOW()),
('drug_operation', '药品经营', 4, '0', 'admin', NOW()),
('medical_device', '医疗器械', 5, '0', 'admin', NOW()),
('cosmetics', '化妆品', 6, '0', 'admin', NOW()),
('special_equipment', '特种设备', 7, '0', 'admin', NOW()),
('industrial_product', '工业产品', 8, '0', 'admin', NOW()),
('metrology_standard', '计量标准', 9, '0', 'admin', NOW()),
('certification', '认证认可', 10, '0', 'admin', NOW()),
('inspection_testing', '检验检测', 11, '0', 'admin', NOW()),
('advertising_supervision', '广告监管', 12, '0', 'admin', NOW()),
('intellectual_property', '知识产权', 13, '0', 'admin', NOW());
