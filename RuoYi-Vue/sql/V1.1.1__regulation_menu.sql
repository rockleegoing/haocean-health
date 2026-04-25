-- ============================================
-- 脚本：V1.1.1__regulation_menu.sql
-- 版本：1.1.1
-- 日期：2026-04-25
-- 描述：创建法律法规和定性依据菜单
-- 作者：Claude
-- ============================================

-- 法律法规菜单
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律法规', '3', '3', 'regulation', 'system/regulation/index', 1, 0, 'C', '0', '0', 'system:regulation:list', '#', 'admin', sysdate(), '', null, '法律法规菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 法律法规按钮
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律法规查询', @parentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:query', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律法规新增', @parentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:add', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律法规修改', @parentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:edit', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律法规删除', @parentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律法规导出', @parentId, '5', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:export', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律法规导入', @parentId, '6', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:import', '#', 'admin', sysdate(), '', null, '');

-- 定性依据菜单
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('定性依据', '3', '4', 'legalBasis', 'system/legalBasis/index', 1, 0, 'C', '0', '0', 'system:legalBasis:list', '#', 'admin', sysdate(), '', null, '定性依据菜单');

-- 按钮父菜单ID
SELECT @parentId2 := LAST_INSERT_ID();

-- 定性依据按钮
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('定性依据查询', @parentId2, '1', '#', '', 1, 0, 'F', '0', '0', 'system:legalBasis:query', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('定性依据新增', @parentId2, '2', '#', '', 1, 0, 'F', '0', '0', 'system:legalBasis:add', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('定性依据修改', @parentId2, '3', '#', '', 1, 0, 'F', '0', '0', 'system:legalBasis:edit', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('定性依据删除', @parentId2, '4', '#', '', 1, 0, 'F', '0', '0', 'system:legalBasis:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('定性依据导出', @parentId2, '5', '#', '', 1, 0, 'F', '0', '0', 'system:legalBasis:export', '#', 'admin', sysdate(), '', null, '');
