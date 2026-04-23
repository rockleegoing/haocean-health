package com.ruoyi.app.api.repository

import android.content.Context
import com.drake.net.Post
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.api.OKHttpUtils
import com.ruoyi.app.model.entity.ResultEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 设备仓库
 * 负责设备相关的数据操作，包括心跳上报、设备状态管理等
 */
class DeviceRepository(private val context: Context) {

    companion object {
        private const val TAG = "DeviceRepository"
        /** 心跳间隔：5 分钟 */
        private const val HEARTBEAT_INTERVAL_MS = 5 * 60 * 1000L
    }

    /**
     * 上报心跳到后端
     * @param deviceUuid 设备 UUID
     * @param status 设备状态：1=在线，0=离线
     * @return 上报结果
     */
    suspend fun sendHeartbeat(deviceUuid: String, status: String = "1"): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val params = mapOf(
                    "deviceUuid" to deviceUuid,
                    "status" to status
                )

                val response = Post<ResultEntity<Any>>(
                    ConfigApi.baseUrl + "/device/device/heartbeat"
                ) {
                    body = OKHttpUtils.getRequestBody(params)
                }.await()

                if (response.code == 200) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.msg ?: "心跳上报失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}