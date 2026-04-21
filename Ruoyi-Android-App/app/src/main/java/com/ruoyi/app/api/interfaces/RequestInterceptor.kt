package com.ruoyi.app.api.interfaces

import com.drake.net.interceptor.RequestInterceptor
import com.drake.net.request.BaseRequest
import com.tencent.mmkv.MMKV

/** 请求拦截器, 一般用于添加全局参数 */
class RequestInterceptor : RequestInterceptor {

    override fun interceptor(request: BaseRequest) {
        request.addHeader("client", "Android")
        request.setHeader("Authorization", "Bearer " + MMKV.defaultMMKV().decodeString("token"))
    }
}