-- --------------------------------------------------------------
-- 设备与认证模块 - 默认数据脚本
-- --------------------------------------------------------------

-- ----------------------------
-- 1. 插入测试激活码 (10 个，有效期 365 天)
-- ----------------------------
INSERT INTO `sys_activation_code` (`code_value`, `status`, `expire_time`, `remark`, `create_by`) VALUES
('TEST0001', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 1', 'admin'),
('TEST0002', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 2', 'admin'),
('TEST0003', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 3', 'admin'),
('TEST0004', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 4', 'admin'),
('TEST0005', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 5', 'admin'),
('TEST0006', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 6', 'admin'),
('TEST0007', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 7', 'admin'),
('TEST0008', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 8', 'admin'),
('TEST0009', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 9', 'admin'),
('TEST0010', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 10', 'admin');
