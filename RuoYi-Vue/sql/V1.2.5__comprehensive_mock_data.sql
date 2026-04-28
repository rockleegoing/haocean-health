-- ============================================
-- 脚本：V1.2.5__comprehensive_mock_data.sql
-- 版本：1.2.5
-- 日期：2026-04-28
-- 描述：为全面测试创建模拟数据
-- 法律法规类型：法律、法规、规章、规范性文件
-- ============================================

-- ============================================
-- 1. 添加更多法律法规
-- ============================================

-- 插入法规 (regulation_id = 3)
INSERT INTO `sys_regulation` (`regulation_id`, `title`, `legal_type`, `supervision_types`, `publish_date`, `effective_date`, `issuing_authority`, `content`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
(3, '医疗机构管理条例', '法规', '["医疗机构"]', '2022-03-15', '2022-05-01', '国务院', '为了加强医疗机构管理，规范医疗机构执业行为，保障人民群众身体健康和生命安全，制定本条例。', '0', '0', 'admin', NOW());

-- 插入规章 (regulation_id = 4)
INSERT INTO `sys_regulation` (`regulation_id`, `title`, `legal_type`, `supervision_types`, `publish_date`, `effective_date`, `issuing_authority`, `content`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
(4, '医疗机构管理条例实施细则', '规章', '["医疗机构"]', '2022-06-01', '2022-07-01', '国家卫生健康委员会', '根据《医疗机构管理条例》制定本实施细则，对医疗机构的设置审批、执业规范、监督管理等作出具体规定。', '0', '0', 'admin', NOW());

-- 插入规范性文件 (regulation_id = 5)
INSERT INTO `sys_regulation` (`regulation_id`, `title`, `legal_type`, `supervision_types`, `publish_date`, `effective_date`, `issuing_authority`, `content`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
(5, '医疗机构校验管理办法', '规范性文件', '["医疗机构"]', '2022-08-01', '2022-09-01', '国家卫生健康委员会', '为加强医疗机构监督管理，规范医疗机构校验工作，根据《医疗机构管理条例》等相关规定，制定本办法。', '0', '0', 'admin', NOW());

-- 插入另一部法律 (regulation_id = 6)
INSERT INTO `sys_regulation` (`regulation_id`, `title`, `legal_type`, `supervision_types`, `publish_date`, `effective_date`, `issuing_authority`, `content`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
(6, '中华人民共和国传染病防治法', '法律', '["公共场所", "医疗机构"]', '2013-06-29', '2013-06-29', '全国人民代表大会常务委员会', '为了预防、控制和消除传染病的发生与流行，保障人体健康和公共卫生，制定本法。', '0', '0', 'admin', NOW());

-- 插入法规 (regulation_id = 7)
INSERT INTO `sys_regulation` (`regulation_id`, `title`, `legal_type`, `supervision_types`, `publish_date`, `effective_date`, `issuing_authority`, `content`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
(7, '突发公共卫生事件应急条例', '法规', '["公共场所", "医疗机构"]', '2003-05-09', '2003-05-09', '国务院', '为了有效预防、及时控制和消除突发公共卫生事件的危害，保障公众身体健康与生命安全，制定本条例。', '0', '0', 'admin', NOW());

-- ============================================
-- 2. 为每个法规添加章节
-- ============================================

-- 医疗机构管理条例 章节 (regulation_id = 3)
INSERT INTO `sys_regulation_chapter` (`regulation_id`, `chapter_no`, `chapter_title`, `sort_order`, `create_by`, `create_time`) VALUES
(3, '第一章', '总则', 1, 'admin', NOW()),
(3, '第二章', '设置审批', 2, 'admin', NOW()),
(3, '第三章', '执业规范', 3, 'admin', NOW()),
(3, '第四章', '监督管理', 4, 'admin', NOW()),
(3, '第五章', '法律责任', 5, 'admin', NOW());

-- 医疗机构管理条例实施细则 章节 (regulation_id = 4)
INSERT INTO `sys_regulation_chapter` (`regulation_id`, `chapter_no`, `chapter_title`, `sort_order`, `create_by`, `create_time`) VALUES
(4, '第一章', '总则', 1, 'admin', NOW()),
(4, '第二章', '机构设置', 2, 'admin', NOW()),
(4, '第三章', '人员配备', 3, 'admin', NOW()),
(4, '第四章', '执业行为', 4, 'admin', NOW());

-- 医疗机构校验管理办法 章节 (regulation_id = 5)
INSERT INTO `sys_regulation_chapter` (`regulation_id`, `chapter_no`, `chapter_title`, `sort_order`, `create_by`, `create_time`) VALUES
(5, '第一章', '总则', 1, 'admin', NOW()),
(5, '第二章', '校验申请', 2, 'admin', NOW()),
(5, '第三章', '校验审查', 3, 'admin', NOW()),
(5, '第四章', '校验结论', 4, 'admin', NOW());

-- 传染病防治法 章节 (regulation_id = 6)
INSERT INTO `sys_regulation_chapter` (`regulation_id`, `chapter_no`, `chapter_title`, `sort_order`, `create_by`, `create_time`) VALUES
(6, '第一章', '总则', 1, 'admin', NOW()),
(6, '第二章', '传染病预防', 2, 'admin', NOW()),
(6, '第三章', '疫情报告、通报和公布', 3, 'admin', NOW()),
(6, '第四章', '疫情控制', 4, 'admin', NOW()),
(6, '第五章', '医疗救治', 5, 'admin', NOW()),
(6, '第六章', '法律责任', 6, 'admin', NOW());

-- 突发公共卫生事件应急条例 章节 (regulation_id = 7)
INSERT INTO `sys_regulation_chapter` (`regulation_id`, `chapter_no`, `chapter_title`, `sort_order`, `create_by`, `create_time`) VALUES
(7, '第一章', '总则', 1, 'admin', NOW()),
(7, '第二章', '应急准备', 2, 'admin', NOW()),
(7, '第三章', '应急处理', 3, 'admin', NOW()),
(7, '第四章', '法律责任', 4, 'admin', NOW());

-- ============================================
-- 3. 为每个法规添加条款
-- ============================================

-- 医疗机构管理条例 条款 (regulation_id = 3)
INSERT INTO `sys_regulation_article` (`regulation_id`, `chapter_id`, `article_no`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
-- 第一章 总则
(3, 8, '第一条', '在中华人民共和国境内从事疾病诊断、治疗活动的医院、卫生院、疗养院、门诊部、诊所、卫生所（室）和急救站等医疗机构的管理，适用本条例。', 1, 'admin', NOW()),
(3, 8, '第二条', '医疗机构应当以救死扶伤、防病治病、为公民的健康服务为宗旨，遵守医疗卫生管理法律、行政法规、部门规章和诊疗护理规范、常规，恪守医疗服务职业道德。', 2, 'admin', NOW()),
-- 第二章 设置审批
(3, 9, '第一条', '医疗机构设置规划由县级以上地方人民政府卫生行政部门制定，报本级人民政府审批后实施。', 1, 'admin', NOW()),
(3, 9, '第二条', '设置医疗机构应当符合医疗机构设置规划和医疗机构基本标准。', 2, 'admin', NOW()),
(3, 9, '第三条', '有下列情形之一的，不得申请设置医疗机构：（一）不能独立承担民事责任的单位；（二）正在服刑或者不具有完全民事行为能力的人员；（三）因违反有关医疗卫生管理法律、法规、规章，被吊销执业许可证的人员。', 3, 'admin', NOW()),
-- 第三章 执业规范
(3, 10, '第一条', '医疗机构执业必须遵守医疗卫生管理法律、法规、规章和诊疗护理规范、常规。', 1, 'admin', NOW()),
(3, 10, '第二条', '医疗机构应当加强对医务人员的医德医风教育，督促医务人员恪守医疗服务职业道德。', 2, 'admin', NOW()),
(3, 10, '第三条', '未经医师（士）亲自诊查病人，医疗机构不得出具疾病诊断书、健康证明书或者死亡证明书等证明文件。', 3, 'admin', NOW()),
(3, 10, '第四条', '医疗机构施行手术、特殊检查或者特殊治疗时，必须征得患者同意，并应当取得其家属或者关系人同意并签字。', 4, 'admin', NOW()),
(3, 10, '第五条', '医疗机构必须按照人民政府或者物价部门的有关规定收取医疗费用，详列细项，并出具收据。', 5, 'admin', NOW()),
-- 第四章 监督管理
(3, 11, '第一条', '县级以上人民政府卫生行政部门负责本行政区域内医疗机构的管理监督工作。', 1, 'admin', NOW()),
(3, 11, '第二条', '医疗机构执业违反本条例规定的，由县级以上人民政府卫生行政部门给予警告，责令其改正。', 2, 'admin', NOW()),
-- 第五章 法律责任
(3, 12, '第一条', '违反本条例规定，未取得《医疗机构执业许可证》擅自执业的，由县级以上人民政府卫生行政部门责令其停止执业活动，没收非法所得和药品、器械，并可以根据情节处以一万元以下的罚款。', 1, 'admin', NOW()),
(3, 12, '第二条', '逾期不校验《医疗机构执业许可证》仍从事诊疗活动的，由县级以上人民政府卫生行政部门责令其限期补办校验手续；拒不校验的，吊销其《医疗机构执业许可证》。', 2, 'admin', NOW());

-- 医疗机构管理条例实施细则 条款 (regulation_id = 4)
INSERT INTO `sys_regulation_article` (`regulation_id`, `chapter_id`, `article_no`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
-- 第一章 总则
(4, 13, '第一条', '根据《医疗机构管理条例》制定本实施细则。', 1, 'admin', NOW()),
(4, 13, '第二条', '本实施细则适用于从事疾病诊断、治疗活动的医院、卫生院、疗养院、门诊部、诊所、卫生所（室）和急救站等医疗机构。', 2, 'admin', NOW()),
-- 第二章 机构设置
(4, 14, '第一条', '医疗机构设置规划应当符合当地医疗卫生发展规划要求，合理配置医疗资源。', 1, 'admin', NOW()),
(4, 14, '第二条', '设置三级医院由省级卫生行政部门审批；设置二级医院由设区的市级卫生行政部门审批。', 2, 'admin', NOW()),
(4, 14, '第三条', '在城市设置诊所的个人，必须经医师执业注册并从事五年以上同一专业临床工作。', 3, 'admin', NOW()),
-- 第三章 人员配备
(4, 15, '第一条', '医疗机构应当配备与其业务相适应的卫生技术人员。', 1, 'admin', NOW()),
(4, 15, '第二条', '医疗机构不得使用非卫生技术人员从事医疗卫生技术工作。', 2, 'admin', NOW()),
-- 第四章 执业行为
(4, 16, '第一条', '医疗机构应当按照核准登记的诊疗科目开展诊疗活动。', 1, 'admin', NOW()),
(4, 16, '第二条', '医疗机构不得出卖、转让、出借《医疗机构执业许可证》。', 2, 'admin', NOW()),
(4, 16, '第三条', '医疗机构对传染病、职业病、精神病的诊治必须按照有关规定进行。', 3, 'admin', NOW());

-- 医疗机构校验管理办法 条款 (regulation_id = 5)
INSERT INTO `sys_regulation_article` (`regulation_id`, `chapter_id`, `article_no`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
-- 第一章 总则
(5, 17, '第一条', '为加强医疗机构监督管理，规范医疗机构校验工作，根据《医疗机构管理条例》制定本办法。', 1, 'admin', NOW()),
(5, 17, '第二条', '医疗机构校验是指卫生行政部门依法对医疗机构执业条件进行核查的行政行为。', 2, 'admin', NOW()),
-- 第二章 校验申请
(5, 18, '第一条', '医疗机构应当于校验有效期届满前三十日内向校验部门申请校验。', 1, 'admin', NOW()),
(5, 18, '第二条', '医疗机构申请校验应当提交校验申请书、执业许可证副本等材料。', 2, 'admin', NOW()),
-- 第三章 校验审查
(5, 19, '第一条', '校验部门应当对医疗机构提交的材料进行审查，并进行现场核查。', 1, 'admin', NOW()),
(5, 19, '第二条', '校验审查内容包括：床位设定、科室设置、人员配备、设备设施、执业行为等。', 2, 'admin', NOW()),
-- 第四章 校验结论
(5, 20, '第一条', '校验合格的，由校验部门在执业许可证副本上加盖校验合格章。', 1, 'admin', NOW()),
(5, 20, '第二条', '校验不合格的，责令其限期整改后重新申请校验；逾期不整改或整改后仍不合格的，吊销其执业许可证。', 2, 'admin', NOW());

-- 传染病防治法 条款 (regulation_id = 6)
INSERT INTO `sys_regulation_article` (`regulation_id`, `chapter_id`, `article_no`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
-- 第一章 总则
(6, 21, '第一条', '为了预防、控制和消除传染病的发生与流行，保障人体健康和公共卫生，制定本法。', 1, 'admin', NOW()),
(6, 21, '第二条', '国家对传染病防治实行预防为主的方针，防治结合、分类管理、依靠科学、依靠群众。', 2, 'admin', NOW()),
(6, 21, '第三条', '本法规定的传染病分为甲类、乙类和丙类。', 3, 'admin', NOW()),
-- 第二章 传染病预防
(6, 22, '第一条', '各级人民政府应当开展预防传染病的健康教育，普及传染病防治知识。', 1, 'admin', NOW()),
(6, 22, '第二条', '医疗机构应当确定专门的部门或者人员，承担传染病疫情报告工作。', 2, 'admin', NOW()),
(6, 22, '第三条', '疾病预防控制机构应当主动收集、分析、调查、核实传染病疫情信息。', 3, 'admin', NOW()),
-- 第三章 疫情报告、通报和公布
(6, 23, '第一条', '疾病预防控制机构、医疗机构和采供血机构及其执行职务的人员发现本法规定的传染病疫情或者发现其他传染病暴发、流行，应当在规定的时限内向所在地疾病预防控制机构报告。', 1, 'admin', NOW()),
(6, 23, '第二条', '任何单位和个人不得隐瞒、谎报、缓报传染病疫情。', 2, 'admin', NOW()),
-- 第四章 疫情控制
(6, 24, '第一条', '医疗机构发现甲类传染病时，应当及时采取下列措施：对病人、病原携带者，予以隔离治疗。', 1, 'admin', NOW()),
(6, 24, '第二条', '疾病预防控制机构发现传染病疫情或者接到疫情报告时，应当及时采取疫情调查、密切接触者管理、应急接种等控制措施。', 2, 'admin', NOW()),
-- 第五章 医疗救治
(6, 25, '第一条', '县级以上人民政府应当加强和完善传染病医疗救治服务网络建设。', 1, 'admin', NOW()),
(6, 25, '第二条', '医疗机构应当建立健全传染病管理工作制度，配备相应的防护设施。', 2, 'admin', NOW()),
-- 第六章 法律责任
(6, 26, '第一条', '违反本法规定，有下列行为之一的，由县级以上人民政府卫生行政部门责令限期改正，给予警告。（一）未依照本法规定履行传染病疫情报告职责，或者隐瞒、谎报、缓报传染病疫情的。', 1, 'admin', NOW()),
(6, 26, '第二条', '违反本法规定，导致传染病传播、流行，给他人人身、财产造成损害的，应当依法承担民事责任。', 2, 'admin', NOW());

-- 突发公共卫生事件应急条例 条款 (regulation_id = 7)
INSERT INTO `sys_regulation_article` (`regulation_id`, `chapter_id`, `article_no`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
-- 第一章 总则
(7, 27, '第一条', '为了有效预防、及时控制和消除突发公共卫生事件的危害，保障公众身体健康与生命安全，维护正常的社会秩序，制定本条例。', 1, 'admin', NOW()),
(7, 27, '第二条', '本条例所称突发公共卫生事件，是指突然发生，造成或者可能造成公众健康严重损害的重大传染病疫情、群体性不明原因疾病、重大食物和职业中毒以及其他严重影响公众健康的事件。', 2, 'admin', NOW()),
-- 第二章 应急准备
(7, 28, '第一条', '国务院有关部门、县级以上地方人民政府及其有关部门应当依照本条例的规定，建立和完善突发公共卫生事件应急处理工作机制。', 1, 'admin', NOW()),
(7, 28, '第二条', '县级以上人民政府应当建立和完善突发公共卫生事件监测与预警系统。', 2, 'admin', NOW()),
-- 第三章 应急处理
(7, 29, '第一条', '突发公共卫生事件发生后，国务院有关部门和县级以上地方人民政府及其有关部门应当按照突发事件应急预案的规定，分级负责，相互配合，采取应急处理措施。', 1, 'admin', NOW()),
(7, 29, '第二条', '医疗卫生机构应当对因突发事件致病的人员提供医疗救护和现场救援，对需要转送的病人应当按照规定转送。', 2, 'admin', NOW()),
(7, 29, '第三条', '有关人民政府应当根据实际情况和需要，采取下列应急处理措施：组织专业人员对突发事件现场进行处理；对传染病病人、疑似传染病病人采取就地隔离、就地观察、就地治疗的措施。', 3, 'admin', NOW()),
-- 第四章 法律责任
(7, 30, '第一条', '医疗卫生机构有下列行为之一的，由卫生行政主管部门责令改正、通报批评、给予警告；对主要负责人、负有责任的主管人员和其他直接责任人员依法给予降级或者撤职的纪律处分。（一）未依照本条例的规定履行报告职责，隐瞒、缓报或者谎报的。', 1, 'admin', NOW()),
(7, 30, '第二条', '有关人员在突发事件应急处理工作中，有下列行为之一的，对有关责任人员依法给予行政处分或者纪律处分。（一）未依照本条例的规定及时采取应急处理措施，造成严重后果的。', 2, 'admin', NOW());

-- ============================================
-- 4. 添加更多定性依据
-- ============================================

INSERT INTO `sys_legal_basis` (`basis_no`, `title`, `violation_type`, `issuing_authority`, `effective_date`, `legal_level`, `clauses`, `legal_liability`, `discretion_standard`, `regulation_id`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
-- 医疗机构管理条例 定性依据
('LB-003', '出卖、转让、出借医疗机构执业许可证', '医疗机构管理', '国家卫生健康委员会', '2022-05-01', '规章', '["医疗机构管理条例》第二十三条', '由县级以上人民政府卫生行政部门责令改正，没收非法所得，并处罚款；情节严重的，吊销执业许可证。', '轻微：责令改正，没收非法所得，处5000元以下罚款；一般：责令改正，没收非法所得，处5000-10000元罚款；严重：吊销执业许可证。', 3, '0', '0', 'admin', NOW()),
('LB-004', '使用非卫生技术人员从事医疗卫生技术工作', '医疗机构管理', '国家卫生健康委员会', '2022-05-01', '规章', '["医疗机构管理条例》第二十八条]', '由县级以上人民政府卫生行政部门责令其限期改正，并可处以5000元以下罚款；情节严重的，吊销其《医疗机构执业许可证》。', '轻微：责令限期改正；一般：处以3000元以下罚款；严重：处以3000-5000元罚款，吊销执业许可证。', 3, '0', '0', 'admin', NOW()),
('LB-005', '逾期不校验医疗机构执业许可证', '医疗机构管理', '国家卫生健康委员会', '2022-05-01', '规章', '["医疗机构管理条例》第二十二条', '由县级以上人民政府卫生行政部门责令其限期补办校验手续；拒不校验的，吊销其《医疗机构执业许可证》。', '轻微：责令限期补办校验；一般：通报批评；严重：吊销执业许可证。', 3, '0', '0', 'admin', NOW()),
-- 医疗机构管理条例实施细则 定性依据
('LB-006', '超出核准登记的诊疗科目开展诊疗活动', '超范围执业', '国家卫生健康委员会', '2022-07-01', '规章', '["医疗机构管理条例实施细则》第八十条', '由县级以上人民政府卫生行政部门给予警告，并可处以3000元以下的罚款；情节严重的，吊销其《医疗机构执业许可证》。', '轻微：警告；一般：处以1000元以下罚款；严重：处以1000-3000元罚款，吊销执业许可证。', 4, '0', '0', 'admin', NOW()),
('LB-007', '未配备与其业务相适应的卫生技术人员', '人员配备不符', '国家卫生健康委员会', '2022-07-01', '规章', '["医疗机构管理条例实施细则》第八条', '由县级以上人民政府卫生行政部门责令其限期改正；逾期不改正的，处以警告。', '轻微：警告；一般：责令限期改正；严重：处以罚款。', 4, '0', '0', 'admin', NOW()),
-- 医疗机构校验管理办法 定性依据
('LB-008', '未按规定申请医疗机构校验', '医疗机构校验', '国家卫生健康委员会', '2022-09-01', '规范性文件', '["医疗机构校验管理办法》第十四条', '由校验部门责令其补办校验手续，可以给予警告，并可处以5000元以下罚款。', '轻微：警告；一般：责令限期补办，处2000元以下罚款；严重：处以2000-5000元罚款。', 5, '0', '0', 'admin', NOW()),
('LB-009', '校验不合格经责令整改后仍不合格', '医疗机构校验', '国家卫生健康委员会', '2022-09-01', '规范性文件', '["医疗机构校验管理办法》第十七条', '吊销其《医疗机构执业许可证》。', '整改后仍不合格的，直接吊销执业许可证。', 5, '0', '0', 'admin', NOW()),
-- 传染病防治法 定性依据
('LB-010', '未依照规定履行传染病疫情报告职责', '传染病防治', '全国人民代表大会常务委员会', '2013-06-29', '法律', '["传染病防治法》第三十条', '由县级以上人民政府卫生行政部门责令限期改正，通报批评，给予警告；对负有责任的主管人员和其他直接责任人员，依法给予处分。', '轻微：责令改正，警告；一般：通报批评；严重：给予处分。', 6, '0', '0', 'admin', NOW()),
('LB-011', '隐瞒、谎报、缓报传染病疫情', '传染病防治', '全国人民代表大会常务委员会', '2013-06-29', '法律', '["传染病防治法》第三十七条', '由上级机关责令改正，通报批评，给予警告；对主要负责人给予降级、撤职处分。', '轻微：警告；一般：降级处分；严重：撤职处分。', 6, '0', '0', 'admin', NOW()),
('LB-012', '疾病预防控制机构未按规定进行疫情调查', '传染病防治', '全国人民代表大会常务委员会', '2013-06-29', '法律', '["传染病防治法》第四十条', '由上级卫生行政部门责令改正，通报批评，给予警告。', '轻微：警告；一般：通报批评；严重：对责任人给予处分。', 6, '0', '0', 'admin', NOW()),
-- 突发公共卫生事件应急条例 定性依据
('LB-013', '未依照条例规定及时采取应急处理措施', '应急管理', '国务院', '2003-05-09', '法规', '["突发公共卫生事件应急条例》第四十五条', '对有关责任人员依法给予行政处分或者纪律处分。', '轻微：行政处分；一般：降级处分；严重：撤职处分。', 7, '0', '0', 'admin', NOW()),
('LB-014', '医疗卫生机构未依照条例规定履行报告职责', '应急管理', '国务院', '2003-05-09', '法规', '["突发公共卫生事件应急条例》第四十五条', '对主要负责人、负有责任的主管人员和其他直接责任人员依法给予降级或者撤职的纪律处分。', '轻微：降级处分；一般：撤职处分；严重：开除处分。', 7, '0', '0', 'admin', NOW());

-- ============================================
-- 5. 添加更多处理依据
-- ============================================

INSERT INTO `sys_processing_basis` (`basis_no`, `title`, `violation_type`, `issuing_authority`, `effective_date`, `legal_level`, `clauses`, `legal_liability`, `discretion_standard`, `regulation_id`, `status`, `del_flag`, `create_by`, `create_time`) VALUES
-- 医疗机构管理条例 处理依据
('PB-003', '出卖、转让、出借医疗机构执业许可证', '医疗机构管理', '国家卫生健康委员会', '2022-05-01', '规章', '["医疗机构管理条例》第二十三条', '没收非法所得，并处罚款；情节严重的，吊销执业许可证。', '轻微：没收非法所得，处5000元以下罚款；一般：没收非法所得，处5000-10000元罚款；严重：吊销执业许可证。', 3, '0', '0', 'admin', NOW()),
('PB-004', '使用非卫生技术人员从事医疗卫生技术工作', '机构管理', '国家卫生健康委员会', '2022-05-01', '规章', '["医疗机构管理条例》第二十八条', '责令限期改正，并可处以5000元以下罚款；情节严重的，吊销执业许可证。', '轻微：责令限期改正；一般：处以3000元以下罚款；严重：处以3000-5000元罚款，吊销执业许可证。', 3, '0', '0', 'admin', NOW()),
('PB-005', '逾期不校验执业许可证且拒不补办', '医疗机构管理', '国家卫生健康委员会', '2022-05-01', '规章', '["医疗机构管理条例》第二十二条', '吊销其《医疗机构执业许可证》。', '逾期不校验且拒不补办的，直接吊销执业许可证。', 3, '0', '0', 'admin', NOW()),
-- 医疗机构管理条例实施细则 处理依据
('PB-006', '超出核准登记的诊疗科目开展诊疗活动', '超范围执业', '国家卫生健康委员会', '2022-07-01', '规章', '["医疗机构管理条例实施细则》第八十条', '给予警告，并可处以3000元以下的罚款；情节严重的，吊销执业许可证。', '轻微：警告；一般：处以1000元以下罚款；严重：处以1000-3000元罚款，吊销执业许可证。', 4, '0', '0', 'admin', NOW()),
('PB-007', '转让、出借执业许可证行为', '机构管理', '国家卫生健康委员会', '2022-07-01', '规章', '["医疗机构管理条例实施细则》第八十一条', '没收非法所得，并处以罚款；情节严重的，吊销执业许可证。', '轻微：没收非法所得，处3000元以下罚款；一般：没收非法所得，处3000-5000元罚款；严重：吊销执业许可证。', 4, '0', '0', 'admin', NOW()),
-- 医疗机构校验管理办法 处理依据
('PB-008', '不按期申请校验且拒不改正', '校验管理', '国家卫生健康委员会', '2022-09-01', '规范性文件', '["医疗机构校验管理办法》第十七条', '吊销其《医疗机构执业许可证》。', '责令限期补办校验手续，逾期不补办或整改后仍不合格的，吊销执业许可证。', 5, '0', '0', 'admin', NOW()),
('PB-009', '校验中发现不符合医疗机构基本标准', '校验管理', '国家卫生健康委员会', '2022-09-01', '规范性文件', '["医疗机构校验管理办法》第十六条', '责令其限期整改，整改期间不得开展诊疗活动；逾期不整改或整改后仍不合格的，吊销执业许可证。', '轻微：责令限期整改；一般：整改期间停止执业；严重：吊销执业许可证。', 5, '0', '0', 'admin', NOW()),
-- 传染病防治法 处理依据
('PB-010', '疾病预防控制机构未履行传染病防治职责', '传染病防治', '全国人民代表大会常务委员会', '2013-06-29', '法律', '["传染病防治法》第六十五条', '责令改正，通报批评，给予警告；对负有责任的主管人员和其他直接责任人员依法给予处分。', '轻微：警告；一般：通报批评；严重：给予处分。', 6, '0', '0', 'admin', NOW()),
('PB-011', '医疗机构未按规定报告传染病疫情', '传染病防治', '全国人民代表大会常务委员会', '2013-06-29', '法律', '["传染病防治法》第三十条', '责令改正，通报批评，给予警告；对负有责任的主管人员和其他直接责任人员依法给予处分。', '轻微：警告；一般：通报批评；严重：给予处分。', 6, '0', '0', 'admin', NOW()),
-- 突发公共卫生事件应急条例 处理依据
('PB-012', '医疗卫生机构未按规定履行应急处理职责', '应急管理', '国务院', '2003-05-09', '法规', '["突发公共卫生事件应急条例》第四十五条', '对主要负责人、负有责任的主管人员和其他直接责任人员依法给予降级或者撤职的纪律处分。', '轻微：降级处分；一般：撤职处分；严重：开除处分。', 7, '0', '0', 'admin', NOW()),
('PB-013', '在突发事件应急处理中玩忽职守', '应急管理', '国务院', '2003-05-09', '法规', '["突发公共卫生事件应急条例》第四十六条', '给予行政处分；构成犯罪的，依法追究刑事责任。', '轻微：行政处分；一般：降级处分；严重：追究刑事责任。', 7, '0', '0', 'admin', NOW());

-- ============================================
-- 6. 添加依据与条款的关联
-- ============================================

-- 获取最大的link_id
SET @maxLinkId = (SELECT COALESCE(MAX(link_id), 0) FROM sys_basis_chapter_link);

-- 法律 1 (基本医疗卫生与健康促进法) - 已有章节 1-9, article 1-15
-- 章节1 (总则) - articles 1,2
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 1, 1, 1, 'legal', 1, 'admin', NOW()),
(@maxLinkId + 2, 1, 2, 'legal', 2, 'admin', NOW());

-- 章节3 (医疗机构) - articles 5,6,7
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 3, 3, 5, 'legal', 3, 'admin', NOW()),
(@maxLinkId + 4, 3, 6, 'legal', 4, 'admin', NOW()),
(@maxLinkId + 5, 3, 7, 'legal', 5, 'admin', NOW());

-- 章节9 (法律责任) - articles 14,15
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 6, 9, 14, 'legal', 6, 'admin', NOW()),
(@maxLinkId + 7, 9, 15, 'legal', 7, 'admin', NOW());

-- 法律 2 (生物安全法) - 已有章节 4,5,10,11,12
-- 章节4 (总则) - article 4
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 8, 4, 4, 'legal', 8, 'admin', NOW()),
(@maxLinkId + 9, 4, 4, 'legal', 9, 'admin', NOW());

-- 章节11 (防控措施) - articles 18,19
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 10, 11, 18, 'legal', 10, 'admin', NOW()),
(@maxLinkId + 11, 11, 19, 'legal', 10, 'admin', NOW());

-- 法规 3 (医疗机构管理条例) - 章节 8,9,10,11,12
-- 章节8 (总则) - articles 22,23
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 12, 8, 22, 'legal', 11, 'admin', NOW()),
(@maxLinkId + 13, 8, 23, 'legal', 12, 'admin', NOW()),
(@maxLinkId + 14, 8, 22, 'processing', 6, 'admin', NOW()),
(@maxLinkId + 15, 8, 23, 'processing', 7, 'admin', NOW());

-- 章节10 (执业规范) - articles 26,27,28,29,30
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 16, 10, 26, 'legal', 12, 'admin', NOW()),
(@maxLinkId + 17, 10, 27, 'legal', 12, 'admin', NOW()),
(@maxLinkId + 18, 10, 28, 'legal', 13, 'admin', NOW()),
(@maxLinkId + 19, 10, 29, 'legal', 13, 'admin', NOW()),
(@maxLinkId + 20, 10, 26, 'processing', 8, 'admin', NOW()),
(@maxLinkId + 21, 10, 27, 'processing', 8, 'admin', NOW()),
(@maxLinkId + 22, 10, 28, 'processing', 9, 'admin', NOW()),
(@maxLinkId + 23, 10, 29, 'processing', 9, 'admin', NOW());

-- 章节12 (法律责任) - articles 33,34
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 24, 12, 33, 'legal', 14, 'admin', NOW()),
(@maxLinkId + 25, 12, 34, 'legal', 14, 'admin', NOW()),
(@maxLinkId + 26, 12, 33, 'processing', 10, 'admin', NOW()),
(@maxLinkId + 27, 12, 34, 'processing', 11, 'admin', NOW());

-- 规章 4 (医疗机构管理条例实施细则) - 章节 13,14,15,16
-- 章节13 (总则) - articles 35,36
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 28, 13, 35, 'legal', 15, 'admin', NOW()),
(@maxLinkId + 29, 13, 36, 'legal', 15, 'admin', NOW());

-- 章节14 (机构设置) - articles 37,38,39
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 30, 14, 37, 'legal', 16, 'admin', NOW()),
(@maxLinkId + 31, 14, 38, 'legal', 16, 'admin', NOW()),
(@maxLinkId + 32, 14, 39, 'legal', 16, 'admin', NOW()),
(@maxLinkId + 33, 14, 37, 'processing', 12, 'admin', NOW());

-- 章节16 (执业行为) - articles 43,44,45
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 34, 16, 43, 'legal', 16, 'admin', NOW()),
(@maxLinkId + 35, 16, 44, 'legal', 17, 'admin', NOW()),
(@maxLinkId + 36, 16, 45, 'legal', 17, 'admin', NOW()),
(@maxLinkId + 37, 16, 43, 'processing', 13, 'admin', NOW()),
(@maxLinkId + 38, 16, 44, 'processing', 13, 'admin', NOW());

-- 规范性文件 5 (医疗机构校验管理办法) - 章节 17,18,19,20
-- 章节18 (校验申请) - articles 47,48
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 39, 18, 47, 'legal', 18, 'admin', NOW()),
(@maxLinkId + 40, 18, 48, 'legal', 18, 'admin', NOW()),
(@maxLinkId + 41, 18, 47, 'processing', 14, 'admin', NOW());

-- 章节20 (校验结论) - articles 51,52
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 42, 20, 51, 'legal', 19, 'admin', NOW()),
(@maxLinkId + 43, 20, 52, 'legal', 19, 'admin', NOW()),
(@maxLinkId + 44, 20, 51, 'processing', 15, 'admin', NOW()),
(@maxLinkId + 45, 20, 52, 'processing', 16, 'admin', NOW());

-- 法律 6 (传染病防治法) - 章节 21,22,23,24,25,26
-- 章节23 (疫情报告) - articles 57,58
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 46, 23, 57, 'legal', 20, 'admin', NOW()),
(@maxLinkId + 47, 23, 58, 'legal', 21, 'admin', NOW()),
(@maxLinkId + 48, 23, 57, 'processing', 17, 'admin', NOW()),
(@maxLinkId + 49, 23, 58, 'processing', 17, 'admin', NOW());

-- 章节24 (疫情控制) - articles 60,61
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 50, 24, 60, 'legal', 22, 'admin', NOW()),
(@maxLinkId + 51, 24, 61, 'legal', 22, 'admin', NOW());

-- 章节26 (法律责任) - articles 65,66
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 52, 26, 65, 'legal', 20, 'admin', NOW()),
(@maxLinkId + 53, 26, 66, 'legal', 21, 'admin', NOW()),
(@maxLinkId + 54, 26, 65, 'processing', 18, 'admin', NOW()),
(@maxLinkId + 55, 26, 66, 'processing', 19, 'admin', NOW());

-- 法规 7 (突发公共卫生事件应急条例) - 章节 27,28,29,30
-- 章节29 (应急处理) - articles 70,71,72
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 56, 29, 70, 'legal', 23, 'admin', NOW()),
(@maxLinkId + 57, 29, 71, 'legal', 23, 'admin', NOW()),
(@maxLinkId + 58, 29, 72, 'legal', 24, 'admin', NOW()),
(@maxLinkId + 59, 29, 70, 'processing', 20, 'admin', NOW()),
(@maxLinkId + 60, 29, 71, 'processing', 20, 'admin', NOW()),
(@maxLinkId + 61, 29, 72, 'processing', 21, 'admin', NOW());

-- 章节30 (法律责任) - articles 74,75
INSERT INTO `sys_basis_chapter_link` (`link_id`, `chapter_id`, `article_id`, `basis_type`, `basis_id`, `create_by`, `create_time`) VALUES
(@maxLinkId + 62, 30, 74, 'legal', 23, 'admin', NOW()),
(@maxLinkId + 63, 30, 75, 'legal', 24, 'admin', NOW()),
(@maxLinkId + 64, 30, 74, 'processing', 22, 'admin', NOW()),
(@maxLinkId + 65, 30, 75, 'processing', 22, 'admin', NOW());

-- ============================================
-- 7. 验证数据
-- ============================================

SELECT '=== 法律法规统计 ===' AS '';
SELECT regulation_id, title, legal_type FROM sys_regulation WHERE del_flag='0' ORDER BY regulation_id;

SELECT '=== 章节统计 ===' AS '';
SELECT r.regulation_id, r.title, COUNT(c.chapter_id) as chapter_count
FROM sys_regulation r
LEFT JOIN sys_regulation_chapter c ON r.regulation_id = c.regulation_id
WHERE r.del_flag='0'
GROUP BY r.regulation_id, r.title;

SELECT '=== 条款统计 ===' AS '';
SELECT r.regulation_id, r.title, COUNT(a.article_id) as article_count
FROM sys_regulation r
LEFT JOIN sys_regulation_article a ON r.regulation_id = a.regulation_id
WHERE r.del_flag='0'
GROUP BY r.regulation_id, r.title;

SELECT '=== 定性依据统计 ===' AS '';
SELECT regulation_id, COUNT(*) as count FROM sys_legal_basis WHERE del_flag='0' GROUP BY regulation_id;

SELECT '=== 处理依据统计 ===' AS '';
SELECT regulation_id, COUNT(*) as count FROM sys_processing_basis WHERE del_flag='0' GROUP BY regulation_id;

SELECT '=== 关联数据统计 ===' AS '';
SELECT basis_type, COUNT(*) as link_count FROM sys_basis_chapter_link GROUP BY basis_type;
