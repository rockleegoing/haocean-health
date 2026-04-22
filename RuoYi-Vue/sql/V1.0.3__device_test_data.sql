-- ============================================================
-- 脚本：V1.0.3__device_test_data.sql
-- 版本：V1.0.3
-- 日期：2026-04-22
-- 描述：插入设备管理测试数据
-- ============================================================

START TRANSACTION;

-- 插入测试设备数据
INSERT INTO sys_device (device_id, device_uuid, device_name, device_model, device_os, app_version, current_user_id, current_user_name, activation_code_id, last_sync_time, last_login_time, last_login_ip, status, remark, create_time, update_time, create_by, update_by)
VALUES
-- 在线设备
(1, 'android-device-uuid-001', '测试设备 001', 'iPhone 15 Pro', 'iOS 17.0', '2.1.0', 1, 'admin', 1, NOW(), NOW(), '192.168.1.100', '1', '测试设备 1', NOW(), NOW(), 'admin', NULL),
(2, 'android-device-uuid-002', '测试设备 002', 'Samsung Galaxy S24', 'Android 14', '2.1.0', 2, 'user1', 2, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_SUB(NOW(), INTERVAL 30 MINUTE), '192.168.1.101', '1', '测试设备 2', NOW(), NOW(), 'admin', NULL),
(3, 'android-device-uuid-003', '测试设备 003', 'Xiaomi 14 Ultra', 'Android 14', '2.0.5', 3, 'user2', 3, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR), '192.168.1.102', '1', '测试设备 3', NOW(), NOW(), 'admin', NULL),
(4, 'android-device-uuid-004', '测试设备 004', 'OPPO Find X7', 'Android 14', '2.1.0', NULL, NULL, 4, DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR), '192.168.1.103', '1', '测试设备 4', NOW(), NOW(), 'admin', NULL),
(5, 'android-device-uuid-005', '测试设备 005', 'vivo X100 Pro', 'Android 14', '2.0.8', NULL, NULL, 5, DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR), '192.168.1.104', '1', '测试设备 5', NOW(), NOW(), 'admin', NULL),

-- 离线设备
(6, 'android-device-uuid-006', '测试设备 006', 'Huawei Mate 60 Pro', 'HarmonyOS 4.0', '2.0.5', 4, 'user3', 6, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), '192.168.1.105', '0', '测试设备 6', NOW(), NOW(), 'admin', NULL),
(7, 'android-device-uuid-007', '测试设备 007', 'OnePlus 12', 'Android 14', '2.1.0', 5, 'user4', 7, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), '192.168.1.106', '0', '测试设备 7', NOW(), NOW(), 'admin', NULL),
(8, 'android-device-uuid-008', '测试设备 008', 'Realme GT5 Pro', 'Android 14', '2.0.3', NULL, NULL, 8, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), '192.168.1.107', '0', '测试设备 8', NOW(), NOW(), 'admin', NULL),
(9, 'android-device-uuid-009', '测试设备 009', 'Honor Magic6 Pro', 'Android 14', '2.0.5', NULL, NULL, 9, DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), '192.168.1.108', '0', '测试设备 9', NOW(), NOW(), 'admin', NULL),
(10, 'android-device-uuid-010', '测试设备 010', 'Motorola Edge 50 Pro', 'Android 14', '2.0.0', NULL, NULL, 10, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), '192.168.1.109', '0', '测试设备 10', NOW(), NOW(), 'admin', NULL),

-- 未绑定激活码的设备
(11, 'android-device-uuid-011', '测试设备 011', 'Google Pixel 8 Pro', 'Android 14', '2.1.0', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR), '192.168.1.110', '1', '未绑定激活码', NOW(), NOW(), 'admin', NULL),
(12, 'android-device-uuid-012', '测试设备 012', 'Sony Xperia 1 VI', 'Android 14', '2.0.8', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 12 HOUR), DATE_SUB(NOW(), INTERVAL 10 HOUR), '192.168.1.111', '0', '未绑定激活码', NOW(), NOW(), 'admin', NULL);

-- 验证插入结果
SELECT '设备测试数据插入成功' AS result, COUNT(*) AS total FROM sys_device WHERE device_uuid LIKE 'android-device-uuid-%';

COMMIT;
