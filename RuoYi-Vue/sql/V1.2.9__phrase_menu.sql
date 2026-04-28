-- ============================================
-- 脚本：V1.2.9__phrase_menu.sql
-- 版本：1.2.9
-- 日期：2026-04-28
-- 描述：创建规范用语菜单
-- ============================================

-- 检查是否已存在规范用语目录
SET @existPhrase = (SELECT COUNT(1) FROM sys_menu WHERE menu_name = '规范用语' AND parent_id = 0);
SET @phraseParentId = (SELECT menu_id FROM sys_menu WHERE menu_name = '规范用语' AND parent_id = 0);

-- 规范用语文档（作为一级菜单，位于移动卫生执法系统官网之后）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '规范用语', '0', '5', 'phrase', NULL, 1, 0, 'M', '0', '0', '', 'list', 'admin', NOW(), '', NULL, '规范用语目录'
FROM DUAL WHERE @existPhrase = 0;

-- 获取规范用语目录ID
SET @phraseParentId = LAST_INSERT_ID();

-- 规范用语书本管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '规范用语管理', @phraseParentId, '1', 'phrase', 'system/phrase/index', 1, 0, 'C', '0', '0', 'system:phrase:list', '#', 'admin', NOW(), '', NULL, '规范用语管理菜单'
FROM DUAL WHERE @existPhrase = 0;

-- 获取规范用语管理按钮父ID
SET @phraseBtnParentId = LAST_INSERT_ID();

-- 规范用语按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '规范用语查询', @phraseBtnParentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:phrase:query', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL WHERE @existPhrase = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '规范用语新增', @phraseBtnParentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:phrase:add', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL WHERE @existPhrase = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '规范用语修改', @phraseBtnParentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:phrase:edit', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL WHERE @existPhrase = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '规范用语删除', @phraseBtnParentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:phrase:remove', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL WHERE @existPhrase = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '规范用语导出', @phraseBtnParentId, '5', '#', '', 1, 0, 'F', '0', '0', 'system:phrase:export', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL WHERE @existPhrase = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '规范用语导入', @phraseBtnParentId, '6', '#', '', 1, 0, 'F', '0', '0', 'system:phrase:import', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL WHERE @existPhrase = 0;
