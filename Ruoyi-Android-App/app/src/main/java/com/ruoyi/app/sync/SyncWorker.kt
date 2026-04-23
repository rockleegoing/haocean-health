package com.ruoyi.app.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ruoyi.app.sync.model.SyncStatus
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val syncManager = SyncManager.getInstance()
    private val dataVersionManager = DataVersionManager.getInstance(context)

    override suspend fun doWork(): Result {
        return try {
            // 检查是否需要增量同步
            val hasUpdate = checkForUpdates()

            if (hasUpdate) {
                // 执行增量同步
                val result = syncManager.syncAll()

                when (result.status) {
                    SyncStatus.SUCCESS -> Result.success()
                    SyncStatus.MAX_RETRIES_EXCEEDED -> Result.retry()
                    else -> Result.failure()
                }
            } else {
                // 没有更新，跳过
                Result.success()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private suspend fun checkForUpdates(): Boolean {
        // 预留：检查各模块是否有更新
        // 当前阶段三框架搭建，暂不实现具体检查逻辑
        // 等后端接口开发后完善
        return false
    }

    companion object {
        private const val WORK_NAME = "sync_periodic_work"
        private const val SYNC_INTERVAL_MINUTES = 30L

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                SYNC_INTERVAL_MINUTES, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun cancelScheduled(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
