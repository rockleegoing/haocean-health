-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律目录', '1255', '1', 'law', 'system/law/index', 1, 0, 'C', '0', '0', 'system:law:list', '#', 'admin', sysdate(), '', null, '法律目录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律目录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:law:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律目录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:law:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律目录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:law:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律目录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:law:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律目录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:law:export',       '#', 'admin', sysdate(), '', null, '');