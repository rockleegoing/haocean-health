-- ============================================
-- 脚本：V1.1.0__sys_regulation_tables.sql
-- 版本：1.1.0
-- 日期：2026-04-25
-- 描述：创建法律法规相关表
-- 作者：Claude
-- ============================================

-- 1. 法律法规主表
CREATE TABLE IF NOT EXISTS `sys_regulation` (
    `regulation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '法律法规ID',
    `title` varchar(255) NOT NULL COMMENT '法律名称',
    `legal_type` varchar(20) NOT NULL COMMENT '法律类型：法律/法规/规章/规范性文件/批复文件/标准',
    `supervision_types` varchar(500) DEFAULT NULL COMMENT '监管类型列表（JSON数组）',
    `publish_date` varchar(20) DEFAULT NULL COMMENT '发布日期',
    `effective_date` varchar(20) DEFAULT NULL COMMENT '实施日期',
    `issuing_authority` varchar(100) DEFAULT NULL COMMENT '颁发机构',
    `content` text COMMENT '完整内容',
    `version` varchar(20) DEFAULT '1.0' COMMENT '版本号',
    `status` char(1) DEFAULT '0' COMMENT '状态：0正常 1停用',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志：0存在 1删除',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`regulation_id`),
    KEY `idx_legal_type` (`legal_type`),
    KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='法律法规表';

-- 2. 章节表
CREATE TABLE IF NOT EXISTS `sys_regulation_chapter` (
    `chapter_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '章节ID',
    `regulation_id` bigint(20) NOT NULL COMMENT '关联法律法规ID',
    `chapter_no` varchar(50) DEFAULT NULL COMMENT '章节号（如：第一章）',
    `chapter_title` varchar(255) DEFAULT NULL COMMENT '章节标题',
    `sort_order` int(11) DEFAULT 0 COMMENT '排序',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`chapter_id`),
    KEY `idx_regulation_id` (`regulation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='法规章节表';

-- 3. 条款表
CREATE TABLE IF NOT EXISTS `sys_regulation_article` (
    `article_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '条款ID',
    `chapter_id` bigint(20) DEFAULT NULL COMMENT '关联章节ID',
    `regulation_id` bigint(20) NOT NULL COMMENT '关联法律法规ID',
    `article_no` varchar(50) DEFAULT NULL COMMENT '条款号（如：第一条）',
    `content` text COMMENT '条款内容',
    `sort_order` int(11) DEFAULT 0 COMMENT '排序',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`article_id`),
    KEY `idx_chapter_id` (`chapter_id`),
    KEY `idx_regulation_id` (`regulation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='法规条款表';

-- 4. 定性依据表
CREATE TABLE IF NOT EXISTS `sys_legal_basis` (
    `basis_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '定性依据ID',
    `basis_no` varchar(20) DEFAULT NULL COMMENT '编号（如：001）',
    `title` varchar(255) NOT NULL COMMENT '标题',
    `violation_type` varchar(100) DEFAULT NULL COMMENT '违法类型',
    `issuing_authority` varchar(100) DEFAULT NULL COMMENT '颁发机构',
    `effective_date` varchar(20) DEFAULT NULL COMMENT '实施时间',
    `legal_level` varchar(20) DEFAULT NULL COMMENT '效级',
    `clauses` text COMMENT '条款内容（JSON数组）',
    `legal_liability` text COMMENT '法律责任',
    `discretion_standard` text COMMENT '裁量标准',
    `regulation_id` bigint(20) DEFAULT NULL COMMENT '关联法律法规ID',
    `status` char(1) DEFAULT '0' COMMENT '状态：0正常 1停用',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`basis_id`),
    KEY `idx_regulation_id` (`regulation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定性依据表';

-- 5. 收藏表
CREATE TABLE IF NOT EXISTS `sys_collection` (
    `collection_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `target_type` varchar(20) NOT NULL COMMENT '目标类型：regulation/article/basis',
    `target_id` bigint(20) NOT NULL COMMENT '目标ID',
    `create_time` datetime DEFAULT NULL COMMENT '收藏时间',
    PRIMARY KEY (`collection_id`),
    UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
    KEY `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 6. 初始化示例数据
INSERT INTO `sys_regulation` (`regulation_id`, `title`, `legal_type`, `supervision_types`, `publish_date`, `effective_date`, `issuing_authority`, `content`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
(1, '中华人民共和国基本医疗卫生与健康促进法', '法律', '["医疗机构"]', '2019-12-28', '2020-06-01', '全国人民代表大会常务委员会', '为了发展医疗卫生与健康事业，保障公民享有基本医疗卫生服务，提升公民全生命周期健康水平，制定本法。', '0', '0', 'admin', NOW()),
(2, '中华人民共和国生物安全法', '法律', '["公共场所"]', '2020-10-17', '2021-04-15', '全国人民代表大会常务委员会', '为了维护国家安全，防范和应对生物安全风险，保障人民生命健康，保护生物资源和生态环境，促进生物技术健康发展，制定本法。', '0', '0', 'admin', NOW());

-- 章节示例数据
INSERT INTO `sys_regulation_chapter` (`regulation_id`, `chapter_no`, `chapter_title`, `sort_order`, `create_by`, `create_time`) VALUES
(1, '第一章', '总则', 1, 'admin', NOW()),
(1, '第二章', '基本医疗卫生服务', 2, 'admin', NOW()),
(1, '第三章', '医疗机构', 3, 'admin', NOW()),
(2, '第一章', '总则', 1, 'admin', NOW()),
(2, '第二章', '生物安全风险防控体制', 2, 'admin', NOW());

-- 条款示例数据
INSERT INTO `sys_regulation_article` (`regulation_id`, `chapter_id`, `article_no`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(1, 1, '第一条', '为了发展医疗卫生与健康事业，保障公民享有基本医疗卫生服务，提升公民全生命周期健康水平，根据宪法，制定本法。', 1, 'admin', NOW()),
(1, 1, '第二条', '国家和社会尊重、保护公民的健康权。国家实施健康中国战略，建立健康教育制度体系，提升公民健康素养。', 2, 'admin', NOW()),
(1, 2, '第一条', '基本医疗卫生服务，是指维护人体健康所必需、与经济社会发展水平相适应、公民可以公平获得的医疗卫生服务。', 1, 'admin', NOW()),
(2, 1, '第一条', '为了维护国家安全，防范和应对生物安全风险，保障人民生命健康，保护生物资源和生态环境，促进生物技术健康发展，根据宪法，制定本法。', 1, 'admin', NOW());

-- 定性依据示例数据
INSERT INTO `sys_legal_basis` (`basis_no`, `title`, `violation_type`, `issuing_authority`, `effective_date`, `legal_level`, `clauses`, `legal_liability`, `discretion_standard`, `regulation_id`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
('001', '超出资质认可范围从事诊疗活动', '超出资质认可范围', '卫健委', '2020-06-01', '法律', '["基本医疗卫生与健康促进法第三十八条"]', '由县级以上人民政府卫生健康主管部门责令停止执业，没收违法所得，并处罚款；情节严重的，吊销执业证书。', '轻微：责令停止执业，处1万元以下罚款；一般：责令停止执业，处1-3万元罚款；严重：责令停止执业，处3-5万元罚款，吊销执业证书。', 1, '0', '0', 'admin', NOW()),
('002', '医疗卫生机构等未按规定报告传染病疫情', '传染病防治', '卫健委', '2020-06-01', '法律', '["传染病防治法第三十条"]', '由属地卫生健康主管部门责令改正，通报批评，给予警告；对负有责任的主管人员和其他直接责任人员，依法给予处分。', '轻微：责令改正，警告；一般：责令改正，通报批评；严重：给予处分，构成犯罪的，依法追究刑事责任。', 1, '0', '0', 'admin', NOW());
