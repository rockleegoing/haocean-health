-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律条款', '1255', '1', 'legalterm', 'system/legalterm/index', 1, 0, 'C', '0', '0', 'system:legalterm:list', '#', 'admin', sysdate(), '', null, '法律条款菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律条款查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:legalterm:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律条款新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:legalterm:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律条款修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:legalterm:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律条款删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:legalterm:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('法律条款导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:legalterm:export',       '#', 'admin', sysdate(), '', null, '');