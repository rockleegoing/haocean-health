-- ============================================
-- 脚本：V1.1.8__regulation_menu_fix.sql
-- 版本：1.1.8
-- 日期：2026-04-26
-- 描述：修复法律法规模块菜单结构
-- ============================================

-- Step 1: 删除现有的法律相关菜单（从叶子节点到根节点）
-- 删除 V1.1.7 批量操作菜单下的按钮
DELETE FROM sys_menu WHERE menu_name IN ('导入Excel', '导出Excel', '导入JSON', '导出JSON') AND parent_id IN (SELECT menu_id FROM sys_menu WHERE menu_name = '批量操作');

-- 删除 V1.1.7 批量操作菜单
DELETE FROM sys_menu WHERE menu_name = '批量操作' AND parent_id IN (SELECT menu_id FROM sys_menu WHERE menu_name = '法律法规');

-- 删除 V1.1.4 条款管理按钮
DELETE FROM sys_menu WHERE menu_name IN ('条款查询', '条款新增', '条款修改', '条款删除', '条款导出') AND parent_id IN (SELECT menu_id FROM sys_menu WHERE menu_name = '条款管理');

-- 删除 V1.1.4 条款管理菜单
DELETE FROM sys_menu WHERE menu_name = '条款管理' AND parent_id IN (SELECT menu_id FROM sys_menu WHERE menu_name = '章节管理');

-- 删除 V1.1.4 章节管理按钮
DELETE FROM sys_menu WHERE menu_name IN ('章节查询', '章节新增', '章节修改', '章节删除', '章节导出') AND parent_id IN (SELECT menu_id FROM sys_menu WHERE menu_name = '章节管理');

-- 删除 V1.1.4 章节管理菜单
DELETE FROM sys_menu WHERE menu_name = '章节管理' AND parent_id IN (SELECT menu_id FROM sys_menu WHERE menu_name = '法律法规');

-- 删除 V1.1.1 法律法规按钮
DELETE FROM sys_menu WHERE menu_name IN ('法律法规查询', '法律法规新增', '法律法规修改', '法律法规删除', '法律法规导出', '法律法规导入') AND parent_id IN (SELECT menu_id FROM sys_menu WHERE menu_name = '法律法规');

-- 删除 V1.1.1 法律法规菜单
DELETE FROM sys_menu WHERE menu_name = '法律法规';

-- 删除 V1.1.1 定性依据按钮
DELETE FROM sys_menu WHERE menu_name IN ('定性依据查询', '定性依据新增', '定性依据修改', '定性依据删除', '定性依据导出') AND parent_id IN (SELECT menu_id FROM sys_menu WHERE menu_name = '定性依据');

-- 删除 V1.1.1 定性依据菜单
DELETE FROM sys_menu WHERE menu_name = '定性依据';

-- Step 2: 创建正确的菜单结构（在"系统管理" parent_id=1 下）
-- 法律法规目录菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('法律法规', '1', '1', 'regulation', NULL, 1, 0, 'M', '0', '0', '', 'book', 'admin', NOW(), '法律法规目录');

-- 获取法律法规目录ID
SET @regulationParentId = LAST_INSERT_ID();

-- 法律法规管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('法律法规管理', @regulationParentId, '1', 'regulation', 'system/regulation/index', 1, 0, 'C', '0', '0', 'system:regulation:list', '#', 'admin', NOW(), '法律法规管理菜单');

-- 法律法规管理按钮父ID
SET @regulationBtnParentId = LAST_INSERT_ID();

-- 法律法规管理按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('法律法规查询', @regulationBtnParentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:query', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('法律法规新增', @regulationBtnParentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:add', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('法律法规修改', @regulationBtnParentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:edit', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('法律法规删除', @regulationBtnParentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:remove', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('法律法规导出', @regulationBtnParentId, '5', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:export', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('法律法规导入', @regulationBtnParentId, '6', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:import', '#', 'admin', NOW(), '');

-- 章节管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('章节管理', @regulationParentId, '2', 'chapter', 'system/regulation/chapter/index', 1, 0, 'C', '0', '0', 'system:regulation:chapter:list', '#', 'admin', NOW(), '章节管理菜单');

-- 章节管理按钮父ID
SET @chapterBtnParentId = LAST_INSERT_ID();

-- 章节管理按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('章节查询', @chapterBtnParentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:chapter:query', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('章节新增', @chapterBtnParentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:chapter:add', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('章节修改', @chapterBtnParentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:chapter:edit', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('章节删除', @chapterBtnParentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:chapter:remove', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('章节导出', @chapterBtnParentId, '5', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:chapter:export', '#', 'admin', NOW(), '');

-- 条款管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('条款管理', @regulationParentId, '3', 'article', 'system/regulation/article/index', 1, 0, 'C', '0', '0', 'system:regulation:article:list', '#', 'admin', NOW(), '条款管理菜单');

-- 条款管理按钮父ID
SET @articleBtnParentId = LAST_INSERT_ID();

-- 条款管理按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('条款查询', @articleBtnParentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:article:query', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('条款新增', @articleBtnParentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:article:add', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('条款修改', @articleBtnParentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:article:edit', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('条款删除', @articleBtnParentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:article:remove', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('条款导出', @articleBtnParentId, '5', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:article:export', '#', 'admin', NOW(), '');

-- 批量操作菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('批量操作', @regulationParentId, '4', 'batch', 'system/regulation/batch/index', 1, 0, 'C', '0', '0', 'system:regulation:batch:list', '#', 'admin', NOW(), '批量操作菜单');

-- 批量操作按钮父ID
SET @batchBtnParentId = LAST_INSERT_ID();

-- 批量操作按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('导入Excel', @batchBtnParentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:import', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('导出Excel', @batchBtnParentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:export', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('导入JSON', @batchBtnParentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:importJson', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('导出JSON', @batchBtnParentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:regulation:exportJson', '#', 'admin', NOW(), '');

-- 定性依据菜单（在"系统管理"下，order_num=2）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('定性依据', '1', '2', 'legalBasis', NULL, 1, 0, 'M', '0', '0', '', 'document', 'admin', NOW(), '定性依据目录');

-- 获取定性依据目录ID
SET @legalBasisParentId = LAST_INSERT_ID();

-- 定性依据管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('定性依据管理', @legalBasisParentId, '1', 'legalBasis', 'system/legalBasis/index', 1, 0, 'C', '0', '0', 'system:legalBasis:list', '#', 'admin', NOW(), '定性依据管理菜单');

-- 定性依据管理按钮父ID
SET @legalBasisBtnParentId = LAST_INSERT_ID();

-- 定性依据管理按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('定性依据查询', @legalBasisBtnParentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:legalBasis:query', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('定性依据新增', @legalBasisBtnParentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:legalBasis:add', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('定性依据修改', @legalBasisBtnParentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:legalBasis:edit', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('定性依据删除', @legalBasisBtnParentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:legalBasis:remove', '#', 'admin', NOW(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('定性依据导出', @legalBasisBtnParentId, '5', '#', '', 1, 0, 'F', '0', '0', 'system:legalBasis:export', '#', 'admin', NOW(), '');

-- 完成
SELECT '法律法规模块菜单修复完成' AS result;
