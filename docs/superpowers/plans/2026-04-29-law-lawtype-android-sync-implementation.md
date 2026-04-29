# 法律法规 Android 同步实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现法律法规（Law）及法律类型（LawType）的 Android 端数据同步功能

**Architecture:** 后端新建 AppLawTypeController 提供 /app/lawtype/treeList 接口；Android 端新增 LawTypeEntity/LawTypeDao/LawTypeApi/LawTypeRepository，修改 LawEntity/LawDao/LawApi/LawRepository，集成到 SyncManager

**Tech Stack:** Kotlin, Room, Retrofit (drake.net), Coroutines

---

## 文件结构

### 后端（RuoYi-Vue）

| 文件 | 操作 | 职责 |
|-----|------|-----|
| `ruoyi-admin/.../AppLawTypeController.java` | 新建 | 法律类型 Android 接口 |
| `ruoyi-system/.../LawMapper.xml` | 确认 | selectLawList 已含 typeId |

### Android（Ruoyi-Android-App）

| 文件 | 操作 | 职责 |
|-----|------|-----|
| `app/.../entity/LawTypeEntity.kt` | 新建 | 法律类型实体 |
| `app/.../dao/LawTypeDao.kt` | 新建 | 法律类型 DAO |
| `app/.../api/LawTypeApi.kt` | 新建 | 法律类型 API |
| `app/.../repository/LawTypeRepository.kt` | 新建 | 法律类型 Repository |
| `app/.../entity/LawEntity.kt` | 修改 | 添加 typeId 字段 |
| `app/.../dao/LawDao.kt` | 修改 | 添加 typeId 索引 |
| `app/.../api/LawApi.kt` | 修改 | LawDto 添加 typeId |
| `app/.../repository/LawRepository.kt` | 修改 | syncLaws 增强 |
| `app/.../database/AppDatabase.kt` | 修改 | 注册 LawTypeEntity，version 16→17 |
| `app/.../sync/SyncManager.kt` | 修改 | 添加 MODULE_LAW_TYPE |

---

## Task 1: 后端 - 新建 AppLawTypeController

**Files:**
- Create: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppLawTypeController.java`

- [ ] **Step 1: 创建 AppLawTypeController.java**

```java
package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.LawType;
import com.ruoyi.system.service.ILawTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Android 端法律类型接口（匿名可访问）
 */
@RestController
@RequestMapping("/app/lawtype")
public class AppLawTypeController extends BaseController {

    @Autowired
    private ILawTypeService lawTypeService;

    /**
     * 获取法律类型树形结构
     */
    @Anonymous
    @GetMapping("/treeList")
    public AjaxResult treeList() {
        LawType lawType = new LawType();
        List<LawType> list = lawTypeService.selectLawTypeList(lawType);
        return AjaxResult.success(handleTree(list));
    }

    /**
     * 处理树形结构
     */
    private List<LawType> handleTree(List<LawType> list) {
        List<LawType> returnList = new ArrayList<>();
        List<Long> tempList = list.stream().map(LawType::getId).collect(Collectors.toList());
        for (LawType lawType : list) {
            if (!tempList.contains(lawType.getParentId())) {
                recursionFn(list, lawType);
                returnList.add(lawType);
            }
        }
        if (returnList.isEmpty()) {
            returnList = list;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<LawType> list, LawType t) {
        List<LawType> childList = getChildList(list, t);
        t.setChildren(childList);
        for (LawType tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 获取子列表
     */
    private List<LawType> getChildList(List<LawType> list, LawType t) {
        List<LawType> tlist = new ArrayList<>();
        for (LawType n : list) {
            if (n.getParentId() != null && n.getParentId().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<LawType> list, LawType t) {
        return getChildList(list, t).size() > 0;
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppLawTypeController.java
git commit -m "feat(android-api): 新增 AppLawTypeController 提供法律类型树接口"
```

---

## Task 2: Android - 新建 LawTypeEntity

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/LawTypeEntity.kt`

- [ ] **Step 1: 创建 LawTypeEntity.kt**

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 法律类型实体
 * 对应数据库表：law_type
 */
@Entity(
    tableName = "law_type",
    indices = [Index(value = ["parentId"])]
)
data class LawTypeEntity(
    @PrimaryKey val id: Long,           // 主键
    val parentId: Long,                 // 父类型ID（0为顶级）
    val ancestors: String,              // 祖先路径（格式：0,1,2）
    val name: String,                   // 类型名称
    val icon: String?,                  // 图标
    val sort: Int,                     // 排序
    val status: String                 // 状态（0正常 1停用）
)
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/LawTypeEntity.kt
git commit -m "feat(android): 新增 LawTypeEntity 法律类型实体"
```

---

## Task 3: Android - 新建 LawTypeDao

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/LawTypeDao.kt`

- [ ] **Step 1: 创建 LawTypeDao.kt**

```kotlin
package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.LawTypeEntity

@Dao
interface LawTypeDao {
    @Query("SELECT * FROM law_type ORDER BY sort")
    suspend fun getAll(): List<LawTypeEntity>

    @Query("SELECT * FROM law_type WHERE parentId = :parentId ORDER BY sort")
    suspend fun getByParentId(parentId: Long): List<LawTypeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lawTypes: List<LawTypeEntity>)

    @Query("DELETE FROM law_type")
    suspend fun deleteAll()
}
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/LawTypeDao.kt
git commit -m "feat(android): 新增 LawTypeDao"
```

---

## Task 4: Android - 新建 LawTypeApi

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawTypeApi.kt`

- [ ] **Step 1: 创建 LawTypeApi.kt**

```kotlin
package com.ruoyi.app.feature.law.api

import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

object LawTypeApi {

    suspend fun getLawTypeTree(): LawTypeTreeResponse = withContext(Dispatchers.IO) {
        Get<LawTypeTreeResponse>("${ConfigApi.baseUrl}/app/lawtype/treeList").await()
    }
}

@Serializable
data class LawTypeTreeResponse(
    val code: Int,                              // 响应码
    val msg: String,                            // 消息
    val data: List<LawTypeDto> = emptyList()    // 法律类型列表
)

/** 法律类型DTO */
@Serializable
data class LawTypeDto(
    val id: Long,                    // 主键
    val parentId: Long,              // 父类型ID
    val ancestors: String,            // 祖先路径
    val name: String,                // 类型名称
    val icon: String?,               // 图标
    val sort: Int,                   // 排序
    val status: String,              // 状态
    val children: List<LawTypeDto> = emptyList()  // 子节点
)
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawTypeApi.kt
git commit -m "feat(android): 新增 LawTypeApi"
```

---

## Task 5: Android - 新建 LawTypeRepository

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawTypeRepository.kt`

- [ ] **Step 1: 创建 LawTypeRepository.kt**

```kotlin
package com.ruoyi.app.feature.law.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.LawTypeEntity
import com.ruoyi.app.feature.law.api.LawTypeApi
import com.ruoyi.app.feature.law.api.LawTypeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LawTypeRepository(private val context: Context) {

    private val lawTypeDao = AppDatabase.getInstance(context).lawTypeDao()

    /**
     * 同步法律类型
     */
    suspend fun syncLawTypes(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = LawTypeApi.getLawTypeTree()
                if (response.code == 200) {
                    val entities = response.data.flatMap { flattenTree(it) }
                    lawTypeDao.insertAll(entities)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 扁平化树形结构
     */
    private fun flattenTree(dto: LawTypeDto): List<LawTypeEntity> {
        val result = mutableListOf(toEntity(dto))
        dto.children.forEach { child ->
            result.addAll(flattenTree(child))
        }
        return result
    }

    /**
     * 转换为 Entity
     */
    private fun toEntity(dto: LawTypeDto) = LawTypeEntity(
        id = dto.id,
        parentId = dto.parentId,
        ancestors = dto.ancestors,
        name = dto.name,
        icon = dto.icon,
        sort = dto.sort,
        status = dto.status
    )

    /**
     * 获取本地所有法律类型
     */
    suspend fun getAllLawTypes(): List<LawTypeEntity> {
        return withContext(Dispatchers.IO) {
            lawTypeDao.getAll()
        }
    }

    /**
     * 获取顶级类型
     */
    suspend fun getRootTypes(): List<LawTypeEntity> {
        return withContext(Dispatchers.IO) {
            lawTypeDao.getByParentId(0)
        }
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawTypeRepository.kt
git commit -m "feat(android): 新增 LawTypeRepository"
```

---

## Task 6: Android - 修改 LawEntity 添加 typeId

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/LawEntity.kt`

- [ ] **Step 1: 修改 LawEntity.kt**

将：
```kotlin
data class LawEntity(
    @PrimaryKey val id: Long,           // 法律ID
    val name: String,                    // 法律名称
    val releaseTime: Long?              // 发布日期
)
```

改为：
```kotlin
data class LawEntity(
    @PrimaryKey val id: Long,           // 法律ID
    val name: String,                    // 法律名称
    val releaseTime: Long?,              // 发布日期
    val typeId: Long?                    // 法律法规类型ID
)
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/LawEntity.kt
git commit -m "feat(android): LawEntity 添加 typeId 字段"
```

---

## Task 7: Android - 修改 LawDao 添加 typeId 索引

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/LawDao.kt`

- [ ] **Step 1: 修改 LawDao.kt**

将：
```kotlin
@Dao
interface LawDao {
    @Query("SELECT * FROM law ORDER BY name")
    suspend fun getAll(): List<LawEntity>
    ...
}
```

在 `@Entity` 注解的 indices 中添加 `Index(value = ["typeId"])`，并确保 LawEntity 也有对应索引。

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/LawDao.kt
git commit -m "feat(android): LawDao 添加 typeId 索引"
```

---

## Task 8: Android - 修改 LawApi 更新 Dto

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawApi.kt`

- [ ] **Step 1: 修改 LawDto**

将：
```kotlin
@Serializable
data class LawDto(
    val id: Long,                    // 法律ID
    val name: String                 // 法律名称
)
```

改为：
```kotlin
@Serializable
data class LawDto(
    val id: Long,                    // 法律ID
    val name: String,                 // 法律名称
    val typeId: Long? = null          // 法律法规类型ID
)
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawApi.kt
git commit -m "feat(android): LawDto 添加 typeId 字段"
```

---

## Task 9: Android - 修改 LawRepository 增强 syncLaws

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt`

- [ ] **Step 1: 修改 toEntity 扩展函数**

将：
```kotlin
fun LawDto.toEntity() = LawEntity(
    id = id,
    name = name,
    releaseTime = null
)
```

改为：
```kotlin
fun LawDto.toEntity() = LawEntity(
    id = id,
    name = name,
    releaseTime = null,
    typeId = typeId
)
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt
git commit -m "feat(android): LawRepository syncLaws 支持 typeId"
```

---

## Task 10: Android - 修改 AppDatabase 注册 LawTypeEntity

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt`

- [ ] **Step 1: 添加 LawTypeEntity 到 entities 列表**

在 entities 列表中添加 `LawTypeEntity::class`

- [ ] **Step 2: 添加 lawTypeDao() 抽象方法**

```kotlin
abstract fun lawTypeDao(): LawTypeDao
```

- [ ] **Step 3: 版本号升级**

将 `version = 16` 改为 `version = 17`

- [ ] **Step 4: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt
git commit -m "feat(android): AppDatabase 注册 LawTypeEntity，版本升级到 17"
```

---

## Task 11: Android - 修改 SyncManager 添加同步模块

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/SyncManager.kt`

- [ ] **Step 1: 添加模块常量**

在 `companion object` 中添加：
```kotlin
const val MODULE_LAW_TYPE = "法律法规类型"
```

- [ ] **Step 2: 添加到 FULL_SYNC_MODULES 列表**

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
    MODULE_EVIDENCE_MATERIAL,
    MODULE_LAW_TYPE,    // 新增
    MODULE_LAW          // 新增（如果需要单独模块）
)
```

- [ ] **Step 3: 添加 syncModule 中的处理**

在 `syncModule` 方法的 `when` 中添加：
```kotlin
MODULE_LAW_TYPE -> syncLawType(context)
```

- [ ] **Step 4: 添加 syncLawType 方法**

```kotlin
private suspend fun syncLawType(context: Context?): Boolean {
    if (context == null) return false
    return try {
        val repository = LawTypeRepository(context)
        val result = repository.syncLawTypes()
        if (result.isFailure) {
            Log.e("SyncManager", "法律法规类型同步失败: ${result.exceptionOrNull()?.message}", result.exceptionOrNull())
        }
        result.isSuccess
    } catch (e: Exception) {
        Log.e("SyncManager", "法律法规类型同步异常: ${e.message}", e)
        false
    }
}
```

- [ ] **Step 5: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/SyncManager.kt
git commit -m "feat(android): SyncManager 添加 MODULE_LAW_TYPE"
```

---

## Task 12: 验证和测试

- [ ] **Step 1: 后端验证**

启动后端服务，访问 `GET /app/lawtype/treeList` 确认返回树形结构

- [ ] **Step 2: Android 编译验证**

```bash
cd Ruoyi-Android-App
./gradlew assembleDebug
```

确认编译通过

- [ ] **Step 3: 同步功能测试**

启动 App，触发同步，确认 LawType 和 Law 数据同步成功

---

## Spec 覆盖率检查

| 设计文档需求 | 对应任务 |
|-------------|---------|
| 后端 AppLawTypeController | Task 1 |
| LawTypeEntity | Task 2 |
| LawTypeDao | Task 3 |
| LawTypeApi | Task 4 |
| LawTypeRepository | Task 5 |
| LawEntity typeId | Task 6 |
| LawDao typeId 索引 | Task 7 |
| LawDto typeId | Task 8 |
| LawRepository syncLaws 增强 | Task 9 |
| AppDatabase 注册 | Task 10 |
| SyncManager 集成 | Task 11 |

---

**Plan complete and saved to `docs/superpowers/plans/2026-04-29-law-lawtype-android-sync-implementation.md`**

**Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**