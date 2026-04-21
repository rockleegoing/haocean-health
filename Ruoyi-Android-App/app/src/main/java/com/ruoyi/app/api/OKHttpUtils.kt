package com.ruoyi.app.api

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.drake.net.NetConfig
import com.drake.net.cookie.PersistentCookieJar
import com.drake.net.interceptor.LogRecordInterceptor
import com.drake.net.okhttp.setConverter
import com.drake.net.okhttp.setDebug
import com.drake.net.okhttp.setDialogFactory
import com.drake.net.okhttp.setErrorHandler
import com.drake.net.okhttp.setRequestInterceptor
import com.drake.net.request.MediaConst.JSON
import com.ruoyi.app.BuildConfig
import com.ruoyi.app.R
import com.ruoyi.app.api.interfaces.ErrorHandler
import com.ruoyi.app.api.interfaces.RequestInterceptor
import com.ruoyi.code.Frame
import com.ruoyi.code.dialog.BubbleDialog
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Collections.emptySet
import java.util.concurrent.TimeUnit

object OKHttpUtils {

    fun initialize(context: Context) {
        NetConfig.initialize(ConfigApi.baseUrl, context) {

            // 超时设置
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)

            // 本框架支持Http缓存协议和强制缓存模式
            cache(Cache(context.cacheDir, 1024 * 1024 * 128))
            // 缓存设置, 当超过maxSize最大值会根据最近最少使用算法清除缓存来限制缓存大小

            // LogCat是否输出异常日志, 异常日志可以快速定位网络请求错误
            setDebug(BuildConfig.DEBUG)

            // AndroidStudio OkHttp Profiler 插件输出网络日志
            addInterceptor(LogRecordInterceptor(BuildConfig.DEBUG))

            // 添加持久化Cookie管理
            cookieJar(PersistentCookieJar(context))

            // 統一 异常处理
            setErrorHandler(ErrorHandler())
            // 通知栏监听网络日志
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    ChuckerInterceptor.Builder(context)
                        .collector(ChuckerCollector(context))
                        .maxContentLength(250000L)
                        .redactHeaders(emptySet())
                        .alwaysReadResponseBody(false)
                        .build()
                )
            }

            // 添加请求拦截器, 可配置全局/动态参数
            setRequestInterceptor(RequestInterceptor())

            // 数据转换器
            setConverter(SerializationConverter())

            // 自定义全局加载对话框
            setDialogFactory {
                BubbleDialog(it, Frame.getString(R.string.bubble_loading_title))
            }
        }
    }

    inline fun <reified T> getRequestBody(result: T): RequestBody {
        if (result == null) {
            return getRequestBody("")
        }
        val jsonString = Json.encodeToString(result)
        return getRequestBody(jsonString)
    }

    fun getRequestBody(result: String): RequestBody {
        return result.toRequestBody(JSON);
    }

}
