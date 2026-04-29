# Android 端数据同步重新设计

## 1. 需求概述

将 RuoYi-Vue 后端的法律法规、规范用语、监管事项相关表同步到 Android 端本地数据库，支持离线查看。

**同步范围：**
- `law` + `legal_term`（法律法规主表+条款）
- `normative_category` + `normative_language`（规范用语分类+用语）
- `normative_matter_bind` + `normative_term_bind`（规范用语监管事项关联+法律条款关联）
- `regulatory_category_bind`（行业分类监管事项关联）
- `regulatory_matter` + `regulatory_matter_item`（监管事项主表+明细）

**设计原则：**
- 按功能分组接口（`/app/law/*`、`/app/normative/*`、`/app/regulatory/*`）
- 离线存储到 Room 本地数据库
- 完全替换现有的 LAW、PHRASE、SUPERVISION 同步逻辑

---

## 2. 后端接口设计

### 2.1 法律相关接口 `/app/law/*`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/app/law/list` | GET | 法律法规列表 |
| `/app/law/{lawId}` | GET | 法律法规详情 |
| `/app/law/{lawId}/term/list` | GET | 法律条款列表 |
| `/app/law/term/{termId}` | GET | 条款详情 |

### 2.2 规范用语接口 `/app/normative/*`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/app/normative/category/list` | GET | 规范用语分类列表 |
| `/app/normative/language/list` | GET | 规范用语列表 |
| `/app/normative/matterbind/list` | GET | 规范用语监管事项关联列表 |
| `/app/normative/termbind/list` | GET | 规范用语法律条款关联列表 |

### 2.3 监管事项接口 `/app/regulatory/*`

| 接口 | 方法 | 说明 |
|------|------|------|
| `/app/regulatory/matter/list` | GET | 监管事项列表 |
| `/app/regulatory/matter/{matterId}` | GET | 监管事项详情 |
| `/app/regulatory/matter/{matterId}/item/list` | GET | 监管事项明细列表 |
| `/app/regulatory/categorybind/list` | GET | 行业分类监管事项关联列表 |

---

## 3. Android 端架构设计

### 3.1 分层结构

```
API Layer
├── LawApi           (app/law/*)
├── NormativeApi     (app/normative/*)
└── RegulatoryApi    (app/regulatory/*)

Repository Layer
├── LawRepository
├── NormativeRepository
└── RegulatoryRepository

Database Layer (Room)
├── law / legal_term 表
├── normative_category / normative_language / normative_matter_bind / normative_term_bind 表
└── regulatory_matter / regulatory_matter_item / regulatory_category_bind 表

SyncManager
└── 统一调度三个 Repository
```

### 3.2 数据库变更

**新增 Entity（9个）：**

| Entity | 对应表 | 说明 |
|---------|--------|------|
| LawEntity | law | 法律法规 |
| LegalTermEntity | legal_term | 法律条款 |
| NormativeCategoryEntity | normative_category | 规范用语分类 |
| NormativeLanguageEntity | normative_language | 规范用语 |
| NormativeMatterBindEntity | normative_matter_bind | 规范用语-监管事项关联 |
| NormativeTermBindEntity | normative_term_bind | 规范用语-法律条款关联 |
| RegulatoryMatterEntity | regulatory_matter | 监管事项 |
| RegulatoryMatterItemEntity | regulatory_matter_item | 监管事项明细 |
| RegulatoryCategoryBindEntity | regulatory_category_bind | 行业分类-监管事项关联 |

**删除 Entity（待清理）：**

- `RegulationEntity`（旧法律表）
- `RegulationChapterEntity`、`RegulationArticleEntity`
- `LegalBasisEntity`、`LegalTypeEntity`、`SupervisionTypeEntity`
- `ProcessingBasisEntity`、`LegalBasisContentEntity`、`ProcessingBasisContentEntity`、`BasisChapterLinkEntity`
- `PhraseBookEntity`、`PhraseBookFtsEntity`、`PhraseItemEntity`、`PhraseItemFtsEntity`、`PhraseDetailEntity`、`PhraseDetailFtsEntity`
- `SupervisionCategoryEntity`（被 regulatory_matter 替换）
- `SupervisionItemEntity`（被 regulatory_matter_item 替换）

### 3.3 SyncManager 模块变更

**删除模块：**
- `MODULE_LAW`
- `MODULE_PHRASE`
- `MODULE_SUPERVISION`

**新增模块：**
- `MODULE_NORMATIVE`（规范用语 + 关联）
- `MODULE_REGULATORY`（监管事项 + 关联）

---

## 4. 文件变更清单

### 4.1 新增文件

**Entity（9个）：**
- `data/database/entity/LawEntity.kt`
- `data/database/entity/LegalTermEntity.kt`
- `data/database/entity/NormativeCategoryEntity.kt`
- `data/database/entity/NormativeLanguageEntity.kt`
- `data/database/entity/NormativeMatterBindEntity.kt`
- `data/database/entity/NormativeTermBindEntity.kt`
- `data/database/entity/RegulatoryMatterEntity.kt`
- `data/database/entity/RegulatoryMatterItemEntity.kt`
- `data/database/entity/RegulatoryCategoryBindEntity.kt`

**DAO（9个）：**
- `data/database/dao/LawDao.kt`
- `data/database/dao/LegalTermDao.kt`
- `data/database/dao/NormativeCategoryDao.kt`
- `data/database/dao/NormativeLanguageDao.kt`
- `data/database/dao/NormativeMatterBindDao.kt`
- `data/database/dao/NormativeTermBindDao.kt`
- `data/database/dao/RegulatoryMatterDao.kt`
- `data/database/dao/RegulatoryMatterItemDao.kt`
- `data/database/dao/RegulatoryCategoryBindDao.kt`

**API（3个）：**
- `feature/law/api/LawApi.kt`
- `feature/normative/api/NormativeApi.kt`
- `feature/regulatory/api/RegulatoryApi.kt`

**Repository（3个）：**
- `feature/law/repository/LawRepository.kt`
- `feature/normative/repository/NormativeRepository.kt`
- `feature/regulatory/repository/RegulatoryRepository.kt`

### 4.2 修改文件

- `AppDatabase.kt` - 移除旧 entity/dao，新增新 entity/dao，版本号+1
- `SyncManager.kt` - 删除 LawSyncManager 引用，更新模块列表
- `SyncWorker.kt` - 更新同步模块名称

### 4.3 删除文件

- `sync/LawSyncManager.kt`
- `feature/law/repository/LawRepository.kt`（旧版本）
- `api/repository/PhraseRepository.kt`（如存在）
- `data/database/entity/RegulationEntity.kt` 等旧 entity（12个）
- `data/database/dao/RegulationDao.kt` 等旧 dao

---

## 5. 数据流

```
后端接口
    ↓
Api (DTO)
    ↓
Repository.convertToEntity()
    ↓
Room Entity
    ↓
DAO.insertAll()
    ↓
本地数据库
```

---

## 6. 实现顺序

1. 后端：实现 `/app/law/*`、`/app/normative/*`、`/app/regulatory/*` 接口
2. Android Entity + DAO：创建 9 个新表结构
3. Android API：实现 3 个 API 类
4. Android Repository：实现 3 个 Repository 类
5. Android AppDatabase：更新 entity/dao 列表
6. Android SyncManager：更新同步模块
7. 清理：删除旧文件
