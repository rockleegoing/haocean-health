-- ============================================
-- 脚本：V1.1.7__regulation_menu.sql
-- 版本：1.1.7
-- 日期：2026-04-26
-- 描述：添加法律法规批量操作菜单
-- ============================================

-- 批量操作菜单（按钮级权限）
-- menu_name: 批量操作, order_num: 6, path: /system/regulation/batch
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('批量操作', (SELECT menu_id FROM sys_menu WHERE menu_name = '法律法规管理'), 6, '/system/regulation/batch', 'system/regulation/batch/index', 1, 0, 'F', '0', '0', '', 'btn', 'admin', NOW(), '批量操作菜单');

-- 获取刚插入的批量操作菜单ID
SET @parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '批量操作' AND parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '法律法规管理'));

-- 导入Excel
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('导入Excel', @parent_id, 1, '', '', 1, 0, 'F', '0', '0', 'system:regulation:import', '#', 'admin', NOW(), '导入Excel权限');

-- 导出Excel
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('导出Excel', @parent_id, 2, '', '', 1, 0, 'F', '0', '0', 'system:regulation:export', '#', 'admin', NOW(), '导出Excel权限');

-- 导入JSON
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('导入JSON', @parent_id, 3, '', '', 1, 0, 'F', '0', '0', 'system:regulation:importJson', '#', 'admin', NOW(), '导入JSON权限');

-- 导出JSON
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('导出JSON', @parent_id, 4, '', '', 1, 0, 'F', '0', '0', 'system:regulation:exportJson', '#', 'admin', NOW(), '导出JSON权限');
