package com.ruoyi.app.sync.model

enum class SyncStatus {
    SUCCESS,
    FAILED,
    CANCELLED,
    NETWORK_ERROR,
    MAX_RETRIES_EXCEEDED
}

data class SyncResult(
    val status: SyncStatus,
    val message: String = "",
    val failedModule: String? = null
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
