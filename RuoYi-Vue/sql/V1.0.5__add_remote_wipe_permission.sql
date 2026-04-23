-- ============================================================
-- 脚本：V1.0.5__add_remote_wipe_permission.sql
-- 版本：V1.0.5
-- 日期：2026-04-23
-- 描述：添加设备远程清除按钮权限
-- 作者：系统开发团队
-- ============================================================

START TRANSACTION;

-- 添加远程清除按钮权限（挂载到设备管理菜单下）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, menu_type, visible, perms, icon, component, create_time, update_time, create_by, update_by, remark)
VALUES ('设备清除',
        (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '设备管理' AND parent_id > 0 LIMIT 1) AS temp),
        3,
        '#',
        'F',
        '0',
        'device:device:remoteWipe',
        '',
        '',
        NOW(),
        NOW(),
        'admin',
        'admin',
        '设备远程清除按钮权限');

COMMIT;
