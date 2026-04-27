-- ============================================
-- 脚本：V1.2.3__processing_basis_tables.sql
-- 版本：1.2.3
-- 日期：2026-04-28
-- 描述：创建处理依据表和依据章节关联表
-- ============================================

-- 1. 处理依据表（与定性依据表结构一致）
CREATE TABLE IF NOT EXISTS `sys_processing_basis` (
    `basis_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '处理依据ID',
    `basis_no` varchar(20) DEFAULT NULL COMMENT '编号',
    `title` varchar(255) NOT NULL COMMENT '标题',
    `violation_type` varchar(100) DEFAULT NULL COMMENT '违法类型',
    `issuing_authority` varchar(100) DEFAULT NULL COMMENT '颁发机构',
    `effective_date` varchar(20) DEFAULT NULL COMMENT '实施时间',
    `legal_level` varchar(20) DEFAULT NULL COMMENT '法律层级',
    `clauses` text COMMENT '条款内容（JSON数组）',
    `legal_liability` text COMMENT '法律责任',
    `discretion_standard` text COMMENT '裁量标准',
    `regulation_id` bigint(20) DEFAULT NULL COMMENT '关联法规ID',
    `status` char(1) DEFAULT '0' COMMENT '状态：0正常 1停用',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`basis_id`),
    KEY `idx_regulation_id` (`regulation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处理依据表';

-- 2. 依据章节关联表
CREATE TABLE IF NOT EXISTS `sys_basis_chapter_link` (
    `link_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `basis_type` varchar(20) NOT NULL COMMENT '关联类型：legal/processing',
    `basis_id` bigint(20) NOT NULL COMMENT '依据ID',
    `chapter_id` bigint(20) NOT NULL COMMENT '章节ID',
    `article_id` bigint(20) DEFAULT NULL COMMENT '条款ID',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    PRIMARY KEY (`link_id`),
    UNIQUE KEY `uk_basis_chapter_article` (`basis_type`, `basis_id`, `chapter_id`, `article_id`),
    KEY `idx_chapter` (`chapter_id`),
    KEY `idx_article` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='依据章节关联表';

-- 3. 菜单数据
-- 处理依据管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '处理依据管理', menu_id, 6, 'processingBasis', 'system/processingBasis/index', 'C', '0', '0', 'system:processingBasis:list', 'form', 'admin', sysdate()
FROM sys_menu WHERE menu_name = '法律法规' AND del_flag = '0';

-- 依据关联管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '依据关联管理', menu_id, 7, 'basisLink', 'system/basisLink/index', 'C', '0', '0', 'system:basisLink:list', 'link', 'admin', sysdate()
FROM sys_menu WHERE menu_name = '法律法规' AND del_flag = '0';

-- 4. 权限数据
INSERT INTO sys_post (post_code, post_name, post_sort, status, create_by, create_time) VALUES
('processingBasis:add', '处理依据新增', 1, '0', 'admin', sysdate()),
('processingBasis:edit', '处理依据修改', 2, '0', 'admin', sysdate()),
('processingBasis:remove', '处理依据删除', 3, '0', 'admin', sysdate()),
('processingBasis:list', '处理依据查询', 4, '0', 'admin', sysdate()),
('basisLink:add', '关联新增', 5, '0', 'admin', sysdate()),
('basisLink:remove', '关联删除', 6, '0', 'admin', sysdate()),
('basisLink:list', '关联查询', 7, '0', 'admin', sysdate());
