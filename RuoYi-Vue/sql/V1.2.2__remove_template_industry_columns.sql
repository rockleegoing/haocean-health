-- V1.2.2__remove_template_industry_columns.sql
-- 移除 sys_document_template 表的冗余行业分类字段
-- 行业分类与模板的关联已通过中间表 sys_document_template_industry 管理

-- 移除冗余字段
ALTER TABLE sys_document_template
DROP COLUMN IF EXISTS industry_category_id,
DROP COLUMN IF EXISTS industry_category_name;
