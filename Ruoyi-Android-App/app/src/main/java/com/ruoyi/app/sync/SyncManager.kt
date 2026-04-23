package com.ruoyi.app.sync

import android.content.Context
import com.ruoyi.app.sync.model.SyncProgress
import com.ruoyi.app.sync.model.SyncResult
import com.ruoyi.app.sync.model.SyncStatus
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
        const val MODULE_LAW = "法律法规"
        const val MODULE_PHRASE = "规范用语"
        const val MODULE_SUPERVISION = "监管事项"
        const val MODULE_DOCUMENT_TEMPLATE = "文书模板"
        const val MODULE_MEDIA_FILE = "媒体文件"

        // 全量同步模块列表（阶段二）
        val FULL_SYNC_MODULES = listOf(
            MODULE_USER_PERMISSION,
            MODULE_INDUSTRY_CATEGORY,
            MODULE_LAW,
            MODULE_PHRASE,
            MODULE_SUPERVISION,
            MODULE_DOCUMENT_TEMPLATE,
            MODULE_MEDIA_FILE
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
                MODULE_LAW -> syncLaw(context)
                MODULE_PHRASE -> syncPhrase(context)
                MODULE_SUPERVISION -> syncSupervision(context)
                MODULE_DOCUMENT_TEMPLATE -> syncDocumentTemplate(context)
                MODULE_MEDIA_FILE -> syncMediaFile(context)
                else -> true
            }
        } catch (e: Exception) {
            false
        }
    }

    // ==================== 各模块同步方法 ====================
    // TODO: 等后端接口开发后完善具体实现

    private suspend fun syncUserPermissions(): Boolean {
        // 用户权限同步（阶段一已预加载，阶段二确认同步完成）
        // 这里主要确认本地数据完整性
        return true
    }

    private suspend fun syncIndustryCategory(context: Context?): Boolean {
        // 行业分类同步
        // TODO: 调用后端 GET /industry/category/list
        // 存储到 IndustryCategoryEntity
        return true
    }

    private suspend fun syncLaw(context: Context?): Boolean {
        // 法律法规同步
        // TODO: 调用后端 GET /law/regulation/list
        // 存储到 law_regulation 表
        return true
    }

    private suspend fun syncPhrase(context: Context?): Boolean {
        // 规范用语同步
        // TODO: 调用后端 GET /phrase/library/list
        // 存储到 phrase_library 表
        return true
    }

    private suspend fun syncSupervision(context: Context?): Boolean {
        // 监管事项同步
        // TODO: 调用后端 GET /supervision/item/list
        // 存储到 supervision_item 表
        return true
    }

    private suspend fun syncDocumentTemplate(context: Context?): Boolean {
        // 文书模板同步
        // TODO: 调用后端 GET /document/template/list
        // 存储到 document_template 表
        return true
    }

    private suspend fun syncMediaFile(context: Context?): Boolean {
        // 媒体文件同步
        // TODO: 调用后端 GET /media/file/list
        // 下载文件到本地存储
        return true
    }
}
