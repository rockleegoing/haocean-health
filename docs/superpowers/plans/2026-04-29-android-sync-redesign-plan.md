# Android 数据同步重新设计实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将法律法规、规范用语、监管事项相关表同步到 Android 端本地数据库，支持离线查看

**Architecture:** 按功能分组接口（/app/law/*, /app/normative/*, /app/regulatory/*），通过 Repository 层调用 API 并转换为 Room Entity 存储

**Tech Stack:** Spring Boot (后端), Room (Android 本地数据库), Retrofit/OkHttp (Android 网络)

---

## 阶段一：后端接口实现

### Task 1: 后端 - 法律相关接口 `/app/law/*`

**Files:**
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppLawController.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/ILawService.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/LawServiceImpl.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/mapper/LawMapper.java`
- Create: `ruoyi-system/src/main/resources/mapper/system/LawMapper.xml`

- [ ] **Step 1: 创建 AppLawController**

```java
@RestController
@RequestMapping("/app/law")
public class AppLawController {

    @Autowired
    private ILawService lawService;

    @Anonymous
    @GetMapping("/list")
    public AjaxResult listLaw() {
        return AjaxResult.success(lawService.selectLawList(new Law()));
    }

    @Anonymous
    @GetMapping("/{lawId}")
    public AjaxResult getLaw(@PathVariable Long lawId) {
        return AjaxResult.success(lawService.selectLawById(lawId));
    }

    @Anonymous
    @GetMapping("/{lawId}/term/list")
    public AjaxResult listTermByLaw(@PathVariable Long lawId) {
        LegalTerm term = new LegalTerm();
        term.setLawId(lawId);
        return AjaxResult.success(lawService.selectLegalTermList(term));
    }

    @Anonymous
    @GetMapping("/term/{termId}")
    public AjaxResult getTerm(@PathVariable Long termId) {
        return AjaxResult.success(lawService.selectLegalTermById(termId));
    }
}
```

- [ ] **Step 2: 创建 Law Domain 和 Service 层**

实现 `selectLawList`, `selectLawById`, `selectLegalTermList`, `selectLegalTermById` 方法

- [ ] **Step 3: 创建 LawMapper 和 XML**

实现 `selectLawList`, `selectLawById`, `selectLegalTermList`, `selectLegalTermById` SQL

- [ ] **Step 4: 测试接口**

启动后端，访问 `GET /app/law/list` 验证返回数据

- [ ] **Step 5: Commit**

```bash
git add -A && git commit -m "feat(app): 添加 /app/law/* 法律接口"
```

---

### Task 2: 后端 - 规范用语接口 `/app/normative/*`

**Files:**
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppNormativeController.java`

- [ ] **Step 1: 创建 AppNormativeController**

```java
@RestController
@RequestMapping("/app/normative")
public class AppNormativeController {

    @Autowired
    private INormativeCategoryService categoryService;

    @Autowired
    private INormativeLanguageService languageService;

    @Autowired
    private INormativeMatterBindService matterBindService;

    @Autowired
    private INormativeTermBindService termBindService;

    @Anonymous
    @GetMapping("/category/list")
    public AjaxResult listCategory() {
        return AjaxResult.success(categoryService.selectNormativeCategoryList(new NormativeCategory()));
    }

    @Anonymous
    @GetMapping("/language/list")
    public AjaxResult listLanguage(@RequestParam(required = false) Long categoryId) {
        NormativeLanguage language = new NormativeLanguage();
        if (categoryId != null) {
            language.setCategoryId(categoryId);
        }
        return AjaxResult.success(languageService.selectNormativeLanguageList(language));
    }

    @Anonymous
    @GetMapping("/matterbind/list")
    public AjaxResult listMatterBind() {
        return AjaxResult.success(matterBindService.selectNormativeMatterBindList(new NormativeMatterBind()));
    }

    @Anonymous
    @GetMapping("/termbind/list")
    public AjaxResult listTermBind(@RequestParam(required = false) Long legalTermId) {
        NormativeTermBind bind = new NormativeTermBind();
        if (legalTermId != null) {
            bind.setLegalTermId(legalTermId);
        }
        return AjaxResult.success(termBindService.selectNormativeTermBindList(bind));
    }
}
```

- [ ] **Step 2: 测试接口**

访问 `GET /app/normative/category/list`, `/app/normative/language/list` 等验证

- [ ] **Step 3: Commit**

```bash
git add -A && git commit -m "feat(app): 添加 /app/normative/* 规范用语接口"
```

---

### Task 3: 后端 - 监管事项接口 `/app/regulatory/*`

**Files:**
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppRegulatoryController.java`

- [ ] **Step 1: 创建 AppRegulatoryController**

```java
@RestController
@RequestMapping("/app/regulatory")
public class AppRegulatoryController {

    @Autowired
    private IRegulatoryMatterService matterService;

    @Autowired
    private IRegulatoryMatterItemService itemService;

    @Autowired
    private IRegulatoryCategoryBindService categoryBindService;

    @Anonymous
    @GetMapping("/matter/list")
    public AjaxResult listMatter() {
        return AjaxResult.success(matterService.selectRegulatoryMatterList(new RegulatoryMatter()));
    }

    @Anonymous
    @GetMapping("/matter/{matterId}")
    public AjaxResult getMatter(@PathVariable Long matterId) {
        return AjaxResult.success(matterService.selectRegulatoryMatterById(matterId));
    }

    @Anonymous
    @GetMapping("/matter/{matterId}/item/list")
    public AjaxResult listItemByMatter(@PathVariable Long matterId) {
        RegulatoryMatterItem item = new RegulatoryMatterItem();
        item.setMatterId(matterId);
        return AjaxResult.success(itemService.selectRegulatoryMatterItemList(item));
    }

    @Anonymous
    @GetMapping("/categorybind/list")
    public AjaxResult listCategoryBind() {
        return AjaxResult.success(categoryBindService.selectRegulatoryCategoryBindList(new RegulatoryCategoryBind()));
    }
}
```

- [ ] **Step 2: 测试接口**

访问 `GET /app/regulatory/matter/list` 等验证

- [ ] **Step 3: Commit**

```bash
git add -A && git commit -m "feat(app): 添加 /app/regulatory/* 监管事项接口"
```

---

## 阶段二：Android Entity 和 DAO

### Task 4: Android - 创建法律相关 Entity 和 DAO

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/LawEntity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/LegalTermEntity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/LawDao.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/LegalTermDao.kt`

- [ ] **Step 1: 创建 LawEntity**

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "law",
    indices = [Index(value = ["name"])]
)
data class LawEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val releaseTime: Long?
)
```

- [ ] **Step 2: 创建 LegalTermEntity**

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "legal_term",
    indices = [Index(value = ["lawId"]), Index(value = ["zhCode"])]
)
data class LegalTermEntity(
    @PrimaryKey val id: Long,
    val lawId: Long,
    val part: Int?,
    val partBranch: Int?,
    val chapter: Int?,
    val quarter: Int?,
    val article: Int?,
    val section: Int?,
    val subparagraph: Int?,
    val item: Int?,
    val zhCode: String?,
    val content: String?
)
```

- [ ] **Step 3: 创建 LawDao**

```kotlin
package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.LawEntity

@Dao
interface LawDao {
    @Query("SELECT * FROM law ORDER BY name")
    suspend fun getAll(): List<LawEntity>

    @Query("SELECT * FROM law WHERE id = :lawId")
    suspend fun getById(lawId: Long): LawEntity?

    @Query("SELECT * FROM law WHERE name LIKE '%' || :keyword || '%'")
    suspend fun search(keyword: String): List<LawEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(laws: List<LawEntity>)

    @Query("DELETE FROM law")
    suspend fun deleteAll()
}
```

- [ ] **Step 4: 创建 LegalTermDao**

```kotlin
package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.LegalTermEntity

@Dao
interface LegalTermDao {
    @Query("SELECT * FROM legal_term WHERE lawId = :lawId ORDER BY zhCode")
    suspend fun getByLawId(lawId: Long): List<LegalTermEntity>

    @Query("SELECT * FROM legal_term WHERE id = :termId")
    suspend fun getById(termId: Long): LegalTermEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(terms: List<LegalTermEntity>)

    @Query("DELETE FROM legal_term")
    suspend fun deleteAll()
}
```

- [ ] **Step 5: Commit**

```bash
git add -A && git commit -m "feat(android): 添加 LawEntity LegalTermEntity LawDao LegalTermDao"
```

---

### Task 5: Android - 创建规范用语相关 Entity 和 DAO

**Files:**
- Create: `NormativeCategoryEntity.kt`
- Create: `NormativeLanguageEntity.kt`
- Create: `NormativeMatterBindEntity.kt`
- Create: `NormativeTermBindEntity.kt`
- Create: 对应 4 个 DAO

- [ ] **Step 1: 创建 NormativeCategoryEntity**

```kotlin
@Entity(tableName = "normative_category")
data class NormativeCategoryEntity(
    @PrimaryKey val code: Long,
    val name: String,
    val parentCode: Long?,
    val sortOrder: Int,
    val status: String
)
```

- [ ] **Step 2: 创建 NormativeLanguageEntity**

```kotlin
@Entity(
    tableName = "normative_language",
    indices = [Index(value = ["categoryId"])]
)
data class NormativeLanguageEntity(
    @PrimaryKey val id: Long,
    val categoryId: Long?,
    val standardCode: String?,
    val standardPhrase: String,
    val supervisoryOpinion: String?,
    val basisType: Int?
)
```

- [ ] **Step 3: 创建 NormativeMatterBindEntity**

```kotlin
@Entity(
    tableName = "normative_matter_bind",
    indices = [Index(value = ["normativeLanguageId"]), Index(value = ["regulatoryMatterId"])]
)
data class NormativeMatterBindEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val normativeLanguageId: Long,
    val regulatoryMatterId: Long,
    val basisType: Int?
)
```

- [ ] **Step 4: 创建 NormativeTermBindEntity**

```kotlin
@Entity(
    tableName = "normative_term_bind",
    indices = [Index(value = ["normativeLanguageId"]), Index(value = ["legalTermId"])]
)
data class NormativeTermBindEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val legalTermId: Long,
    val normativeLanguageId: Long,
    val basisType: Int?
)
```

- [ ] **Step 5: 创建 4 个 DAO**

每个 DAO 包含基本的 CRUD 方法

- [ ] **Step 6: Commit**

```bash
git add -A && git commit -m "feat(android): 添加规范用语相关 Entity 和 DAO"
```

---

### Task 6: Android - 创建监管事项相关 Entity 和 DAO

**Files:**
- Create: `RegulatoryMatterEntity.kt`
- Create: `RegulatoryMatterItemEntity.kt`
- Create: `RegulatoryCategoryBindEntity.kt`
- Create: 对应 3 个 DAO

- [ ] **Step 1: 创建 RegulatoryMatterEntity**

```kotlin
@Entity(tableName = "regulatory_matter")
data class RegulatoryMatterEntity(
    @PrimaryKey val matterId: Long,
    val matterName: String,
    val categoryId: Long?,
    val description: String?,
    val status: String
)
```

- [ ] **Step 2: 创建 RegulatoryMatterItemEntity**

```kotlin
@Entity(
    tableName = "regulatory_matter_item",
    indices = [Index(value = ["matterId"])]
)
data class RegulatoryMatterItemEntity(
    @PrimaryKey val itemId: Long,
    val matterId: Long,
    val itemNo: String?,
    val name: String,
    val description: String?,
    val legalBasis: String?
)
```

- [ ] **Step 3: 创建 RegulatoryCategoryBindEntity**

```kotlin
@Entity(
    tableName = "regulatory_category_bind",
    indices = [Index(value = ["matterId"]), Index(value = ["industryCategoryId"])]
)
data class RegulatoryCategoryBindEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val industryCategoryId: Long,
    val matterId: Long
)
```

- [ ] **Step 4: 创建 3 个 DAO**

每个 DAO 包含基本的 CRUD 方法

- [ ] **Step 5: Commit**

```bash
git add -A && git commit -m "feat(android): 添加监管事项相关 Entity 和 DAO"
```

---

## 阶段三：Android API 实现

### Task 7: Android - 创建 LawApi

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawApi.kt`

- [ ] **Step 1: 创建 LawApi**

```kotlin
package com.ruoyi.app.feature.law.api

import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.model.entity.SyncDataEntity

object LawApi {

    suspend fun getLawList(): ListResponse<LawDto> {
        return Get(ConfigApi.baseUrl + "/app/law/list").await()
    }

    suspend fun getLawById(lawId: Long): LawDto {
        return Get(ConfigApi.baseUrl + "/app/law/$lawId").await()
    }

    suspend fun getTermListByLaw(lawId: Long): ListResponse<LegalTermDto> {
        return Get(ConfigApi.baseUrl + "/app/law/$lawId/term/list").await()
    }

    suspend fun getTermById(termId: Long): LegalTermDto {
        return Get(ConfigApi.baseUrl + "/app/law/term/$termId").await()
    }
}

@kotlinx.serialization.Serializable
data class ListResponse<T>(
    val code: Int,
    val msg: String,
    val rows: List<T> = emptyList()
)

@kotlinx.serialization.Serializable
data class LawDto(
    val id: Long,
    val name: String,
    val releaseTime: String?
)

@kotlinx.serialization.Serializable
data class LegalTermDto(
    val id: Long,
    val lawId: Long,
    val zhCode: String?,
    val content: String?,
    val article: Int?
)
```

- [ ] **Step 2: Commit**

```bash
git add -A && git commit -m "feat(android): 添加 LawApi"
```

---

### Task 8: Android - 创建 NormativeApi

**Files:**
- Create: `NormativeApi.kt`

- [ ] **Step 1: 创建 NormativeApi**

```kotlin
package com.ruoyi.app.feature.normative.api

import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi

object NormativeApi {

    suspend fun getCategoryList(): ListResponse<NormativeCategoryDto> {
        return Get(ConfigApi.baseUrl + "/app/normative/category/list").await()
    }

    suspend fun getLanguageList(categoryId: Long? = null): ListResponse<NormativeLanguageDto> {
        val url = if (categoryId != null) {
            "${ConfigApi.baseUrl}/app/normative/language/list?categoryId=$categoryId"
        } else {
            "${ConfigApi.baseUrl}/app/normative/language/list"
        }
        return Get(url).await()
    }

    suspend fun getMatterBindList(): ListResponse<NormativeMatterBindDto> {
        return Get(ConfigApi.baseUrl + "/app/normative/matterbind/list").await()
    }

    suspend fun getTermBindList(legalTermId: Long? = null): ListResponse<NormativeTermBindDto> {
        val url = if (legalTermId != null) {
            "${ConfigApi.baseUrl}/app/normative/termbind/list?legalTermId=$legalTermId"
        } else {
            "${ConfigApi.baseUrl}/app/normative/termbind/list"
        }
        return Get(url).await()
    }
}

@kotlinx.serialization.Serializable
data class NormativeCategoryDto(
    val code: Long,
    val name: String,
    val parentCode: Long?,
    val sortOrder: Int?,
    val status: String?
)

@kotlinx.serialization.Serializable
data class NormativeLanguageDto(
    val id: Long,
    val categoryId: Long?,
    val standardCode: String?,
    val standardPhrase: String,
    val supervisoryOpinion: String?,
    val basisType: Int?
)

@kotlinx.serialization.Serializable
data class NormativeMatterBindDto(
    val id: Long,
    val normativeLanguageId: Long,
    val regulatoryMatterId: Long,
    val basisType: Int?
)

@kotlinx.serialization.Serializable
data class NormativeTermBindDto(
    val id: Long,
    val legalTermId: Long,
    val normativeLanguageId: Long,
    val basisType: Int?
)
```

- [ ] **Step 2: Commit**

```bash
git add -A && git commit -m "feat(android): 添加 NormativeApi"
```

---

### Task 9: Android - 创建 RegulatoryApi

**Files:**
- Create: `RegulatoryApi.kt`

- [ ] **Step 1: 创建 RegulatoryApi**

```kotlin
package com.ruoyi.app.feature.regulatory.api

import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi

object RegulatoryApi {

    suspend fun getMatterList(): ListResponse<RegulatoryMatterDto> {
        return Get(ConfigApi.baseUrl + "/app/regulatory/matter/list").await()
    }

    suspend fun getMatterById(matterId: Long): SingleResponse<RegulatoryMatterDto> {
        return Get(ConfigApi.baseUrl + "/app/regulatory/matter/$matterId").await()
    }

    suspend fun getItemListByMatter(matterId: Long): ListResponse<RegulatoryMatterItemDto> {
        return Get(ConfigApi.baseUrl + "/app/regulatory/matter/$matterId/item/list").await()
    }

    suspend fun getCategoryBindList(): ListResponse<RegulatoryCategoryBindDto> {
        return Get(ConfigApi.baseUrl + "/app/regulatory/categorybind/list").await()
    }
}

@kotlinx.serialization.Serializable
data class SingleResponse<T>(
    val code: Int,
    val msg: String,
    val data: T?
)

@kotlinx.serialization.Serializable
data class RegulatoryMatterDto(
    val matterId: Long,
    val matterName: String,
    val categoryId: Long?,
    val description: String?,
    val status: String?
)

@kotlinx.serialization.Serializable
data class RegulatoryMatterItemDto(
    val itemId: Long,
    val matterId: Long,
    val itemNo: String?,
    val name: String,
    val description: String?,
    val legalBasis: String?
)

@kotlinx.serialization.Serializable
data class RegulatoryCategoryBindDto(
    val id: Long,
    val industryCategoryId: Long,
    val matterId: Long
)
```

- [ ] **Step 2: Commit**

```bash
git add -A && git commit -m "feat(android): 添加 RegulatoryApi"
```

---

## 阶段四：Android Repository 实现

### Task 10: Android - 创建 LawRepository

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt`

- [ ] **Step 1: 创建 LawRepository**

```kotlin
package com.ruoyi.app.feature.law.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.LawEntity
import com.ruoyi.app.data.database.entity.LegalTermEntity
import com.ruoyi.app.feature.law.api.LawApi
import com.ruoyi.app.feature.law.api.LawDto
import com.ruoyi.app.feature.law.api.LegalTermDto

class LawRepository(private val context: Context) {

    private val lawDao = AppDatabase.getInstance(context).lawDao()
    private val termDao = AppDatabase.getInstance(context).legalTermDao()

    suspend fun syncLawsFromServer(): Result<Unit> {
        return try {
            val response = LawApi.getLawList()
            if (response.code == 200) {
                val entities = response.rows.map { it.toEntity() }
                lawDao.insertAll(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncTermsFromServer(lawId: Long): Result<Unit> {
        return try {
            val response = LawApi.getTermListByLaw(lawId)
            if (response.code == 200) {
                val entities = response.rows.map { it.toEntity(lawId) }
                termDao.insertAll(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllLaws(): List<LawEntity> = lawDao.getAll()
    suspend fun getTermsByLawId(lawId: Long): List<LegalTermEntity> = termDao.getByLawId(lawId)
    suspend fun searchLaws(keyword: String): List<LawEntity> = lawDao.search(keyword)
}

fun LawDto.toEntity() = LawEntity(id = id, name = name, releaseTime = null)
fun LegalTermDto.toEntity(lawId: Long) = LegalTermEntity(
    id = id, lawId = lawId, zhCode = zhCode, content = content,
    part = null, partBranch = null, chapter = null, quarter = null,
    article = article, section = null, subparagraph = null, item = null
)
```

- [ ] **Step 2: Commit**

```bash
git add -A && git commit -m "feat(android): 添加 LawRepository"
```

---

### Task 11: Android - 创建 NormativeRepository

**Files:**
- Create: `NormativeRepository.kt`

- [ ] **Step 1: 创建 NormativeRepository**

实现 `syncCategories`, `syncLanguages`, `syncMatterBinds`, `syncTermBinds` 方法

- [ ] **Step 2: Commit**

```bash
git add -A && git commit -m "feat(android): 添加 NormativeRepository"
```

---

### Task 12: Android - 创建 RegulatoryRepository

**Files:**
- Create: `RegulatoryRepository.kt`

- [ ] **Step 1: 创建 RegulatoryRepository**

实现 `syncMatters`, `syncItems`, `syncCategoryBinds` 方法

- [ ] **Step 2: Commit**

```bash
git add -A && git commit -m "feat(android): 添加 RegulatoryRepository"
```

---

## 阶段五：AppDatabase 更新

### Task 13: 更新 AppDatabase

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt`

- [ ] **Step 1: 移除旧的 import 和 entity/dao 声明**

删除：
- `RegulationEntity`, `RegulationChapterEntity`, `RegulationArticleEntity`
- `LegalBasisEntity`, `LegalTypeEntity`, `SupervisionTypeEntity`
- `ProcessingBasisEntity`, `LegalBasisContentEntity`, `ProcessingBasisContentEntity`, `BasisChapterLinkEntity`
- `PhraseBookEntity`, `PhraseItemEntity`, `PhraseDetailEntity`
- `SupervisionCategoryEntity`, `SupervisionItemEntity`
- 以及对应的 dao 声明

- [ ] **Step 2: 添加新的 import 和 entity/dao 声明**

添加 9 个新 Entity 和对应的 DAO

- [ ] **Step 3: 更新数据库版本**

`version = 16`（从 15 升级）

- [ ] **Step 4: Commit**

```bash
git add -A && git commit -m "feat(android): 更新 AppDatabase 使用新 Entity 和 DAO"
```

---

## 阶段六：SyncManager 更新

### Task 14: 更新 SyncManager

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/SyncManager.kt`

- [ ] **Step 1: 更新模块常量**

删除 `MODULE_LAW`, `MODULE_PHRASE`, `MODULE_SUPERVISION`
添加 `MODULE_NORMATIVE`, `MODULE_REGULATORY`

- [ ] **Step 2: 更新 FULL_SYNC_MODULES**

```kotlin
val FULL_SYNC_MODULES = listOf(
    MODULE_USER_PERMISSION,
    MODULE_INDUSTRY_CATEGORY,
    MODULE_UNIT,
    MODULE_NORMATIVE,
    MODULE_REGULATORY,
    MODULE_DOCUMENT_CATEGORY,
    MODULE_DOCUMENT_TEMPLATE,
    MODULE_MEDIA_FILE,
    MODULE_ENFORCEMENT_RECORD,
    MODULE_EVIDENCE_MATERIAL
)
```

- [ ] **Step 3: 更新 syncModule 方法**

删除 `syncLaw`, `syncPhrase`, `syncSupervision`
添加 `syncNormative`, `syncRegulatory`

- [ ] **Step 4: Commit**

```bash
git add -A && git commit -m "feat(android): 更新 SyncManager 模块列表"
```

---

## 阶段七：清理旧文件

### Task 15: 删除旧文件

**Files:**
- Delete: `sync/LawSyncManager.kt`
- Delete: `feature/law/repository/LawRepository.kt`（旧版本）
- Delete: 旧的 entity 文件（12个）
- Delete: 旧的 dao 文件

- [ ] **Step 1: 删除 LawSyncManager.kt**

- [ ] **Step 2: 删除旧 entity 和 dao**

- [ ] **Step 3: Commit**

```bash
git add -A && git commit -m "chore(android): 删除旧的同步相关文件"
```

---

## 自检清单

完成实现后，确认：

1. **Spec 覆盖**：
   - [ ] 后端 `/app/law/*` 接口已实现
   - [ ] 后端 `/app/normative/*` 接口已实现
   - [ ] 后端 `/app/regulatory/*` 接口已实现
   - [ ] Android 9 个新 Entity 已创建
   - [ ] Android 9 个新 DAO 已创建
   - [ ] Android 3 个新 API 已创建
   - [ ] Android 3 个新 Repository 已创建
   - [ ] AppDatabase 已更新
   - [ ] SyncManager 已更新
   - [ ] 旧文件已删除

2. **类型一致性**：
   - [ ] Entity 字段名与后端 DTO 一致
   - [ ] API 返回类型与 Repository 期望一致
   - [ ] DAO 方法与 Repository 调用匹配

3. **编译验证**：
   - [ ] 后端可启动
   - [ ] Android 可编译
