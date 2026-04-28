-- ============================================
-- 脚本：V1.2.8__legal_basis_content_migration.sql
-- 版本：1.2.8
-- 日期：2026-04-28
-- 描述：为现有定性依据记录补充内容数据
-- ============================================

-- 定性依据内容标签定义
-- sort_order: 1-编号, 2-违法类型, 3-颁发机构, 4-实施时间, 5-效级, 6-条款内容, 7-法律责任, 8-裁量标准

-- 基础医疗卫生与健康促进法 相关定性依据 (regulation_id=1)
INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(1, '编号', 'LB001', 1, 'admin', NOW()),
(1, '违法类型', '超出资质认可范围', 2, 'admin', NOW()),
(1, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(1, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(1, '效级', '法律', 5, 'admin', NOW()),
(1, '条款内容', '基本医疗卫生与健康促进法第三十八条：医疗卫生机构不得使用非卫生技术人员从事医疗卫生技术工作。', 6, 'admin', NOW()),
(1, '法律责任', '由县级以上人民政府卫生健康主管部门责令停止执业，没收违法所得，并处罚款；情节严重的，吊销执业证书。', 7, 'admin', NOW()),
(1, '裁量标准', '轻微：责令停止执业，处1万元以下罚款；一般：责令停止执业，处1-3万元罚款；严重：责令停止执业，处3-5万元罚款，吊销执业证书。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(2, '编号', 'LB002', 1, 'admin', NOW()),
(2, '违法类型', '传染病防治', 2, 'admin', NOW()),
(2, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(2, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(2, '效级', '法律', 5, 'admin', NOW()),
(2, '条款内容', '传染病防治法第三十条：疾病预防控制机构、医疗机构和采供血机构及其工作人员发现传染病疫情，应当按照规定及时报告。', 6, 'admin', NOW()),
(2, '法律责任', '由属地卫生健康主管部门责令改正，通报批评，给予警告；对负有责任的主管人员和其他直接责任人员，依法给予处分。', 7, 'admin', NOW()),
(2, '裁量标准', '轻微：责令改正，警告；一般：责令改正，通报批评；严重：给予处分，构成犯罪的，依法追究刑事责任。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(3, '编号', 'LB003', 1, 'admin', NOW()),
(3, '违法类型', '使用非卫生技术人员', 2, 'admin', NOW()),
(3, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(3, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(3, '效级', '法律', 5, 'admin', NOW()),
(3, '条款内容', '基本医疗卫生与健康促进法第三十八条：医疗卫生机构不得使用非卫生技术人员从事医疗卫生技术工作。', 6, 'admin', NOW()),
(3, '法律责任', '由县级以上人民政府卫生健康主管部门责令停止执业，没收违法所得，并处罚款；情节严重的，吊销执业证书。', 7, 'admin', NOW()),
(3, '裁量标准', '轻微：责令停止执业，处1万元以下罚款；一般：责令停止执业，处1-3万元罚款；严重：责令停止执业，处3-5万元罚款，吊销执业证书。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(4, '编号', 'LB004', 1, 'admin', NOW()),
(4, '违法类型', '医疗机构未按规定报告传染病疫情', 2, 'admin', NOW()),
(4, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(4, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(4, '效级', '法律', 5, 'admin', NOW()),
(4, '条款内容', '传染病防治法第三十条：疾病预防控制机构、医疗机构和采供血机构及其工作人员发现传染病疫情，应当按照规定及时报告。', 6, 'admin', NOW()),
(4, '法律责任', '由属地卫生健康主管部门责令改正，通报批评，给予警告。', 7, 'admin', NOW()),
(4, '裁量标准', '轻微：责令改正，警告；一般：责令改正，通报批评；严重：给予处分。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(5, '编号', 'LB005', 1, 'admin', NOW()),
(5, '违法类型', '医疗机构工作人员不履行报告职责', 2, 'admin', NOW()),
(5, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(5, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(5, '效级', '法律', 5, 'admin', NOW()),
(5, '条款内容', '传染病防治法第三十七条：疾病预防控制机构、医疗机构和采供血机构及其工作人员发现传染病疫情，应当按照规定及时报告。', 6, 'admin', NOW()),
(5, '法律责任', '对负有责任的主管人员和直接责任人员，依法给予处分。', 7, 'admin', NOW()),
(5, '裁量标准', '轻微：通报批评；一般：警告处分；严重：降级或撤职处分。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(6, '编号', 'LB006', 1, 'admin', NOW()),
(6, '违法类型', '违反医疗质量管理规定', 2, 'admin', NOW()),
(6, '颁发机构', '国务院', 3, 'admin', NOW()),
(6, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(6, '效级', '行政法规', 5, 'admin', NOW()),
(6, '条款内容', '医疗质量管理条例第二十二条：医疗机构应当建立健全医疗质量管理制度。', 6, 'admin', NOW()),
(6, '法律责任', '由卫生健康主管部门责令改正，视情节给予警告或罚款。', 7, 'admin', NOW()),
(6, '裁量标准', '轻微：责令改正；一般：警告；严重：处以1-3万元罚款。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(7, '编号', 'LB007', 1, 'admin', NOW()),
(7, '违法类型', '未建立医疗质量安全管理制度', 2, 'admin', NOW()),
(7, '颁发机构', '国务院', 3, 'admin', NOW()),
(7, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(7, '效级', '行政法规', 5, 'admin', NOW()),
(7, '条款内容', '医疗质量管理条例第二十二条：医疗机构应当建立健全医疗质量管理制度。', 6, 'admin', NOW()),
(7, '法律责任', '由卫生健康主管部门责令改正，视情节给予警告或罚款。', 7, 'admin', NOW()),
(7, '裁量标准', '轻微：责令改正；一般：警告；严重：处以1-3万元罚款。', 8, 'admin', NOW());

-- 生物安全法 相关定性依据 (regulation_id=2)
INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(8, '编号', 'LB008', 1, 'admin', NOW()),
(8, '违法类型', '生物安全事件未按规定报告', 2, 'admin', NOW()),
(8, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(8, '实施时间', '2020-10-17', 4, 'admin', NOW()),
(8, '效级', '法律', 5, 'admin', NOW()),
(8, '条款内容', '生物安全法第四十六条：从事生物技术研究、开发与应用活动，应当遵守国家有关规定。', 6, 'admin', NOW()),
(8, '法律责任', '由主管部门责令改正，没收违法所得，并处罚款。', 7, 'admin', NOW()),
(8, '裁量标准', '轻微：责令改正；一般：罚款1-5万元；严重：罚款5-10万元。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(9, '编号', 'LB009', 1, 'admin', NOW()),
(9, '违法类型', '未建立生物安全管理制度', 2, 'admin', NOW()),
(9, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(9, '实施时间', '2020-10-17', 4, 'admin', NOW()),
(9, '效级', '法律', 5, 'admin', NOW()),
(9, '条款内容', '生物安全法第三十八条：从事生物技术研究、开发与应用活动的单位应当建立健全生物安全管理制度。', 6, 'admin', NOW()),
(9, '法律责任', '由主管部门责令改正，视情节给予警告或罚款。', 7, 'admin', NOW()),
(9, '裁量标准', '轻微：责令改正；一般：警告；严重：处以5万元以下罚款。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(10, '编号', 'LB010', 1, 'admin', NOW()),
(10, '违法类型', '违规开展高致病性病原微生物实验活动', 2, 'admin', NOW()),
(10, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(10, '实施时间', '2020-10-17', 4, 'admin', NOW()),
(10, '效级', '法律', 5, 'admin', NOW()),
(10, '条款内容', '生物安全法第四十六条：从事高致病性病原微生物实验活动，应当经国家认可实验室进行。', 6, 'admin', NOW()),
(10, '法律责任', '由主管部门责令停止实验，没收违法所得，并处罚款；情节严重的，吊销相关资质。', 7, 'admin', NOW()),
(10, '裁量标准', '轻微：责令改正；一般：停止实验，罚款5万元；严重：吊销资质。', 8, 'admin', NOW());

-- 医疗机构管理条例 相关定性依据 (regulation_id=3)
INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(11, '编号', 'LB011', 1, 'admin', NOW()),
(11, '违法类型', '出卖、转让、出借医疗机构执业许可证', 2, 'admin', NOW()),
(11, '颁发机构', '国务院', 3, 'admin', NOW()),
(11, '实施时间', '2022-05-01', 4, 'admin', NOW()),
(11, '效级', '行政法规', 5, 'admin', NOW()),
(11, '条款内容', '医疗机构管理条例第二十三条：医疗机构执业许可证不得出卖、转让、出借。', 6, 'admin', NOW()),
(11, '法律责任', '由卫生健康主管部门责令改正，没收违法所得，并处罚款；情节严重的，吊销执业许可证。', 7, 'admin', NOW()),
(11, '裁量标准', '轻微：责令改正，没收违法所得；一般：罚款5000-1万元；严重：吊销执业许可证。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(12, '编号', 'LB012', 1, 'admin', NOW()),
(12, '违法类型', '使用非卫生技术人员从事医疗卫生技术工作', 2, 'admin', NOW()),
(12, '颁发机构', '国务院', 3, 'admin', NOW()),
(12, '实施时间', '2022-05-01', 4, 'admin', NOW()),
(12, '效级', '行政法规', 5, 'admin', NOW()),
(12, '条款内容', '医疗机构管理条例第二十八条：医疗机构不得使用非卫生技术人员从事医疗卫生技术工作。', 6, 'admin', NOW()),
(12, '法律责任', '由卫生健康主管部门责令限期改正，没收违法所得，并处罚款；情节严重的，吊销执业许可证。', 7, 'admin', NOW()),
(12, '裁量标准', '轻微：责令改正；一般：罚款5000元以下；严重：吊销执业许可证。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(13, '编号', 'LB013', 1, 'admin', NOW()),
(13, '违法类型', '逾期不校验医疗机构执业许可证', 2, 'admin', NOW()),
(13, '颁发机构', '国务院', 3, 'admin', NOW()),
(13, '实施时间', '2022-05-01', 4, 'admin', NOW()),
(13, '效级', '行政法规', 5, 'admin', NOW()),
(13, '条款内容', '医疗机构管理条例第二十二条：医疗机构应当按照规定办理执业许可证校验手续。', 6, 'admin', NOW()),
(13, '法律责任', '由卫生健康主管部门责令限期补办校验手续；逾期不补办的，责令停止执业。', 7, 'admin', NOW()),
(13, '裁量标准', '轻微：责令限期补办；一般：停止执业3个月以下；严重：停止执业6个月以上。', 8, 'admin', NOW());

-- 医疗机构校验管理办法 相关 (regulation_id=4)
INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(14, '编号', 'LB014', 1, 'admin', NOW()),
(14, '违法类型', '超出核准登记的诊疗科目开展诊疗活动', 2, 'admin', NOW()),
(14, '颁发机构', '国家卫生健康委', 3, 'admin', NOW()),
(14, '实施时间', '2016-04-19', 4, 'admin', NOW()),
(14, '效级', '部门规章', 5, 'admin', NOW()),
(14, '条款内容', '医疗机构管理条例第二十七条：医疗机构应当按照核准登记的诊疗科目开展诊疗活动。', 6, 'admin', NOW()),
(14, '法律责任', '由卫生健康主管部门责令改正，没收违法所得，并处罚款；情节严重的，吊销执业许可证。', 7, 'admin', NOW()),
(14, '裁量标准', '轻微：责令改正；一般：罚款3000-5000元；严重：吊销执业许可证。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(15, '编号', 'LB015', 1, 'admin', NOW()),
(15, '违法类型', '未配备与其业务相适应的卫生技术人员', 2, 'admin', NOW()),
(15, '颁发机构', '国家卫生健康委', 3, 'admin', NOW()),
(15, '实施时间', '2016-04-19', 4, 'admin', NOW()),
(15, '效级', '部门规章', 5, 'admin', NOW()),
(15, '条款内容', '医疗机构管理条例第二十五条：医疗机构应当配备与其业务相适应的卫生技术人员。', 6, 'admin', NOW()),
(15, '法律责任', '由卫生健康主管部门责令改正，视情节给予警告或罚款。', 7, 'admin', NOW()),
(15, '裁量标准', '轻微：责令改正；一般：警告；严重：处以5000元以下罚款。', 8, 'admin', NOW());

-- 医疗机构校验管理办法 相关 (regulation_id=5)
INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(16, '编号', 'LB016', 1, 'admin', NOW()),
(16, '违法类型', '未按规定申请医疗机构校验', 2, 'admin', NOW()),
(16, '颁发机构', '国家卫生健康委', 3, 'admin', NOW()),
(16, '实施时间', '2016-04-19', 4, 'admin', NOW()),
(16, '效级', '部门规章', 5, 'admin', NOW()),
(16, '条款内容', '医疗机构校验管理办法第九条：医疗机构应当于校验期满前3个月内向登记机关申请校验。', 6, 'admin', NOW()),
(16, '法律责任', '由卫生健康主管部门责令限期补办校验手续。', 7, 'admin', NOW()),
(16, '裁量标准', '轻微：责令限期补办；一般：停止执业1-3个月；严重：停止执业6个月以上。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(17, '编号', 'LB017', 1, 'admin', NOW()),
(17, '违法类型', '校验不合格经责令整改后仍不合格', 2, 'admin', NOW()),
(17, '颁发机构', '国家卫生健康委', 3, 'admin', NOW()),
(17, '实施时间', '2016-04-19', 4, 'admin', NOW()),
(17, '效级', '部门规章', 5, 'admin', NOW()),
(17, '条款内容', '医疗机构校验管理办法第二十五条：校验不合格经责令整改后仍不合格的，登记机关应当予以注销。', 6, 'admin', NOW()),
(17, '法律责任', '由登记机关责令停止执业，情节严重的，吊销执业许可证。', 7, 'admin', NOW()),
(17, '裁量标准', '轻微：责令限期整改；一般：停止执业3-6个月；严重：吊销执业许可证。', 8, 'admin', NOW());

-- 传染病防治法 相关 (regulation_id=6)
INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(18, '编号', 'LB018', 1, 'admin', NOW()),
(18, '违法类型', '未依照规定履行传染病疫情报告职责', 2, 'admin', NOW()),
(18, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(18, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(18, '效级', '法律', 5, 'admin', NOW()),
(18, '条款内容', '传染病防治法第三十条：疾病预防控制机构、医疗机构和采供血机构及其工作人员发现传染病疫情，应当按照规定及时报告。', 6, 'admin', NOW()),
(18, '法律责任', '由属地卫生健康主管部门责令改正，通报批评，给予警告；对负有责任的主管人员和其他直接责任人员，依法给予处分。', 7, 'admin', NOW()),
(18, '裁量标准', '轻微：责令改正，警告；一般：通报批评；严重：给予处分。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(19, '编号', 'LB019', 1, 'admin', NOW()),
(19, '违法类型', '隐瞒、谎报、缓报传染病疫情', 2, 'admin', NOW()),
(19, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(19, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(19, '效级', '法律', 5, 'admin', NOW()),
(19, '条款内容', '传染病防治法第三十七条：任何单位和个人发现传染病疫情，应当及时向属地疾病预防控制机构报告。', 6, 'admin', NOW()),
(19, '法律责任', '由属地卫生健康主管部门责令改正，通报批评，给予警告；对负有责任的主管人员和其他直接责任人员，依法给予处分；构成犯罪的，依法追究刑事责任。', 7, 'admin', NOW()),
(19, '裁量标准', '轻微：责令改正，警告；一般：通报批评；严重：给予处分；构成犯罪的，追究刑事责任。', 8, 'admin', NOW());

INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(20, '编号', 'LB020', 1, 'admin', NOW()),
(20, '违法类型', '疾病预防控制机构未按规定进行疫情调查', 2, 'admin', NOW()),
(20, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(20, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(20, '效级', '法律', 5, 'admin', NOW()),
(20, '条款内容', '传染病防治法第四十条：疾病预防控制机构接到传染病疫情报告或者发现传染病疫情时，应当及时采取流行病学调查、控制等措施。', 6, 'admin', NOW()),
(20, '法律责任', '由属地卫生健康主管部门责令改正，通报批评，给予警告；对负有责任的主管人员和其他直接责任人员，依法给予处分。', 7, 'admin', NOW()),
(20, '裁量标准', '轻微：责令改正，警告；一般：通报批评；严重：给予处分。', 8, 'admin', NOW());

-- 处理依据也需要补充内容数据 (basis_id 1-22 对应处理依据)
INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(1, '编号', 'PB001', 1, 'admin', NOW()),
(1, '违法类型', '超出资质认可范围', 2, 'admin', NOW()),
(1, '颁发机构', '卫健委', 3, 'admin', NOW()),
(1, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(1, '效级', '法律', 5, 'admin', NOW()),
(1, '条款内容', '基本医疗卫生与健康促进法第三十八条', 6, 'admin', NOW()),
(1, '法律责任', '责令停止执业、没收违法所得、罚款、吊销执业证书', 7, 'admin', NOW()),
(1, '裁量标准', '轻微：停执+1万以下罚款；一般：停执+1-3万罚款；严重：停执+3-5万罚款+吊证', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(2, '编号', 'PB002', 1, 'admin', NOW()),
(2, '违法类型', '传染病疫情瞒报', 2, 'admin', NOW()),
(2, '颁发机构', '卫健委', 3, 'admin', NOW()),
(2, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(2, '效级', '法律', 5, 'admin', NOW()),
(2, '条款内容', '传染病防治法第三十条', 6, 'admin', NOW()),
(2, '法律责任', '责令改正、通报批评、警告、处分', 7, 'admin', NOW()),
(2, '裁量标准', '轻微：责令改正+警告；一般：责令改正+通报批评；严重：处分+追究刑责', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(3, '编号', 'PB003', 1, 'admin', NOW()),
(3, '违法类型', '使用非卫生技术人员', 2, 'admin', NOW()),
(3, '颁发机构', '卫健委', 3, 'admin', NOW()),
(3, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(3, '效级', '法律', 5, 'admin', NOW()),
(3, '条款内容', '基本医疗卫生与健康促进法第三十八条', 6, 'admin', NOW()),
(3, '法律责任', '责令停止执业、没收违法所得、罚款、吊销执业证书', 7, 'admin', NOW()),
(3, '裁量标准', '轻微：停执+1万以下罚款；一般：停执+1-3万罚款；严重：停执+3-5万罚款+吊证', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(4, '编号', 'PB004', 1, 'admin', NOW()),
(4, '违法类型', '医疗机构未按规定报告传染病疫情', 2, 'admin', NOW()),
(4, '颁发机构', '卫健委', 3, 'admin', NOW()),
(4, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(4, '效级', '法律', 5, 'admin', NOW()),
(4, '条款内容', '传染病防治法第三十条', 6, 'admin', NOW()),
(4, '法律责任', '责令改正、通报批评、警告、处分', 7, 'admin', NOW()),
(4, '裁量标准', '轻微：责令改正+警告；一般：责令改正+通报批评；严重：处分+追究刑责', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(5, '编号', 'PB005', 1, 'admin', NOW()),
(5, '违法类型', '医疗机构工作人员不履行报告职责', 2, 'admin', NOW()),
(5, '颁发机构', '卫健委', 3, 'admin', NOW()),
(5, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(5, '效级', '法律', 5, 'admin', NOW()),
(5, '条款内容', '传染病防治法第三十七条', 6, 'admin', NOW()),
(5, '法律责任', '通报批评、警告、处分', 7, 'admin', NOW()),
(5, '裁量标准', '轻微：通报批评；一般：警告处分；严重：降级或撤职处分', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(6, '编号', 'PB006', 1, 'admin', NOW()),
(6, '违法类型', '违反医疗质量管理规定', 2, 'admin', NOW()),
(6, '颁发机构', '国务院', 3, 'admin', NOW()),
(6, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(6, '效级', '行政法规', 5, 'admin', NOW()),
(6, '条款内容', '医疗质量管理条例第二十二条', 6, 'admin', NOW()),
(6, '法律责任', '责令改正、警告、罚款', 7, 'admin', NOW()),
(6, '裁量标准', '轻微：责令改正；一般：警告；严重：处以1-3万元罚款', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(7, '编号', 'PB007', 1, 'admin', NOW()),
(7, '违法类型', '未建立医疗质量安全管理制度', 2, 'admin', NOW()),
(7, '颁发机构', '国务院', 3, 'admin', NOW()),
(7, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(7, '效级', '行政法规', 5, 'admin', NOW()),
(7, '条款内容', '医疗质量管理条例第二十二条', 6, 'admin', NOW()),
(7, '法律责任', '责令改正、警告、罚款', 7, 'admin', NOW()),
(7, '裁量标准', '轻微：责令改正；一般：警告；严重：处以1-3万元罚款', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(8, '编号', 'PB008', 1, 'admin', NOW()),
(8, '违法类型', '生物安全事件未按规定报告', 2, 'admin', NOW()),
(8, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(8, '实施时间', '2020-10-17', 4, 'admin', NOW()),
(8, '效级', '法律', 5, 'admin', NOW()),
(8, '条款内容', '生物安全法第四十六条', 6, 'admin', NOW()),
(8, '法律责任', '责令改正、没收违法所得、罚款', 7, 'admin', NOW()),
(8, '裁量标准', '轻微：责令改正；一般：罚款1-5万元；严重：罚款5-10万元', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(9, '编号', 'PB009', 1, 'admin', NOW()),
(9, '违法类型', '未建立生物安全管理制度', 2, 'admin', NOW()),
(9, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(9, '实施时间', '2020-10-17', 4, 'admin', NOW()),
(9, '效级', '法律', 5, 'admin', NOW()),
(9, '条款内容', '生物安全法第三十八条', 6, 'admin', NOW()),
(9, '法律责任', '责令改正、警告、罚款', 7, 'admin', NOW()),
(9, '裁量标准', '轻微：责令改正；一般：警告；严重：处以5万元以下罚款', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(10, '编号', 'PB010', 1, 'admin', NOW()),
(10, '违法类型', '违规开展高致病性病原微生物实验活动', 2, 'admin', NOW()),
(10, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(10, '实施时间', '2020-10-17', 4, 'admin', NOW()),
(10, '效级', '法律', 5, 'admin', NOW()),
(10, '条款内容', '生物安全法第四十六条', 6, 'admin', NOW()),
(10, '法律责任', '责令停止实验、没收违法所得、罚款、吊销资质', 7, 'admin', NOW()),
(10, '裁量标准', '轻微：责令改正；一般：停止实验，罚款5万元；严重：吊销资质', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(11, '编号', 'PB011', 1, 'admin', NOW()),
(11, '违法类型', '出卖、转让、出借医疗机构执业许可证', 2, 'admin', NOW()),
(11, '颁发机构', '国务院', 3, 'admin', NOW()),
(11, '实施时间', '2022-05-01', 4, 'admin', NOW()),
(11, '效级', '行政法规', 5, 'admin', NOW()),
(11, '条款内容', '医疗机构管理条例第二十三条', 6, 'admin', NOW()),
(11, '法律责任', '责令改正、没收违法所得、罚款、吊销执业许可证', 7, 'admin', NOW()),
(11, '裁量标准', '轻微：责令改正+没收违法所得；一般：罚款5000-1万元；严重：吊销执业许可证', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(12, '编号', 'PB012', 1, 'admin', NOW()),
(12, '违法类型', '使用非卫生技术人员从事医疗卫生技术工作', 2, 'admin', NOW()),
(12, '颁发机构', '国务院', 3, 'admin', NOW()),
(12, '实施时间', '2022-05-01', 4, 'admin', NOW()),
(12, '效级', '行政法规', 5, 'admin', NOW()),
(12, '条款内容', '医疗机构管理条例第二十八条', 6, 'admin', NOW()),
(12, '法律责任', '责令限期改正、没收违法所得、罚款、吊销执业许可证', 7, 'admin', NOW()),
(12, '裁量标准', '轻微：责令改正；一般：罚款5000元以下；严重：吊销执业许可证', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(13, '编号', 'PB013', 1, 'admin', NOW()),
(13, '违法类型', '逾期不校验医疗机构执业许可证', 2, 'admin', NOW()),
(13, '颁发机构', '国务院', 3, 'admin', NOW()),
(13, '实施时间', '2022-05-01', 4, 'admin', NOW()),
(13, '效级', '行政法规', 5, 'admin', NOW()),
(13, '条款内容', '医疗机构管理条例第二十二条', 6, 'admin', NOW()),
(13, '法律责任', '责令限期补办校验手续、停止执业', 7, 'admin', NOW()),
(13, '裁量标准', '轻微：责令限期补办；一般：停止执业3个月以下；严重：停止执业6个月以上', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(14, '编号', 'PB014', 1, 'admin', NOW()),
(14, '违法类型', '超出核准登记的诊疗科目开展诊疗活动', 2, 'admin', NOW()),
(14, '颁发机构', '国家卫生健康委', 3, 'admin', NOW()),
(14, '实施时间', '2016-04-19', 4, 'admin', NOW()),
(14, '效级', '部门规章', 5, 'admin', NOW()),
(14, '条款内容', '医疗机构管理条例第二十七条', 6, 'admin', NOW()),
(14, '法律责任', '责令改正、没收违法所得、罚款、吊销执业许可证', 7, 'admin', NOW()),
(14, '裁量标准', '轻微：责令改正；一般：罚款3000-5000元；严重：吊销执业许可证', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(15, '编号', 'PB015', 1, 'admin', NOW()),
(15, '违法类型', '未配备与其业务相适应的卫生技术人员', 2, 'admin', NOW()),
(15, '颁发机构', '国家卫生健康委', 3, 'admin', NOW()),
(15, '实施时间', '2016-04-19', 4, 'admin', NOW()),
(15, '效级', '部门规章', 5, 'admin', NOW()),
(15, '条款内容', '医疗机构管理条例第二十五条', 6, 'admin', NOW()),
(15, '法律责任', '责令改正、警告、罚款', 7, 'admin', NOW()),
(15, '裁量标准', '轻微：责令改正；一般：警告；严重：处以5000元以下罚款', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(16, '编号', 'PB016', 1, 'admin', NOW()),
(16, '违法类型', '未按规定申请医疗机构校验', 2, 'admin', NOW()),
(16, '颁发机构', '国家卫生健康委', 3, 'admin', NOW()),
(16, '实施时间', '2016-04-19', 4, 'admin', NOW()),
(16, '效级', '部门规章', 5, 'admin', NOW()),
(16, '条款内容', '医疗机构校验管理办法第九条', 6, 'admin', NOW()),
(16, '法律责任', '责令限期补办校验手续', 7, 'admin', NOW()),
(16, '裁量标准', '轻微：责令限期补办；一般：停止执业1-3个月；严重：停止执业6个月以上', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(17, '编号', 'PB017', 1, 'admin', NOW()),
(17, '违法类型', '校验不合格经责令整改后仍不合格', 2, 'admin', NOW()),
(17, '颁发机构', '国家卫生健康委', 3, 'admin', NOW()),
(17, '实施时间', '2016-04-19', 4, 'admin', NOW()),
(17, '效级', '部门规章', 5, 'admin', NOW()),
(17, '条款内容', '医疗机构校验管理办法第二十五条', 6, 'admin', NOW()),
(17, '法律责任', '责令停止执业、吊销执业许可证', 7, 'admin', NOW()),
(17, '裁量标准', '轻微：责令限期整改；一般：停止执业3-6个月；严重：吊销执业许可证', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(18, '编号', 'PB018', 1, 'admin', NOW()),
(18, '违法类型', '未依照规定履行传染病疫情报告职责', 2, 'admin', NOW()),
(18, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(18, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(18, '效级', '法律', 5, 'admin', NOW()),
(18, '条款内容', '传染病防治法第三十条', 6, 'admin', NOW()),
(18, '法律责任', '责令改正、通报批评、警告、处分', 7, 'admin', NOW()),
(18, '裁量标准', '轻微：责令改正，警告；一般：通报批评；严重：给予处分', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(19, '编号', 'PB019', 1, 'admin', NOW()),
(19, '违法类型', '隐瞒、谎报、缓报传染病疫情', 2, 'admin', NOW()),
(19, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(19, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(19, '效级', '法律', 5, 'admin', NOW()),
(19, '条款内容', '传染病防治法第三十七条', 6, 'admin', NOW()),
(19, '法律责任', '责令改正、通报批评、警告、处分、追究刑事责任', 7, 'admin', NOW()),
(19, '裁量标准', '轻微：责令改正，警告；一般：通报批评；严重：给予处分；构成犯罪的，追究刑事责任', 8, 'admin', NOW());

INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(20, '编号', 'PB020', 1, 'admin', NOW()),
(20, '违法类型', '疾病预防控制机构未按规定进行疫情调查', 2, 'admin', NOW()),
(20, '颁发机构', '全国人大常委会', 3, 'admin', NOW()),
(20, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(20, '效级', '法律', 5, 'admin', NOW()),
(20, '条款内容', '传染病防治法第四十条', 6, 'admin', NOW()),
(20, '法律责任', '责令改正、通报批评、警告、处分', 7, 'admin', NOW()),
(20, '裁量标准', '轻微：责令改正，警告；一般：通报批评；严重：给予处分', 8, 'admin', NOW());
