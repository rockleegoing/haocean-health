-- ============================================
-- 规范用语模块数据库脚本
-- 创建日期: 2026-04-25
-- ============================================

-- 1. 书本表
DROP TABLE IF EXISTS sys_phrase_book;
CREATE TABLE sys_phrase_book (
    book_id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '书本ID',
    book_name        VARCHAR(128) NOT NULL COMMENT '书本名称',
    book_code        VARCHAR(64) NOT NULL COMMENT '书本编码',
    book_desc        VARCHAR(512) COMMENT '书本描述',
    industry_code    VARCHAR(32) COMMENT '适用行业编码',
    industry_name    VARCHAR(128) COMMENT '适用行业名称',
    cover_url        VARCHAR(256) COMMENT '封面图片URL',
    sort_order       INT DEFAULT 0 COMMENT '排序号',
    status           CHAR(1) DEFAULT '0' COMMENT '状态:0正常,1停用',
    version          INT DEFAULT 1 COMMENT '版本号',
    del_flag         CHAR(1) DEFAULT '0' COMMENT '删除标志:0存在,1删除',
    create_by        VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time      DATETIME COMMENT '创建时间',
    update_by        VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time      DATETIME COMMENT '更新时间',
    remark           VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_book_code (book_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规范用语书本表';

-- 2. 用语项表
DROP TABLE IF EXISTS sys_phrase_item;
CREATE TABLE sys_phrase_item (
    item_id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '项ID',
    book_id          BIGINT NOT NULL COMMENT '所属书本ID',
    item_name        VARCHAR(128) NOT NULL COMMENT '项名称',
    item_code        VARCHAR(64) NOT NULL COMMENT '项编码',
    item_desc        VARCHAR(512) COMMENT '项描述',
    phase_type       VARCHAR(32) COMMENT '环节类型:CHECK_BEFORE-检查前,CHECK_ING-检查中,CHECK_AFTER-检查后',
    scene_type       VARCHAR(32) COMMENT '场景类型',
    industry_code    VARCHAR(32) COMMENT '适用行业编码',
    sort_order       INT DEFAULT 0 COMMENT '排序号',
    status           CHAR(1) DEFAULT '0' COMMENT '状态:0正常,1停用',
    version          INT DEFAULT 1 COMMENT '版本号',
    del_flag         CHAR(1) DEFAULT '0' COMMENT '删除标志:0存在,1删除',
    create_by        VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time      DATETIME COMMENT '创建时间',
    update_by        VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time      DATETIME COMMENT '更新时间',
    remark           VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_item_code (item_code),
    KEY idx_book_id (book_id),
    KEY idx_phase_type (phase_type),
    KEY idx_scene_type (scene_type),
    KEY idx_industry (industry_code),
    CONSTRAINT fk_phrase_item_book FOREIGN KEY (book_id) REFERENCES sys_phrase_book(book_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规范用语项表';

-- 3. 项明细表
DROP TABLE IF EXISTS sys_phrase_detail;
CREATE TABLE sys_phrase_detail (
    detail_id        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    item_id          BIGINT NOT NULL COMMENT '所属项ID',
    detail_title     VARCHAR(256) NOT NULL COMMENT '明细标题',
    detail_content   TEXT NOT NULL COMMENT '用语内容',
    detail_type      VARCHAR(32) COMMENT '明细类型:TEXT-文本,HTML-HTML内容',
    sort_order       INT DEFAULT 0 COMMENT '排序号',
    version          INT DEFAULT 1 COMMENT '版本号',
    del_flag         CHAR(1) DEFAULT '0' COMMENT '删除标志:0存在,1删除',
    create_by        VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time      DATETIME COMMENT '创建时间',
    update_by        VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time      DATETIME COMMENT '更新时间',
    remark           VARCHAR(500) COMMENT '备注',
    KEY idx_item_id (item_id),
    CONSTRAINT fk_phrase_detail_item FOREIGN KEY (item_id) REFERENCES sys_phrase_item(item_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规范用语项明细表';

-- 4. 索引优化
CREATE INDEX idx_phrase_item_filter ON sys_phrase_item(phase_type, scene_type, industry_code, del_flag);
CREATE INDEX idx_phrase_item_book_phase ON sys_phrase_item(book_id, phase_type, del_flag);
CREATE INDEX idx_phrase_book_update ON sys_phrase_book(update_time, del_flag);
CREATE INDEX idx_phrase_item_update ON sys_phrase_item(update_time, del_flag);
CREATE INDEX idx_phrase_detail_update ON sys_phrase_detail(update_time, del_flag);

-- ============================================
-- 测试数据
-- ============================================

-- 插入测试书本
INSERT INTO sys_phrase_book (book_name, book_code, book_desc, industry_code, industry_name, sort_order, status, version, create_time) VALUES
('公共场所卫生规范用语', 'PHRASE_001', '公共场所卫生监督检查常用规范用语', 'IND_001', '公共场所卫生', 1, '0', 1, NOW()),
('医疗机构规范用语', 'PHRASE_002', '医疗机构卫生监督检查常用规范用语', 'IND_002', '医疗机构卫生', 2, '0', 1, NOW()),
('学校卫生规范用语', 'PHRASE_003', '学校卫生监督检查常用规范用语', 'IND_003', '学校卫生', 3, '0', 1, NOW());

-- 插入测试项
INSERT INTO sys_phrase_item (book_id, item_name, item_code, item_desc, phase_type, scene_type, industry_code, sort_order, status, version, create_time) VALUES
(1, '证照公示检查', 'PHRASE_ITEM_001', '检查公共场所证照公示情况', 'CHECK_BEFORE', '证照检查', 'IND_001', 1, '0', 1, NOW()),
(1, '环境卫生检查', 'PHRASE_ITEM_002', '检查公共场所环境卫生状况', 'CHECK_ING', '现场检查', 'IND_001', 2, '0', 1, NOW()),
(1, '检查记录填写', 'PHRASE_ITEM_003', '检查完毕后填写检查记录', 'CHECK_AFTER', '文书填写', 'IND_001', 3, '0', 1, NOW()),
(2, '医疗机构资质检查', 'PHRASE_ITEM_004', '检查医疗机构执业许可证', 'CHECK_BEFORE', '资质检查', 'IND_002', 1, '0', 1, NOW()),
(2, '诊疗行为检查', 'PHRASE_ITEM_005', '检查医护人员诊疗行为', 'CHECK_ING', '诊疗检查', 'IND_002', 2, '0', 1, NOW());

-- 插入测试明细
INSERT INTO sys_phrase_detail (item_id, detail_title, detail_content, detail_type, sort_order, version, create_time) VALUES
(1, '开场白', '您好，我们是XX卫生监督局的执法人员，这是我们的执法证件（出示证件）。根据《公共场所卫生管理条例》的规定，我们依法对贵单位进行卫生监督检查。', 'TEXT', 1, 1, NOW()),
(1, '证照要求', '请将贵单位的公共场所卫生许可证、从业人员健康证明等相关证照悬挂于明显位置。', 'TEXT', 2, 1, NOW()),
(1, '证照核查', '经检查，贵单位卫生许可证有效，从业人员健康证明齐全，符合《公共场所卫生管理条例》的要求。', 'TEXT', 3, 1, NOW()),
(2, '环境检查', '现在我们对贵单位的卫生环境进行检查，请予以配合。', 'TEXT', 1, 1, NOW()),
(2, '结果记录', '经检查，贵单位卫生状况良好，各项卫生设施运转正常。', 'TEXT', 2, 1, NOW()),
(3, '结束语', '检查完毕，感谢贵单位的配合。如有任何卫生方面的问题，请及时与我们联系。', 'TEXT', 1, 1, NOW());
