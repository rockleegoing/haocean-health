-- 执法单位菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('执法单位', '3', '2', 'unit', 'system/unit/index', 1, 0, 'C', '0', '0', 'system:unit:list', '#', 'admin', sysdate(), '', null, '执法单位菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('执法单位查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:unit:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('执法单位新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:unit:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('执法单位修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:unit:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('执法单位删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:unit:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('执法单位导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:unit:export',       '#', 'admin', sysdate(), '', null, '');
