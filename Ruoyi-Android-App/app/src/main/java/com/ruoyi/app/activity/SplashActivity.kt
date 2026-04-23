package com.ruoyi.app.activity

import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.view.animation.LinearInterpolator
import com.ruoyi.app.App
import com.ruoyi.app.api.repository.ActivationRepository
import com.ruoyi.app.databinding.ActivitySplashBinding
import com.ruoyi.app.widget.SplashDialog
import com.ruoyi.app.widget.SplashDialog.SplashDialogClickListener
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.SpUtils
import kotlinx.coroutines.runBlocking

/**
 * 启动页活动类
 * 应用启动时首先展示的页面，负责显示欢迎动画和处理用户协议确认
 */
class SplashActivity : BaseBindingActivity<ActivitySplashBinding>() {

    /** 用户是否已同意隐私政策和用户协议的标志 */
    private var isAgreement by SpUtils("isAgreement", false)

    /** Handler + Runnable，用于延迟跳转（替代 Timer，避免旋转时问题） */
    private var jumpRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())

    /**
     * 初始化视图
     * 检查用户是否已同意协议，未同意则显示协议对话框，已同意则直接跳转
     */
    override fun initView() {
        if (!isAgreement) {
            // 创建并显示隐私政策对话框
            val splashDialog = SplashDialog(this, object :SplashDialogClickListener{
                override fun onClick(type: Int) {
                    if (type == 1) {
                        // 用户点击"同意"按钮，保存同意状态并跳转
                        isAgreement = true
                        startActivity()
                    }
                    if (type == 2) {
                        // 用户点击"拒绝"按钮，退出应用
                        finish()
                    }
                }
            } )

            if (!splashDialog.isShowing) {
                splashDialog.show()
            }
        } else {
            // 用户已同意过协议，直接跳转
            startActivity()
        }
    }

    /**
     * 初始化数据
     * 启动欢迎文字的波浪起伏动画效果，增强视觉体验
     */
    override fun initData() {
        // 创建值动画器，从0到1循环变化
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1500  // 动画周期1.5秒
        animator.interpolator = LinearInterpolator()  // 使用线性插值器，保证匀速运动
        animator.repeatCount = ValueAnimator.INFINITE  // 无限循环播放

        // 监听动画值变化，实现缩放效果
        animator.addUpdateListener { animation: ValueAnimator ->
            val value = animation.animatedValue as Float
            // 计算缩放比例，模拟波浪起伏效果（缩放范围：0.9 ~ 1.1）
            val scale =
                (1 + 0.1 * Math.sin(value * 2 * Math.PI)).toFloat()
            binding.tvWelecome.setScaleX(scale)
            binding.tvWelecome.setScaleY(scale)
        }

        animator.start()
    }

    /**
     * 跳转到登录页或激活页
     * 延迟2秒后初始化应用并跳转到对应页面，给用户展示启动页的时间
     */
    private fun startActivity() {
        // 防止重复启动跳转
        if (jumpRunnable != null) return

        jumpRunnable = Runnable {
            App.init()  // 初始化应用配置和数据

            // 检查是否已激活
            val isActivated = runBlocking {
                val activationRepository = ActivationRepository(this@SplashActivity)
                val status = activationRepository.getActivationStatus()
                status != null && status.isActivated
            }

            if (isActivated) {
                // 已激活，跳转到登录页
                LoginActivity.startActivity(this@SplashActivity)
            } else {
                // 未激活，跳转到激活页
                ActivationActivity.startActivity(this@SplashActivity)
            }
            finish()  // 关闭启动页
        }
        handler.postDelayed(jumpRunnable!!, 2000)  // 延迟2秒执行跳转任务
    }

    /**
     * 销毁活动时清理资源
     * 取消定时器任务，避免内存泄漏
     */
    override fun onDestroy() {
        super.onDestroy()
        jumpRunnable?.let { handler.removeCallbacks(it) }  // 取消延迟跳转
        jumpRunnable = null
    }

}