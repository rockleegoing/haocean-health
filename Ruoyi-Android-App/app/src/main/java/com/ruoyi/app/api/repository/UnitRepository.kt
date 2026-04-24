package com.ruoyi.app.api.repository

import android.content.Context
import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.UnitEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 执法单位仓库
 */
class UnitRepository(private val context: Context) {

    private val unitDao = AppDatabase.getInstance(context).unitDao()

    /**
     * 从后端获取单位列表并存储到本地
     */
    suspend fun syncUnitsFromServer(): Result<List<UnitEntity>> = withContext(Dispatchers.IO) {
        try {
            val result = Get<UnitResult>(ConfigApi.baseUrl + ConfigApi.unitList).await()
            if (result.code == ConfigApi.SUCESSS) {
                val units = result.data.map { it.toEntity() }
                unitDao.insertUnits(units)
                Result.success(units)
            } else {
                Result.failure(Exception(result.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 从本地获取所有单位
     */
    suspend fun getAllUnitsFromLocal(): List<UnitEntity> = withContext(Dispatchers.IO) {
        unitDao.getAllUnits()
    }

    /**
     * 从本地搜索单位
     */
    suspend fun searchUnits(keyword: String): List<UnitEntity> = withContext(Dispatchers.IO) {
        unitDao.searchUnits(keyword)
    }

    /**
     * 根据分类获取单位
     */
    suspend fun getUnitsByCategory(categoryId: Long): List<UnitEntity> = withContext(Dispatchers.IO) {
        unitDao.getUnitsByCategory(categoryId)
    }

    /**
     * 获取单位详情
     */
    suspend fun getUnitById(unitId: Long): UnitEntity? = withContext(Dispatchers.IO) {
        unitDao.getUnitById(unitId)
    }

    /**
     * 根据区域获取单位
     */
    suspend fun getUnitsByRegion(region: String): List<UnitEntity> = withContext(Dispatchers.IO) {
        unitDao.getUnitsByRegion(region)
    }
}

/**
 * API 响应实体
 */
@kotlinx.serialization.Serializable
data class UnitResult(
    val code: Int,
    val msg: String,
    val data: List<UnitDTO> = emptyList()
)

@kotlinx.serialization.Serializable
data class UnitDTO(
    val unitId: Long,
    val unitName: String,
    val industryCategoryId: Long?,
    val industryCategoryName: String?,
    val region: String?,
    val supervisionType: String?,
    val creditCode: String?,
    val legalPerson: String?,
    val contactPhone: String?,
    val businessAddress: String?,
    val latitude: Double?,
    val longitude: Double?,
    val status: String?,
    val delFlag: String?,
    val createTime: String?,
    val updateTime: String?,
    val remark: String?
) {
    fun toEntity(): UnitEntity {
        return UnitEntity(
            unitId = unitId,
            unitName = unitName,
            industryCategoryId = industryCategoryId,
            industryCategoryName = industryCategoryName,
            region = region,
            supervisionType = supervisionType,
            creditCode = creditCode,
            legalPerson = legalPerson,
            contactPhone = contactPhone,
            businessAddress = businessAddress,
            latitude = latitude,
            longitude = longitude,
            status = status ?: "0",
            delFlag = delFlag ?: "0",
            createTime = createTime?.toLongOrNull() ?: System.currentTimeMillis(),
            updateTime = updateTime?.toLongOrNull(),
            remark = remark
        )
    }
}
