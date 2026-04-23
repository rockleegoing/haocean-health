# 数据同步策略与登录改造实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现离线优先的数据同步体系，包含登录页数据预加载、阻塞式全量同步、后台增量同步框架

**Architecture:** 三阶段分层同步架构。阶段一在登录页后台预加载 /getInfo 数据；阶段二登录成功后强制阻塞式全量同步（仅用户权限，其他预留）；阶段三基于 WorkManager 的后台增量同步框架。覆盖安装时清空数据重置激活状态。

**Tech Stack:** Kotlin + MVVM + Room + WorkManager + Foreground Service + TheRouter + Coroutines

---

## 一、文件结构

```
com.ruoyi.app/
├── App.kt                                          # Application 类（需修改：添加覆盖安装检测）
├── activity/
│   ├── LoginActivity.kt                            # 登录页（需修改：添加数据预加载逻辑）
│   └── MainActivity.kt                             # 首页（需修改：添加 WorkManager 调度）
├── sync/                                           # 新增：同步模块
│   ├── SyncManager.kt                             # 同步管理器（核心）
│   ├── SyncService.kt                              # Foreground Service
│   ├── SyncWorker.kt                               # WorkManager Worker
│   ├── DataVersionManager.kt                       # 数据版本管理
│   └── model/
│       ├── SyncProgress.kt                         # 同步进度数据类
│       └── SyncResult.kt                           # 同步结果数据类
├── data/database/
│   ├── dao/
│   │   └── UserDao.kt                              # 修改：扩展 /getInfo 数据存储
│   └── AppDatabase.kt                              # 修改：添加预留表（暂不添加，等后端接口）
└── api/repository/
    └── AuthRepository.kt                            # 修改：添加 /getInfo 本地存储逻辑
```

---

## 二、实现任务

### Task 1: 覆盖安装检测逻辑

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/App.kt`
- Modify: `app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt`

- [ ] **Step 1: 在 App.kt 添加版本检测和清空逻辑**

在 `App.kt` 的 `onCreate()` 方法中，在 `Frame.init(this)` 之前添加覆盖安装检测：

```kotlin
// 检测覆盖安装
val prefs = getSharedPreferences("app_info", Context.MODE_PRIVATE)
val lastVersion = prefs.getInt("last_version", 0)
val currentVersion = try {
    packageManager.getPackageInfo(packageName, 0).versionCode
} catch (e: Exception) {
    0
}

if (currentVersion > lastVersion && lastVersion > 0) {
    // 覆盖安装：清空数据 + 重置激活状态
    GlobalScope.launch(Dispatchers.IO) {
        AppDatabase.getInstance(this@App).clearAllTables()
    }
    MMKV.defaultMMKV().removeValueForKey("activation_status")
    prefs.edit().putInt("last_version", currentVersion).apply()
    // 跳转到激活页面（不在 onCreate 中跳转，等登录页处理）
}
prefs.edit().putInt("last_version", currentVersion).apply()
```

- [ ] **Step 2: 验证编译通过**

Run: `./gradlew assembleDebug 2>&1 | tail -20`
Expected: BUILD SUCCESSFUL

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/ruoyi/app/App.kt
git commit -m "feat(app): 添加覆盖安装检测逻辑"
```

---

### Task 2: 创建同步模块基础类

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/sync/model/SyncProgress.kt`
- Create: `app/src/main/java/com/ruoyi/app/sync/model/SyncResult.kt`

- [ ] **Step 1: 创建 SyncProgress.kt**

```kotlin
package com.ruoyi.app.sync.model

data class SyncProgress(
    val currentModule: String,      // 当前同步模块名称
    val currentStep: Int,            // 当前步骤（第几个模块）
    val totalSteps: Int,            // 总步骤数
    val progressPercent: Int,        // 进度百分比 0-100
    val downloadedBytes: Long,       // 已下载字节数
    val totalBytes: Long,           // 总字节数
    val estimatedRemainingSeconds: Int // 预估剩余秒数
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
```

- [ ] **Step 2: 创建 SyncResult.kt**

```kotlin
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
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/ruoyi/app/sync/model/SyncProgress.kt app/src/main/java/com/ruoyi/app/sync/model/SyncResult.kt
git commit -m "feat(sync): 添加同步进度和结果数据类"
```

---

### Task 3: 创建 SyncManager 同步管理器

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/sync/SyncManager.kt`

- [ ] **Step 1: 创建 SyncManager.kt**

```kotlin
package com.ruoyi.app.sync

import com.ruoyi.app.sync.model.SyncProgress
import com.ruoyi.app.sync.model.SyncResult
import com.ruoyi.app.sync.model.SyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
                    kotlinx.coroutines.delay(delayMs)
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
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/ruoyi/app/sync/SyncManager.kt
git commit -m "feat(sync): 添加 SyncManager 同步管理器"
```

---

### Task 4: 创建 SyncService Foreground Service

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/sync/SyncService.kt`

- [ ] **Step 1: 创建 SyncService.kt**

```kotlin
package com.ruoyi.app.sync

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.ruoyi.app.R
import com.ruoyi.app.activity.MainActivity
import com.ruoyi.app.sync.model.SyncStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SyncService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var syncJob: Job? = null

    companion object {
        const val CHANNEL_ID = "sync_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.ruoyi.app.sync.START"
        const val ACTION_STOP = "com.ruoyi.app.sync.STOP"

        fun start(context: Context) {
            val intent = Intent(context, SyncService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, SyncService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                startForeground(NOTIFICATION_ID, createNotification("正在准备同步...", 0))
                startSync()
            }
            ACTION_STOP -> {
                stopSync()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startSync() {
        syncJob?.cancel()
        syncJob = serviceScope.launch {
            val manager = SyncManager.getInstance()

            // 监听进度更新
            launch {
                manager.progress.collectLatest { progress ->
                    updateNotification("同步中：${progress.currentModule}", progress.progressPercent)
                }
            }

            // 执行同步
            val result = manager.syncAll()

            // 同步完成处理
            when (result.status) {
                SyncStatus.SUCCESS -> {
                    updateNotification("同步完成", 100)
                    delay(2000)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
                SyncStatus.MAX_RETRIES_EXCEEDED -> {
                    updateNotification("同步失败，点击重试", -1)
                    // 不自动停止，等待外部处理
                }
                else -> {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }
    }

    private fun stopSync() {
        syncJob?.cancel()
        SyncManager.getInstance().cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "数据同步",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "数据同步进度通知"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(content: String, progress: Int): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("数据同步")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher) // TODO: 替换为合适的图标
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        if (progress >= 0) {
            builder.setProgress(100, progress, false)
        } else {
            // 失败状态，不显示进度条
            builder.setProgress(0, 0, false)
        }

        return builder.build()
    }

    private fun updateNotification(content: String, progress: Int) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, createNotification(content, progress))
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/ruoyi/app/sync/SyncService.kt
git commit -m "feat(sync): 添加 SyncService Foreground Service"
```

---

### Task 5: 创建 SyncWaitActivity 同步等待页面

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/activity/SyncWaitActivity.kt`
- Create: `app/src/main/res/layout/activity_sync_wait.xml`

- [ ] **Step 1: 创建 activity_sync_wait.xml 布局**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/white">

    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="正在同步数据..."
        android:textSize="20sp"
        android:textColor="@color/black"
        android:textStyle="bold"
        app:layout_constraintBottom_toTopOf="@id/tvModule"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_chainStyle="packed" />

    <TextView
        android:id="@+id/tvModule"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="准备中..."
        android:textSize="14sp"
        android:textColor="@color/gray"
        app:layout_constraintBottom_toTopOf="@id/progressBar"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvTitle" />

    <ProgressBar
        android:id="@+id/progressBar"
        style="@style/Widget.AppCompat.ProgressBar.Horizontal"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginHorizontal="48dp"
        android:layout_marginTop="32dp"
        android:max="100"
        android:progress="0"
        app:layout_constraintBottom_toTopOf="@id/tvProgress"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvModule" />

    <TextView
        android:id="@+id/tvProgress"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:text="0%"
        android:textSize="14sp"
        android:textColor="@color/black"
        app:layout_constraintBottom_toTopOf="@id/tvSize"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/progressBar" />

    <TextView
        android:id="@+id/tvSize"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:text="0 KB / 0 KB"
        android:textSize="12sp"
        android:textColor="@color/gray"
        app:layout_constraintBottom_toTopOf="@id/tvRemaining"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvProgress" />

    <TextView
        android:id="@+id/tvRemaining"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:text="预估剩余时间：计算中..."
        android:textSize="12sp"
        android:textColor="@color/gray"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvSize" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

- [ ] **Step 2: 创建 SyncWaitActivity.kt**

```kotlin
package com.ruoyi.app.activity

import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.ruoyi.app.R
import com.ruoyi.app.databinding.ActivitySyncWaitBinding
import com.ruoyi.app.sync.SyncManager
import com.ruoyi.app.sync.SyncService
import com.ruoyi.app.sync.model.SyncStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SyncWaitActivity : BaseBindingActivity<ActivitySyncWaitBinding>() {

    private val syncManager = SyncManager.getInstance()

    companion object {
        const val EXTRA_FROM_LOGIN = "from_login"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 保持屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(binding.root)

        observeSyncProgress()
        observeSyncResult()
        startSyncService()
    }

    private fun observeSyncProgress() {
        lifecycleScope.launch {
            syncManager.progress.collectLatest { progress ->
                binding.apply {
                    tvTitle.text = "正在同步数据..."
                    tvModule.text = progress.currentModule
                    progressBar.progress = progress.progressPercent
                    tvProgress.text = "${progress.progressPercent}%"

                    val downloadedMb = progress.downloadedBytes / (1024 * 1024)
                    val totalMb = progress.totalBytes / (1024 * 1024)
                    tvSize.text = if (progress.totalBytes > 0) {
                        "$downloadedMb MB / $totalMb MB"
                    } else {
                        "数据加载中..."
                    }

                    tvRemaining.text = if (progress.estimatedRemainingSeconds > 0) {
                        "预估剩余时间：${progress.estimatedRemainingSeconds / 60}分${progress.estimatedRemainingSeconds % 60}秒"
                    } else {
                        "预估剩余时间：计算中..."
                    }
                }
            }
        }
    }

    private fun observeSyncResult() {
        lifecycleScope.launch {
            syncManager.result.collectLatest { result ->
                result ?: return@collectLatest

                when (result.status) {
                    SyncStatus.SUCCESS -> {
                        // 同步成功，跳转到首页
                        TheRouter.build(Constant.mainRoute).navigation()
                        finish()
                    }
                    SyncStatus.MAX_RETRIES_EXCEEDED -> {
                        showRetryDialog(result.message ?: "数据同步失败")
                    }
                    SyncStatus.NETWORK_ERROR -> {
                        showRetryDialog("网络连接失败，请检查网络后重试")
                    }
                    else -> {
                        // 其他情况不处理，等用户操作
                    }
                }
            }
        }
    }

    private fun showRetryDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("同步失败")
            .setMessage(message)
            .setPositiveButton("确定") { _: DialogInterface?, _: Int ->
                // 回到登录页
                TheRouter.build(Constant.loginRoute).navigation()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun startSyncService() {
        SyncService.start(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        SyncService.stop(this)
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/ruoyi/app/activity/SyncWaitActivity.kt app/src/main/res/layout/activity_sync_wait.xml
git commit -m "feat(sync): 添加 SyncWaitActivity 同步等待页面"
```

---

### Task 6: 修改 LoginActivity 添加数据预加载逻辑

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/activity/LoginActivity.kt`
- Modify: `app/src/main/java/com/ruoyi/app/Constant.kt` (如果需要添加路由常量)

- [ ] **Step 1: 修改 LoginActivity.kt 添加数据预加载**

在 `LoginActivity.kt` 的 `initData()` 方法中，在检查 token 之前添加数据预加载逻辑：

```kotlin
override fun initData() {
    // 1. 检查覆盖安装（由 App.onCreate 处理，此处只需检查是否需要跳转激活）
    // 2. 初始化 Room DB（如果还没初始化）
    lifecycleScope.launch {
        AppDatabase.getInstance(this@LoginActivity)
    }

    // 3. 预加载 /getInfo 数据（后台异步，失败不阻塞）
    lifecycleScope.launch {
        try {
            val authRepo = (application as App).authRepository
            val result = authRepo.preloadLoginData()
            if (result.isSuceess()) {
                // 数据预加载成功，已存入本地
            }
        } catch (e: Exception) {
            // 预加载失败，不阻塞，用户可以用本地数据登录
        }
    }

    // 4. 检查 token 是否已登录
    val token = MMKV.defaultMMKV().decodeString("token")
    if (!token.isNullOrEmpty()) {
        TheRouter.build(Constant.mainRoute).navigation()
        finish()
        return
    }

    // 原有逻辑继续...
}
```

- [ ] **Step 2: 在 AuthRepository 添加 preloadLoginData 方法**

在 `AuthRepository.kt` 中添加：

```kotlin
suspend fun preloadLoginData(): ResultEntity<MineEntity> {
    return withContext(Dispatchers.IO) {
        try {
            val response = Get<MineEntity>(ConfigApi.getInfo) {
                setHeader("Authorization", "Bearer ${MMKV.defaultMMKV().decodeString("token")}")
            }.await()

            if (response.code == ConfigApi.SUCESSS && response.data != null) {
                // 存储到本地数据库
                response.data.user?.let { user ->
                    AppDatabase.getInstance(context).userDao().insertUser(user)
                }
                // 存储权限信息
                // TODO: 根据后端接口完善
            }
            response
        } catch (e: Exception) {
            ResultEntity().apply { msg = e.message }
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/ruoyi/app/activity/LoginActivity.kt app/src/main/java/com/ruoyi/app/api/repository/AuthRepository.kt
git commit -m "feat(login): 登录页添加 /getInfo 数据预加载"
```

---

### Task 7: 修改 UserViewModel 登录成功后跳转到 SyncWaitActivity

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/model/UserViewModel.kt`

- [ ] **Step 1: 修改 login 方法跳转逻辑**

在 `UserViewModel.kt` 中，找到 `login` 方法，修改登录成功后的跳转：

```kotlin
fun login(activity: FragmentActivity, username: String, password: String, code: String) {
    scopeDialog(activity) {
        val data = authRepository.login(code, username, password, uuid.value)
        if (data.isSuceess()) {
            MMKV.defaultMMKV().encode("token", data.token)
            // 登录成功，跳转到同步等待页面
            TheRouter.build(Constant.syncWaitRoute).navigation()
        } else {
            errorMsg.value = data.msg
        }
    }.catch {
        errorMsg.value = it.message
    }
}
```

- [ ] **Step 2: 添加路由常量**

在 `Constant.kt` 中添加：

```kotlin
const val syncWaitRoute = "/app/syncWait"
```

- [ ] **Step 3: 在 TheRouter 初始化时注册 SyncWaitActivity**

在 `App.kt` 或 TheRouter 配置中添加路由注册（如果还没注册）：

```kotlin
// 在路由初始化处添加
TheRouter.build(syncWaitRoute).routerClass(SyncWaitActivity::class.java)
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/ruoyi/app/model/UserViewModel.kt app/src/main/java/com/ruoyi/app/Constant.kt
git commit -m "feat(login): 登录成功后跳转到同步等待页面"
```

---

### Task 8: 创建 DataVersionManager 数据版本管理

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/sync/DataVersionManager.kt`

- [ ] **Step 1: 创建 DataVersionManager.kt**

```kotlin
package com.ruoyi.app.sync

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.DataVersionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataVersionManager private constructor(private val context: Context) {

    private val database = AppDatabase.getInstance(context)
    private val dataVersionDao = database.dataVersionDao()

    companion object {
        @Volatile
        private var INSTANCE: DataVersionManager? = null

        fun getInstance(context: Context): DataVersionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataVersionManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        // 模块名称常量
        const val MODULE_USER_PERMISSION = "user_permission"
        const val MODULE_INDUSTRY_CATEGORY = "industry_category"
        const val MODULE_LAW = "law"
        const val MODULE_PHRASE = "phrase"
        const val MODULE_SUPERVISION = "supervision"
        const val MODULE_DOCUMENT_TEMPLATE = "document_template"
        const val MODULE_MEDIA_FILE = "media_file"
    }

    suspend fun getLastSyncTime(moduleName: String): Long = withContext(Dispatchers.IO) {
        dataVersionDao.getByModule(moduleName)?.lastSyncTime ?: 0L
    }

    suspend fun updateSyncTime(moduleName: String, lastModified: Long) = withContext(Dispatchers.IO) {
        val existing = dataVersionDao.getByModule(moduleName)
        if (existing != null) {
            val updated = existing.copy(lastSyncTime = lastModified)
            dataVersionDao.insertDataVersion(updated)
        } else {
            val newVersion = DataVersionEntity(
                id = System.currentTimeMillis(),
                tableName = moduleName,
                dataVersion = lastModified,
                lastSyncTime = lastModified,
                syncType = "incremental"
            )
            dataVersionDao.insertDataVersion(newVersion)
        }
    }

    suspend fun hasUpdate(moduleName: String, serverLastModified: Long): Boolean = withContext(Dispatchers.IO) {
        val localLastSync = getLastSyncTime(moduleName)
        serverLastModified > localLastSync
    }

    suspend fun resetModuleVersion(moduleName: String) = withContext(Dispatchers.IO) {
        updateSyncTime(moduleName, 0L)
    }

    suspend fun resetAll() = withContext(Dispatchers.IO) {
        dataVersionDao.clearAll()
    }
}
```

- [ ] **Step 2: 在 DataVersionEntity 添加模块名字段（如果还没有）**

检查 `DataVersionEntity.kt` 是否有 `tableName` 字段，如果没有需要添加。

- [ ] **Step 3: 在 DataVersionDao 添加查询方法**

检查 `DataVersionDao.kt` 是否有 `getByModule` 方法，如果没有需要添加：

```kotlin
@Query("SELECT * FROM sys_data_version WHERE tableName = :moduleName LIMIT 1")
suspend fun getByModule(moduleName: String): DataVersionEntity?
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/ruoyi/app/sync/DataVersionManager.kt
git commit -m "feat(sync): 添加 DataVersionManager 数据版本管理"
```

---

### Task 9: 创建 SyncWorker WorkManager Worker

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/sync/SyncWorker.kt`

- [ ] **Step 1: 创建 SyncWorker.kt**

```kotlin
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
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/ruoyi/app/sync/SyncWorker.kt
git commit -m "feat(sync): 添加 SyncWorker WorkManager Worker"
```

---

### Task 10: 修改 MainActivity 添加 WorkManager 调度

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/activity/MainActivity.kt`

- [ ] **Step 1: 在 initData() 中调度 WorkManager**

```kotlin
override fun initData() {
    // 原有逻辑...

    // 调度后台同步 WorkManager
    SyncWorker.schedule(this)
}
```

- [ ] **Step 2: Commit**

```bash
git add app/src/main/java/com/ruoyi/app/activity/MainActivity.kt
git commit -m "feat(main): 首页调度 WorkManager 后台同步"
```

---

## 三、验证检查清单

完成所有 Task 后，验证以下场景：

- [ ] **首次安装流程**：安装 App → 进入登录页 → 数据预加载 → 登录 → 同步等待 → 进入首页
- [ ] **覆盖安装流程**：安装新版本 → App 检测到覆盖安装 → 清空数据 → 跳转激活页
- [ ] **同步进度显示**：登录成功后显示 SyncWaitActivity，进度条正确更新
- [ ] **同步失败处理**：模拟网络断开 → 3 次重试后 → 弹窗回到登录页
- [ ] **WorkManager 调度**：进入首页后 WorkManager 正确调度，30 分钟后执行下一次同步
- [ ] **编译验证**：./gradlew assembleDebug 编译通过

---

## 四、待后端接口开发后继续

以下任务需要在后端接口开发完成后继续：

1. **Task 11**: 完善 UserDao 和 UserEntity 支持 /getInfo 完整数据存储
2. **Task 12**: 完善 SyncManager.syncUserPermissions() 实际同步逻辑
3. **Task 13**: 添加预留模块的同步方法骨架（行业分类、法律法规等）
4. **Task 14**: 实现 DataVersionManager.hasUpdate() 增量检查逻辑

---

**计划完成，等待实现。**