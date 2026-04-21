package com.ruoyi.code.base

import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ktx.immersionBar
import com.ruoyi.app.R
import java.util.*
import kotlin.math.pow

abstract class BaseActivity : AppCompatActivity() {

    private val activityCallbacks: SparseArray<OnActivityCallback?> by lazy { SparseArray(1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        immersionBar {
            fitsSystemWindows(true)
            statusBarDarkFont(true)
            statusBarColor(R.color.white)
            navigationBarColor(R.color.white)
        }
        initView();
        initData();
    }

    abstract fun getLayoutId(): View

    abstract fun initView()

    abstract fun initData()

    open fun startActivityForResult(
        intent: Intent,
        callback: OnActivityCallback
    ) {
        val requestCode: Int = Random().nextInt(2.0.pow(16.0).toInt())
        activityCallbacks.put(requestCode, callback)
        startActivityForResult(intent, requestCode, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var callback: OnActivityCallback?
        if ((activityCallbacks.get(requestCode).also { callback = it }) != null) {
            callback?.onActivityResult(resultCode, data)
            activityCallbacks.remove(requestCode)
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    interface OnActivityCallback {

        /**
         * 结果回调
         *
         * @param resultCode        结果码
         * @param data              数据
         */
        fun onActivityResult(resultCode: Int, data: Intent?)
    }
}