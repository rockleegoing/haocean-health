package com.ruoyi.app.sync

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

    suspend fun syncAll(): SyncResult {
        reset()

        // 阶段二同步步骤（目前只有用户权限，其他预留）
        val modules = listOf("用户权限")
        val totalSteps = modules.size

        for ((index, module) in modules.withIndex()) {
            if (isCancelled) return SyncResult.cancelled()

            _progress.value = _progress.value.copy(
                currentModule = "同步中：$module",
                currentStep = index + 1,
                totalSteps = totalSteps,
                progressPercent = ((index + 1) * 100) / totalSteps
            )

            // 调用同步模块（当前只有用户权限同步）
            val moduleResult = syncModule(module)
            if (!moduleResult) {
                // 模块同步失败，指数退避重试
                repeat(maxRetries) { retryIndex ->
                    if (isCancelled) return SyncResult.cancelled()
                    retryCount++
                    val delayMs = (1 shl retryIndex) * 1000L // 1s, 2s, 4s
                    delay(delayMs)
                    val retryResult = syncModule(module)
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

    private suspend fun syncModule(module: String): Boolean {
        return try {
            when (module) {
                "用户权限" -> syncUserPermissions()
                else -> true // 预留模块暂不实现
            }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun syncUserPermissions(): Boolean {
        // 预留：用户权限同步逻辑
        // 当前阶段二只需要同步用户权限，其他模块预留
        // TODO: 等后端 /getInfo 接口支持后，调用并存储到本地
        return true
    }
}
