package com.ruoyi.app.sync.model

data class SyncProgress(
    val currentModule: String,
    val currentStep: Int,
    val totalSteps: Int,
    val progressPercent: Int,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val estimatedRemainingSeconds: Int
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
