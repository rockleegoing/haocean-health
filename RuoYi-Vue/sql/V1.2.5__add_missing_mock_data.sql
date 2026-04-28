-- ============================================
-- 脚本：V1.2.5__add_missing_mock_data.sql
-- 版本：1.2.5
-- 日期：2026-04-28
-- 描述：补充缺失的定性依据、处理依据和关联数据
-- ============================================

-- ============================================
-- 1. 添加法规3-7的定性依据
-- ============================================

INSERT INTO `sys_legal_basis` (`basis_no`, `title`, `violation_type`, `issuing_authority`, `effective_date`, `legal_level`, `clauses`, `legal_liability`, `discretion_standard`, `regulation_id`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
-- 医疗机构管理条例 定性依据 (regulation_id = 3)
('LB-003', '出卖、转让、出借医疗机构执业许可证', '医疗机构管理', '国家卫生健康委员会', '2022-05-01', '规章', '["医疗机构管理条例》第二十三条]', '由县级以上人民政府卫生行政部门责令改正，没收非法所得，并处罚款；情节严重的，吊销执业许可证。', '轻微：责令改正，没收非法所得，处5000元以下罚款；一般：责令改正，没收非法所得，处5000-10000元罚款；严重：吊销执业许可证。', 3, '0', '0', 'admin', NOW()),
('LB-004', '使用非卫生技术人员从事医疗卫生技术工作', '医疗机构管理', '国家卫生健康委员会', '2022-05-01', '规章', '["医疗机构管理条例》第二十八条]', '由县级以上人民政府卫生行政部门责令其限期改正，并可处以5000元以下罚款；情节严重的，吊销其《医疗机构执业许可证》。', '轻微：责令限期改正；一般：处以3000元以下罚款；严重：处以3000-5000元罚款，吊销执业许可证。', 3, '0', '0', 'admin', NOW()),
('LB-005', '逾期不校验医疗机构执业许可证', '医疗机构管理', '国家卫生健康委员会', '2022-05-01', '规章', '["医疗机构管理条例》第二十二条]', '由县级以上人民政府卫生行政部门责令其限期补办校验手续；拒不校验的，吊销其《医疗机构执业许可证》。', '轻微：责令限期补办校验；一般：通报批评；严重：吊销执业许可证。', 3, '0', '0', 'admin', NOW()),
-- 医疗机构管理条例实施细则 定性依据 (regulation_id = 4)
('LB-006', '超出核准登记的诊疗科目开展诊疗活动', '超范围执业', '国家卫生健康委员会', '2022-07-01', '规章', '["医疗机构管理条例实施细则》第八十条]', '由县级以上人民政府卫生行政部门给予警告，并可处以3000元以下的罚款；情节严重的，吊销其《医疗机构执业许可证》。', '轻微：警告；一般：处以1000元以下罚款；严重：处以1000-3000元罚款，吊销执业许可证。', 4, '0', '0', 'admin', NOW()),
('LB-007', '未配备与其业务相适应的卫生技术人员', '人员配备不符', '国家卫生健康委员会', '2022-07-01', '规章', '["医疗机构管理条例实施细则》第八条]', '由县级以上人民政府卫生行政部门责令其限期改正；逾期不改正的，处以警告。', '轻微：警告；一般：责令限期改正；严重：处以罚款。', 4, '0', '0', 'admin', NOW()),
-- 医疗机构校验管理办法 定性依据 (regulation_id = 5)
('LB-008', '未按规定申请医疗机构校验', '医疗机构校验', '国家卫生健康委员会', '2022-09-01', '规范性文件', '["医疗机构校验管理办法》第十四条]', '由校验部门责令其补办校验手续，可以给予警告，并可处以5000元以下罚款。', '轻微：警告；一般：责令限期补办，处2000元以下罚款；严重：处以2000-5000元罚款。', 5, '0', '0', 'admin', NOW()),
('LB-009', '校验不合格经责令整改后仍不合格', '医疗机构校验', '国家卫生健康委员会', '2022-09-01', '规范性文件', '["医疗机构校验管理办法》第十七条]', '吊销其《医疗机构执业许可证》。', '整改后仍不合格的，直接吊销执业许可证。', 5, '0', '0', 'admin', NOW()),
-- 传染病防治法 定性依据 (regulation_id = 6)
('LB-010', '未依照规定履行传染病疫情报告职责', '传染病防治', '全国人民代表大会常务委员会', '2013-06-29', '法律', '["传染病防治法》第三十条]', '由县级以上人民政府卫生行政部门责令限期改正，通报批评，给予警告；对负有责任的主管人员和其他直接责任人员，依法给予处分。', '轻微：责令改正，警告；一般：通报批评；严重：给予处分。', 6, '0', '0', 'admin', NOW()),
('LB-011', '隐瞒、谎报、缓报传染病疫情', '传染病防治', '全国人民代表大会常务委员会', '2013-06-29', '法律', '["传染病防治法》第三十七条]', '由上级机关责令改正，通报批评，给予警告；对主要负责人给予降级、撤职处分。', '轻微：警告；一般：降级处分；严重：撤职处分。', 6, '0', '0', 'admin', NOW()),
('LB-012', '疾病预防控制机构未按规定进行疫情调查', '传染病防治', '全国人民代表大会常务委员会', '2013-06-29', '法律', '["传染病防治法》第四十条]', '由上级卫生行政部门责令改正，通报批评，给予警告。', '轻微：警告；一般：通报批评；严重：对责任人给予处分。', 6, '0', '0', 'admin', NOW()),
-- 突发公共卫生事件应急条例 定性依据 (regulation_id = 7)
('LB-013', '未依照条例规定及时采取应急处理措施', '应急管理', '国务院', '2003-05-09', '法规', '["突发公共卫生事件应急条例》第四十五条]', '对有关责任人员依法给予行政处分或者纪律处分。', '轻微：行政处分；一般：降级处分；严重：撤职处分。', 7, '0', '0', 'admin', NOW()),
('LB-014', '医疗卫生机构未依照条例规定履行报告职责', '应急管理', '国务院', '2003-05-09', '法规', '["突发公共卫生事件应急条例》第四十五条]', '对主要负责人、负有责任的主管人员和其他直接责任人员依法给予降级或者撤职的纪律处分。', '轻微：降级处分；一般：撤职处分；严重：开除处分。', 7, '0', '0', 'admin', NOW());

-- ============================================
-- 2. 添加法规3-7的处理依据
-- ============================================

INSERT INTO `sys_processing_basis` (`basis_no`, `title`, `violation_type`, `issuing_authority`, `effective_date`, `legal_level`, `clauses`, `legal_liability`, `discretion_standard`, `regulation_id`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
-- 医疗机构管理条例 处理依据 (regulation_id = 3)
('PB-003', '出卖、转让、出借医疗机构执业许可证', '医疗机构管理', '国家卫生健康委员会', '2022-05-01', '规章', '["医疗机构管理条例》第二十三条]', '没收非法所得，并处罚款；情节严重的，吊销执业许可证。', '轻微：没收非法所得，处5000元以下罚款；一般：没收非法所得，处5000-10000元罚款；严重：吊销执业许可证。', 3, '0', '0', 'admin', NOW()),
('PB-004', '使用非卫生技术人员从事医疗卫生技术工作', '机构管理', '国家卫生健康委员会', '2022-05-01', '规章', '["医疗机构管理条例》第二十八条]', '责令限期改正，并可处以5000元以下罚款；情节严重的，吊销执业许可证。', '轻微：责令限期改正；一般：处以3000元以下罚款；严重：处以3000-5000元罚款，吊销执业许可证。', 3, '0', '0', 'admin', NOW()),
('PB-005', '逾期不校验执业许可证且拒不补办', '医疗机构管理', '国家卫生健康委员会', '2022-05-01', '规章', '["医疗机构管理条例》第二十二条]', '吊销其《医疗机构执业许可证》。', '逾期不校验且拒不补办的，直接吊销执业许可证。', 3, '0', '0', 'admin', NOW()),
-- 医疗机构管理条例实施细则 处理依据 (regulation_id = 4)
('PB-006', '超出核准登记的诊疗科目开展诊疗活动', '超范围执业', '国家卫生健康委员会', '2022-07-01', '规章', '["医疗机构管理条例实施细则》第八十条]', '给予警告，并可处以3000元以下的罚款；情节严重的，吊销执业许可证。', '轻微：警告；一般：处以1000元以下罚款；严重：处以1000-3000元罚款，吊销执业许可证。', 4, '0', '0', 'admin', NOW()),
('PB-007', '转让、出借执业许可证行为', '机构管理', '国家卫生健康委员会', '2022-07-01', '规章', '["医疗机构管理条例实施细则》第八十一条]', '没收非法所得，并处以罚款；情节严重的，吊销执业许可证。', '轻微：没收非法所得，处3000元以下罚款；一般：没收非法所得，处3000-5000元罚款；严重：吊销执业许可证。', 4, '0', '0', 'admin', NOW()),
-- 医疗机构校验管理办法 处理依据 (regulation_id = 5)
('PB-008', '不按期申请校验且拒不改正', '校验管理', '国家卫生健康委员会', '2022-09-01', '规范性文件', '["医疗机构校验管理办法》第十七条]', '吊销其《医疗机构执业许可证》。', '责令限期补办校验手续，逾期不补办或整改后仍不合格的，吊销执业许可证。', 5, '0', '0', 'admin', NOW()),
('PB-009', '校验中发现不符合医疗机构基本标准', '校验管理', '国家卫生健康委员会', '2022-09-01', '规范性文件', '["医疗机构校验管理办法》第十六条]', '责令其限期整改，整改期间不得开展诊疗活动；逾期不整改或整改后仍不合格的，吊销执业许可证。', '轻微：责令限期整改；一般：整改期间停止执业；严重：吊销执业许可证。', 5, '0', '0', 'admin', NOW()),
-- 传染病防治法 处理依据 (regulation_id = 6)
('PB-010', '疾病预防控制机构未履行传染病防治职责', '传染病防治', '全国人民代表大会常务委员会', '2013-06-29', '法律', '["传染病防治法》第六十五条]', '责令改正，通报批评，给予警告；对负有责任的主管人员和其他直接责任人员依法给予处分。', '轻微：警告；一般：通报批评；严重：给予处分。', 6, '0', '0', 'admin', NOW()),
('PB-011', '医疗机构未按规定报告传染病疫情', '传染病防治', '全国人民代表大会常务委员会', '2013-06-29', '法律', '["传染病防治法》第三十条]', '责令改正，通报批评，给予警告；对负有责任的主管人员和其他直接责任人员依法给予处分。', '轻微：警告；一般：通报批评；严重：给予处分。', 6, '0', '0', 'admin', NOW()),
-- 突发公共卫生事件应急条例 处理依据 (regulation_id = 7)
('PB-012', '医疗卫生机构未按规定履行应急处理职责', '应急管理', '国务院', '2003-05-09', '法规', '["突发公共卫生事件应急条例》第四十五条]', '对主要负责人、负有责任的主管人员和其他直接责任人员依法给予降级或者撤职的纪律处分。', '轻微：降级处分；一般：撤职处分；严重：开除处分。', 7, '0', '0', 'admin', NOW()),
('PB-013', '在突发事件应急处理中玩忽职守', '应急管理', '国务院', '2003-05-09', '法规', '["突发公共卫生事件应急条例》第四十六条]', '给予行政处分；构成犯罪的，依法追究刑事责任。', '轻微：行政处分；一般：降级处分；严重：追究刑事责任。', 7, '0', '0', 'admin', NOW());

-- ============================================
-- 3. 添加依据与条款的关联
-- ============================================

-- 获取最大的link_id
SET @maxLinkId = (SELECT COALESCE(MAX(link_id), 0) FROM sys_basis_chapter_link);

-- 法规 3 (医疗机构管理条例)
-- 章节8 (总则) - articles 22,23
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 1, 8, 22, 'legal', 11, 'admin', NOW()),
(@maxLinkId + 2, 8, 23, 'legal', 12, 'admin', NOW()),
(@maxLinkId + 3, 8, 22, 'processing', 6, 'admin', NOW()),
(@maxLinkId + 4, 8, 23, 'processing', 7, 'admin', NOW());

-- 章节10 (执业规范) - articles 26,27,28,29,30
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 5, 10, 26, 'legal', 12, 'admin', NOW()),
(@maxLinkId + 6, 10, 27, 'legal', 12, 'admin', NOW()),
(@maxLinkId + 7, 10, 28, 'legal', 13, 'admin', NOW()),
(@maxLinkId + 8, 10, 29, 'legal', 13, 'admin', NOW()),
(@maxLinkId + 9, 10, 26, 'processing', 8, 'admin', NOW()),
(@maxLinkId + 10, 10, 27, 'processing', 8, 'admin', NOW()),
(@maxLinkId + 11, 10, 28, 'processing', 9, 'admin', NOW()),
(@maxLinkId + 12, 10, 29, 'processing', 9, 'admin', NOW());

-- 章节11 (监督管理) - articles 32,33
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 13, 11, 32, 'legal', 14, 'admin', NOW()),
(@maxLinkId + 14, 11, 33, 'legal', 14, 'admin', NOW()),
(@maxLinkId + 15, 11, 32, 'processing', 10, 'admin', NOW()),
(@maxLinkId + 16, 11, 33, 'processing', 11, 'admin', NOW());

-- 章节12 (法律责任) - articles 34,35
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 17, 12, 34, 'legal', 14, 'admin', NOW()),
(@maxLinkId + 18, 12, 35, 'legal', 14, 'admin', NOW()),
(@maxLinkId + 19, 12, 34, 'processing', 10, 'admin', NOW()),
(@maxLinkId + 20, 12, 35, 'processing', 11, 'admin', NOW());

-- 规章 4 (医疗机构管理条例实施细则)
-- 章节13 (总则) - articles 36,37
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 21, 13, 36, 'legal', 15, 'admin', NOW()),
(@maxLinkId + 22, 13, 37, 'legal', 15, 'admin', NOW());

-- 章节14 (机构设置) - articles 38,39,40
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 23, 14, 38, 'legal', 16, 'admin', NOW()),
(@maxLinkId + 24, 14, 39, 'legal', 16, 'admin', NOW()),
(@maxLinkId + 25, 14, 40, 'legal', 16, 'admin', NOW()),
(@maxLinkId + 26, 14, 38, 'processing', 12, 'admin', NOW());

-- 章节15 (人员配备) - articles 41,42
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 27, 15, 41, 'legal', 17, 'admin', NOW()),
(@maxLinkId + 28, 15, 42, 'legal', 17, 'admin', NOW());

-- 章节16 (执业行为) - articles 43,44,45
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 29, 16, 43, 'legal', 16, 'admin', NOW()),
(@maxLinkId + 30, 16, 44, 'legal', 17, 'admin', NOW()),
(@maxLinkId + 31, 16, 45, 'legal', 17, 'admin', NOW()),
(@maxLinkId + 32, 16, 43, 'processing', 13, 'admin', NOW()),
(@maxLinkId + 33, 16, 44, 'processing', 13, 'admin', NOW());

-- 规范性文件 5 (医疗机构校验管理办法)
-- 章节17 (总则) - articles 46,47
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 34, 17, 46, 'legal', 18, 'admin', NOW()),
(@maxLinkId + 35, 17, 47, 'legal', 18, 'admin', NOW());

-- 章节18 (校验申请) - articles 48,49
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 36, 18, 48, 'legal', 18, 'admin', NOW()),
(@maxLinkId + 37, 18, 49, 'legal', 18, 'admin', NOW()),
(@maxLinkId + 38, 18, 48, 'processing', 14, 'admin', NOW());

-- 章节19 (校验审查) - articles 50,51
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 39, 19, 50, 'legal', 19, 'admin', NOW()),
(@maxLinkId + 40, 19, 51, 'legal', 19, 'admin', NOW());

-- 章节20 (校验结论) - articles 52,53
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 41, 20, 52, 'legal', 19, 'admin', NOW()),
(@maxLinkId + 42, 20, 53, 'legal', 19, 'admin', NOW()),
(@maxLinkId + 43, 20, 52, 'processing', 15, 'admin', NOW()),
(@maxLinkId + 44, 20, 53, 'processing', 16, 'admin', NOW());

-- 法律 6 (传染病防治法)
-- 章节21 (总则) - articles 54,55,56
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 45, 21, 54, 'legal', 20, 'admin', NOW()),
(@maxLinkId + 46, 21, 55, 'legal', 20, 'admin', NOW()),
(@maxLinkId + 47, 21, 56, 'legal', 21, 'admin', NOW());

-- 章节22 (传染病预防) - articles 57,58,59
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 48, 22, 57, 'legal', 22, 'admin', NOW()),
(@maxLinkId + 49, 22, 58, 'legal', 22, 'admin', NOW()),
(@maxLinkId + 50, 22, 59, 'legal', 22, 'admin', NOW());

-- 章节23 (疫情报告) - articles 60,61
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 51, 23, 60, 'legal', 20, 'admin', NOW()),
(@maxLinkId + 52, 23, 61, 'legal', 21, 'admin', NOW()),
(@maxLinkId + 53, 23, 60, 'processing', 17, 'admin', NOW()),
(@maxLinkId + 54, 23, 61, 'processing', 17, 'admin', NOW());

-- 章节24 (疫情控制) - articles 62,63
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 55, 24, 62, 'legal', 22, 'admin', NOW()),
(@maxLinkId + 56, 24, 63, 'legal', 22, 'admin', NOW());

-- 章节25 (医疗救治) - articles 64,65
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 57, 25, 64, 'legal', 20, 'admin', NOW()),
(@maxLinkId + 58, 25, 65, 'legal', 21, 'admin', NOW());

-- 章节26 (法律责任) - articles 66,67
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 59, 26, 66, 'legal', 20, 'admin', NOW()),
(@maxLinkId + 60, 26, 67, 'legal', 21, 'admin', NOW()),
(@maxLinkId + 61, 26, 66, 'processing', 18, 'admin', NOW()),
(@maxLinkId + 62, 26, 67, 'processing', 19, 'admin', NOW());

-- 法规 7 (突发公共卫生事件应急条例)
-- 章节27 (总则) - articles 68,69
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 63, 27, 68, 'legal', 23, 'admin', NOW()),
(@maxLinkId + 64, 27, 69, 'legal', 24, 'admin', NOW());

-- 章节28 (应急准备) - articles 70,71
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 65, 28, 70, 'legal', 23, 'admin', NOW()),
(@maxLinkId + 66, 28, 71, 'legal', 24, 'admin', NOW());

-- 章节29 (应急处理) - articles 72,73,74
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 67, 29, 72, 'legal', 23, 'admin', NOW()),
(@maxLinkId + 68, 29, 73, 'legal', 23, 'admin', NOW()),
(@maxLinkId + 69, 29, 74, 'legal', 24, 'admin', NOW()),
(@maxLinkId + 70, 29, 72, 'processing', 20, 'admin', NOW()),
(@maxLinkId + 71, 29, 73, 'processing', 20, 'admin', NOW()),
(@maxLinkId + 72, 29, 74, 'processing', 21, 'admin', NOW());

-- 章节30 (法律责任) - articles 75,76
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 73, 30, 75, 'legal', 23, 'admin', NOW()),
(@maxLinkId + 74, 30, 76, 'legal', 24, 'admin', NOW()),
(@maxLinkId + 75, 30, 75, 'processing', 22, 'admin', NOW()),
(@maxLinkId + 76, 30, 76, 'processing', 22, 'admin', NOW());

-- ============================================
-- 4. 验证数据
-- ============================================

SELECT '=== 法律法规统计 ===' AS '';
SELECT regulation_id, title, legal_type FROM sys_regulation WHERE del_flag='0' ORDER BY regulation_id;

SELECT '=== 定性依据统计 ===' AS '';
SELECT regulation_id, COUNT(*) as count FROM sys_legal_basis WHERE del_flag='0' GROUP BY regulation_id;

SELECT '=== 处理依据统计 ===' AS '';
SELECT regulation_id, COUNT(*) as count FROM sys_processing_basis WHERE del_flag='0' GROUP BY regulation_id;

SELECT '=== 关联数据统计 ===' AS '';
SELECT basis_type, COUNT(*) as link_count FROM sys_basis_chapter_link GROUP BY basis_type;
