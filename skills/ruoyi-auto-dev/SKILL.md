---
name: ruoyi-auto-dev
description: RuoYi 项目自动化开发技能。当用户要创建新模块、新功能、新页面时自动使用此技能。根据业务需求自动设计数据库表结构，调用代码生成器 API 完成从建表到生成前后端代码的全流程。适用于任何 RuoYi-Vue 项目的开发场景。
---

# RuoYi 自动化开发技能

## 技能目标

根据用户的业务需求描述，自动化完成以下工作：

1. **设计数据库表结构** - 根据业务描述生成合理的 CREATE TABLE SQL
2. **创建数据表** - 调用后端 API 执行建表
3. **导入表到生成器** - 将新表导入代码生成器模块
4. **配置生成参数** - 设置包名、作者、模板类型等
5. **生成代码** - 执行代码生成，输出完整的后端和前端代码
6. **生成菜单 SQL** - 提供系统菜单导入脚本

## 工作流程

### 步骤 1：理解需求并设计数据库

首先与用户沟通，明确以下信息：

- 模块名称和业务功能
- 需要哪些字段（字段名、类型、约束）
- 是否需要树形结构或主子表结构
- 前端类型偏好（Vue2/Vue3）

**设计原则：**

- 遵循项目命名规范（表前缀 `sys_` 或其他业务前缀）
- 包含基础字段：`create_by`, `create_time`, `update_by`, `update_time`, `remark`, `del_flag`
- 主键使用 `id`，自增或雪花算法
- 字段类型参考 MySQL 最佳实践

### 步骤 2：生成建表 SQL

使用以下模板生成建表 SQL：

```sql
CREATE TABLE IF NOT EXISTS `表名` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  -- 业务字段
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(2) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表注释';
```

### 步骤 3：调用 API 创建表并导入

**API 端点：** `POST /tool/gen/createTable`

**请求参数：**
- `sql`: 建表 SQL 语句
- `tplWebType`: 前端模板类型（vue 或 elementUIPlus）

**响应处理：**
- 成功：获取生成的 tableId，进入步骤 4
- 失败：分析错误原因，调整 SQL 后重试

### 步骤 4：配置生成参数

**API 端点：** `PUT /tool/gen`

**配置项：**
- `tableName`: 表名
- `className`: 类名（自动转换）
- `functionName`: 功能描述
- `moduleName`: 模块名
- `packageName`: 包路径
- `tplCategory`: 模板类型（crud/tree/sub）
- `parentMenuId`: 上级菜单 ID（默认系统工具）

### 步骤 5：生成代码

**API 端点：** `GET /tool/gen/genCode/{tableName}`

**前提条件：** `generator.yml` 中 `allowOverwrite: true`

**生成的文件：**
- 后端：Entity, Mapper, XML, Service, Controller
- 前端：Vue 页面，API 文件，TypeScript 类型定义

### 步骤 6：生成菜单 SQL

**API 端点：** `GET /tool/gen/preview/{tableId}`

从预览结果中提取 `menu.sql` 内容，提供给用户执行。

## 注意事项

### 数据库设计

- 避免使用 MySQL 保留字作为字段名
- 字段长度要合理（如 varchar(200) 而不是 varchar(50)）
- 外键字段命名规范：`xxx_id`
- 状态字段统一用 `status`，类型用 `char(1)`

### 代码生成配置

- 包路径默认：`com.ruoyi.system`
- 作者名：从 `generator.yml` 读取
- 表前缀：`sys_` 或其他配置的前缀

### 权限控制

- 确保当前用户有 `tool:gen:*` 权限
- 创建表需要 `admin` 角色

## 错误处理

| 错误 | 处理方式 |
|------|----------|
| 表已存在 | 提示用户选择删除重建或跳过 |
| API 调用失败 | 检查后端服务是否启动 |
| 权限不足 | 提示用户使用 admin 账号 |
| 生成配置校验失败 | 检查树表/主子表必填字段 |

## 输出格式

任务完成后，提供以下内容：

1. **执行摘要** - 完成了哪些操作
2. **代码位置** - 生成的文件路径
3. **菜单 SQL** - 需要执行的菜单插入语句
4. **后续步骤** - 还需要手动完成的工作

## 示例

### 用户输入

> 创建一个部门管理模块，包含部门名称、负责人、电话、排序字段

### 技能执行

1. 设计 `sys_dept` 表结构
2. 调用 API 创建表
3. 配置生成参数（tplCategory=tree，支持树形结构）
4. 生成代码
5. 输出菜单 SQL

### 生成的表结构

```sql
CREATE TABLE sys_dept (
  id bigint NOT NULL AUTO_INCREMENT COMMENT '部门 ID',
  parent_id bigint DEFAULT '0' COMMENT '父部门 ID',
  ancestors varchar(500) DEFAULT '' COMMENT '祖级列表',
  dept_name varchar(100) DEFAULT '' COMMENT '部门名称',
  leader varchar(50) DEFAULT NULL COMMENT '负责人',
  phone varchar(20) DEFAULT NULL COMMENT '联系电话',
  order_num int DEFAULT '0' COMMENT '显示顺序',
  create_by varchar(64) DEFAULT '' COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT '' COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  del_flag char(2) DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';
```
