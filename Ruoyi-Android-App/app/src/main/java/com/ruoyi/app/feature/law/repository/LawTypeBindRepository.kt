package com.ruoyi.app.feature.law.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.LawTypeBindEntity
import com.ruoyi.app.feature.law.api.LawTypeBindApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LawTypeBindRepository(private val context: Context) {

    private val lawTypeBindDao = AppDatabase.getInstance(context).lawTypeBindDao()

    /**
     * 同步所有绑定关系
     */
    suspend fun syncLawTypeBinds(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = LawTypeBindApi.getAllBinds()
                if (response.code == 200) {
                    // 清空本地中间表，再插入服务端数据
                    lawTypeBindDao.deleteAll()
                    val entities = response.data.map { dto ->
                        LawTypeBindEntity(
                            lawId = dto.lawId,
                            typeId = dto.typeId,
                            createTime = null
                        )
                    }
                    lawTypeBindDao.insertAll(entities)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 获取某法律的类型ID列表
     */
    suspend fun getTypeIdsByLawId(lawId: Long): List<Long> {
        return withContext(Dispatchers.IO) {
            lawTypeBindDao.getByLawId(lawId).map { it.typeId }
        }
    }

    /**
     * 获取本地所有绑定
     */
    suspend fun getAll(): List<LawTypeBindEntity> {
        return withContext(Dispatchers.IO) {
            lawTypeBindDao.getAll()
        }
    }
}
