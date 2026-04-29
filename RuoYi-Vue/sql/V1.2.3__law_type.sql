-- ============================================
-- 脚本：V1.2.3__law_type.sql
-- 版本：1.2.3
-- 日期：2026-04-29
-- 描述：法律法规类型管理功能
-- ============================================

-- 1. 创建 law_type 表
CREATE TABLE `law_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父类型ID（0为顶级）',
  `ancestors` VARCHAR(500) DEFAULT '' COMMENT '祖先路径',
  `name` VARCHAR(50) NOT NULL COMMENT '类型名称',
  `icon` VARCHAR(100) DEFAULT '' COMMENT '图标',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8MB4 COMMENT='法律法规类型表';

-- 2. 为 law 表添加 type_id 字段
ALTER TABLE `law` ADD COLUMN `type_id` BIGINT COMMENT '法律法规类型ID（末级）' AFTER `release_time`;

-- 3. 插入初始数据（示例）
INSERT INTO `law_type` (`id`, `parent_id`, `ancestors`, `name`, `icon`, `sort`, `status`) VALUES
(1, 0, '0', '国家法律', 'el-icon-document', 1, '0'),
(2, 0, '0', '行政法规', 'el-icon-collection', 2, '0'),
(3, 0, '0', '地方性法规', 'el-icon-location', 3, '0');

INSERT INTO `law_type` (`id`, `parent_id`, `ancestors`, `name`, `icon`, `sort`, `status`) VALUES
(4, 1, '0,1', '宪法', 'el-icon-document', 1, '0'),
(5, 1, '0,1', '基本法律', 'el-icon-document', 2, '0'),
(6, 2, '0,2', '国务院规章', 'el-icon-collection', 1, '0'),
(7, 3, '0,3', '省级法规', 'el-icon-location', 1, '0');

-- 4. 添加菜单（法律类型管理）
-- 父菜单：法律法规
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT '法律类型管理', menu_id, 2, 'lawType', 'system/lawType/index', 1, 0, 'C', '0', '0', 'system:lawType:list', 'el-icon-set-up', 'admin', NOW(), '法律类型管理菜单'
FROM `sys_menu` WHERE `menu_name` = '法律法规' AND `parent_id` = 0 LIMIT 1;

SET @parent_id = LAST_INSERT_ID();

-- 按钮权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`) VALUES
('法律类型查询', @parent_id, 1, '#', '', 1, 0, 'F', '0', '0', 'system:lawType:query', '#', 'admin', NOW(), ''),
('法律类型新增', @parent_id, 2, '#', '', 1, 0, 'F', '0', '0', 'system:lawType:add', '#', 'admin', NOW(), ''),
('法律类型修改', @parent_id, 3, '#', '', 1, 0, 'F', '0', '0', 'system:lawType:edit', '#', 'admin', NOW(), ''),
('法律类型删除', @parent_id, 4, '#', '', 1, 0, 'F', '0', '0', 'system:lawType:remove', '#', 'admin', NOW(), '');