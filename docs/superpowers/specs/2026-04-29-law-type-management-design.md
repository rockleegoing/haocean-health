# 法律法规类型管理设计

> **日期：** 2026-04-29
> **状态：** 已确认

## 1. 背景

根据原型图需求，需要设计法律法规类型管理和与法律文档的关联方案。

**关键决策：**
- 关系类型：**一对多**（一个法律只能属于一种类型）
- 实现方式：**在 law 表添加 type_id 字段**（不采用中间表）
- 原因：避免 ALTER TABLE 的迁移风险，提供增量脚本

---

## 2. 数据库设计

### 2.1 新增表：law_type（法律法规类型表）

```sql
CREATE TABLE `law_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父类型ID（0为顶级）',
  `ancestors` VARCHAR(500) DEFAULT '' COMMENT '祖先路径（如：0,1,2）',
  `name` VARCHAR(50) NOT NULL COMMENT '类型名称',
  `icon` VARCHAR(100) DEFAULT '' COMMENT '图标（用于Android显示）',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8MB4 COMMENT='法律法规类型表';
```

### 2.2 修改表：law

```sql
ALTER TABLE `law` ADD COLUMN `type_id` BIGINT COMMENT '法律法规类型ID（末级）' AFTER `release_time`;
```

### 2.3 字段说明

| 字段 | 类型 | 说明 |
|-----|------|------|
| `id` | BIGINT | 主键 |
| `parent_id` | BIGINT | 父类型ID，0表示顶级节点 |
| `ancestors` | VARCHAR(500) | 祖先路径，便于查询子树 |
| `name` | VARCHAR(50) | 类型名称 |
| `icon` | VARCHAR(100) | 图标名称/URL，用于 Android 客户端展示 |
| `sort` | INT | 同级排序 |
| `status` | CHAR(1) | 状态：0正常 1停用 |

---

## 3. 树形结构设计

### 3.1 结构示意

```
法律法规类型（顶级，parent_id=0）
├── 国家法律（parent_id=0）
│   ├── 宪法
│   ├── 基本法律
│   └── 普通法律
├── 行政法规（parent_id=0）
│   ├── 国务院规章
│   └── 部门规章
└── 地方性法规（parent_id=0）
    ├── 省级法规
    └── 市级法规
```

### 3.2 约束

- **最多支持二级**：父节点不能是子节点的父节点
- **末级才能关联法律**：法律只能关联到叶子节点类型

---

## 4. 功能设计

### 4.1 类型管理

| 功能 | 说明 |
|-----|------|
| 列表查询 | 分页展示类型列表，支持名称搜索 |
| 新增类型 | 支持选择父类型，填写名称、图标、排序 |
| 编辑类型 | 修改类型信息 |
| 删除类型 | 需校验是否有子类型或关联法律 |
| 状态切换 | 启用/停用类型 |

### 4.2 图标选择器

- 前端使用 Element UI 图标库或第三方图标选择组件
- 提供对话框式图标选择界面
- 选中后回填 `icon` 字段

### 4.3 联动查询

| 场景 | 行为 |
|-----|------|
| 点击类型节点 | 右侧展示该类型下的法律列表 |
| 点击顶级节点 | 展示该顶级下所有末级类型的法律汇总 |
| 点击末级节点 | 仅展示该末级类型的法律 |

### 4.4 法律编辑

- 新增/编辑法律时，可选择末级类型
- 下拉框仅显示末级类型（`parent_id != 0`）

---

## 5. 文件清单

### 5.1 后端（RuoYi-Vue）

```
ruoyi-system/src/main/java/com/ruoyi/system/
├── domain/LawType.java                    # 新增：实体类
├── service/ISysLawTypeService.java        # 新增：接口
├── service/impl/SysLawTypeServiceImpl.java # 新增：实现
└── mapper/SysLawTypeMapper.java           # 新增：Mapper接口

ruoyi-system/src/main/resources/mapper/system/
└── SysLawTypeMapper.xml                   # 新增：Mapper XML

ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/
└── SysLawTypeController.java              # 新增：控制器
```

### 5.2 前端（ruoyi-ui）

```
ruoyi-ui/src/views/system/
├── lawType/index.vue                      # 新增：类型管理页面
└── law/index.vue                          # 修改：集成左侧类型栏+联动
```

### 5.3 SQL 脚本

```
sql/V1.2.3__law_type.sql                   # 新增：建表+字段+菜单+初始数据
```

---

## 6. SQL 脚本内容

```sql
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
  `status` CHAR(1) DEFAULT '0' COMMENT '状态',
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
```

---

## 7. Android 端适配

- 同步 `law_type` 表到本地数据库
- 左侧使用树形结构展示类型列表
- 图标字段用于渲染类型图标
- 点击末级类型后查询对应法律列表
