-- ============================================
-- 脚本：V1.2.1__dict_menu.sql
-- 版本：1.2.1
-- 日期：2026-04-27
-- 描述：创建法律类型和监管类型菜单（作为法律法规子菜单）
-- ============================================

-- 获取法律法规目录ID
SET @regulationParentId = (SELECT menu_id FROM sys_menu WHERE menu_name = '法律法规' AND parent_id = 0);

-- 检查是否已存在菜单
SET @existLegalType = (SELECT COUNT(1) FROM sys_menu WHERE menu_name = '法律类型管理' AND parent_id = @regulationParentId);
SET @existSupervisionType = (SELECT COUNT(1) FROM sys_menu WHERE menu_name = '监管类型管理' AND parent_id = @regulationParentId);

-- 法律类型管理菜单（order=6，在定性依据之后）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '法律类型管理', @regulationParentId, '6', 'legalType', 'system/regulation/legalType/index', 1, 0, 'C', '0', '0', 'system:legalType:list', '#', 'admin', NOW(), '法律类型管理菜单'
FROM DUAL WHERE @existLegalType = 0;

-- 获取法律类型管理按钮父ID
SET @legalTypeBtnParentId = LAST_INSERT_ID();

-- 法律类型按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '法律类型查询', @legalTypeBtnParentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:legalType:query', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existLegalType = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '法律类型新增', @legalTypeBtnParentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:legalType:add', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existLegalType = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '法律类型修改', @legalTypeBtnParentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:legalType:edit', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existLegalType = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '法律类型删除', @legalTypeBtnParentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:legalType:remove', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existLegalType = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '法律类型导出', @legalTypeBtnParentId, '5', '#', '', 1, 0, 'F', '0', '0', 'system:legalType:export', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existLegalType = 0;

-- 监管类型管理菜单（order=7）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '监管类型管理', @regulationParentId, '7', 'supervisionType', 'system/regulation/supervisionType/index', 1, 0, 'C', '0', '0', 'system:supervisionType:list', '#', 'admin', NOW(), '监管类型管理菜单'
FROM DUAL WHERE @existSupervisionType = 0;

-- 获取监管类型管理按钮父ID
SET @supervisionTypeBtnParentId = LAST_INSERT_ID();

-- 监管类型按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '监管类型查询', @supervisionTypeBtnParentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:supervisionType:query', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existSupervisionType = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '监管类型新增', @supervisionTypeBtnParentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:supervisionType:add', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existSupervisionType = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '监管类型修改', @supervisionTypeBtnParentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:supervisionType:edit', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existSupervisionType = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '监管类型删除', @supervisionTypeBtnParentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:supervisionType:remove', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existSupervisionType = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '监管类型导出', @supervisionTypeBtnParentId, '5', '#', '', 1, 0, 'F', '0', '0', 'system:supervisionType:export', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existSupervisionType = 0;
