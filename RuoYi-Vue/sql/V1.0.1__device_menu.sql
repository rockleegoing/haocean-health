-- ============================================================
-- 脚本：V1.0.1__device_menu.sql
-- 版本：V1.0.1
-- 日期：2026-04-22
-- 描述：添加设备管理和激活码管理的菜单权限
-- 作者：系统开发团队
-- ============================================================

START TRANSACTION;

-- 1. 添加设备管理父菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, menu_type, visible, perms, icon, component, create_time, update_time, create_by, update_by, remark)
VALUES ('设备管理', 0, 3, '/device', 'M', '0', 'device:menu', 'component', 'Layout', NOW(), NOW(), 'admin', 'admin', '设备管理父菜单');

-- 2. 获取父菜单 ID
SET @parentId = LAST_INSERT_ID();

-- 3. 添加激活码管理子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, menu_type, visible, perms, icon, component, create_time, update_time, create_by, update_by, remark)
VALUES ('激活码管理', @parentId, 1, '/device/activation-code', 'C', '0', 'device:activationCode:list', '#', 'device/activation-code/index', NOW(), NOW(), 'admin', 'admin', '激活码管理菜单');

-- 4. 添加设备管理子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, menu_type, visible, perms, icon, component, create_time, update_time, create_by, update_by, remark)
VALUES ('设备管理', @parentId, 2, '/device/device', 'C', '0', 'device:device:list', '#', 'device/device/index', NOW(), NOW(), 'admin', 'admin', '设备管理菜单');

COMMIT;
