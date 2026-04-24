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
     * 同步所有数据
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
}
