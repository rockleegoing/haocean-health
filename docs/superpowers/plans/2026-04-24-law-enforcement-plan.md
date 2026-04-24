# 便捷执法模块实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现便捷执法模块第一期功能：快捷入口、快速操作（拍照/录音/录像/导航）、执法记录管理（列表/详情/上报/删除）

**Architecture:** 使用现有 MVVM 架构，沿用 View 系统（Activity/Fragment + ViewBinding）。数据存储使用 Room 数据库，同步使用 SyncManager 集成。

**Tech Stack:** Kotlin, Room, ViewBinding, TheRouter, WorkManager, MediaRecorder

---

## 文件结构

```
app/src/main/java/com/ruoyi/app/
├── feature/lawenforcement/                    # 新增目录
│   ├── ui/
│   │   ├── LawEnforcementFragment.kt         # 快捷入口 Fragment
│   │   ├── RecordListActivity.kt            # 记录列表
│   │   ├── RecordDetailActivity.kt          # 记录详情
│   │   └── adapter/
│   │       ├── RecordListAdapter.kt          # 记录列表适配器
│   │       └── EvidenceAdapter.kt            # 证据材料适配器
│   ├── viewmodel/
│   │   ├── LawEnforcementViewModel.kt       # 主 ViewModel
│   │   ├── RecordListViewModel.kt           # 记录列表 ViewModel
│   │   └── RecordDetailViewModel.kt         # 记录详情 ViewModel
│   ├── model/
│   │   ├── EnforcementRecord.kt             # 执法记录实体
│   │   ├── EvidenceMaterial.kt               # 证据材料实体
│   │   └── RecordStatus.kt                  # 记录状态枚举
│   ├── repository/
│   │   └── LawEnforcementRepository.kt      # 数据仓库
│   └── api/
│       └── LawEnforcementApi.kt             # API 接口
├── data/database/
│   ├── AppDatabase.kt                       # 修改：添加新 DAO
│   └── dao/
│       ├── EnforcementRecordDao.kt           # 新增
│       └── EvidenceMaterialDao.kt            # 新增
└── data/database/entity/
    ├── EnforcementRecordEntity.kt             # 新增
    └── EvidenceMaterialEntity.kt             # 新增

app/src/main/res/layout/
├── fragment_law_enforcement.xml              # 新增
├── activity_record_list.xml                  # 新增
├── activity_record_detail.xml                # 新增
├── activity_record_edit.xml                  # 新增
├── item_enforcement_record.xml               # 新增
└── item_evidence_material.xml                # 新增
```

---

## Task 1: 创建执法记录和证据材料 Entity

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/data/database/entity/EnforcementRecordEntity.kt`
- Create: `app/src/main/java/com/ruoyi/app/data/database/entity/EvidenceMaterialEntity.kt`
- Create: `app/src/main/java/com/ruoyi/app/feature/lawenforcement/model/RecordStatus.kt`

- [ ] **Step 1: 创建 EnforcementRecordEntity.kt**

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "t_enforcement_record")
data class EnforcementRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recordNo: String,                  // 记录编号
    val unitId: Long,                     // 单位 ID
    val unitName: String,                 // 单位名称
    val industryId: Long,                // 行业 ID
    val industryCode: String,             // 行业代码
    val recordType: String,               // 记录类型
    val recordStatus: String,             // 记录状态
    val description: String?,             // 备注说明
    val longitude: Double?,              // 经度
    val latitude: Double?,                // 纬度
    val locationName: String?,            // 位置名称
    val syncStatus: String = "PENDING",  // 同步状态
    val createBy: String,                 // 创建人
    val createTime: Long,                 // 创建时间
    val updateBy: String?,                // 更新人
    val updateTime: Long?,                // 更新时间
    val delFlag: String = "0",           // 删除标志
    // 以下为非持久化字段，用于列表展示
    @Ignore
    val photoCount: Int = 0,              // 照片数量（非持久化）
    @Ignore
    val audioCount: Int = 0,              // 录音数量（非持久化）
    @Ignore
    val videoCount: Int = 0               // 录像数量（非持久化）
)
```

---

## ⚠️ 实施前需要确认的问题

在开始实施前，请确认以下事项：

### 1. FileProvider 配置
需要在 `res/xml/file_paths.xml` 中添加证据文件路径配置：

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-files-path name="photos" path="photos/" />
    <external-files-path name="videos" path="videos/" />
    <external-files-path name="audio" path="audio/" />
</paths>
```

并在 `AndroidManifest.xml` 中注册：
```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

### 2. 录音功能方案
系统 Intent `MediaStore.Audio.Media.RECORD_SOUND_ACTION` 无法返回文件路径。建议：
- **方案A（推荐）**：使用自定义录音界面，直接保存录音文件
- **方案B**：使用 MediaStore 查询最新录音文件（不推荐，复杂且不可靠）

### 3. 证据统计查询
RecordListAdapter 需要在绑定时查询每条记录的证据数量。需要在 Repository 中添加统计查询方法，或在 Entity 中添加临时字段存储统计结果。

---

## Task 1: 创建执法记录和证据材料 Entity

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "t_evidence_material",
    indices = [Index("recordId")]
)
data class EvidenceMaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recordId: Long,                   // 关联记录 ID
    val evidenceType: String,             // 类型：photo/audio/video
    val filePath: String,                // 文件路径
    val fileName: String?,                // 文件名
    val fileSize: Long?,                 // 文件大小
    val duration: Int?,                   // 时长（音视频）
    val description: String?,              // 描述
    val syncStatus: String = "PENDING",   // 同步状态
    val createTime: Long                  // 创建时间
)
```

- [ ] **Step 3: 创建 RecordStatus.kt**

```kotlin
package com.ruoyi.app.feature.lawenforcement.model

object RecordStatus {
    const val DRAFT = "DRAFT"           // 草稿/待上报
    const val SUBMITTED = "SUBMITTED"   // 已上报
    const val APPROVED = "APPROVED"    // 已审核
    const val REJECTED = "REJECTED"    // 已驳回
}

object EvidenceType {
    const val PHOTO = "photo"
    const val AUDIO = "audio"
    const val VIDEO = "video"
}

object SyncStatus {
    const val PENDING = "PENDING"
    const val SYNCING = "SYNCING"
    const val SUCCESS = "SUCCESS"
    const val FAILED = "FAILED"
}
```

- [ ] **Step 4: 提交代码**

```bash
git add app/src/main/java/com/ruoyi/app/data/database/entity/EnforcementRecordEntity.kt
git add app/src/main/java/com/ruoyi/app/data/database/entity/EvidenceMaterialEntity.kt
git add app/src/main/java/com/ruoyi/app/feature/lawenforcement/model/RecordStatus.kt
git commit -m "feat(lawenforcement): 添加执法记录和证据材料实体"
```

---

## Task 2: 创建 DAO 接口

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/data/database/dao/EnforcementRecordDao.kt`
- Create: `app/src/main/java/com/ruoyi/app/data/database/dao/EvidenceMaterialDao.kt`

- [ ] **Step 1: 创建 EnforcementRecordDao.kt**

```kotlin
package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EnforcementRecordDao {

    @Query("SELECT * FROM t_enforcement_record WHERE delFlag = '0' ORDER BY createTime DESC")
    fun getAllRecords(): Flow<List<EnforcementRecordEntity>>

    @Query("SELECT * FROM t_enforcement_record WHERE delFlag = '0' AND recordStatus = :status ORDER BY createTime DESC")
    fun getRecordsByStatus(status: String): Flow<List<EnforcementRecordEntity>>

    @Query("""
        SELECT * FROM t_enforcement_record
        WHERE delFlag = '0'
        AND (:status IS NULL OR :status = '' OR recordStatus = :status)
        AND (:unitId IS NULL OR unitId = :unitId)
        AND (:industryId IS NULL OR industryId = :industryId)
        AND (:startTime IS NULL OR createTime >= :startTime)
        AND (:endTime IS NULL OR createTime <= :endTime)
        ORDER BY createTime DESC
    """)
    fun getRecordsFiltered(
        status: String?,
        unitId: Long?,
        industryId: Long?,
        startTime: Long?,
        endTime: Long?
    ): Flow<List<EnforcementRecordEntity>>

    @Query("SELECT * FROM t_enforcement_record WHERE id = :id")
    suspend fun getRecordById(id: Long): EnforcementRecordEntity?

    @Query("SELECT * FROM t_enforcement_record WHERE unitId = :unitId AND delFlag = '0' ORDER BY createTime DESC")
    fun getRecordsByUnitId(unitId: Long): Flow<List<EnforcementRecordEntity>>

    @Query("SELECT * FROM t_enforcement_record WHERE syncStatus = :syncStatus AND delFlag = '0'")
    suspend fun getRecordsBySyncStatus(syncStatus: String): List<EnforcementRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: EnforcementRecordEntity): Long

    @Update
    suspend fun updateRecord(record: EnforcementRecordEntity)

    @Query("UPDATE t_enforcement_record SET recordStatus = :status, updateTime = :updateTime WHERE id = :id")
    suspend fun updateRecordStatus(id: Long, status: String, updateTime: Long)

    @Query("UPDATE t_enforcement_record SET syncStatus = :syncStatus WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, syncStatus: String)

    @Query("UPDATE t_enforcement_record SET delFlag = '1', updateTime = :updateTime WHERE id = :id")
    suspend fun deleteRecord(id: Long, updateTime: Long)

    @Query("SELECT COUNT(*) FROM t_enforcement_record WHERE delFlag = '0'")
    suspend fun getRecordCount(): Int

    @Query("SELECT COUNT(*) FROM t_enforcement_record WHERE delFlag = '0' AND recordStatus = :status")
    suspend fun getRecordCountByStatus(status: String): Int

    @Query("SELECT * FROM t_enforcement_record WHERE unitId = :unitId AND createTime >= :startOfDay AND createTime < :endOfDay AND delFlag = '0' LIMIT 1")
    suspend fun getTodayRecordByUnit(unitId: Long, startOfDay: Long, endOfDay: Long): EnforcementRecordEntity?
}
```

- [ ] **Step 2: 创建 EvidenceMaterialDao.kt**

```kotlin
package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.EvidenceMaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenceMaterialDao {

    @Query("SELECT * FROM t_evidence_material WHERE recordId = :recordId ORDER BY createTime ASC")
    fun getMaterialsByRecordId(recordId: Long): Flow<List<EvidenceMaterialEntity>>

    @Query("SELECT * FROM t_evidence_material WHERE recordId = :recordId ORDER BY createTime ASC")
    suspend fun getMaterialsByRecordIdSync(recordId: Long): List<EvidenceMaterialEntity>

    @Query("SELECT * FROM t_evidence_material WHERE recordId = :recordId AND evidenceType = :type ORDER BY createTime ASC")
    fun getMaterialsByType(recordId: Long, type: String): Flow<List<EvidenceMaterialEntity>>

    @Query("SELECT * FROM t_evidence_material WHERE id = :id")
    suspend fun getMaterialById(id: Long): EvidenceMaterialEntity?

    @Query("SELECT * FROM t_evidence_material WHERE syncStatus = :syncStatus")
    suspend fun getMaterialsBySyncStatus(syncStatus: String): List<EvidenceMaterialEntity>

    @Query("SELECT COUNT(*) FROM t_evidence_material WHERE recordId = :recordId AND evidenceType = :type")
    suspend fun getMaterialCountByType(recordId: Long, type: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: EvidenceMaterialEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterials(materials: List<EvidenceMaterialEntity>)

    @Update
    suspend fun updateMaterial(material: EvidenceMaterialEntity)

    @Query("UPDATE t_evidence_material SET syncStatus = :syncStatus WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, syncStatus: String)

    @Delete
    suspend fun deleteMaterial(material: EvidenceMaterialEntity)

    @Query("DELETE FROM t_evidence_material WHERE recordId = :recordId")
    suspend fun deleteMaterialsByRecordId(recordId: Long)
}
```

- [ ] **Step 3: 提交代码**

```bash
git add app/src/main/java/com/ruoyi/app/data/database/dao/EnforcementRecordDao.kt
git add app/src/main/java/com/ruoyi/app/data/database/dao/EvidenceMaterialDao.kt
git commit -m "feat(lawenforcement): 添加执法记录和证据材料 DAO"
```

---

## Task 3: 更新 AppDatabase 添加新 DAO

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt`

- [ ] **Step 1: 更新 AppDatabase.kt**

```kotlin
@Database(
    entities = [
        UserEntity::class,
        RoleEntity::class,
        DeptEntity::class,
        IndustryCategoryEntity::class,
        ActivationCodeEntity::class,
        DeviceEntity::class,
        SyncQueueEntity::class,
        DataVersionEntity::class,
        UnitEntity::class,
        // 新增
        EnforcementRecordEntity::class,
        EvidenceMaterialEntity::class
    ],
    version = 4,  // 版本号 +1
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // ... 现有方法

    // 新增
    abstract fun enforcementRecordDao(): EnforcementRecordDao
    abstract fun evidenceMaterialDao(): EvidenceMaterialDao
}
```

- [ ] **Step 2: 更新版本号**

注意：如果当前 version 是 3，需要改为 4，并在 `fallbackToDestructiveMigration()` 前添加 `.addMigrations()` 或确认可以接受数据丢失。

- [ ] **Step 3: 提交代码**

```bash
git add app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt
git commit -m "feat(lawenforcement): 更新数据库版本号并添加新 DAO"
```

---

## Task 4: 创建 Repository

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/feature/lawenforcement/repository/LawEnforcementRepository.kt`

- [ ] **Step 1: 创建 LawEnforcementRepository.kt**

```kotlin
package com.ruoyi.app.feature.lawenforcement.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import com.ruoyi.app.data.database.entity.EvidenceMaterialEntity
import com.ruoyi.app.feature.lawenforcement.model.EvidenceType
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.app.feature.lawenforcement.model.SyncStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LawEnforcementRepository(private val context: Context) {

    private val recordDao = AppDatabase.getInstance(context).enforcementRecordDao()
    private val evidenceDao = AppDatabase.getInstance(context).evidenceMaterialDao()

    // 获取所有记录
    fun getAllRecords(): Flow<List<EnforcementRecordEntity>> {
        return recordDao.getAllRecords()
    }

    // 按状态获取记录
    fun getRecordsByStatus(status: String): Flow<List<EnforcementRecordEntity>> {
        return recordDao.getRecordsByStatus(status)
    }

    // 多条件筛选
    fun getRecordsFiltered(
        status: String?,
        unitId: Long?,
        industryId: Long?,
        startTime: Long?,
        endTime: Long?
    ): Flow<List<EnforcementRecordEntity>> {
        return recordDao.getRecordsFiltered(status, unitId, industryId, startTime, endTime)
    }

    // 获取单条记录
    suspend fun getRecordById(id: Long): EnforcementRecordEntity? {
        return withContext(Dispatchers.IO) {
            recordDao.getRecordById(id)
        }
    }

    // 创建或获取今日记录
    suspend fun getOrCreateTodayRecord(unitId: Long, unitName: String, industryId: Long, industryCode: String, userName: String): EnforcementRecordEntity {
        return withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val startOfDay = getStartOfDay(now)
            val endOfDay = startOfDay + 24 * 60 * 60 * 1000

            // 检查今日是否已有记录
            val existing = recordDao.getTodayRecordByUnit(unitId, startOfDay, endOfDay)
            if (existing != null) {
                return@withContext existing
            }

            // 创建新记录
            val recordNo = generateRecordNo()
            val newRecord = EnforcementRecordEntity(
                recordNo = recordNo,
                unitId = unitId,
                unitName = unitName,
                industryId = industryId,
                industryCode = industryCode,
                recordType = "ROUTINE",  // 例行检查
                recordStatus = RecordStatus.DRAFT,
                syncStatus = SyncStatus.PENDING,
                createBy = userName,
                createTime = now
            )
            val id = recordDao.insertRecord(newRecord)
            newRecord.copy(id = id)
        }
    }

    // 更新记录状态
    suspend fun updateRecordStatus(id: Long, status: String) {
        withContext(Dispatchers.IO) {
            recordDao.updateRecordStatus(id, status, System.currentTimeMillis())
        }
    }

    // 更新同步状态
    suspend fun updateRecordSyncStatus(id: Long, syncStatus: String) {
        withContext(Dispatchers.IO) {
            recordDao.updateSyncStatus(id, syncStatus)
        }
    }

    // 删除记录（软删除）
    suspend fun deleteRecord(id: Long) {
        withContext(Dispatchers.IO) {
            recordDao.deleteRecord(id, System.currentTimeMillis())
        }
    }

    // 获取证据材料
    fun getEvidenceMaterials(recordId: Long): Flow<List<EvidenceMaterialEntity>> {
        return evidenceDao.getMaterialsByRecordId(recordId)
    }

    // 按类型获取证据材料
    fun getEvidenceMaterialsByType(recordId: Long, type: String): Flow<List<EvidenceMaterialEntity>> {
        return evidenceDao.getMaterialsByType(recordId, type)
    }

    // 添加证据材料
    suspend fun addEvidenceMaterial(
        recordId: Long,
        type: String,
        filePath: String,
        fileName: String?,
        fileSize: Long?,
        duration: Int?,
        description: String?
    ): Long {
        return withContext(Dispatchers.IO) {
            val material = EvidenceMaterialEntity(
                recordId = recordId,
                evidenceType = type,
                filePath = filePath,
                fileName = fileName,
                fileSize = fileSize,
                duration = duration,
                description = description,
                syncStatus = SyncStatus.PENDING,
                createTime = System.currentTimeMillis()
            )
            evidenceDao.insertMaterial(material)
        }
    }

    // 删除证据材料
    suspend fun deleteEvidenceMaterial(material: EvidenceMaterialEntity) {
        withContext(Dispatchers.IO) {
            evidenceDao.deleteMaterial(material)
        }
    }

    // 获取待同步记录
    suspend fun getPendingRecords(): List<EnforcementRecordEntity> {
        return withContext(Dispatchers.IO) {
            recordDao.getRecordsBySyncStatus(SyncStatus.PENDING)
        }
    }

    // 获取待同步证据
    suspend fun getPendingEvidences(): List<EvidenceMaterialEntity> {
        return withContext(Dispatchers.IO) {
            evidenceDao.getMaterialsBySyncStatus(SyncStatus.PENDING)
        }
    }

    // 生成记录编号
    private fun generateRecordNo(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
        val dateStr = dateFormat.format(Date())
        val random = (1000..9999).random()
        return "ER$dateStr$random"
    }

    // 获取当天开始时间戳
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
```

- [ ] **Step 2: 提交代码**

```bash
git add app/src/main/java/com/ruoyi/app/feature/lawenforcement/repository/LawEnforcementRepository.kt
git commit -m "feat(lawenforcement): 添加执法数据仓库"
```

---

## Task 5: 创建 ViewModel

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/feature/lawenforcement/viewmodel/LawEnforcementViewModel.kt`
- Create: `app/src/main/java/com/ruoyi/app/feature/lawenforcement/viewmodel/RecordListViewModel.kt`
- Create: `app/src/main/java/com/ruoyi/app/feature/lawenforcement/viewmodel/RecordDetailViewModel.kt`

- [ ] **Step 1: 创建 LawEnforcementViewModel.kt**

```kotlin
package com.ruoyi.app.feature.lawenforcement.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import com.ruoyi.app.feature.lawenforcement.model.EvidenceType
import com.ruoyi.app.feature.lawenforcement.repository.LawEnforcementRepository
import kotlinx.coroutines.launch

class LawEnforcementViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LawEnforcementRepository(application)

    // 当前选中的单位
    val selectedUnit = MutableLiveData<UnitInfo?>()
    val selectedUnitLiveData = selectedUnit

    // 当前执法记录
    val currentRecord = MutableLiveData<EnforcementRecordEntity?>()

    // 操作结果
    val operationResult = MutableLiveData<OperationResult>()

    // 加载状态
    val isLoading = MutableLiveData<Boolean>()

    // 错误信息
    val error = MutableLiveData<String>()

    data class UnitInfo(
        val unitId: Long,
        val unitName: String,
        val industryId: Long,
        val industryCode: String
    )

    sealed class OperationResult {
        data class Success(val message: String) : OperationResult()
        data class Error(val message: String) : OperationResult()
    }

    /**
     * 快速拍照后添加到记录
     */
    fun addPhotoToRecord(filePath: String, fileName: String?, fileSize: Long?) {
        val unit = selectedUnit.value
        if (unit == null) {
            error.value = "请先选择执法单位"
            return
        }

        viewModelScope.launch {
            try {
                isLoading.value = true
                val record = repository.getOrCreateTodayRecord(
                    unitId = unit.unitId,
                    unitName = unit.unitName,
                    industryId = unit.industryId,
                    industryCode = unit.industryCode,
                    userName = getCurrentUserName()
                )
                repository.addEvidenceMaterial(
                    recordId = record.id,
                    type = EvidenceType.PHOTO,
                    filePath = filePath,
                    fileName = fileName,
                    fileSize = fileSize,
                    duration = null,
                    description = null
                )
                currentRecord.value = record
                operationResult.value = OperationResult.Success("照片已保存")
            } catch (e: Exception) {
                error.value = "保存失败：${e.message}"
                operationResult.value = OperationResult.Error("保存失败：${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 快速录音后添加到记录
     */
    fun addAudioToRecord(filePath: String, fileName: String?, fileSize: Long?, duration: Int?) {
        val unit = selectedUnit.value
        if (unit == null) {
            error.value = "请先选择执法单位"
            return
        }

        viewModelScope.launch {
            try {
                isLoading.value = true
                val record = repository.getOrCreateTodayRecord(
                    unitId = unit.unitId,
                    unitName = unit.unitName,
                    industryId = unit.industryId,
                    industryCode = unit.industryCode,
                    userName = getCurrentUserName()
                )
                repository.addEvidenceMaterial(
                    recordId = record.id,
                    type = EvidenceType.AUDIO,
                    filePath = filePath,
                    fileName = fileName,
                    fileSize = fileSize,
                    duration = duration,
                    description = null
                )
                currentRecord.value = record
                operationResult.value = OperationResult.Success("录音已保存")
            } catch (e: Exception) {
                error.value = "保存失败：${e.message}"
                operationResult.value = OperationResult.Error("保存失败：${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 快速录像后添加到记录
     */
    fun addVideoToRecord(filePath: String, fileName: String?, fileSize: Long?, duration: Int?) {
        val unit = selectedUnit.value
        if (unit == null) {
            error.value = "请先选择执法单位"
            return
        }

        viewModelScope.launch {
            try {
                isLoading.value = true
                val record = repository.getOrCreateTodayRecord(
                    unitId = unit.unitId,
                    unitName = unit.unitName,
                    industryId = unit.industryId,
                    industryCode = unit.industryCode,
                    userName = getCurrentUserName()
                )
                repository.addEvidenceMaterial(
                    recordId = record.id,
                    type = EvidenceType.VIDEO,
                    filePath = filePath,
                    fileName = fileName,
                    fileSize = fileSize,
                    duration = duration,
                    description = null
                )
                currentRecord.value = record
                operationResult.value = OperationResult.Success("录像已保存")
            } catch (e: Exception) {
                error.value = "保存失败：${e.message}"
                operationResult.value = OperationResult.Error("保存失败：${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 设置当前选中的单位
     */
    fun setSelectedUnit(unitId: Long, unitName: String, industryId: Long, industryCode: String) {
        selectedUnit.value = UnitInfo(unitId, unitName, industryId, industryCode)
    }

    /**
     * 获取当前用户名
     */
    private fun getCurrentUserName(): String {
        // 从本地数据库获取当前登录用户
        val user = com.ruoyi.app.data.database.AppDatabase.getInstance(getApplication())
            .userDao().getCurrentUser()
        return user?.userName ?: "unknown"
    }
}
```

- [ ] **Step 2: 创建 RecordListViewModel.kt**

```kotlin
package com.ruoyi.app.feature.lawenforcement.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.app.feature.lawenforcement.repository.LawEnforcementRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecordListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LawEnforcementRepository(application)

    // 记录列表
    val records = MutableLiveData<List<EnforcementRecordEntity>>()

    // 筛选条件
    data class FilterParams(
        val status: String? = null,
        val unitId: Long? = null,
        val industryId: Long? = null,
        val startTime: Long? = null,
        val endTime: Long? = null
    )
    val filterParams = MutableLiveData(FilterParams())

    // 加载状态
    val isLoading = MutableLiveData<Boolean>()

    // 错误信息
    val error = MutableLiveData<String>()

    // 操作结果
    val operationResult = MutableLiveData<String>()

    init {
        loadRecords()
    }

    /**
     * 加载记录列表
     */
    fun loadRecords() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val params = filterParams.value ?: FilterParams()

                repository.getRecordsFiltered(
                    status = params.status,
                    unitId = params.unitId,
                    industryId = params.industryId,
                    startTime = params.startTime,
                    endTime = params.endTime
                ).collectLatest { recordList ->
                    records.value = recordList
                    isLoading.value = false
                }
            } catch (e: Exception) {
                error.value = "加载失败：${e.message}"
                isLoading.value = false
            }
        }
    }

    /**
     * 按状态筛选
     */
    fun filterByStatus(status: String?) {
        val current = filterParams.value ?: FilterParams()
        filterParams.value = current.copy(status = status)
        loadRecords()
    }

    /**
     * 按单位筛选
     */
    fun filterByUnit(unitId: Long?) {
        val current = filterParams.value ?: FilterParams()
        filterParams.value = current.copy(unitId = unitId)
        loadRecords()
    }

    /**
     * 按行业筛选
     */
    fun filterByIndustry(industryId: Long?) {
        val current = filterParams.value ?: FilterParams()
        filterParams.value = current.copy(industryId = industryId)
        loadRecords()
    }

    /**
     * 按日期范围筛选
     */
    fun filterByDateRange(startTime: Long?, endTime: Long?) {
        val current = filterParams.value ?: FilterParams()
        filterParams.value = current.copy(startTime = startTime, endTime = endTime)
        loadRecords()
    }

    /**
     * 清空筛选条件
     */
    fun clearFilters() {
        filterParams.value = FilterParams()
        loadRecords()
    }

    /**
     * 上报记录
     */
    fun submitRecord(recordId: Long) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.updateRecordStatus(recordId, RecordStatus.SUBMITTED)
                operationResult.value = "上报成功"
                loadRecords()
            } catch (e: Exception) {
                error.value = "上报失败：${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 删除记录
     */
    fun deleteRecord(recordId: Long) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.deleteRecord(recordId)
                operationResult.value = "删除成功"
                loadRecords()
            } catch (e: Exception) {
                error.value = "删除失败：${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}
```

- [ ] **Step 3: 创建 RecordDetailViewModel.kt**

```kotlin
package com.ruoyi.app.feature.lawenforcement.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import com.ruoyi.app.data.database.entity.EvidenceMaterialEntity
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.app.feature.lawenforcement.model.SyncStatus
import com.ruoyi.app.feature.lawenforcement.repository.LawEnforcementRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecordDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LawEnforcementRepository(application)

    // 记录详情
    val record = MutableLiveData<EnforcementRecordEntity?>()

    // 证据材料列表
    val evidenceMaterials = MutableLiveData<List<EvidenceMaterialEntity>>()

    // 加载状态
    val isLoading = MutableLiveData<Boolean>()

    // 错误信息
    val error = MutableLiveData<String>()

    // 操作结果
    val operationResult = MutableLiveData<String>()

    /**
     * 加载记录详情
     */
    fun loadRecord(recordId: Long) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val recordEntity = repository.getRecordById(recordId)
                record.value = recordEntity

                if (recordEntity != null) {
                    repository.getEvidenceMaterials(recordId).collectLatest { materials ->
                        evidenceMaterials.value = materials
                        isLoading.value = false
                    }
                } else {
                    error.value = "记录不存在"
                    isLoading.value = false
                }
            } catch (e: Exception) {
                error.value = "加载失败：${e.message}"
                isLoading.value = false
            }
        }
    }

    /**
     * 上报记录
     */
    fun submitRecord() {
        val currentRecord = record.value ?: return

        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.updateRecordStatus(currentRecord.id, RecordStatus.SUBMITTED)
                record.value = currentRecord.copy(recordStatus = RecordStatus.SUBMITTED)
                operationResult.value = "上报成功"
            } catch (e: Exception) {
                error.value = "上报失败：${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 删除证据材料
     */
    fun deleteEvidence(material: EvidenceMaterialEntity) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.deleteEvidenceMaterial(material)
                operationResult.value = "删除成功"
            } catch (e: Exception) {
                error.value = "删除失败：${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 删除记录
     */
    fun deleteRecord(recordId: Long) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.deleteRecord(recordId)
                operationResult.value = "删除成功"
            } catch (e: Exception) {
                error.value = "删除失败：${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 获取证据统计
     */
    fun getEvidenceStats(): EvidenceStats {
        val materials = evidenceMaterials.value ?: emptyList()
        return EvidenceStats(
            photoCount = materials.count { it.evidenceType == "photo" },
            audioCount = materials.count { it.evidenceType == "audio" },
            videoCount = materials.count { it.evidenceType == "video" }
        )
    }

    data class EvidenceStats(
        val photoCount: Int,
        val audioCount: Int,
        val videoCount: Int
    )
}
```

- [ ] **Step 4: 提交代码**

```bash
git add app/src/main/java/com/ruoyi/app/feature/lawenforcement/viewmodel/LawEnforcementViewModel.kt
git add app/src/main/java/com/ruoyi/app/feature/lawenforcement/viewmodel/RecordListViewModel.kt
git add app/src/main/java/com/ruoyi/app/feature/lawenforcement/viewmodel/RecordDetailViewModel.kt
git commit -m "feat(lawenforcement): 添加 ViewModel"
```

---

## Task 6: 创建布局文件

**Files:**
- Create: `app/src/main/res/layout/fragment_law_enforcement.xml`
- Create: `app/src/main/res/layout/activity_record_list.xml`
- Create: `app/src/main/res/layout/activity_record_detail.xml`
- Create: `app/src/main/res/layout/item_enforcement_record.xml`
- Create: `app/src/main/res/layout/item_evidence_material.xml`
- Create: `app/src/main/res/xml/file_paths.xml`
- Modify: `app/src/main/AndroidManifest.xml`

- [ ] **Step 1: 创建 file_paths.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-files-path name="photos" path="photos/" />
    <external-files-path name="videos" path="videos/" />
    <external-files-path name="audio" path="audio/" />
</paths>
```

- [ ] **Step 2: 更新 AndroidManifest.xml**

在 `<application>` 标签内添加 FileProvider：

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

- [ ] **Step 3: 创建 fragment_law_enforcement.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">

    <!-- 标题 -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="便捷执法"
        android:textSize="18sp"
        android:textStyle="bold"
        android:textColor="@color/text_primary" />

    <Space
        android:layout_width="match_parent"
        android:layout_height="12dp" />

    <!-- 快捷入口网格 -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center">

        <!-- 拍照 -->
        <LinearLayout
            android:id="@+id/btn_camera"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical"
            android:gravity="center"
            android:padding="12dp"
            android:background="?attr/selectableItemBackground"
            android:clickable="true"
            android:focusable="true">

            <ImageView
                android:layout_width="48dp"
                android:layout_height="48dp"
                android:src="@drawable/ic_camera"
                android:contentDescription="拍照" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:text="拍照"
                android:textSize="14sp" />
        </LinearLayout>

        <!-- 录音 -->
        <LinearLayout
            android:id="@+id/btn_audio"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical"
            android:gravity="center"
            android:padding="12dp"
            android:background="?attr/selectableItemBackground"
            android:clickable="true"
            android:focusable="true">

            <ImageView
                android:layout_width="48dp"
                android:layout_height="48dp"
                android:src="@drawable/ic_mic"
                android:contentDescription="录音" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:text="录音"
                android:textSize="14sp" />
        </LinearLayout>

        <!-- 录像 -->
        <LinearLayout
            android:id="@+id/btn_video"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical"
            android:gravity="center"
            android:padding="12dp"
            android:background="?attr/selectableItemBackground"
            android:clickable="true"
            android:focusable="true">

            <ImageView
                android:layout_width="48dp"
                android:layout_height="48dp"
                android:src="@drawable/ic_videocam"
                android:contentDescription="录像" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:text="录像"
                android:textSize="14sp" />
        </LinearLayout>

        <!-- 导航 -->
        <LinearLayout
            android:id="@+id/btn_navigation"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical"
            android:gravity="center"
            android:padding="12dp"
            android:background="?attr/selectableItemBackground"
            android:clickable="true"
            android:focusable="true">

            <ImageView
                android:layout_width="48dp"
                android:layout_height="48dp"
                android:src="@drawable/ic_navigation"
                android:contentDescription="导航" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:text="导航"
                android:textSize="14sp" />
        </LinearLayout>

        <!-- 记录 -->
        <LinearLayout
            android:id="@+id/btn_records"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical"
            android:gravity="center"
            android:padding="12dp"
            android:background="?attr/selectableItemBackground"
            android:clickable="true"
            android:focusable="true">

            <ImageView
                android:layout_width="48dp"
                android:layout_height="48dp"
                android:src="@drawable/ic_assignment"
                android:contentDescription="记录" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="8dp"
                android:text="记录"
                android:textSize="14sp" />
        </LinearLayout>

    </LinearLayout>

</LinearLayout>
```

- [ ] **Step 2: 创建 activity_record_list.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <!-- 标题栏 -->
    <com.ruoyi.code.widget.TitleBar
        android:id="@+id/titlebar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:title="执法记录" />

    <!-- 筛选栏 -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="12dp"
        android:background="@color/background">

        <Spinner
            android:id="@+id/spinner_status"
            android:layout_width="0dp"
            android:layout_height="40dp"
            android:layout_weight="1"
            android:background="@drawable/bg_spinner" />

        <Space
            android:layout_width="8dp"
            android:layout_height="match_parent" />

        <Spinner
            android:id="@+id/spinner_unit"
            android:layout_width="0dp"
            android:layout_height="40dp"
            android:layout_weight="1"
            android:background="@drawable/bg_spinner" />

        <Space
            android:layout_width="8dp"
            android:layout_height="match_parent" />

        <TextView
            android:id="@+id/btn_date_filter"
            android:layout_width="wrap_content"
            android:layout_height="40dp"
            android:gravity="center"
            android:paddingHorizontal="12dp"
            android:text="日期筛选"
            android:background="@drawable/bg_spinner" />

    </LinearLayout>

    <!-- 记录列表 -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerview"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:padding="8dp" />

    <!-- 空状态 -->
    <LinearLayout
        android:id="@+id/empty_view"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:gravity="center"
        android:orientation="vertical"
        android:visibility="gone">

        <ImageView
            android:layout_width="120dp"
            android:layout_height="120dp"
            android:src="@drawable/ic_empty_records"
            android:alpha="0.5" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:text="暂无执法记录"
            android:textSize="16sp"
            android:textColor="@color/text_secondary" />

    </LinearLayout>

    <!-- 加载中 -->
    <ProgressBar
        android:id="@+id/progress_bar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:visibility="gone" />

</LinearLayout>
```

- [ ] **Step 3: 创建 activity_record_detail.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <!-- 标题栏 -->
    <com.ruoyi.code.widget.TitleBar
        android:id="@+id/titlebar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:title="记录详情" />

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="16dp">

            <!-- 基本信息卡片 -->
            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                app:cardCornerRadius="12dp"
                app:cardElevation="2dp">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="16dp">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="基本信息"
                        android:textSize="16sp"
                        android:textStyle="bold" />

                    <Space
                        android:layout_width="match_parent"
                        android:layout_height="12dp" />

                    <!-- 记录编号 -->
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal">

                        <TextView
                            android:layout_width="80dp"
                            android:layout_height="wrap_content"
                            android:text="记录编号"
                            android:textColor="@color/text_secondary" />

                        <TextView
                            android:id="@+id/tv_record_no"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content" />
                    </LinearLayout>

                    <!-- 单位名称 -->
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="8dp"
                        android:orientation="horizontal">

                        <TextView
                            android:layout_width="80dp"
                            android:layout_height="wrap_content"
                            android:text="单位名称"
                            android:textColor="@color/text_secondary" />

                        <TextView
                            android:id="@+id/tv_unit_name"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content" />
                    </LinearLayout>

                    <!-- 行业分类 -->
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="8dp"
                        android:orientation="horizontal">

                        <TextView
                            android:layout_width="80dp"
                            android:layout_height="wrap_content"
                            android:text="行业分类"
                            android:textColor="@color/text_secondary" />

                        <TextView
                            android:id="@+id/tv_industry"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content" />
                    </LinearLayout>

                    <!-- 记录状态 -->
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="8dp"
                        android:orientation="horizontal">

                        <TextView
                            android:layout_width="80dp"
                            android:layout_height="wrap_content"
                            android:text="记录状态"
                            android:textColor="@color/text_secondary" />

                        <TextView
                            android:id="@+id/tv_status"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:textColor="@color/status_color" />
                    </LinearLayout>

                    <!-- 创建时间 -->
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginTop="8dp"
                        android:orientation="horizontal">

                        <TextView
                            android:layout_width="80dp"
                            android:layout_height="wrap_content"
                            android:text="创建时间"
                            android:textColor="@color/text_secondary" />

                        <TextView
                            android:id="@+id/tv_create_time"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content" />
                    </LinearLayout>

                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>

            <!-- 证据材料 -->
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="16dp"
                android:text="证据材料"
                android:textSize="16sp"
                android:textStyle="bold" />

            <Space
                android:layout_width="match_parent"
                android:layout_height="8dp" />

            <!-- 照片统计 -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal">

                <TextView
                    android:id="@+id/tv_photo_count"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:drawableStart="@drawable/ic_camera"
                    android:drawablePadding="4dp"
                    android:text="0 张照片" />

                <Space
                    android:layout_width="16dp"
                    android:layout_height="wrap_content" />

                <TextView
                    android:id="@+id/tv_audio_count"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:drawableStart="@drawable/ic_mic"
                    android:drawablePadding="4dp"
                    android:text="0 条录音" />

                <Space
                    android:layout_width="16dp"
                    android:layout_height="wrap_content" />

                <TextView
                    android:id="@+id/tv_video_count"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:drawableStart="@drawable/ic_videocam"
                    android:drawablePadding="4dp"
                    android:text="0 条录像" />

            </LinearLayout>

            <Space
                android:layout_width="match_parent"
                android:layout_height="8dp" />

            <!-- 证据网格 -->
            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/rv_evidence"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:nestedScrollingEnabled="false" />

        </LinearLayout>
    </ScrollView>

    <!-- 操作按钮 -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="16dp"
        android:background="@color/background">

        <Button
            android:id="@+id/btn_submit"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="上报" />

        <Space
            android:layout_width="8dp"
            android:layout_height="match_parent" />

        <Button
            android:id="@+id/btn_edit"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="编辑"
            style="@style/Widget.MaterialComponents.Button.OutlinedButton" />

        <Space
            android:layout_width="8dp"
            android:layout_height="match_parent" />

        <Button
            android:id="@+id/btn_delete"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="删除"
            android:backgroundTint="@color/error"
            style="@style/Widget.MaterialComponents.Button" />

    </LinearLayout>

</LinearLayout>
```

- [ ] **Step 4: 创建 item_enforcement_record.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="4dp"
    app:cardCornerRadius="12dp"
    app:cardElevation="2dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <!-- 顶部：状态 + 时间 -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal">

            <View
                android:id="@+id/status_indicator"
                android:layout_width="8dp"
                android:layout_height="8dp"
                android:layout_gravity="center_vertical"
                android:background="@drawable/bg_status_dot" />

            <TextView
                android:id="@+id/tv_status"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:layout_marginStart="8dp"
                android:textSize="12sp"
                android:textColor="@color/text_secondary" />

            <TextView
                android:id="@+id/tv_create_time"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textSize="12sp"
                android:textColor="@color/text_secondary" />

        </LinearLayout>

        <!-- 单位名称 -->
        <TextView
            android:id="@+id/tv_unit_name"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:textSize="16sp"
            android:textStyle="bold"
            android:maxLines="1"
            android:ellipsize="end" />

        <!-- 行业分类 -->
        <TextView
            android:id="@+id/tv_industry"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:textSize="12sp"
            android:textColor="@color/text_secondary" />

        <!-- 证据统计 + 操作按钮 -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="12dp"
            android:orientation="horizontal"
            android:gravity="center_vertical">

            <!-- 证据统计 -->
            <LinearLayout
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:orientation="horizontal">

                <TextView
                    android:id="@+id/tv_photo_count"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:drawableStart="@drawable/ic_camera"
                    android:drawablePadding="2dp"
                    android:textSize="12sp" />

                <TextView
                    android:id="@+id/tv_audio_count"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginStart="12dp"
                    android:drawableStart="@drawable/ic_mic"
                    android:drawablePadding="2dp"
                    android:textSize="12sp" />

                <TextView
                    android:id="@+id/tv_video_count"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginStart="12dp"
                    android:drawableStart="@drawable/ic_videocam"
                    android:drawablePadding="2dp"
                    android:textSize="12sp" />

            </LinearLayout>

            <!-- 操作按钮 -->
            <Button
                android:id="@+id/btn_edit"
                android:layout_width="wrap_content"
                android:layout_height="32dp"
                android:text="编辑"
                android:textSize="12sp"
                style="@style/Widget.MaterialComponents.Button.TextButton" />

            <Button
                android:id="@+id/btn_submit"
                android:layout_width="wrap_content"
                android:layout_height="32dp"
                android:text="上报"
                android:textSize="12sp"
                style="@style/Widget.MaterialComponents.Button.TextButton" />

            <Button
                android:id="@+id/btn_delete"
                android:layout_width="wrap_content"
                android:layout_height="32dp"
                android:text="删除"
                android:textSize="12sp"
                android:textColor="@color/error"
                style="@style/Widget.MaterialComponents.Button.TextButton" />

        </LinearLayout>

    </LinearLayout>

</com.google.android.material.card.MaterialCardView>
```

- [ ] **Step 5: 创建 item_evidence_material.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:padding="2dp">

    <!-- 照片/视频缩略图 -->
    <ImageView
        android:id="@+id/iv_thumbnail"
        android:layout_width="match_parent"
        android:layout_height="100dp"
        android:scaleType="centerCrop"
        android:background="@color/background" />

    <!-- 播放图标（音视频） -->
    <ImageView
        android:id="@+id/iv_play_icon"
        android:layout_width="36dp"
        android:layout_height="36dp"
        android:layout_gravity="center"
        android:src="@drawable/ic_play_circle"
        android:visibility="gone" />

    <!-- 时长标签 -->
    <TextView
        android:id="@+id/tv_duration"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="4dp"
        android:paddingHorizontal="6dp"
        android:paddingVertical="2dp"
        android:background="@drawable/bg_duration"
        android:textColor="@android:color/white"
        android:textSize="10sp"
        android:visibility="gone" />

</FrameLayout>
```

- [ ] **Step 6: 提交代码**

```bash
git add app/src/main/res/layout/fragment_law_enforcement.xml
git add app/src/main/res/layout/activity_record_list.xml
git add app/src/main/res/layout/activity_record_detail.xml
git add app/src/main/res/layout/item_enforcement_record.xml
git add app/src/main/res/layout/item_evidence_material.xml
git commit -m "feat(lawenforcement): 添加布局文件"
```

---

## Task 7: 创建快捷入口 Fragment

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/feature/lawenforcement/ui/LawEnforcementFragment.kt`

- [ ] **Step 1: 创建 LawEnforcementFragment.kt**

```kotlin
package com.ruoyi.app.feature.lawenforcement.ui

import android.Manifest
import android.content.ContentObserver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.ruoyi.app.feature.lawenforcement.viewmodel.LawEnforcementViewModel
import com.ruoyi.code.base.BaseBindingFragment
import com.ruoyi.code.router.TheRouter
import com.ruoyi.code.utils.SelectedUnitManager
import com.ruoyi.code.utils.ToastUtils
import com.ruoyi.code.utils.ActivationManager
import com.ruoyi.ruoyi_app.R
import com.ruoyi.ruoyi_app.databinding.FragmentLawEnforcementBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LawEnforcementFragment : BaseBindingFragment<FragmentLawEnforcementBinding>() {

    private val viewModel: LawEnforcementViewModel by viewModels()

    private var currentPhotoPath: String? = null
    private var currentVideoPath: String? = null
    private var currentAudioPath: String? = null

    // 照片拍摄Launcher
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoPath != null) {
            viewModel.addPhotoToRecord(currentPhotoPath!!, File(currentPhotoPath!!).name, File(currentPhotoPath!!).length())
        }
    }

    // 视频拍摄Launcher
    private val takeVideoLauncher = registerForActivityResult(
        ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && currentVideoPath != null) {
            viewModel.addVideoToRecord(currentVideoPath!!, File(currentVideoPath!!).name, File(currentVideoPath!!).length(), null)
        }
    }

    // 音频录制Launcher (使用系统录音机)
    private val recordAudioLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 录音结果由录音App处理，临时方案
    }

    // 权限请求Launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (!allGranted) {
            ToastUtils.showShort("需要相关权限才能使用此功能")
        }
    }

    override fun initView() {
        setupClickListeners()
        observeViewModel()
        loadSelectedUnit()
    }

    override fun initData() {
        // 初始化
    }

    private fun setupClickListeners() {
        // 拍照
        binding.btnCamera.setOnClickListener {
            if (checkActivation()) {
                checkPermissionsAndTakePhoto()
            }
        }

        // 录音
        binding.btnAudio.setOnClickListener {
            if (checkActivation()) {
                checkPermissionsAndRecord()
            }
        }

        // 录像
        binding.btnVideo.setOnClickListener {
            if (checkActivation()) {
                checkPermissionsAndTakeVideo()
            }
        }

        // 导航
        binding.btnNavigation.setOnClickListener {
            if (checkActivation()) {
                openNavigation()
            }
        }

        // 记录列表
        binding.btnRecords.setOnClickListener {
            if (checkActivation()) {
                // 跳转到记录列表
                TheRouter.build(Constant.recordListRoute).navigation()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.error.observe(this) { error ->
            ToastUtils.showShort(error)
        }

        viewModel.operationResult.observe(this) { result ->
            when (result) {
                is LawEnforcementViewModel.OperationResult.Success -> {
                    ToastUtils.showShort(result.message)
                }
                is LawEnforcementViewModel.OperationResult.Error -> {
                    ToastUtils.showShort(result.message)
                }
            }
        }
    }

    private fun loadSelectedUnit() {
        val unit = SelectedUnitManager.getSelectedUnit()
        if (unit != null) {
            viewModel.setSelectedUnit(
                unitId = unit.unitId,
                unitName = unit.unitName,
                industryId = unit.industryCategoryId ?: 0,
                industryCode = unit.industryCategoryName ?: ""
            )
        }
    }

    private fun checkActivation(): Boolean {
        // 检查设备激活状态
        val activationManager = ActivationManager.getInstance()
        if (!activationManager.isActivated()) {
            ToastUtils.showShort("请先激活设备")
            // 可选：跳转到激活页面
            // TheRouter.build("/activation").navigation()
            return false
        }
        return true
    }

    private fun checkPermissionsAndTakePhoto() {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            takePhoto()
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    private fun takePhoto() {
        val photoFile = createImageFile()
        currentPhotoPath = photoFile.absolutePath

        val photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )

        takePictureLauncher.launch(photoUri)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
        val storageDir = requireContext().getExternalFilesDir("photos")
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun checkPermissionsAndTakeVideo() {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            takeVideo()
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    private fun takeVideo() {
        val videoFile = createVideoFile()
        currentVideoPath = videoFile.absolutePath

        val videoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            videoFile
        )

        takeVideoLauncher.launch(videoUri)
    }

    private fun createVideoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
        val storageDir = requireContext().getExternalFilesDir("videos")
        return File.createTempFile("MP4_${timeStamp}_", ".mp4", storageDir)
    }

    private fun checkPermissionsAndRecord() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            recordAudio()
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    private fun recordAudio() {
        // 启动系统录音机
        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            recordAudioLauncher.launch(intent)
        } else {
            ToastUtils.showShort("未找到录音应用")
        }
        // 注意：系统录音机无法返回文件路径
        // 替代方案：监听 ContentObserver 检测新录音文件（见下方代码）
    }

    // ContentObserver 用于监听新录音文件
    private var audioObserver: ContentObserver? = null

    private fun registerAudioObserver() {
        audioObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                // 查询最新创建的录音文件
                queryLatestAudioFile()?.let { filePath ->
                    viewModel.addAudioToRecord(filePath, File(filePath).name, File(filePath).length(), null)
                }
            }
        }
        requireContext().contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            audioObserver!!
        )
    }

    private fun queryLatestAudioFile(): String? {
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        val cursor = requireContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.Audio.Media.DATA)
                if (columnIndex != -1) {
                    return it.getString(columnIndex)
                }
            }
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        audioObserver?.let {
            requireContext().contentResolver.unregisterContentObserver(it)
        }
    }

    private fun openNavigation() {
        val unit = SelectedUnitManager.getSelectedUnit()
        if (unit == null) {
            ToastUtils.showShort("请先选择执法单位")
            return
        }

        // 检查经纬度
        if (unit.latitude == null || unit.longitude == null) {
            ToastUtils.showShort("该单位暂无位置信息")
            return
        }

        // 检测高德地图
        val gaodeInstalled = isAppInstalled("com.autonavi.minimap")
        if (gaodeInstalled) {
            // 调用高德地图导航
            val uri = Uri.parse("androidamap://route?sourceApplication=appname&dlat=${unit.latitude}&dlon=${unit.longitude}&dname=${Uri.encode(unit.unitName)}&dev=0&t=1")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.autonavi.minimap")
            startActivity(intent)
        } else {
            // 使用浏览器打开
            val url = "https://uri.amap.com/navigation?to=${unit.longitude},${unit.latitude},${Uri.encode(unit.unitName)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            requireContext().packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        const val TAG = "LawEnforcementFragment"
    }
}
```

- [ ] **Step 2: 提交代码**

```bash
git add app/src/main/java/com/ruoyi/app/feature/lawenforcement/ui/LawEnforcementFragment.kt
git commit -m "feat(lawenforcement): 添加便捷执法入口 Fragment"
```

---

## Task 8: 创建记录列表 Activity

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/feature/lawenforcement/ui/RecordListActivity.kt`
- Create: `app/src/main/java/com/ruoyi/app/feature/lawenforcement/ui/adapter/RecordListAdapter.kt`

- [ ] **Step 1: 创建 RecordListAdapter.kt**

```kotlin
package com.ruoyi.app.feature.lawenforcement.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.ruoyi_app.databinding.ItemEnforcementRecordBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordListAdapter(
    private val onItemClick: (EnforcementRecordEntity) -> Unit,
    private val onEditClick: (EnforcementRecordEntity) -> Unit,
    private val onSubmitClick: (EnforcementRecordEntity) -> Unit,
    private val onDeleteClick: (EnforcementRecordEntity) -> Unit
) : ListAdapter<EnforcementRecordEntity, RecordListAdapter.ViewHolder>(DiffCallback()) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEnforcementRecordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemEnforcementRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            binding.btnEdit.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClick(getItem(position))
                }
            }

            binding.btnSubmit.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSubmitClick(getItem(position))
                }
            }

            binding.btnDelete.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(getItem(position))
                }
            }
        }

        fun bind(record: EnforcementRecordEntity) {
            binding.tvUnitName.text = record.unitName
            binding.tvIndustry.text = record.industryCode
            binding.tvCreateTime.text = dateFormat.format(Date(record.createTime))

            // 状态显示
            val statusText = when (record.recordStatus) {
                RecordStatus.DRAFT -> "待上报"
                RecordStatus.SUBMITTED -> "已上报"
                RecordStatus.APPROVED -> "已审核"
                RecordStatus.REJECTED -> "已驳回"
                else -> record.recordStatus
            }
            binding.tvStatus.text = statusText

            // 状态颜色
            val statusColor = when (record.recordStatus) {
                RecordStatus.DRAFT -> android.graphics.Color.parseColor("#FF9800")
                RecordStatus.SUBMITTED -> android.graphics.Color.parseColor("#2196F3")
                RecordStatus.APPROVED -> android.graphics.Color.parseColor("#4CAF50")
                RecordStatus.REJECTED -> android.graphics.Color.parseColor("#F44336")
                else -> android.graphics.Color.GRAY
            }
            binding.statusIndicator.setBackgroundColor(statusColor)

            // 证据统计显示
            binding.tvPhotoCount.text = "${record.photoCount}"
            binding.tvAudioCount.text = "${record.audioCount}"
            binding.tvVideoCount.text = "${record.videoCount}"

            // 操作按钮可见性
            binding.btnSubmit.visibility = if (record.recordStatus == RecordStatus.DRAFT) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<EnforcementRecordEntity>() {
        override fun areItemsTheSame(oldItem: EnforcementRecordEntity, newItem: EnforcementRecordEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EnforcementRecordEntity, newItem: EnforcementRecordEntity) =
            oldItem == newItem
    }
}
```

- [ ] **Step 2: 创建 RecordListActivity.kt**

```kotlin
package com.ruoyi.app.feature.lawenforcement.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.app.feature.lawenforcement.ui.adapter.RecordListAdapter
import com.ruoyi.app.feature.lawenforcement.viewmodel.RecordListViewModel
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.router.TheRouter
import com.ruoyi.code.utils.ToastUtils
import com.ruoyi.code.widget.OnTitleBarListener
import com.ruoyi.code.widget.TitleBar
import com.ruoyi.ruoyi_app.R
import com.ruoyi.ruoyi_app.databinding.ActivityRecordListBinding
import java.util.Calendar

class RecordListActivity : BaseBindingActivity<ActivityRecordListBinding>() {

    private val viewModel: RecordListViewModel by viewModels()
    private lateinit var adapter: RecordListAdapter

    override fun initView() {
        setupTitleBar()
        setupRecyclerView()
        setupFilters()
        observeViewModel()
    }

    override fun initData() {
        viewModel.loadRecords()
    }

    private fun setupTitleBar() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = RecordListAdapter(
            onItemClick = { record ->
                // 跳转到详情
                val bundle = Bundle().apply {
                    putLong("record_id", record.id)
                }
                TheRouter.build(Constant.recordDetailRoute).with(bundle).navigation()
            },
            onEditClick = { record ->
                // 编辑
                val bundle = Bundle().apply {
                    putLong("record_id", record.id)
                }
                TheRouter.build(Constant.recordEditRoute).with(bundle).navigation()
            },
            onSubmitClick = { record ->
                // 上报确认
                AlertDialog.Builder(this)
                    .setTitle("确认上报")
                    .setMessage("确定要上报这条执法记录吗？")
                    .setPositiveButton("确定") { _, _ ->
                        viewModel.submitRecord(record.id)
                    }
                    .setNegativeButton("取消", null)
                    .show()
            },
            onDeleteClick = { record ->
                // 删除确认
                AlertDialog.Builder(this)
                    .setTitle("确认删除")
                    .setMessage("确定要删除这条执法记录吗？")
                    .setPositiveButton("确定") { _, _ ->
                        viewModel.deleteRecord(record.id)
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        )

        binding.recyclerview.adapter = adapter
    }

    private fun setupFilters() {
        // 状态下拉框
        val statusOptions = listOf("全部", "待上报", "已上报", "已审核", "已驳回")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusOptions)
        binding.spinnerStatus.adapter = statusAdapter
        binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val status = when (position) {
                    0 -> null
                    1 -> RecordStatus.DRAFT
                    2 -> RecordStatus.SUBMITTED
                    3 -> RecordStatus.APPROVED
                    4 -> RecordStatus.REJECTED
                    else -> null
                }
                viewModel.filterByStatus(status)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 单位下拉框（简化版，暂无单位筛选）
        val unitOptions = listOf("全部单位")
        val unitAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unitOptions)
        binding.spinnerUnit.adapter = unitAdapter

        // 日期筛选
        binding.btnDateFilter.setOnClickListener {
            showDateRangePicker()
        }

    private fun showDateRangePicker() {
        val startCalendar = Calendar.getInstance()
        val endCalendar = Calendar.getInstance()

        // 选择开始日期
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            startCalendar.set(year, month, dayOfMonth, 0, 0, 0)
            // 选择结束日期
            DatePickerDialog(this, { _, year2, month2, dayOfMonth2 ->
                endCalendar.set(year2, month2, dayOfMonth2, 23, 59, 59)
                val startTime = startCalendar.timeInMillis
                val endTime = endCalendar.timeInMillis
                viewModel.filterByDateRange(startTime, endTime)
            }, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show()
    }
    }

    private fun observeViewModel() {
        viewModel.records.observe(this) { records ->
            adapter.submitList(records)
            binding.emptyView.visibility = if (records.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerview.visibility = if (records.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            ToastUtils.showShort(error)
        }

        viewModel.operationResult.observe(this) { result ->
            ToastUtils.showShort(result)
        }
    }
}
```

- [ ] **Step 3: 提交代码**

```bash
git add app/src/main/java/com/ruoyi/app/feature/lawenforcement/ui/RecordListActivity.kt
git add app/src/main/java/com/ruoyi/app/feature/lawenforcement/ui/adapter/RecordListAdapter.kt
git commit -m "feat(lawenforcement): 添加记录列表 Activity 和 Adapter"
```

---

## Task 9: 创建记录详情 Activity

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/feature/lawenforcement/ui/RecordDetailActivity.kt`
- Create: `app/src/main/java/com/ruoyi/app/feature/lawenforcement/ui/adapter/EvidenceAdapter.kt`

- [ ] **Step 1: 创建 EvidenceAdapter.kt**

```kotlin
package com.ruoyi.app.feature.lawenforcement.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ruoyi.app.data.database.entity.EvidenceMaterialEntity
import com.ruoyi.app.feature.lawenforcement.model.EvidenceType
import com.ruoyi.ruoyi_app.R
import com.ruoyi.ruoyi_app.databinding.ItemEvidenceMaterialBinding
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

class EvidenceAdapter(
    private val onItemClick: (EvidenceMaterialEntity) -> Unit
) : ListAdapter<EvidenceMaterialEntity, EvidenceAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEvidenceMaterialBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemEvidenceMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(material: EvidenceMaterialEntity) {
            when (material.evidenceType) {
                EvidenceType.PHOTO -> {
                    // 照片显示缩略图
                    Glide.with(binding.ivThumbnail)
                        .load(File(material.filePath))
                        .centerCrop()
                        .into(binding.ivThumbnail)
                    binding.ivPlayIcon.visibility = View.GONE
                    binding.tvDuration.visibility = View.GONE
                }
                EvidenceType.AUDIO -> {
                    // 录音显示播放图标
                    binding.ivThumbnail.setImageResource(R.drawable.ic_audio_placeholder)
                    binding.ivPlayIcon.visibility = View.VISIBLE
                    material.duration?.let { duration ->
                        binding.tvDuration.visibility = View.VISIBLE
                        binding.tvDuration.text = formatDuration(duration)
                    }
                }
                EvidenceType.VIDEO -> {
                    // 视频显示缩略图和播放图标
                    Glide.with(binding.ivThumbnail)
                        .load(File(material.filePath))
                        .centerCrop()
                        .into(binding.ivThumbnail)
                    binding.ivPlayIcon.visibility = View.VISIBLE
                    material.duration?.let { duration ->
                        binding.tvDuration.visibility = View.VISIBLE
                        binding.tvDuration.text = formatDuration(duration)
                    }
                }
            }
        }

        private fun formatDuration(seconds: Int): String {
            val minutes = seconds / 60
            val secs = seconds % 60
            return String.format(Locale.CHINA, "%02d:%02d", minutes, secs)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<EvidenceMaterialEntity>() {
        override fun areItemsTheSame(oldItem: EvidenceMaterialEntity, newItem: EvidenceMaterialEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EvidenceMaterialEntity, newItem: EvidenceMaterialEntity) =
            oldItem == newItem
    }
}
```

- [ ] **Step 2: 创建 RecordDetailActivity.kt**

```kotlin
package com.ruoyi.app.feature.lawenforcement.ui

import android.content.Intent
import android.os.Bundle
import android.widget.MediaController
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.app.feature.lawenforcement.ui.adapter.EvidenceAdapter
import com.ruoyi.app.feature.lawenforcement.viewmodel.RecordDetailViewModel
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.router.TheRouter
import com.ruoyi.code.utils.ToastUtils
import com.ruoyi.code.widget.OnTitleBarListener
import com.ruoyi.code.widget.TitleBar
import com.ruoyi.ruoyi_app.R
import com.ruoyi.ruoyi_app.databinding.ActivityRecordDetailBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordDetailActivity : BaseBindingActivity<ActivityRecordDetailBinding>() {

    private val viewModel: RecordDetailViewModel by viewModels()
    private lateinit var evidenceAdapter: EvidenceAdapter

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    override fun initView() {
        setupTitleBar()
        setupRecyclerView()
        setupButtons()
        observeViewModel()

        // 获取记录ID并加载
        val recordId = intent.getLongExtra("record_id", -1L)
        if (recordId != -1L) {
            viewModel.loadRecord(recordId)
        } else {
            ToastUtils.showShort("记录不存在")
            finish()
        }
    }

    override fun initData() {
        // 初始化
    }

    private fun setupTitleBar() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
    }

    private fun setupRecyclerView() {
        evidenceAdapter = EvidenceAdapter { material ->
            // 点击证据材料
            when (material.evidenceType) {
                "photo" -> {
                    // 预览照片
                    previewPhoto(material.filePath)
                }
                "audio" -> {
                    // 播放录音
                    playAudio(material.filePath)
                }
                "video" -> {
                    // 播放视频
                    playVideo(material.filePath)
                }
            }
        }

        binding.rvEvidence.adapter = evidenceAdapter
    }

    private fun setupButtons() {
        binding.btnSubmit.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("确认上报")
                .setMessage("确定要上报这条执法记录吗？")
                .setPositiveButton("确定") { _, _ ->
                    viewModel.submitRecord()
                }
                .setNegativeButton("取消", null)
                .show()
        }

        binding.btnEdit.setOnClickListener {
            val record = viewModel.record.value ?: return@setOnClickListener
            val bundle = Bundle().apply {
                putLong("record_id", record.id)
            }
            TheRouter.build(Constant.recordEditRoute).with(bundle).navigation()
        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除这条执法记录吗？")
                .setPositiveButton("确定") { _, _ ->
                    val record = viewModel.record.value ?: return@setPositiveButton
                    viewModel.deleteRecord(record.id)
                    finish()
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.record.observe(this) { record ->
            if (record != null) {
                binding.tvRecordNo.text = record.recordNo
                binding.tvUnitName.text = record.unitName
                binding.tvIndustry.text = record.industryCode
                binding.tvCreateTime.text = dateFormat.format(Date(record.createTime))

                val statusText = when (record.recordStatus) {
                    RecordStatus.DRAFT -> "待上报"
                    RecordStatus.SUBMITTED -> "已上报"
                    RecordStatus.APPROVED -> "已审核"
                    RecordStatus.REJECTED -> "已驳回"
                    else -> record.recordStatus
                }
                binding.tvStatus.text = statusText

                // 上报按钮可见性
                binding.btnSubmit.visibility = if (record.recordStatus == RecordStatus.DRAFT) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
            }
        }

        viewModel.evidenceMaterials.observe(this) { materials ->
            evidenceAdapter.submitList(materials)

            // 更新统计
            val stats = viewModel.getEvidenceStats()
            binding.tvPhotoCount.text = "${stats.photoCount} 张照片"
            binding.tvAudioCount.text = "${stats.audioCount} 条录音"
            binding.tvVideoCount.text = "${stats.videoCount} 条录像"
        }

        viewModel.error.observe(this) { error ->
            ToastUtils.showShort(error)
        }

        viewModel.operationResult.observe(this) { result ->
            ToastUtils.showShort(result)
            if (result == "上报成功") {
                // 刷新数据
                val recordId = intent.getLongExtra("record_id", -1L)
                viewModel.loadRecord(recordId)
            }
        }
    }

    private fun previewPhoto(filePath: String) {
        // 使用系统图片查看器
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val file = File(filePath)
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this@RecordDetailActivity,
                "${packageName}.fileprovider",
                file
            )
            setDataAndType(uri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    private fun playAudio(filePath: String) {
        // 使用系统音乐播放器
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val file = File(filePath)
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this@RecordDetailActivity,
                "${packageName}.fileprovider",
                file
            )
            setDataAndType(uri, "audio/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    private fun playVideo(filePath: String) {
        // 使用系统视频播放器
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val file = File(filePath)
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this@RecordDetailActivity,
                "${packageName}.fileprovider",
                file
            )
            setDataAndType(uri, "video/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }
}
```

- [ ] **Step 3: 提交代码**

```bash
git add app/src/main/java/com/ruoyi/app/feature/lawenforcement/ui/RecordDetailActivity.kt
git add app/src/main/java/com/ruoyi/app/feature/lawenforcement/ui/adapter/EvidenceAdapter.kt
git commit -m "feat(lawenforcement): 添加记录详情 Activity 和证据 Adapter"
```

---

## Task 10: 添加路由常量

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/model/Constant.kt`

- [ ] **Step 1: 更新 Constant.kt**

在常量文件中添加路由常量：

```kotlin
object Constant {
    // ... 现有常量

    // 便捷执法模块
    const val recordListRoute = "/lawenforcement/record/list"
    const val recordDetailRoute = "/lawenforcement/record/detail"
    const val recordEditRoute = "/lawenforcement/record/edit"
}
```

- [ ] **Step 2: 注册路由（参考现有实现）**

在 TheRouter 初始化处添加路由注册（参考其他 Activity 的注册方式）

- [ ] **Step 3: 提交代码**

```bash
git add app/src/main/java/com/ruoyi/app/model/Constant.kt
git commit -m "feat(lawenforcement): 添加路由常量"
```

---

## Task 11: 集成到 SyncManager（可选）

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/sync/SyncManager.kt`

- [ ] **Step 1: 添加同步模块常量**

```kotlin
const val MODULE_ENFORCEMENT_RECORD = "执法记录"
const val MODULE_EVIDENCE_MATERIAL = "证据材料"
```

- [ ] **Step 2: 添加到 FULL_SYNC_MODULES**

```kotlin
val FULL_SYNC_MODULES = listOf(
    // ... 现有模块
    MODULE_ENFORCEMENT_RECORD,
    MODULE_EVIDENCE_MATERIAL
)
```

- [ ] **Step 3: 实现同步逻辑**

在 syncModule 方法中添加执法记录和证据材料的同步逻辑

- [ ] **Step 4: 提交代码**

```bash
git add app/src/main/java/com/ruoyi/app/sync/SyncManager.kt
git commit -m "feat(lawenforcement): 集成到 SyncManager"
```

---

## 实施顺序

1. Task 1: 创建 Entity
2. Task 2: 创建 DAO
3. Task 3: 更新 AppDatabase
4. Task 4: 创建 Repository
5. Task 5: 创建 ViewModel
6. Task 6: 创建布局文件
7. Task 7: 创建 Fragment
8. Task 8: 创建 RecordListActivity
9. Task 9: 创建 RecordDetailActivity
10. Task 10: 添加路由常量
11. Task 11: 集成 SyncManager（可选）

---

## 验收标准

- [ ] Entity、DAO、Repository 创建正确
- [ ] 布局文件显示正确
- [ ] 快捷入口可点击跳转
- [ ] 拍照/录音/录像功能正常
- [ ] 导航功能正常（检测高德地图）
- [ ] 记录列表显示正确
- [ ] 记录详情显示正确
- [ ] 上报/删除功能正常
- [ ] 离线模式下数据正确保存
