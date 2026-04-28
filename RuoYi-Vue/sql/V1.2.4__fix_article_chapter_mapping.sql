-- ============================================
-- 脚本：V1.2.4__fix_article_chapter_mapping.sql
-- 版本：1.2.4
-- 日期：2026-04-28
-- 描述：修复条款表的chapter_id关联错误
-- 问题：regulation_id=2 的条款使用了 regulation_id=1 的章节ID
-- ============================================

-- 查看当前错误数据
SELECT '当前错误的条款数据:' AS '';
SELECT article_id, chapter_id, regulation_id, article_no FROM sys_regulation_article WHERE regulation_id = 2;

-- 查看 regulation_id=2 的正确章节
SELECT 'regulation_id=2 的章节:' AS '';
SELECT chapter_id, regulation_id, chapter_no FROM sys_regulation_chapter WHERE regulation_id = 2;

-- 修复条款的 chapter_id
-- article_id=4: 第一条 应属于 第一章 (chapter_id=4)
-- article_id=16,17: 第一条/第二条 应属于 第三章 (chapter_id=10)
-- article_id=18,19: 第一条/第二条 应属于 第四章 (chapter_id=11)
-- article_id=20,21: 第一条/第二条 应属于 第五章 (chapter_id=12)

UPDATE sys_regulation_article SET chapter_id = 4 WHERE article_id = 4 AND regulation_id = 2;
UPDATE sys_regulation_article SET chapter_id = 10 WHERE article_id IN (16, 17) AND regulation_id = 2;
UPDATE sys_regulation_article SET chapter_id = 11 WHERE article_id IN (18, 19) AND regulation_id = 2;
UPDATE sys_regulation_article SET chapter_id = 12 WHERE article_id IN (20, 21) AND regulation_id = 2;

-- 验证修复结果
SELECT '修复后的条款数据:' AS '';
SELECT article_id, chapter_id, regulation_id, article_no FROM sys_regulation_article WHERE regulation_id = 2;
