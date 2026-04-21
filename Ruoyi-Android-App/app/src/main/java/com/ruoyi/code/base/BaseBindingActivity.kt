package com.ruoyi.code.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ktx.immersionBar
import com.ruoyi.app.R
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.viewbinding.base.ActivityBinding
import com.ruoyi.code.base.viewbinding.base.ActivityBindingDelegate
import com.ruoyi.code.utils.ActivityManager
import com.ruoyi.code.utils.LocaleHelper.getPersistedData
import com.ruoyi.code.utils.LocaleHelper.setLocale
import com.ruoyi.code.witget.WatermarkView
import com.therouter.TheRouter


abstract class BaseBindingActivity<VB : ViewBinding> : AppCompatActivity(),
    ActivityBinding<VB> by ActivityBindingDelegate() {

    override fun attachBaseContext(newBase: Context) {
        val language = getPersistedData(newBase, "zh")
        val context = setLocale(newBase, language!!)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWithBinding()
        immersionBar {
            fitsSystemWindows(true)
            statusBarDarkFont(true)
            statusBarColor(R.color.white)
            navigationBarColor(R.color.white)
        }

        TheRouter.inject(this)
        ActivityManager.instance.addActivity(this)
        if (Constant.isShowWaterMark) {
            window.decorView.post {
                addGlobalWatermark()
            }
        }

        initView()
        initData()
    }

    abstract fun initView()

    abstract fun initData()

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.instance.removeActivity(this)
    }

    /**
     * 全局添加水印（核心：获取Activity的根布局，动态添加WatermarkView）
     */
    @SuppressLint("ResourceAsColor")
    private fun addGlobalWatermark() {
        val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val watermarkView = WatermarkView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // 设置透明背景，避免遮挡页面内容
            setBackgroundColor(android.R.color.transparent)
        }
        rootView.addView(watermarkView)
    }
}