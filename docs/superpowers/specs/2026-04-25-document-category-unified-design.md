# 文书分类统一管理设计方案

## 1. 概述

### 目标
统一前后端的文书分类系统，让移动端(Android)和后台管理系统使用同一套分类数据。

### 范围
- 前端：文书模板管理页面 - 模板类型下拉框改为动态加载文书分类
- 后端：确保 `category_id` 字段正确关联
- Android 端：文书分类/模板同步逻辑
- Android 端：首页文书分类和模板展示

### 概念澄清
- **template_type**：业务类型（如 CHECK_RECORD、PUNISHMENT），表示文书的具体业务分类
- **category**：文书分类（如"快速制作文书"、"其他模板"），用于移动端首页展示布局
- **category_id**：分类表外键，用于关联查询

本次设计主要解决 `category`（文书分类）的统一管理问题。

## 2. 数据层设计

### 分类表 (sys_document_category)
| 字段 | 类型 | 说明 |
|------|------|------|
| category_id | BIGINT | 主键 |
| category_name | VARCHAR | 分类名称 |
| display_type | VARCHAR | 展示方式(grid/table/list) |
| sort | INT | 排序 |
| status | CHAR | 状态(0正常/1停用) |

### 模板表 (sys_document_template)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| template_code | VARCHAR | 模板编码 |
| template_name | VARCHAR | 模板名称 |
| category_id | BIGINT | 外键关联 category_id |
| category | VARCHAR | 冗余存储分类名称 |
| template_type | VARCHAR | 模板类型(业务类型) |
| ... | ... | 其他字段 |

### 数据修正
更新现有模板的 `categoryId`，按 `category` 字符串匹配到对应的 `categoryId`：

```sql
-- 快速制作文书 categoryId = 2
UPDATE sys_document_template SET category_id = 2 WHERE category = '快速制作文书';
-- 其他模板 categoryId = 1
UPDATE sys_document_template SET category_id = 1 WHERE category = '其他模板';
```

**注意**：`template_type` 字段保留不变，它是业务类型（如 CHECK_RECORD、PUNISHMENT），与文书分类是两个不同概念。

## 3. 后端设计

### 现有接口
- `GET /api/admin/document/category/list` - 分类列表（需认证）
- `GET /api/admin/document/category/sync` - 分类同步（匿名）

### 模板同步接口
- `GET /api/admin/document/sync/template` - 返回模板列表含 `categoryId`（驼峰命名）

## 4. 前端设计（模板管理页面）

### 页面路径
`ruoyi-ui/src/views/system/document/template/index.vue`

### 改动点

1. **删除硬编码选项**
   - 删除 `templateTypeOptions` 硬编码数组

2. **新增分类选项**
   ```javascript
   categoryOptions: []  // 从 API 加载，格式：{ categoryId, categoryName, ... }
   ```

3. **表单字段**
   - "模板类型"标签改为"所属分类"
   - 下拉框绑定 `form.categoryId`
   - 下拉框 `@change` 事件自动填充 `form.category` 为分类名称
   ```javascript
   handleCategoryChange(categoryId) {
     const category = this.categoryOptions.find(c => c.categoryId === categoryId)
     if (category) {
       this.form.category = category.categoryName
     }
   }
   ```

4. **列表新增列**
   - 新增"所属分类"列，显示 `category` 字段

5. **分类 API 调用**
   ```javascript
   import { listCategory } from "@/api/system/documentCategory"

   // 在 created 或 mounted 中加载
   listCategory().then(response => {
     this.categoryOptions = response.rows || (response.data && response.data.rows) || []
   })
   ```

## 5. Android 设计

### 5.1 同步逻辑

**分类同步** (`DocumentRepository.syncCategories()`)：
- 调用 `GET /api/admin/document/category/sync`
- 存储到 `document_category` 表

**模板同步** (`DocumentRepository.syncTemplates()`)：
- 调用 `GET /api/admin/document/sync/template`
- 模板实体 `categoryId: Long` 字段存储 API 返回的 `categoryId`
- 模板实体 `category: String?` 字段存储 API 返回的 `category`（分类名称）

### 5.2 首页展示

**查询逻辑** (`HomeFragment.loadDocumentCategories()`)：
1. 获取所有分类
2. 按 `categoryId` 匹配查询模板：
   ```kotlin
   documentRepository.getTemplatesByCategory(category.categoryId)
   ```
3. 兜底：如果 `categoryId` 为 0 或查不到，按 `category` 字符串查询：
   ```kotlin
   documentRepository.getTemplatesByCategoryName(category.categoryName)
   ```

### 5.3 DAO 查询

**getTemplatesByCategory**：
```sql
SELECT * FROM document_template
WHERE isActive = '1' AND categoryId = :categoryId
ORDER BY sort ASC, id ASC
```

**getTemplatesByCategoryName（兜底）**：
```sql
SELECT * FROM document_template
WHERE isActive = '1' AND category = :categoryName
ORDER BY sort ASC, id ASC
```

## 6. 实施步骤

### 阶段一：数据修正
1. 更新数据库中模板的 `category_id` 字段

### 阶段二：前端改造
1. 修改 Vue 模板管理页面
2. 添加分类下拉动态加载
3. 测试新增/编辑功能

### 阶段三：Android 适配
1. 确认 `DocumentTemplate` 模型包含 `categoryId`
2. 确认 API 解析 `categoryId`
3. 确认 `toEntity()` 使用 `categoryId`
4. 确认首页查询逻辑

### 阶段四：联调测试
1. 后端数据验证
2. 前端模板管理测试
3. Android 同步测试
4. Android 首页展示测试

## 7. 注意事项

- `category` 字段保留作为冗余存储，便于列表直接显示
- `categoryId` 为 0 的模板视为未分类，按 `category` 字符串兜底查询
- 移动端同步时优先按 `categoryId` 匹配，匹配不到再按 `category` 名称匹配
