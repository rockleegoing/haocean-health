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
import kotlinx.coroutines.delay
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
            .setSmallIcon(R.mipmap.ic_launcher)
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
