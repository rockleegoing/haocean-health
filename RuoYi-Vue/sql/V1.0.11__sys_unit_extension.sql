-- V1.0.11__sys_unit_extension.sql
-- 日期: 2026-04-25
-- 描述: 扩展 sys_unit 表，新增当事人相关字段

ALTER TABLE `sys_unit`
    ADD COLUMN `person_name` varchar(50) DEFAULT NULL COMMENT '当事人姓名' AFTER `unit_name`,
    ADD COLUMN `registration_address` varchar(255) DEFAULT NULL COMMENT '注册地址' AFTER `person_name`,
    ADD COLUMN `business_area` decimal(10,2) DEFAULT NULL COMMENT '经营面积（平方米）' AFTER `registration_address`,
    ADD COLUMN `license_name` varchar(100) DEFAULT NULL COMMENT '许可证名称' AFTER `business_area`,
    ADD COLUMN `license_no` varchar(50) DEFAULT NULL COMMENT '许可证号' AFTER `license_name`,
    ADD COLUMN `gender` char(1) DEFAULT NULL COMMENT '性别(0男,1女)' AFTER `license_no`,
    ADD COLUMN `nation` varchar(20) DEFAULT NULL COMMENT '民族' AFTER `gender`,
    ADD COLUMN `post` varchar(50) DEFAULT NULL COMMENT '职务' AFTER `nation`,
    ADD COLUMN `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号码' AFTER `post`,
    ADD COLUMN `birthday` datetime DEFAULT NULL COMMENT '出生年月' AFTER `id_card`,
    ADD COLUMN `home_address` varchar(255) DEFAULT NULL COMMENT '家庭住址' AFTER `birthday`;

-- 注：legal_person（法定代表人）是现有字段，无需新增
