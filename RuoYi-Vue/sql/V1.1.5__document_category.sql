-- 文书分类表
CREATE TABLE sys_document_category (
    category_id    BIGINT(20)      NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    category_name  VARCHAR(100)    NOT NULL                  COMMENT '分类名称',
    display_type   VARCHAR(20)     NOT NULL DEFAULT 'grid'   COMMENT '展示方式：grid=九宫格，table=表格，list=列表',
    sort           INT(11)         NOT NULL DEFAULT 0        COMMENT '排序',
    status         CHAR(1)         NOT NULL DEFAULT '0'      COMMENT '状态（0正常 1停用）',
    create_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书分类表';

-- 初始化内置"其他模板"分类（category_id=0）
INSERT INTO sys_document_category (category_id, category_name, display_type, sort, status) VALUES (0, '其他模板', 'grid', 999, '0');
