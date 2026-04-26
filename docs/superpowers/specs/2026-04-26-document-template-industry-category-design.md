# 文书模板关联行业分类设计方案

## 1. 需求概述

- **业务场景**：在首页/选择单位后，只显示该单位所属行业适用的文书模板
- **关联方式**：多对多关系（一个模板可属于多个行业分类）
- **显示规则**：空行业分类的模板不显示，只有设置了行业分类且与当前单位行业匹配的模板才显示

## 2. 数据库设计

### 2.1 修改 `sys_document_template` 表

```sql
ALTER TABLE sys_document_template ADD COLUMN industry_category_id BIGINT DEFAULT NULL COMMENT '行业分类ID';
ALTER TABLE sys_document_template ADD COLUMN industry_category_name VARCHAR(100) DEFAULT NULL COMMENT '行业分类名称';
```

### 2.2 新建中间表 `sys_document_template_industry`

```sql
CREATE TABLE sys_document_template_industry (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    template_id BIGINT NOT NULL COMMENT '文书模板ID',
    industry_category_id BIGINT NOT NULL COMMENT '行业分类ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_industry (template_id, industry_category_id)
) COMMENT='文书模板与行业分类关联表';
```

## 3. 后端设计

### 3.1 实体类修改

**SysDocumentTemplate.java** 新增字段：
```java
private Long industryCategoryId;      // 行业分类ID
private String industryCategoryName;  // 行业分类名称
```

### 3.2 新建关联实体

**SysDocumentTemplateIndustry.java**：
```java
private Long id;
private Long templateId;
private Long industryCategoryId;
```

### 3.3 Mapper

**SysDocumentTemplateIndustryMapper.java**：
- `insertBatch(List<SysDocumentTemplateIndustry>)`
- `deleteByTemplateId(Long templateId)`
- `selectByIndustryCategoryId(Long industryCategoryId)`
- `selectByTemplateId(Long templateId)`

### 3.4 Service 修改

**SysDocumentTemplateServiceImpl.java**：
- `template.setIndustryCategoryName()` - 保存时根据ID查名称
- 保存模板时：先删中间表，再批量插入关联记录

### 3.5 Controller / API 修改

**SysDocumentTemplateController.java**：
- `add()` / `edit()` 新增参数：`industryCategoryIds[]`
- `queryVO` 返回数据包含行业分类信息

**模板同步API** (`/api/admin/document/sync/template`)：
- 返回数据结构增加 `industryCategoryId` 和 `industryCategoryName`

## 4. 前端 Vue 设计

### 4.1 模板管理页面修改

**template/index.vue**：
- **列表页**：新增"行业分类"列
- **表单页**：
  - 新增"行业分类"多选下拉框（el-select multiple）
  - 行业分类选项从 `/api/admin/industry/category/list` 获取

## 5. Android 设计

### 5.1 数据库修改

**DocumentTemplateEntity.kt** 新增字段：
```kotlin
val industryCategoryId: Long? = null     // 行业分类ID
val industryCategoryName: String? = null // 行业分类名称
```

**新建关联表 DocumentTemplateIndustryEntity.kt**：
```kotlin
@Entity(
    tableName = "document_template_industry",
    primaryKeys = ["templateId", "industryCategoryId"],
    indices = [Index("industryCategoryId")]
)
data class DocumentTemplateIndustryEntity(
    val templateId: Long,
    val industryCategoryId: Long
)
```

**AppDatabase.kt**：新增 `DocumentTemplateIndustryDao`

### 5.2 DAO

**DocumentTemplateIndustryDao.java**：
- `insertAll(entities: List<DocumentTemplateIndustryEntity>)`
- `deleteAll()`
- `getTemplateIdsByIndustryCategory(industryCategoryId: Long): List<Long>`

### 5.3 API 修改

**DocumentApi.kt**：
- 模板解析方法增加 `industryCategoryId` 和 `industryCategoryName` 字段解析

### 5.4 Repository 修改

**DocumentRepository.kt**：
- `syncTemplateIndustryRelations()` - 同步模板-行业关联
- `getTemplateIdsByIndustryCategory(industryCategoryId)` - 根据行业获取模板ID列表
- `getTemplatesByIndustryCategory(templateIds)` - 获取指定模板列表

### 5.5 同步逻辑修改

**SyncManager.kt**：
- 新增 `MODULE_DOCUMENT_TEMPLATE_INDUSTRY`
- `syncDocumentTemplate()` 中调用 `repository.syncTemplateIndustryRelations()`

### 5.6 首页过滤逻辑修改

**HomeFragment.kt**：
1. 选择单位后获取 `industryCategoryId`
2. 查询 `document_template_industry` 获取关联的模板ID列表
3. 如无关联模板，隐藏文书模板区域
4. 如有关联模板，按文书分类分组显示

## 6. 数据流

```
[后端数据库]
    ↓ 模板同步API
[Android DocumentApi]
    ↓ 解析并存储
[Local DB: document_template + document_template_industry]
    ↓
[HomeFragment: 选择单位后获取行业ID]
    ↓
[查询 document_template_industry 获取关联模板ID]
    ↓
[过滤显示模板列表]
```

## 7. 实现优先级

| 阶段 | 内容 |
|------|------|
| 1 | 数据库：新增字段和中间表 |
| 2 | 后端：实体、Mapper、Service、Controller |
| 3 | Android：Entity、Dao、Api、同步 |
| 4 | 前端Vue：模板管理页面行业多选 |
| 5 | Android：首页过滤逻辑 |
