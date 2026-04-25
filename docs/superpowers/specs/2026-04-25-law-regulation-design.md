# 法律法规模件设计与实现文档

## 1. 概述

法律法规模块是移动卫生执法系统的核心业务模块，用于管理法律法规、章节、条款及定性依据。

### 1.1 数据结构

```
法规 (sys_regulation)
├── 章节 (sys_regulation_chapter)
│   └── 条款 (sys_regulation_article)
└── 定性依据 (sys_legal_basis)
```

### 1.2 模块组成

| 模块 | 技术栈 | 说明 |
|------|--------|------|
| 后端 | Spring Boot + MyBatis | RESTful API |
| Vue 管理后台 | Vue 2.6 + Element UI | 增删改查管理 |
| Android 移动端 | Kotlin + MVVM | 浏览和查询 |

## 2. 数据库设计

### 2.1 表结构

```sql
-- 法律法规主表
CREATE TABLE sys_regulation (
    regulation_id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,           -- 法律名称
    legal_type VARCHAR(20) NOT NULL,        -- 法律类型
    supervision_types VARCHAR(500),         -- 监管类型(JSON)
    publish_date VARCHAR(20),               -- 发布日期
    effective_date VARCHAR(20),             -- 实施日期
    issuing_authority VARCHAR(100),          -- 颁发机构
    content TEXT,                           -- 完整内容
    status CHAR(1) DEFAULT '0',            -- 状态
    del_flag CHAR(1) DEFAULT '0',          -- 删除标志
    ...
);

-- 章节表
CREATE TABLE sys_regulation_chapter (
    chapter_id BIGINT PRIMARY KEY,
    regulation_id BIGINT NOT NULL,          -- 关联法规ID
    chapter_no VARCHAR(50),                 -- 章节号（如：第一章）
    chapter_title VARCHAR(255),             -- 章节标题
    sort_order INT DEFAULT 0,             -- 排序
    ...
);

-- 条款表
CREATE TABLE sys_regulation_article (
    article_id BIGINT PRIMARY KEY,
    chapter_id BIGINT,                      -- 关联章节ID
    regulation_id BIGINT NOT NULL,          -- 关联法规ID
    article_no VARCHAR(50),                 -- 条款号（如：第一条）
    content TEXT,                           -- 条款内容
    sort_order INT DEFAULT 0,              -- 排序
    ...
);

-- 定性依据表
CREATE TABLE sys_legal_basis (
    basis_id BIGINT PRIMARY KEY,
    basis_no VARCHAR(20),                  -- 编号
    title VARCHAR(255) NOT NULL,           -- 标题
    violation_type VARCHAR(100),           -- 违法类型
    clauses TEXT,                          -- 条款内容
    legal_liability TEXT,                  -- 法律责任
    discretion_standard TEXT,              -- 裁量标准
    regulation_id BIGINT,                  -- 关联法规ID
    ...
);
```

## 3. 后端 API 设计

### 3.1 Controller 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/system/regulation/list` | GET | 法规列表（支持分页） |
| `/system/regulation/{id}` | GET | 法规详情 |
| `/system/regulation` | POST | 新增法规 |
| `/system/regulation` | PUT | 修改法规 |
| `/system/regulation/{ids}` | DELETE | 删除法规 |
| `/system/regulation/chapters/{id}` | GET | 章节列表 |
| `/system/regulation/chapter/{id}` | GET | 章节详情 |
| `/system/regulation/chapter` | POST | 新增章节 |
| `/system/regulation/chapter` | PUT | 修改章节 |
| `/system/regulation/chapter/{ids}` | DELETE | 删除章节 |
| `/system/regulation/articles/{id}` | GET | 条款列表 |
| `/system/regulation/article/{id}` | GET | 条款详情 |
| `/system/regulation/article` | POST | 新增条款 |
| `/system/regulation/article` | PUT | 修改条款 |
| `/system/regulation/article/{ids}` | DELETE | 删除条款 |
| `/system/legalBasis/list` | GET | 定性依据列表 |
| `/system/legalBasis/{id}` | GET | 定性依据详情 |

## 4. Vue 管理后台

### 4.1 页面结构

```
views/system/
├── regulation/
│   ├── index.vue          # 法规列表页
│   ├── chapter/
│   │   └── index.vue       # 章节管理页
│   └── article/
│       └── index.vue       # 条款管理页
└── legalBasis/
    └── index.vue          # 定性依据管理页
```

### 4.2 菜单结构

```
系统管理
├── 法律法规
│   ├── 章节管理  ← 需先选择法规
│   └── 条款管理  ← 需先选择法规和章节
└── 定性依据
```

### 4.3 功能特性

- **法规列表**：分页查询、搜索（名称/类型/机构）、新增/修改/删除/导出
- **章节管理**：按法规筛选、章节增删改查、跳转条款管理
- **条款管理**：按法规和章节筛选、条款增删改查

## 5. Android 移动端

### 5.1 页面结构

```
feature/law/
├── api/
│   └── LawApi.kt           # API 接口
├── model/
│   ├── Regulation.kt
│   ├── RegulationChapter.kt
│   ├── RegulationArticle.kt
│   └── LegalBasis.kt
├── db/
│   ├── entity/            # Room Entity
│   └── dao/               # Room DAO
├── repository/
│   └── LawRepository.kt    # 数据仓库
├── viewmodel/
│   └── LawViewModel.kt
└── ui/
    ├── RegulationListActivity.kt    # 法规列表
    ├── RegulationDetailActivity.kt # 法规详情
    ├── ArticleDetailActivity.kt   # 条款详情
    └── basis/
        ├── LegalBasisListActivity.kt
        └── LegalBasisDetailActivity.kt
```

### 5.2 功能特性

- **法规浏览**：列表展示、分类筛选、全文搜索
- **详情查看**：法规信息、章节列表、条款列表
- **条款阅读**：完整条款内容展示
- **定性依据**：违法类型检索、法律责任和裁量标准

### 5.3 数据同步

使用 Room 本地数据库 + Flow 实现离线访问：
- 启动时从服务器同步法规数据
- 查看详情时同步章节和条款
- 支持离线浏览

## 6. 文件清单

### 6.1 后端

| 文件 | 变更类型 |
|------|----------|
| `SysRegulationController.java` | 修改：新增章节/条款详情接口 |
| `ISysRegulationService.java` | 修改：新增方法声明 |
| `SysRegulationServiceImpl.java` | 修改：填充关联字段 |
| `SysRegulationMapper.java` | 修改：新增方法声明 |
| `SysRegulationMapper.xml` | 修改：新增详情查询 SQL |
| `SysRegulationChapter.java` | 修改：新增 regulationTitle 字段 |
| `SysRegulationArticle.java` | 修改：新增 regulationTitle, chapterTitle 字段 |

### 6.2 前端 Vue

| 文件 | 变更类型 |
|------|----------|
| `regulation.js` | 修改：新增 getChapter, getArticle API |
| `regulation/chapter/index.vue` | 新增：章节管理页面 |
| `regulation/article/index.vue` | 新增：条款管理页面 |
| `V1.1.4__regulation_chapter_article_menu.sql` | 新增：菜单 SQL |

### 6.3 Android

| 文件 | 状态 |
|------|------|
| 已有完整实现 | ✅ 无需修改 |

## 7. 部署说明

### 7.1 执行 SQL 脚本

```sql
-- 1. 创建数据库表（如尚未创建）
source sql/V1.1.0__sys_regulation_tables.sql

-- 2. 创建菜单
source sql/V1.1.1__regulation_menu.sql
source sql/V1.1.4__regulation_chapter_article_menu.sql
```

### 7.2 重启服务

- 重启后端 Spring Boot 服务
- 刷新前端页面

## 8. 测试验证

### 8.1 Vue 管理后台测试

1. 登录系统
2. 进入"系统管理 > 法律法规"
3. 测试章节管理和条款管理菜单

### 8.2 Android 测试

1. 启动 App
2. 进入法律模块
3. 测试法规列表、详情、条款浏览

## 9. 待优化项

- [ ] 章节和条款管理支持后端分页（当前前端分页）
- [ ] 法规详情页增加"管理章节"按钮跳转
- [ ] Android 端增加章节列表展示
- [ ] 增加收藏功能（已预留 sys_collection 表）
