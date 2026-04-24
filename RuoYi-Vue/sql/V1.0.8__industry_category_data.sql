-- 行业分类预置数据
INSERT INTO `sys_industry_category` (`category_name`, `category_code`, `order_num`, `status`, `del_flag`, `create_by`, `create_time`, `remark`) VALUES
('公共场所', 'GCDG', 1, '0', '0', 'admin', sysdate(), '公共场所卫生监督'),
('医疗机构', 'YLJG', 2, '0', '0', 'admin', sysdate(), '医疗机构卫生监督'),
('学校卫生', 'XXWS', 3, '0', '0', 'admin', sysdate(), '学校卫生监督'),
('饮用水卫生', 'YSXWS', 4, '0', '0', 'admin', sysdate(), '饮用水及涉水产品卫生监督'),
('传染病防治', 'CRBFZ', 5, '0', '0', 'admin', sysdate(), '传染病防治卫生监督'),
('放射卫生', 'FSWS', 6, '0', '0', 'admin', sysdate(), '放射卫生监督'),
('消毒产品', 'XCCP', 7, '0', '0', 'admin', sysdate(), '消毒产品卫生监督'),
('职业卫生', 'ZYWS', 8, '0', '0', 'admin', sysdate(), '职业卫生监督');
