-- 给文书模板表添加分类和排序字段
ALTER TABLE sys_document_template
ADD COLUMN category_id BIGINT(20) DEFAULT 0 COMMENT '分类ID，0表示其他模板' AFTER template_name,
ADD COLUMN sort INT(11) NOT NULL DEFAULT 0 COMMENT '排序';
