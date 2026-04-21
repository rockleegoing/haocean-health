package com.ruoyi.app.activity

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import com.ruoyi.app.App
import com.ruoyi.app.databinding.ActivitySplashBinding
import com.ruoyi.app.widget.SplashDialog
import com.ruoyi.app.widget.SplashDialog.SplashDialogClickListener
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.SpUtils
import java.util.Timer
import java.util.TimerTask


class SplashActivity : BaseBindingActivity<ActivitySplashBinding>() {

    private var isAgreement by SpUtils("isAgreement", false)
    private var timer: Timer? = null

    override fun initView() {
        if (!isAgreement) {
            val splashDialog = SplashDialog(this, object :SplashDialogClickListener{
                override fun onClick(type: Int) {
                    if (type == 1) {
                        isAgreement = true
                        startActivity()
                    }
                    if (type == 2) {
                        finish()
                    }
                }
            } )

            if (!splashDialog.isShowing) {
                splashDialog.show()
            }
        } else {
            startActivity()
        }
    }

    override fun initData() {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 1500
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = ValueAnimator.INFINITE

        animator.addUpdateListener { animation: ValueAnimator ->
            val value = animation.animatedValue as Float
            // 计算缩放比例，模拟波浪起伏
            val scale =
                (1 + 0.1 * Math.sin(value * 2 * Math.PI)).toFloat()
            binding.tvWelecome.setScaleX(scale)
            binding.tvWelecome.setScaleY(scale)
        }

        animator.start()
    }

    private fun startActivity() {
        val delayTask = object : TimerTask() {
            override fun run() {
                App.init()
                LoginActivity.startActivity(this@SplashActivity)
                this@SplashActivity.finish()
            }
        }
        timer = Timer()
        timer?.schedule(delayTask, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
    }

}