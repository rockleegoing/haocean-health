-- 文书模板关联行业分类
-- 给文书模板表添加行业分类字段
ALTER TABLE sys_document_template
ADD COLUMN industry_category_id BIGINT(20) DEFAULT NULL COMMENT '行业分类ID' AFTER category,
ADD COLUMN industry_category_name VARCHAR(100) DEFAULT NULL COMMENT '行业分类名称' AFTER industry_category_id;

-- 创建文书模板与行业分类关联表
CREATE TABLE sys_document_template_industry (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    template_id BIGINT(20) NOT NULL COMMENT '文书模板ID',
    industry_category_id BIGINT(20) NOT NULL COMMENT '行业分类ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_industry (template_id, industry_category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书模板与行业分类关联表';
