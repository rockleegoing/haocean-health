-- ============================================
-- 脚本：V1.2.4__law_type_bind.sql
-- 版本：1.2.4
-- 日期：2026-04-30
-- 描述：法律类型多对多关联表
-- ============================================

-- 1. 创建 law_type_bind 中间表
CREATE TABLE `law_type_bind` (
  `law_id` BIGINT NOT NULL COMMENT '法律ID',
  `type_id` BIGINT NOT NULL COMMENT '类型ID',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`law_id`, `type_id`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8MB4 COMMENT='法律类型关联表';

-- 2. 迁移现有 law.type_id 数据到中间表
INSERT INTO `law_type_bind` (`law_id`, `type_id`, `create_by`, `create_time`)
SELECT `id`, `type_id`, 'system', NOW()
FROM `law`
WHERE `type_id` IS NOT NULL;

-- 3. 删除 law 表的 type_id 字段
ALTER TABLE `law` DROP COLUMN `type_id`;
