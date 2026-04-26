-- ============================================
-- 脚本：V1.1.6__document_mock_data.sql
-- 描述：插入模拟文书分类和模板数据
-- 日期：2026-04-25
-- ============================================

-- 插入文书分类数据
INSERT INTO sys_document_category (category_id, category_name, display_type, sort, status) VALUES
(1, '快速制作文书', 'grid', 1, '0'),
(2, '其他模板', 'table', 2, '0');

-- 更新模板的 category 字段，使其与分类名称匹配
-- 先插入新的模拟模板数据（如果还没插入的话）
INSERT INTO `sys_document_template` (`template_code`, `template_name`, `template_type`, `category`, `file_url`, `version`, `is_active`, `del_flag`, `remark`) VALUES
-- 快速制作文书分类（categoryId=1）
('WS001', '现场笔录', 'CHECK_RECORD', '快速制作文书', '/templates/ws001.docx', 1, '1', '0', '公共场所日常监督检查现场笔录'),
('WS002', '卫生监督意见书', 'OPINION', '快速制作文书', '/templates/ws002.docx', 1, '1', '0', '卫生监督意见书模板'),
('WS003', '询问笔录', 'INQUIRY', '快速制作文书', '/templates/ws003.docx', 1, '1', '0', '询问笔录模板'),
('WS004', '送达回证', 'RECEIPT', '快速制作文书', '/templates/ws004.docx', 1, '1', '0', '送达回证模板'),
('WS005', '听证告知书', 'HEARING', '快速制作文书', '/templates/ws005.docx', 1, '1', '0', '听证告知书模板'),
('WS006', '陈述申辩笔录', 'DEFENSE', '快速制作文书', '/templates/ws006.docx', 1, '1', '0', '陈述申辩笔录模板'),
('WS007', '案件处理内部审批表', 'APPROVAL', '快速制作文书', '/templates/ws007.docx', 1, '1', '0', '案件处理内部审批表'),
('WS008', '结案报告', 'CLOSE', '快速制作文书', '/templates/ws008.docx', 1, '1', '0', '结案报告模板'),
('WS009', '合议笔录', 'CONSULTATION', '快速制作文书', '/templates/ws009.docx', 1, '1', '0', '合议笔录模板'),

-- 其他模板分类（categoryId=2）
('WS010', '行政处罚决定书', 'PUNISHMENT', '其他模板', '/templates/ws010.docx', 1, '1', '0', '行政处罚决定书模板'),
('WS011', '当场行政处罚决定书', 'PUNISHMENT_QUICK', '其他模板', '/templates/ws011.docx', 1, '1', '0', '当场行政处罚决定书模板'),
('WS012', '证据先行登记保存决定书', 'EVIDENCE', '其他模板', '/templates/ws012.docx', 1, '1', '0', '证据先行登记保存决定书模板'),
('WS013', '证据先行登记保存处理决定书', 'EVIDENCE_HANDLE', '其他模板', '/templates/ws013.docx', 1, '1', '0', '证据先行登记保存处理决定书'),
('WS014', '查封扣押决定书', 'SEAL', '其他模板', '/templates/ws014.docx', 1, '1', '0', '查封扣押决定书模板'),
('WS015', '行政强制执行决定书', 'ENFORCEMENT', '其他模板', '/templates/ws015.docx', 1, '1', '0', '行政强制执行决定书'),
('WS016', '限期整改通知书', 'RECTIFICATION', '其他模板', '/templates/ws016.docx', 1, '1', '0', '限期整改通知书模板'),
('WS017', '强制检测决定书', 'TESTING', '其他模板', '/templates/ws017.docx', 1, '1', '0', '强制检测决定书模板'),
('WS018', '行政听证告知书', 'ADMIN_HEARING', '其他模板', '/templates/ws018.docx', 1, '1', '0', '行政听证告知书'),
('WS019', '行政处罚事先告知书', 'PUNISHMENT_PRIOR', '其他模板', '/templates/ws019.docx', 1, '1', '0', '行政处罚事先告知书'),
('WS020', '行政复议决定书', 'ADMIN_REVIEW', '其他模板', '/templates/ws020.docx', 1, '1', '0', '行政行政复议决定书')
ON DUPLICATE KEY UPDATE
    template_name = VALUES(template_name),
    category = VALUES(category);
