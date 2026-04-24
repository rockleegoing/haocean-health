-- ============================================
-- 脚本：V1.0.9__sys_unit_table.sql
-- 版本：1.0.9
-- 日期：2026-04-24
-- 描述：创建执法单位表 sys_unit
-- 作者：Claude
-- ============================================

-- 创建执法单位表
CREATE TABLE IF NOT EXISTS `sys_unit` (
    `unit_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '单位ID',
    `unit_name` varchar(100) NOT NULL COMMENT '单位名称',
    `industry_category_id` bigint(20) DEFAULT NULL COMMENT '行业分类ID',
    `region` varchar(50) DEFAULT NULL COMMENT '区域',
    `supervision_type` varchar(50) DEFAULT NULL COMMENT '监管类型',
    `credit_code` varchar(50) DEFAULT NULL COMMENT '统一社会信用代码',
    `legal_person` varchar(50) DEFAULT NULL COMMENT '法定代表人',
    `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `business_address` varchar(255) DEFAULT NULL COMMENT '经营地址',
    `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
    `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
    `status` char(1) DEFAULT '0' COMMENT '状态:0正常,1停用',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志:0存在,1删除',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`unit_id`),
    KEY `idx_industry_category` (`industry_category_id`),
    KEY `idx_region` (`region`),
    KEY `idx_supervision_type` (`supervision_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执法单位表';
