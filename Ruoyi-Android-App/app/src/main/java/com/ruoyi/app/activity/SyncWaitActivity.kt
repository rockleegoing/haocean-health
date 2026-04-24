package com.ruoyi.app.activity

import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.ruoyi.app.databinding.ActivitySyncWaitBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.sync.SyncManager
import com.ruoyi.app.sync.SyncService
import com.ruoyi.app.sync.model.SyncStatus
import com.ruoyi.code.base.BaseBindingActivity
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Route(path = Constant.syncWaitRoute)
class SyncWaitActivity : BaseBindingActivity<ActivitySyncWaitBinding>() {

    private val syncManager = SyncManager.getInstance()

    override fun initView() {
        // 保持屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun initData() {
        observeSyncProgress()
        observeSyncResult()
        startSyncService()
    }

    private fun observeSyncProgress() {
        lifecycleScope.launch {
            syncManager.progress.collectLatest { progress ->
                binding.apply {
                    tvTitle.text = "正在同步数据"
                    tvModule.text = progress.currentModule
                    progressIndicator.progress = progress.progressPercent
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
                        try {
                            TheRouter.build(Constant.mainRoute).navigation()
                            finish()
                        } catch (e: Exception) {
                            android.util.Log.e("SyncWaitActivity", "导航到MainActivity失败", e)
                        }
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
