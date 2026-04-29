-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语类别', '1224', '1', 'normativecategory', 'system/normativecategory/index', 1, 0, 'C', '0', '0', 'system:normativecategory:list', '#', 'admin', sysdate(), '', null, '规范用语类别菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语类别查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'system:normativecategory:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语类别新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'system:normativecategory:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语类别修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'system:normativecategory:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语类别删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'system:normativecategory:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('规范用语类别导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'system:normativecategory:export',       '#', 'admin', sysdate(), '', null, '');