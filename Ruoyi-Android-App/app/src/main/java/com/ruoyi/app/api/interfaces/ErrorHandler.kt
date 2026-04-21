package com.ruoyi.app.api.interfaces

import com.drake.net.Post
import com.drake.net.exception.ResponseException
import com.drake.net.interfaces.NetErrorHandler
import com.ruoyi.app.activity.LoginActivity
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.code.utils.ActivityManager
import com.ruoyi.code.witget.toast
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class ErrorHandler : NetErrorHandler {

    override fun onError(e: Throwable) {
        super.onError(e)
        if (e is ResponseException) {
            Objects.equals(e.tag, ConfigApi.TOKEN_ERROR)
            when (e.tag) {
                ConfigApi.TOKEN_ERROR -> {
                    toast(e.message)
                    // Token失效跳转到登录界面
                    MMKV.defaultMMKV().remove("token")
                    GlobalScope.launch {
                        val result = Post<String>(ConfigApi.logout).await()
                        println(result)
                    }
                    LoginActivity.startActivity(ActivityManager.instance.getTopActivity())
                }
                ConfigApi.API_ERROR -> {
                    toast(e.message)
                }
            }
        }
    }

}