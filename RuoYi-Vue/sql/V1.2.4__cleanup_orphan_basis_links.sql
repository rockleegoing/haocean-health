-- V1.2.4__cleanup_orphan_basis_links.sql
-- 清理孤立的依据关联记录
-- 这些记录的 basis_id 在对应的依据表中不存在

-- 删除孤立的处理依据关联（引用了不存在的 sys_processing_basis）
DELETE FROM sys_basis_chapter_link
WHERE basis_type = 'processing'
AND basis_id IS NOT NULL
AND basis_id NOT IN (SELECT basis_id FROM sys_processing_basis);

-- 删除孤立的定性依据关联（引用了不存在的 sys_legal_basis）
DELETE FROM sys_basis_chapter_link
WHERE basis_type = 'legal'
AND basis_id IS NOT NULL
AND basis_id NOT IN (SELECT basis_id FROM sys_legal_basis);

-- 验证清理结果
SELECT '处理依据关联清理后数量' AS description, COUNT(*) AS count FROM sys_basis_chapter_link WHERE basis_type = 'processing'
UNION ALL
SELECT '定性依据关联清理后数量' AS description, COUNT(*) AS count FROM sys_basis_chapter_link WHERE basis_type = 'legal';
