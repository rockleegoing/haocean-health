-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('监管事项', '1256', '1', 'matter', 'system/matter/index', 1, 0, 'C', '0', '0', 'system:matter:list', '#', 'admin', sysdate(), '', null, '监管事项菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('监管事项查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:matter:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('监管事项新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:matter:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('监管事项修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:matter:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('监管事项删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:matter:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('监管事项导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:matter:export',       '#', 'admin', sysdate(), '', null, '');