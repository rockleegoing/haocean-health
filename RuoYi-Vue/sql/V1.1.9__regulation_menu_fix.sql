-- ============================================
-- 脚本：V1.1.9__regulation_menu_fix.sql
-- 版本：1.1.9
-- 日期：2026-04-26
-- 描述：修复法律法规模块菜单结构（定性依据作为法律法规子菜单）
-- ============================================

-- Step 1: 删除现有的法律相关菜单

-- 先查询需要删除的菜单ID
SET @batchId = (SELECT menu_id FROM sys_menu WHERE menu_name = '批量操作' LIMIT 1);
SET @chapterId = (SELECT menu_id FROM sys_menu WHERE menu_name = '章节管理' LIMIT 1);
SET @articleId = (SELECT menu_id FROM sys_menu WHERE menu_name = '条款管理' LIMIT 1);
SET @regulationId = (SELECT menu_id FROM sys_menu WHERE menu_name = '法律法规' AND parent_id = 0 LIMIT 1);
SET @legalBasisId = (SELECT menu_id FROM sys_menu WHERE menu_name = '定性依据' AND parent_id = 0 LIMIT 1);

-- 删除 V1.1.8 批量操作菜单下的按钮
DELETE FROM sys_menu WHERE parent_id = @batchId;

-- 删除 V1.1.8 批量操作菜单
DELETE FROM sys_menu WHERE menu_id = @batchId;

-- 删除 V1.1.8 条款管理按钮
DELETE FROM sys_menu WHERE parent_id = @articleId;

-- 删除 V1.1.8 条款管理菜单
DELETE FROM sys_menu WHERE menu_id = @articleId;

-- 删除 V1.1.8 章节管理按钮
DELETE FROM sys_menu WHERE parent_id = @chapterId;

-- 删除 V1.1.8 章节管理菜单
DELETE FROM sys_menu WHERE menu_id = @chapterId;

-- 删除 V1.1.8 法律法规按钮
DELETE FROM sys_menu WHERE parent_id = @regulationId;

-- 删除 V1.1.8 法律法规菜单（顶级）
DELETE FROM sys_menu WHERE menu_id = @regulationId;

-- 删除 V1.1.8 定性依据按钮
DELETE FROM sys_menu WHERE parent_id = @legalBasisId;

-- 删除 V1.1.8 定性依据菜单（顶级）
DELETE FROM sys_menu WHERE menu_id = @legalBasisId;

-- Step 2: 创建顶级菜单（parent_id = 0）

-- 法律法规顶级菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('法律法规', '0', '5', 'regulation', NULL, 1, 0, 'M', '0', '0', '', 'book', 'admin', NOW(), '法律法规顶级菜单');

-- 获取法律法规顶级目录ID
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

-- 定性依据管理菜单（作为法律法规的子菜单）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('定性依据', @regulationParentId, '5', 'legalBasis', 'system/legalBasis/index', 1, 0, 'C', '0', '0', 'system:legalBasis:list', '#', 'admin', NOW(), '定性依据管理菜单');

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
SELECT '法律法规模块菜单修复完成（定性依据作为子菜单）' AS result;
