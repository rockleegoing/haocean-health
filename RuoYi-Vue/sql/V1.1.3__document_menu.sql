-- ============================================
-- 脚本：V1.1.3__document_menu.sql
-- 版本：1.1.3
-- 日期：2026-04-25
-- 描述：创建文书管理菜单
-- 作者：Claude
-- ============================================

-- 文书管理父菜单
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('文书管理', '3', '5', 'document', 'system/document/index', 1, 0, 'C', '0', '0', 'system:document:list', '#', 'admin', sysdate(), '', null, '文书管理菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 文书管理按钮
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('文书管理查询', @parentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:document:query', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('文书管理新增', @parentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:document:add', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('文书管理修改', @parentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:document:edit', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('文书管理删除', @parentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:document:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('文书管理导出', @parentId, '5', '#', '', 1, 0, 'F', '0', '0', 'system:document:export', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('文书管理导入', @parentId, '6', '#', '', 1, 0, 'F', '0', '0', 'system:document:import', '#', 'admin', sysdate(), '', null, '');
