-- ============================================
-- 脚本：V1.2.3__processing_basis_tables.sql
-- 版本：1.2.3
-- 日期：2026-04-28
-- 描述：创建处理依据表和依据关联表
-- ============================================

-- 1. 处理依据表
CREATE TABLE IF NOT EXISTS `sys_processing_basis` (
    `basis_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '处理依据ID',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处理依据表';

-- 2. 章节-依据关联表（多对多）
CREATE TABLE IF NOT EXISTS `sys_basis_chapter_link` (
    `link_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `chapter_id` bigint(20) NOT NULL COMMENT '章节ID',
    `article_id` bigint(20) DEFAULT NULL COMMENT '条款ID（可为NULL表示关联整个章节）',
    `basis_type` varchar(20) NOT NULL COMMENT '依据类型：legal定性/processing处理',
    `basis_id` bigint(20) NOT NULL COMMENT '依据ID',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`link_id`),
    KEY `idx_chapter_id` (`chapter_id`),
    KEY `idx_article_id` (`article_id`),
    KEY `idx_basis_type` (`basis_type`),
    KEY `idx_basis_id` (`basis_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='章节依据关联表';

-- 3. 获取法律法规目录ID
SET @regulationParentId = (SELECT menu_id FROM sys_menu WHERE menu_name = '法律法规' AND parent_id = 0);

-- 4. 检查菜单是否已存在
SET @existProcessingBasis = (SELECT COUNT(1) FROM sys_menu WHERE menu_name = '处理依据管理' AND parent_id = @regulationParentId);
SET @existBasisLink = (SELECT COUNT(1) FROM sys_menu WHERE menu_name = '依据关联管理' AND parent_id = @regulationParentId);

-- 5. 处理依据管理菜单（order=8）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '处理依据管理', @regulationParentId, '8', 'processingBasis', 'system/regulation/processingBasis/index', 1, 0, 'C', '0', '0', 'system:processingBasis:list', '#', 'admin', NOW(), '处理依据管理菜单'
FROM DUAL WHERE @existProcessingBasis = 0;

-- 获取处理依据管理按钮父ID
SET @processingBasisBtnParentId = LAST_INSERT_ID();

-- 处理依据管理按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '处理依据查询', @processingBasisBtnParentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:processingBasis:query', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existProcessingBasis = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '处理依据新增', @processingBasisBtnParentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:processingBasis:add', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existProcessingBasis = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '处理依据修改', @processingBasisBtnParentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:processingBasis:edit', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existProcessingBasis = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '处理依据删除', @processingBasisBtnParentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:processingBasis:remove', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existProcessingBasis = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '处理依据导出', @processingBasisBtnParentId, '5', '#', '', 1, 0, 'F', '0', '0', 'system:processingBasis:export', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existProcessingBasis = 0;

-- 6. 依据关联管理菜单（order=9）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '依据关联管理', @regulationParentId, '9', 'basisLink', 'system/regulation/basisLink/index', 1, 0, 'C', '0', '0', 'system:basisLink:list', '#', 'admin', NOW(), '依据关联管理菜单'
FROM DUAL WHERE @existBasisLink = 0;

-- 获取依据关联管理按钮父ID
SET @basisLinkBtnParentId = LAST_INSERT_ID();

-- 依据关联管理按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '依据关联查询', @basisLinkBtnParentId, '1', '#', '', 1, 0, 'F', '0', '0', 'system:basisLink:query', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existBasisLink = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '依据关联新增', @basisLinkBtnParentId, '2', '#', '', 1, 0, 'F', '0', '0', 'system:basisLink:add', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existBasisLink = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '依据关联修改', @basisLinkBtnParentId, '3', '#', '', 1, 0, 'F', '0', '0', 'system:basisLink:edit', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existBasisLink = 0;

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '依据关联删除', @basisLinkBtnParentId, '4', '#', '', 1, 0, 'F', '0', '0', 'system:basisLink:remove', '#', 'admin', NOW(), ''
FROM DUAL WHERE @existBasisLink = 0;

-- 7. 初始化示例数据
INSERT INTO `sys_processing_basis` (`basis_no`, `title`, `violation_type`, `issuing_authority`, `effective_date`, `legal_level`, `clauses`, `legal_liability`, `discretion_standard`, `regulation_id`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
('001', '未经许可擅自从事诊疗活动', '无证行医', '卫健委', '2020-06-01', '法规', '["基本医疗卫生与健康促进法第三十八条"]', '由县级以上人民政府卫生健康主管部门责令停止执业，没收违法所得，并处罚款；情节严重的，吊销执业证书。', '轻微：责令停止执业，处1万元以下罚款；一般：责令停止执业，处1-3万元罚款；严重：责令停止执业，处3-5万元罚款，吊销执业证书。', 1, '0', '0', 'admin', NOW()),
('002', '使用非卫生技术人员从事医疗卫生技术服务', '使用非卫技人员', '卫健委', '2020-06-01', '法规', '["医疗机构管理条例第二十八条"]', '由县级以上人民政府卫生健康主管部门责令限期改正，并可处以5000元以下罚款；情节严重的，吊销其《医疗机构执业许可证》。', '轻微：责令限期改正；一般：处以3000元以下罚款；严重：处以3000-5000元罚款，吊销执业许可证。', 1, '0', '0', 'admin', NOW());
