# 文书分类功能设计方案

## 1. 概述

在首页（便捷执法）添加文书分类展示区域，支持按分类显示文书模板。后端对模板进行分类，App 通过同步获取数据，本地展示。

## 2. 数据库设计

### 2.1 后端数据库

**新建分类表 `sys_document_category`**

```sql
CREATE TABLE sys_document_category (
    category_id    BIGINT(20)      NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    category_name  VARCHAR(100)    NOT NULL                  COMMENT '分类名称',
    display_type   VARCHAR(20)     NOT NULL DEFAULT 'grid'   COMMENT '展示方式：grid=九宫格，table=表格，list=列表',
    sort           INT(11)         NOT NULL DEFAULT 0        COMMENT '排序',
    status         CHAR(1)        NOT NULL DEFAULT '0'      COMMENT '状态（0正常 1停用）',
    create_time    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书分类表';
```

**说明**：
- 系统内置"其他模板"分类（`category_id = 0`），不可删除
- 删除分类时，该分类下的模板自动归入"其他模板"分类

**修改模板表 `sys_document_template`**

```sql
ALTER TABLE sys_document_template
ADD COLUMN category_id BIGINT(20) DEFAULT NULL COMMENT '分类ID' AFTER document_name,
ADD COLUMN sort INT(11) NOT NULL DEFAULT 0 COMMENT '排序';
```

**说明**：
- `sort` 字段用于模板在分类内的自定义排序
- 删除分类时，模板自动归入"其他模板"分类（系统内置默认分类）

### 2.2 本地数据库（Room）

**新建 DocumentCategoryEntity**

| 字段 | 类型 | 说明 |
|------|------|------|
| categoryId | INTEGER (PK) | 分类ID |
| categoryName | TEXT | 分类名称 |
| displayType | TEXT | 展示方式：grid/table/list |
| sort | INTEGER | 排序 |

**修改 DocumentTemplateEntity**

| 新增字段 | 类型 | 说明 |
|----------|------|------|
| categoryId | INTEGER | 关联分类ID（nullable，为 0 时表示"其他模板"） |
| sort | INTEGER | 排序字段 |

## 3. 后端 API 设计

### 3.1 分类管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /document/category/list | 获取所有分类列表 |
| GET | /document/category/{categoryId} | 获取分类详情 |
| POST | /document/category | 新增分类 |
| PUT | /document/category | 修改分类 |
| DELETE | /document/category/{categoryId} | 删除分类 |

**GET /document/category/list 响应**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "categoryId": 1,
      "categoryName": "快速制作文书",
      "displayType": "grid",
      "sort": 1,
      "status": "0",
      "createTime": "2026-04-25 10:00:00"
    },
    {
      "categoryId": 2,
      "categoryName": "其他文书模板",
      "displayType": "table",
      "sort": 2,
      "status": "0",
      "createTime": "2026-04-25 10:00:00"
    }
  ]
}
```

### 3.2 模板接口变更

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /document/template/list | 模板列表新增 categoryId 字段 |
| GET | /document/template/{templateId} | 模板详情新增 categoryId 字段 |
| PUT | /document/template/{templateId} | 修改时可更新 categoryId |

### 3.3 App 同步接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /app/document/category/sync | 同步分类数据到本地 |

## 4. Android 功能设计

### 4.1 首页布局（fragment_home.xml）

```
ScrollView
└── LinearLayout (vertical)
    ├── 便捷执法（现有5个入口单行）
    ├── 分类1：快速制作文书
    │   └── RecyclerView (GridLayoutManager, 3列)
    │       └── DocumentTemplateAdapter (九宫格样式)
    ├── 分类2：其他文书模板
    │   └── RecyclerView (GridLayoutManager, 2列)
    │       └── DocumentTemplateAdapter (表格样式)
    └── ...
```

### 4.2 展示方式

| displayType | 布局 | 每行数量 |
|-------------|------|---------|
| grid | GridLayoutManager | 3列 |
| table | GridLayoutManager | 2列 |
| list | LinearLayoutManager | 1列 |

### 4.3 点击行为

点击模板 → 跳转到 `DocumentFillActivity`
- 传入参数：`unitId`、`templateId`、`templateName`

### 4.4 数据流程

1. 启动时检查本地是否有分类数据
2. 无数据 → 显示空状态提示"请先同步数据"
3. 有数据 → 从 Room 读取分类列表，按 sort 排序展示
4. 每个分类下读取关联的模板列表（按 categoryId + sort 排序）
5. 过滤掉没有模板的分类（不显示）
6. `categoryId = 0` 或 `null` 的模板归入"其他模板"分类（系统内置）

## 5. 数据同步设计

### 5.1 同步时机

- 用户登录后首次同步
- 首页下拉刷新触发
- 设置页手动同步

### 5.2 同步顺序

1. 同步分类数据 → 更新 local_category 表
2. 同步模板数据 → 更新 local_template 表（包含 categoryId、sort）
3. 若模板的 categoryId 对应分类不存在，则归入"其他模板"分类

## 6. 文件变更清单

### 后端

| 文件 | 变更 |
|------|------|
| `sql/Vx.x.x__document_category.sql` | 新建分类表 |
| `sql/Vx.x.x__add_template_category.sql` | 模板表添加 categoryId + sort 字段 |
| `SysDocumentCategory.java` | 实体类 |
| `SysDocumentCategoryMapper.java` | Mapper |
| `ISysDocumentCategoryService.java` | 服务接口 |
| `SysDocumentCategoryServiceImpl.java` | 服务实现（含删除时模板归属处理） |
| `SysDocumentCategoryController.java` | Controller |
| `SysDocumentTemplate.java` | 实体类添加 categoryId + sort |
| `SysDocumentTemplateController.java` | 添加 categoryId + sort 返回 |
| `/app/document/category/sync` | App 分类同步接口 |

### Android

| 文件 | 变更 |
|------|------|
| `DocumentCategoryEntity.kt` | 新建分类实体 |
| `DocumentCategoryDao.kt` | 新建分类 DAO |
| `AppDatabase.kt` | 添加分类表 |
| `DocumentRepository.kt` | 添加分类数据读取（含 sort 排序） |
| `DocumentApi.kt` | 添加分类同步 API |
| `fragment_home.xml` | 添加分类展示区域（ScrollView 包裹） |
| `HomeFragment.kt` | 添加分类数据加载（过滤空分类） |
| `DocumentTemplateAdapter.kt` | 支持 grid/table/list 三种样式 + sort 排序 |
| `DocumentFillActivity.kt` | 接收 categoryId 参数 |

## 7. 测试要点

1. 分类 CRUD 功能正常
2. 模板关联分类正常
3. App 同步分类数据成功
4. 首页按分类展示正确
5. 不同 displayType 展示方式正确
6. 点击模板跳转文书编辑页正常
