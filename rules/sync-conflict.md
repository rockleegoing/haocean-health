# 数据同步与冲突处理规范

## 1. 同步架构

### 1.1 同步方向

```
┌─────────────┐                         ┌─────────────┐
│   App 端      │                         │   后台       │
│  (SQLite)   │                         │  (MySQL)    │
└─────────────┘                         └─────────────┘
       │                                       │
       │  ───────────────────────────────────► │  上行同步
       │         (执法记录、单位变更)            │
       │                                       │
       │  ◄─────────────────────────────────── │  下行同步
       │         (法律法规、模板、公告)          │
       │                                       │
       │  ───────────────────────────────────► │  双向同步
       │         (执法单位信息)                  │
       └                                       └
```

### 1.2 数据分类与同步策略

| 数据类型 | 同步方向 | 冲突策略 | 说明 |
|---------|---------|---------|------|
| 法律法规 | 下行 | 后台为准 | 基础数据，App 只读 |
| 规范用语 | 下行 | 后台为准 | 基础数据，App 只读 |
| 监管事项 | 下行 | 后台为准 | 基础数据，App 只读 |
| 文书模板 | 下行 | 后台为准 | 后台配置，App 下载 |
| 执法单位 | 双向 | 用户选择 | 可能多地修改 |
| 执法记录 | 上行 | 本地为准 | 现场采集数据 |
| 通知公告 | 下行 | 后台为准 | 后台发布 |
| 用户信息 | 双向 | 时间戳新 | 个人资料 |

## 2. 同步队列表设计

### 2.1 表结构

```sql
CREATE TABLE t_sync_queue (
    id              BIGINT PRIMARY KEY AUTOINCREMENT,
    table_name      VARCHAR(64) NOT NULL,     -- 表名
    record_id       BIGINT NOT NULL,          -- 记录 ID
    sync_type       VARCHAR(16) NOT NULL,     -- CREATE/UPDATE/DELETE/DOWNLOAD
    sync_status     VARCHAR(16) DEFAULT 'PENDING',
    priority        INT DEFAULT 0,            -- 优先级 (0 普通 1 紧急)
    retry_count     INT DEFAULT 0,
    max_retry       INT DEFAULT 3,
    last_retry_time DATETIME,
    next_retry_time DATETIME,
    request_data    TEXT,                     -- JSON 格式请求数据
    error_message   TEXT,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX idx_sync_status ON t_sync_queue(sync_status);
CREATE INDEX idx_next_retry ON t_sync_queue(next_retry_time);
CREATE INDEX idx_priority ON t_sync_queue(priority DESC);
```

### 2.2 同步类型

```kotlin
enum class SyncType(val value: String) {
    CREATE("CREATE"),     // 新增
    UPDATE("UPDATE"),     // 更新
    DELETE("DELETE"),     // 删除
    DOWNLOAD("DOWNLOAD")  // 下载 (后台数据下行)
}
```

### 2.3 同步状态

```kotlin
enum class SyncStatus(val value: String) {
    PENDING("PENDING"),    // 待同步
    PROCESSING("PROCESSING"), // 同步中
    SUCCESS("SUCCESS"),    // 同步成功
    FAILED("FAILED"),      // 同步失败
    CONFLICT("CONFLICT")   // 数据冲突
}
```

## 3. 同步时机

### 3.1 触发时机

```kotlin
object SyncScheduler {

    // 1. 登录后全量同步
    fun onLoginSuccess() {
        // 触发下行同步：法律法规、规范用语、监管事项、文书模板
        // 触发双向同步：执法单位
    }

    // 2. 网络恢复同步
    fun onNetworkAvailable() {
        // 检查待同步队列，继续执行
    }

    // 3. 手动下拉刷新
    fun onManualRefresh() {
        // 触发增量同步
    }

    // 4. 定时同步 (可配置)
    fun onScheduledSync() {
        // 后台定时同步
    }

    // 5. 数据变更时
    fun onDataChanged() {
        // 将变更写入同步队列
    }
}
```

### 3.2 网络监听

```kotlin
class NetworkChangeListener : ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network) {
        // 网络可用，触发同步
        SyncScheduler.onNetworkAvailable()
    }

    override fun onLost(network: Network) {
        // 网络丢失，暂停同步
        SyncManager.pause()
    }
}
```

## 4. 冲突检测机制

### 4.1 版本号机制

```sql
-- 所有需要同步的表添加 version 字段
ALTER TABLE t_unit_info ADD COLUMN version INT DEFAULT 1;
```

```kotlin
@Entity(tableName = "t_unit_info")
data class UnitInfo(
    val id: Long,
    val unitName: String,
    val version: Int = 1,  // 版本号
    val updateTime: Long
)
```

### 4.2 冲突检测逻辑

```kotlin
class ConflictDetector {

    /**
     * 检测数据冲突
     * @param local 本地数据
     * @param remote 远程数据
     * @return 是否存在冲突
     */
    fun hasConflict(local: UnitInfo?, remote: UnitInfo): Boolean {
        if (local == null) return false  // 本地不存在，无冲突

        // 版本号比较
        if (local.version != remote.version) {
            return true
        }

        // 时间戳比较 (版本号相同时)
        if (local.updateTime != remote.updateTime) {
            return true
        }

        return false
    }

    /**
     * 获取冲突详情
     */
    fun getConflictDetails(local: UnitInfo, remote: UnitInfo): ConflictInfo {
        return ConflictInfo(
            table = "t_unit_info",
            recordId = local.id,
            localVersion = local.version,
            remoteVersion = remote.version,
            localUpdateTime = local.updateTime,
            remoteUpdateTime = remote.updateTime,
            conflictingFields = findDifferentFields(local, remote)
        )
    }
}
```

### 4.3 冲突信息数据类

```kotlin
data class ConflictInfo(
    val table: String,
    val recordId: Long,
    val localVersion: Int,
    val remoteVersion: Int,
    val localUpdateTime: Long,
    val remoteUpdateTime: Long,
    val conflictingFields: List<String>
)
```

## 5. 冲突解决策略

### 5.1 策略定义

```kotlin
enum class ConflictResolutionStrategy {
    USE_REMOTE,     // 以后台为准
    USE_LOCAL,      // 以本地为准
    USE_NEWER,      // 以时间戳新的为准
    USER_CHOOSE     // 用户选择
}
```

### 5.2 各数据类型策略

```kotlin
object ConflictResolutionManager {

    private val strategyMap = mapOf(
        "t_law_book" to ConflictResolutionStrategy.USE_REMOTE,      // 法律法规
        "t_standard_phrase" to ConflictResolutionStrategy.USE_REMOTE, // 规范用语
        "t_supervision_item" to ConflictResolutionStrategy.USE_REMOTE, // 监管事项
        "t_document_template" to ConflictResolutionStrategy.USE_REMOTE, // 文书模板
        "t_unit_info" to ConflictResolutionStrategy.USER_CHOOSE,    // 执法单位
        "t_law_record" to ConflictResolutionStrategy.USE_LOCAL,     // 执法记录
        "t_notice" to ConflictResolutionStrategy.USE_REMOTE,        // 通知公告
        "t_user_info" to ConflictResolutionStrategy.USE_NEWER       // 用户信息
    )

    fun getStrategy(tableName: String): ConflictResolutionStrategy {
        return strategyMap[tableName] ?: ConflictResolutionStrategy.USE_REMOTE
    }

    /**
     * 解决冲突
     */
    suspend fun resolveConflict(
        conflict: ConflictInfo,
        localData: Map<String, Any?>,
        remoteData: Map<String, Any?>
    ): ConflictResult {
        val strategy = getStrategy(conflict.table)

        return when (strategy) {
            ConflictResolutionStrategy.USE_REMOTE -> {
                applyRemoteData(conflict.table, remoteData)
                ConflictResult.RESOLVED_REMOTE
            }
            ConflictResolutionStrategy.USE_LOCAL -> {
                ConflictResult.RESOLVED_LOCAL
            }
            ConflictResolutionStrategy.USE_NEWER -> {
                if (conflict.remoteUpdateTime > conflict.localUpdateTime) {
                    applyRemoteData(conflict.table, remoteData)
                    ConflictResult.RESOLVED_REMOTE
                } else {
                    ConflictResult.RESOLVED_LOCAL
                }
            }
            ConflictResolutionStrategy.USER_CHOOSE -> {
                // 弹出对话框让用户选择
                showConflictDialog(conflict, localData, remoteData)
                ConflictResult.WAITING_USER
            }
        }
    }
}
```

### 5.3 用户选择对话框

```kotlin
class ConflictResolveDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("数据冲突")
            .setMessage("本地和后台数据不一致，请选择保留哪个版本")
            .setPositiveButton("保留后台版本") { _, _ ->
                listener?.onConflictResolved(Resolution.USE_REMOTE)
            }
            .setNegativeButton("保留本地版本") { _, _ ->
                listener?.onConflictResolved(Resolution.USE_LOCAL)
            }
            .setNeutralButton("对比详情") { _, _ ->
                showDetailComparison()
            }
            .create()
    }
}
```

## 6. 同步服务实现

### 6.1 同步管理器

```kotlin
@Singleton
class SyncManager @Inject constructor(
    private val syncQueueDao: SyncQueueDao,
    private val apiService: ApiService,
    private val conflictDetector: ConflictDetector,
    private val conflictResolver: ConflictResolutionManager
) {

    private val _syncStatus = MutableLiveData<SyncStatus>()
    val syncStatus: LiveData<SyncStatus> = _syncStatus

    private var isPaused = false

    /**
     * 执行同步
     */
    @WorkerThread
    suspend fun sync() {
        if (isPaused) return

        _syncStatus.value = SyncStatus.SYNCING

        try {
            // 1. 获取待同步队列 (按优先级排序)
            val pendingItems = syncQueueDao.getPendingItems(50)

            for (item in pendingItems) {
                if (isPaused) break

                try {
                    executeSync(item)
                } catch (e: Exception) {
                    handleSyncError(item, e)
                }
            }

            // 2. 检查是否需要下行同步
            checkDownloadSync()

            _syncStatus.value = SyncStatus.IDLE

        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.ERROR(e.message)
        }
    }

    /**
     * 执行单条同步
     */
    private suspend fun executeSync(item: SyncQueueItem) {
        syncQueueDao.updateStatus(item.id, SyncStatus.PROCESSING)

        when (item.syncType) {
            SyncType.CREATE -> executeCreate(item)
            SyncType.UPDATE -> executeUpdate(item)
            SyncType.DELETE -> executeDelete(item)
            SyncType.DOWNLOAD -> executeDownload(item)
        }

        syncQueueDao.updateStatus(item.id, SyncStatus.SUCCESS)
        syncQueueDao.delete(item.id)
    }

    /**
     * 处理同步错误
     */
    private suspend fun handleSyncError(item: SyncQueueItem, e: Exception) {
        val newRetryCount = item.retryCount + 1

        if (newRetryCount >= item.maxRetry) {
            // 超过最大重试次数，标记为失败
            syncQueueDao.updateStatus(item.id, SyncStatus.FAILED)
            syncQueueDao.updateErrorMessage(item.id, e.message)
        } else {
            // 计算下次重试时间 (指数退避)
            val delayMs = Math.min(Math.pow(2.0, newRetryCount.toDouble()).toLong() * 1000, 30000)
            val nextRetryTime = System.currentTimeMillis() + delayMs

            syncQueueDao.scheduleRetry(item.id, newRetryCount, nextRetryTime)
        }
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }
}
```

### 6.2 数据变更监听

```kotlin
/**
 * 数据变更时自动加入同步队列
 */
class LocalDataChangeListener @Inject constructor(
    private val syncQueueDao: SyncQueueDao
) {

    fun onCreate(tableName: String, recordId: Long, data: Map<String, Any?>) {
        val item = SyncQueueItem(
            tableName = tableName,
            recordId = recordId,
            syncType = SyncType.CREATE,
            requestData = JSON.toJSONString(data)
        )
        syncQueueDao.insert(item)
    }

    fun onUpdate(tableName: String, recordId: Long, data: Map<String, Any?>) {
        val item = SyncQueueItem(
            tableName = tableName,
            recordId = recordId,
            syncType = SyncType.UPDATE,
            requestData = JSON.toJSONString(data)
        )
        syncQueueDao.insert(item)
    }

    fun onDelete(tableName: String, recordId: Long) {
        val item = SyncQueueItem(
            tableName = tableName,
            recordId = recordId,
            syncType = SyncType.DELETE,
            requestData = """{"id": $recordId}"""
        )
        syncQueueDao.insert(item)
    }
}
```

## 7. 同步状态 UI 展示

### 7.1 状态指示器

```kotlin
sealed class SyncStatus {
    object Idle : SyncStatus()           // 空闲
    object Syncing : SyncStatus()        // 同步中
    object Success : SyncStatus()        // 同步成功
    data class Error(val message: String) : SyncStatus()
    object WaitingUser : SyncStatus()    // 等待用户处理冲突
}
```

### 7.2 状态显示组件

```xml
<!-- 同步状态布局 -->
<LinearLayout
    android:id="@+id/syncStatusView"
    android:visibility="gone">

    <ProgressBar
        android:id="@+id/syncProgress"
        android:indeterminate="true" />

    <TextView
        android:id="@+id/syncStatusText"
        android:text="同步中..." />

    <ImageView
        android:id="@+id/syncErrorIcon"
        android:src="@drawable/ic_error"
        android:visibility="gone" />
</LinearLayout>
```

## 8. 调试与日志

### 8.1 同步日志

```kotlin
object SyncLogger {

    private const val TAG = "SyncManager"

    fun i(message: String) {
        Log.i(TAG, "[Sync] $message")
    }

    fun e(message: String, throwable: Throwable?) {
        Log.e(TAG, "[Sync] $message", throwable)
    }

    fun d(queueItem: SyncQueueItem) {
        Log.d(TAG, "[SyncQueue] ${queueItem.syncType} - ${queueItem.tableName}#${queueItem.recordId}")
    }
}
```

### 8.2 同步历史记录

```sql
CREATE TABLE t_sync_history (
    id              BIGINT PRIMARY KEY AUTOINCREMENT,
    sync_type       VARCHAR(16),
    table_name      VARCHAR(64),
    record_id       BIGINT,
    result          VARCHAR(16),      -- SUCCESS/FAILED
    error_message   TEXT,
    duration_ms     LONG,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

## 9. 最佳实践

### 9.1 批量操作

```kotlin
// 推荐：批量提交
val items = syncQueueDao.getPendingItems(50)
apiService.batchSave(items.map { it.toRequest() })

// 不推荐：单条提交
items.forEach {
    apiService.save(it.toRequest())
}
```

### 9.2 事务处理

```kotlin
@Transaction
suspend fun syncWithTransaction() {
    // 数据库操作在事务中执行
    val items = syncQueueDao.getPendingItems(50)
    // ...
}
```

### 9.3 内存管理

```kotlin
// 限制同步队列大小
val MAX_QUEUE_SIZE = 1000

// 定期清理已完成的历史记录
@Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨 2 点
fun cleanupHistory() {
    syncHistoryDao.deleteBefore(days = 30)
}
```
