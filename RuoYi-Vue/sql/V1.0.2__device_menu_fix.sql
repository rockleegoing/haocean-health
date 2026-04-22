-- ============================================================
-- 脚本：V1.0.2__device_menu_fix.sql
-- 版本：V1.0.2
-- 日期：2026-04-22
-- 描述：修复设备管理菜单 component 字段为 NULL 的问题
-- ============================================================

START TRANSACTION;

-- 修复父菜单 component 字段 (menu_id = 1061)
UPDATE sys_menu
SET component = 'Layout'
WHERE menu_id = 1061;

-- 修复激活码管理子菜单 component 字段 (menu_id = 1062)
UPDATE sys_menu
SET component = 'device/activation-code/index'
WHERE menu_id = 1062;

-- 修复设备管理子菜单 component 字段 (menu_id = 1063)
UPDATE sys_menu
SET component = 'device/device/index'
WHERE menu_id = 1063;

COMMIT;

-- 验证修复结果
SELECT menu_id, menu_name, path, component
FROM sys_menu
WHERE menu_name IN ('设备管理', '激活码管理')
ORDER BY parent_id, order_num;
