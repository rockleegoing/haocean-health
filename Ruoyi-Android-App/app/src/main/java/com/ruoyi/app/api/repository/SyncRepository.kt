package com.ruoyi.app.api.repository

import android.content.Context
import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.model.entity.SyncDataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 数据同步仓库
 * 用于预加载所有用户、部门、角色、菜单数据到本地
 */
class SyncRepository(private val context: Context) {

    /**
     * 同步所有数据（匿名，预加载阶段使用）
     * 调用 /app/sync 接口获取全部数据
     */
    suspend fun syncAllData(): Result<SyncDataEntity> = withContext(Dispatchers.IO) {
        try {
            val data = Get<SyncDataEntity>(ConfigApi.baseUrl + ConfigApi.appSync).await()
            if (data.code == ConfigApi.SUCESSS) {
                Result.success(data)
            } else {
                Result.failure(Exception(data.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 同步当前用户完整数据（登录成功后使用，包含 permissions 等）
     * 调用 /app/sync?userId=xxx 获取当前用户的完整信息
     */
    suspend fun syncCurrentUserData(userId: Long): Result<SyncDataEntity> = withContext(Dispatchers.IO) {
        try {
            val url = "${ConfigApi.baseUrl}${ConfigApi.appSync}?userId=$userId"
            val data = Get<SyncDataEntity>(url).await()
            if (data.code == ConfigApi.SUCESSS) {
                Result.success(data)
            } else {
                Result.failure(Exception(data.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
