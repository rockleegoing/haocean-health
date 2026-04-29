-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范监管关联', '1224', '1', 'normativematterbind', 'system/normativematterbind/index', 1, 0, 'C', '0', '0', 'system:normativematterbind:list', '#', 'admin', sysdate(), '', null, '规范用语监管事项关联菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语监管事项关联查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:normativematterbind:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语监管事项关联新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:normativematterbind:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语监管事项关联修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:normativematterbind:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语监管事项关联删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:normativematterbind:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语监管事项关联导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:normativematterbind:export',       '#', 'admin', sysdate(), '', null, '');