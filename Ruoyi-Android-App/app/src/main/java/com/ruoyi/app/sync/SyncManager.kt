package com.ruoyi.app.sync

import android.content.Context
import android.util.Log
import com.ruoyi.app.api.repository.CategoryRepository
import com.ruoyi.app.api.repository.UnitRepository
import com.ruoyi.app.data.database.entity.UnitEntity
import com.ruoyi.app.feature.document.repository.DocumentRepository
import com.ruoyi.app.feature.normative.repository.NormativeRepository
import com.ruoyi.app.feature.regulatory.repository.RegulatoryRepository
import com.ruoyi.app.sync.model.SyncProgress
import com.ruoyi.app.sync.model.SyncResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay

class SyncManager private constructor() {

    private val _progress = MutableStateFlow(SyncProgress.initial())
    val progress: StateFlow<SyncProgress> = _progress.asStateFlow()

    private val _result = MutableStateFlow<SyncResult?>(null)
    val result: StateFlow<SyncResult?> = _result.asStateFlow()

    private var isCancelled = false
    private var retryCount = 0
    private val maxRetries = 3

    companion object {
        @Volatile
        private var INSTANCE: SyncManager? = null

        fun getInstance(): SyncManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SyncManager().also { INSTANCE = it }
            }
        }

        // 同步模块定义
        const val MODULE_USER_PERMISSION = "用户权限"
        const val MODULE_INDUSTRY_CATEGORY = "行业分类"
        const val MODULE_UNIT = "执法单位"
        const val MODULE_NORMATIVE = "规范用语"
        const val MODULE_REGULATORY = "监管事项"
        const val MODULE_DOCUMENT_CATEGORY = "文书分类"
        const val MODULE_DOCUMENT_TEMPLATE = "文书模板"
        const val MODULE_MEDIA_FILE = "媒体文件"
        const val MODULE_ENFORCEMENT_RECORD = "执法记录"
        const val MODULE_EVIDENCE_MATERIAL = "证据材料"

        // 全量同步模块列表（阶段二）
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
    }

    fun reset() {
        isCancelled = false
        retryCount = 0
        _progress.value = SyncProgress.initial()
        _result.value = null
    }

    fun cancel() {
        isCancelled = true
        _result.value = SyncResult.cancelled()
    }

    /**
     * 执行全量同步（阶段二）
     * 包含所有模块，强制等待完成
     */
    suspend fun syncAll(context: Context? = null): SyncResult {
        reset()

        val totalSteps = FULL_SYNC_MODULES.size

        for ((index, module) in FULL_SYNC_MODULES.withIndex()) {
            if (isCancelled) return SyncResult.cancelled()

            _progress.value = _progress.value.copy(
                currentModule = "同步中：$module",
                currentStep = index + 1,
                totalSteps = totalSteps,
                progressPercent = ((index + 1) * 100) / totalSteps
            )

            // 调用同步模块
            val moduleResult = syncModule(context, module)
            if (!moduleResult) {
                // 模块同步失败，指数退避重试
                repeat(maxRetries) { retryIndex ->
                    if (isCancelled) return SyncResult.cancelled()
                    retryCount++
                    val delayMs = (1 shl retryIndex) * 1000L // 1s, 2s, 4s
                    delay(delayMs)
                    val retryResult = syncModule(context, module)
                    if (retryResult) return@repeat
                }
                // 重试全部失败
                _result.value = SyncResult.maxRetriesExceeded(module)
                return SyncResult.maxRetriesExceeded(module)
            }
        }

        _progress.value = _progress.value.copy(
            progressPercent = 100,
            currentModule = "同步完成"
        )
        _result.value = SyncResult.success()
        return SyncResult.success()
    }

    /**
     * 执行增量同步（阶段三）
     * 只同步有变更的模块
     */
    suspend fun syncIncremental(context: Context): SyncResult {
        reset()

        // TODO: 检查各模块是否有更新
        // val modulesToSync = checkUpdatedModules(context)
        // if (modulesToSync.isEmpty()) return SyncResult.success()

        // 暂时不做增量，等后端接口开发后完善
        return SyncResult.success()
    }

    private suspend fun syncModule(context: Context?, module: String): Boolean {
        return try {
            when (module) {
                MODULE_USER_PERMISSION -> syncUserPermissions()
                MODULE_INDUSTRY_CATEGORY -> syncIndustryCategory(context)
                MODULE_UNIT -> syncUnit(context)
                MODULE_NORMATIVE -> syncNormative(context)
                MODULE_REGULATORY -> syncRegulatory(context)
                MODULE_DOCUMENT_CATEGORY -> syncDocumentCategory(context)
                MODULE_DOCUMENT_TEMPLATE -> syncDocumentTemplate(context)
                MODULE_MEDIA_FILE -> syncMediaFile(context)
                MODULE_ENFORCEMENT_RECORD -> syncEnforcementRecord(context)
                MODULE_EVIDENCE_MATERIAL -> syncEvidenceMaterial(context)
                else -> true
            }
        } catch (e: Exception) {
            Log.e("SyncManager", "同步模块[$module]异常: ${e.message}", e)
            false
        }
    }

    // ==================== 各模块同步方法 ====================
    // TODO: 等后端接口开发后完善具体实现

    private suspend fun syncUserPermissions(): Boolean {
        // 用户权限同步（阶段一已预加载，阶段二确认同步完成）
        // 这里主要确认本地数据完整性
        delay(300) // 模拟网络请求
        return true
    }

    private suspend fun syncIndustryCategory(context: Context?): Boolean {
        if (context == null) return false
        return try {
            val repository = CategoryRepository(context)
            val result = repository.syncCategoriesFromServer()
            if (result.isFailure) {
                Log.e("SyncManager", "行业分类同步失败: ${result.exceptionOrNull()?.message}", result.exceptionOrNull())
            }
            result.isSuccess
        } catch (e: Exception) {
            Log.e("SyncManager", "行业分类同步异常: ${e.message}", e)
            false
        }
    }

    private suspend fun syncUnit(context: Context?): Boolean {
        if (context == null) return false
        return try {
            val repository = UnitRepository(context)

            // 1. 先上传本地待同步单位（避免被服务器数据覆盖）
            val pendingUnits = repository.getPendingUnits()
            Log.d("SyncManager", "待上传单位数量: ${pendingUnits.size}")
            for (unit in pendingUnits) {
                val uploadResult = repository.uploadUnitToServer(unit)
                if (uploadResult.isFailure) {
                    Log.e("SyncManager", "上传单位[${unit.unitName}]失败: ${uploadResult.exceptionOrNull()?.message}", uploadResult.exceptionOrNull())
                }
                if (uploadResult.isSuccess) {
                    repository.markAsSynced(unit.unitId)
                }
            }

            // 2. 再拉取服务器数据更新本地
            val pullResult = repository.syncUnitsFromServer()
            if (pullResult.isFailure) {
                Log.e("SyncManager", "拉取单位列表失败: ${pullResult.exceptionOrNull()?.message}", pullResult.exceptionOrNull())
            }

            pullResult.isSuccess
        } catch (e: Exception) {
            Log.e("SyncManager", "执法单位同步异常: ${e.message}", e)
            false
        }
    }

    private suspend fun uploadUnitToServer(context: Context, unit: UnitEntity): Boolean {
        val repository = UnitRepository(context)
        return repository.uploadUnitToServer(unit).isSuccess
    }

    private suspend fun syncNormative(context: Context?): Boolean {
        // 规范用语同步（分类 + 用语 + 关联）
        if (context == null) return false
        return try {
            val repository = NormativeRepository(context)

            // 1. 同步分类
            val categoryResult = repository.syncCategories()
            if (categoryResult.isFailure) {
                Log.e("SyncManager", "规范用语分类同步失败: ${categoryResult.exceptionOrNull()?.message}", categoryResult.exceptionOrNull())
                return false
            }
            Log.d("SyncManager", "syncNormative: 分类同步成功")

            // 2. 同步规范用语
            val languageResult = repository.syncLanguages()
            if (languageResult.isFailure) {
                Log.e("SyncManager", "规范用语同步失败: ${languageResult.exceptionOrNull()?.message}", languageResult.exceptionOrNull())
                return false
            }
            Log.d("SyncManager", "syncNormative: 用语同步成功")

            // 3. 同步监管事项关联
            val matterBindResult = repository.syncMatterBinds()
            if (matterBindResult.isFailure) {
                Log.e("SyncManager", "监管事项关联同步失败: ${matterBindResult.exceptionOrNull()?.message}", matterBindResult.exceptionOrNull())
                return false
            }
            Log.d("SyncManager", "syncNormative: 监管事项关联同步成功")

            // 4. 同步法律条款关联
            val termBindResult = repository.syncTermBinds()
            if (termBindResult.isFailure) {
                Log.e("SyncManager", "法律条款关联同步失败: ${termBindResult.exceptionOrNull()?.message}", termBindResult.exceptionOrNull())
                return false
            }
            Log.d("SyncManager", "syncNormative: 法律条款关联同步成功")

            Log.d("SyncManager", "syncNormative: 全部规范用语数据同步完成")
            true
        } catch (e: Exception) {
            Log.e("SyncManager", "规范用语同步异常: ${e.message}", e)
            false
        }
    }

    private suspend fun syncRegulatory(context: Context?): Boolean {
        // 监管事项同步（事项 + 明细 + 行业分类关联）
        if (context == null) return false
        return try {
            val repository = RegulatoryRepository(context)

            // 1. 同步监管事项
            val matterResult = repository.syncMatters()
            if (matterResult.isFailure) {
                Log.e("SyncManager", "监管事项同步失败: ${matterResult.exceptionOrNull()?.message}", matterResult.exceptionOrNull())
                return false
            }
            Log.d("SyncManager", "syncRegulatory: 监管事项同步成功")

            // 2. 同步监管事项明细
            try {
                val matters = repository.getAllMatters()
                Log.d("SyncManager", "需要同步明细的监管事项数量: ${matters.size}")
                for (matter in matters) {
                    repository.syncItems(matter.matterId)
                    Log.d("SyncManager", "已同步监管事项[${matter.matterId}]的明细")
                }
            } catch (e: Exception) {
                Log.e("SyncManager", "监管事项明细同步异常: ${e.message}", e)
            }

            // 3. 同步行业分类关联
            val categoryBindResult = repository.syncCategoryBinds()
            if (categoryBindResult.isFailure) {
                Log.e("SyncManager", "行业分类关联同步失败: ${categoryBindResult.exceptionOrNull()?.message}", categoryBindResult.exceptionOrNull())
                return false
            }
            Log.d("SyncManager", "syncRegulatory: 行业分类关联同步成功")

            Log.d("SyncManager", "syncRegulatory: 全部监管事项数据同步完成")
            true
        } catch (e: Exception) {
            Log.e("SyncManager", "监管事项同步异常: ${e.message}", e)
            false
        }
    }

    private suspend fun syncDocumentCategory(context: Context?): Boolean {
        // 文书分类同步
        if (context == null) return false
        return try {
            val repository = DocumentRepository(context)
            repository.syncCategories()
            Log.d("SyncManager", "文书分类同步成功")
            true
        } catch (e: Exception) {
            Log.e("SyncManager", "文书分类同步异常: ${e.message}", e)
            false
        }
    }

    private suspend fun syncDocumentTemplate(context: Context?): Boolean {
        // 文书模板同步（不包含分类，分类已单独同步）
        if (context == null) return false
        return try {
            val repository = DocumentRepository(context)
            // 同步模板
            repository.syncTemplates()
            // 同步套组
            repository.syncGroups()
            // 同步模板与行业分类关联
            repository.syncTemplateIndustry()
            true
        } catch (e: Exception) {
            Log.e("SyncManager", "文书模板同步异常: ${e.message}", e)
            false
        }
    }

    private suspend fun syncMediaFile(context: Context?): Boolean {
        // 媒体文件同步
        // TODO: 调用后端 GET /media/file/list
        // 下载文件到本地存储
        delay(1000) // 模拟网络请求
        return true
    }

    private suspend fun syncEnforcementRecord(context: Context?): Boolean {
        // 执法记录同步
        // TODO: 调用后端 API 同步执法记录
        // 从本地数据库读取待同步的记录 (syncStatus = 'PENDING')
        // 上传到服务器
        // 更新同步状态
        delay(500) // 模拟网络请求
        return true
    }

    private suspend fun syncEvidenceMaterial(context: Context?): Boolean {
        // 证据材料同步
        // TODO: 调用后端 API 同步证据材料
        // 从本地数据库读取待同步的证据 (syncStatus = 'PENDING')
        // 上传到服务器
        // 更新同步状态
        delay(500) // 模拟网络请求
        return true
    }
}
