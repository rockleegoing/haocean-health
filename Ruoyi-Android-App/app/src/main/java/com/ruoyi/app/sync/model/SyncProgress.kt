package com.ruoyi.app.sync.model

data class SyncProgress(
    val currentModule: String,              // 当前同步模块名称
    val currentStep: Int,                   // 当前步骤序号
    val totalSteps: Int,                   // 总步骤数
    val progressPercent: Int,               // 进度百分比（0-100）
    val downloadedBytes: Long,              // 已下载字节数
    val totalBytes: Long,                  // 总字节数
    val estimatedRemainingSeconds: Int     // 预计剩余秒数
) {
    companion object {
        fun initial() = SyncProgress(
            currentModule = "等待开始",
            currentStep = 0,
            totalSteps = 1,
            progressPercent = 0,
            downloadedBytes = 0,
            totalBytes = 0,
            estimatedRemainingSeconds = 0
        )
    }
}
