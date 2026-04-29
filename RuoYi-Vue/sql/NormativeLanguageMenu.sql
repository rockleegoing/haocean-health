-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语', '1224', '1', 'language', 'system/language/index', 1, 0, 'C', '0', '0', 'system:language:list', '#', 'admin', sysdate(), '', null, '规范用语菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:language:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:language:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:language:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:language:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:language:export',       '#', 'admin', sysdate(), '', null, '');