package com.ruoyi.app.sync.model

enum class SyncStatus {
    SUCCESS,
    FAILED,
    CANCELLED,
    NETWORK_ERROR,
    MAX_RETRIES_EXCEEDED
}

data class SyncResult(
    val status: SyncStatus,    // 同步状态
    val message: String = "",  // 结果消息
    val failedModule: String? = null  // 失败的模块名称
) {
    companion object {
        fun success() = SyncResult(SyncStatus.SUCCESS)
        fun failed(message: String, module: String? = null) =
            SyncResult(SyncStatus.FAILED, message, module)
        fun networkError(module: String? = null) =
            SyncResult(SyncStatus.NETWORK_ERROR, "网络连接失败", module)
        fun maxRetriesExceeded(module: String) =
            SyncResult(SyncStatus.MAX_RETRIES_EXCEEDED, "重试次数超限", module)
        fun cancelled() = SyncResult(SyncStatus.CANCELLED)
    }
}
