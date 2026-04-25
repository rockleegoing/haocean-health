-- ============================================
-- 脚本：V1.1.4__regulation_chapter_article_menu.sql
-- 版本：1.1.4
-- 日期：2026-04-25
-- 描述：创建章节管理和条款管理菜单
-- 作者：Claude
-- ============================================

-- 章节管理菜单（挂在"法律法规"菜单下）
-- 首先查询"法律法规"菜单的menu_id
SELECT @parentId := menu_id FROM sys_menu WHERE path = 'regulation' AND status = '0';

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('章节管理', @parentId, '1', 'chapter', 'system/regulation/chapter/index', 1, 0, 'C', '0', '0', 'system:regulation:chapter:list', '#', 'admin', sysdate(), '', null, '章节管理菜单');

-- 章节管理按钮父菜单ID
SELECT @chapterParentId := LAST_INSERT_ID();

-- 章节管理按钮
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('章节查询', @chapterParentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:chapter:query', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('章节新增', @chapterParentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:chapter:add', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('章节修改', @chapterParentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:chapter:edit', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('章节删除', @chapterParentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:chapter:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('章节导出', @chapterParentId, '5', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:chapter:export', '#', 'admin', sysdate(), '', null, '');

-- 条款管理菜单（挂在"章节管理"菜单下）
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('条款管理', @chapterParentId, '1', 'article', 'system/regulation/article/index', 1, 0, 'C', '0', '0', 'system:regulation:article:list', '#', 'admin', sysdate(), '', null, '条款管理菜单');

-- 条款管理按钮父菜单ID
SELECT @articleParentId := LAST_INSERT_ID();

-- 条款管理按钮
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('条款查询', @articleParentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:article:query', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('条款新增', @articleParentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:article:add', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('条款修改', @articleParentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:article:edit', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('条款删除', @articleParentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:article:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('条款导出', @articleParentId, '5', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:article:export', '#', 'admin', sysdate(), '', null, '');
