package com.ruoyi.app.api.repository

import android.content.Context
import com.drake.net.Get
import com.drake.net.Post
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.UnitEntity
import com.ruoyi.app.data.database.entity.toDTO
import com.ruoyi.app.api.OKHttpUtils
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
            if (result.code == ConfigApi.SUCCESS) {
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
     * 从本地分页获取单位
     */
    suspend fun getUnitsPaged(offset: Int, limit: Int): List<UnitEntity> = withContext(Dispatchers.IO) {
        unitDao.getUnitsPaged(limit, offset)
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

    /**
     * 根据监管类型获取单位
     */
    suspend fun getUnitsBySupervisionType(supervisionType: String): List<UnitEntity> = withContext(Dispatchers.IO) {
        unitDao.getUnitsBySupervisionType(supervisionType)
    }

    /**
     * 获取本地待同步的单位列表
     */
    suspend fun getPendingUnits(): List<UnitEntity> {
        return withContext(Dispatchers.IO) {
            unitDao.getPendingUnits()
        }
    }

    /**
     * 标记单位为已同步
     */
    suspend fun markAsSynced(unitId: Long) {
        withContext(Dispatchers.IO) {
            unitDao.markAsSynced(unitId)
        }
    }

    /**
     * 上传单个单位到服务器
     */
    suspend fun uploadUnitToServer(unit: UnitEntity): Result<UnitDTO> {
        return withContext(Dispatchers.IO) {
            try {
                val params = mapOf(
                    "unitId" to unit.unitId.toString(),
                    "unitName" to unit.unitName,
                    "industryCategoryId" to (unit.industryCategoryId?.toString() ?: ""),
                    "industryCategoryName" to (unit.industryCategoryName ?: ""),
                    "region" to (unit.region ?: ""),
                    "supervisionType" to (unit.supervisionType ?: ""),
                    "creditCode" to (unit.creditCode ?: ""),
                    "legalPerson" to (unit.legalPerson ?: ""),
                    "contactPhone" to (unit.contactPhone ?: ""),
                    "businessAddress" to (unit.businessAddress ?: ""),
                    "latitude" to (unit.latitude?.toString() ?: ""),
                    "longitude" to (unit.longitude?.toString() ?: ""),
                    "status" to unit.status,
                    "remark" to (unit.remark ?: ""),
                    "personName" to (unit.personName ?: ""),
                    "registrationAddress" to (unit.registrationAddress ?: ""),
                    "businessArea" to (unit.businessArea?.toString() ?: ""),
                    "licenseName" to (unit.licenseName ?: ""),
                    "licenseNo" to (unit.licenseNo ?: ""),
                    "gender" to (unit.gender ?: ""),
                    "nation" to (unit.nation ?: ""),
                    "post" to (unit.post ?: ""),
                    "idCard" to (unit.idCard ?: ""),
                    "birthday" to (unit.birthday?.toString() ?: ""),
                    "homeAddress" to (unit.homeAddress ?: "")
                )
                val result = Post<UnitResult>(ConfigApi.baseUrl + ConfigApi.appUnitAdd) {
                    body = OKHttpUtils.getRequestBody(params)
                }.await()
                if (result.code == ConfigApi.SUCCESS) {
                    Result.success(result.data.firstOrNull() ?: unit.toDTO())
                } else {
                    Result.failure(Exception(result.msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
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
    val remark: String?,
    val personName: String? = null,
    val registrationAddress: String? = null,
    val businessArea: Double? = null,
    val licenseName: String? = null,
    val licenseNo: String? = null,
    val gender: String? = null,
    val nation: String? = null,
    val post: String? = null,
    val idCard: String? = null,
    val birthday: Long? = null,
    val homeAddress: String? = null
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
            remark = remark,
            personName = personName,
            registrationAddress = registrationAddress,
            businessArea = businessArea,
            licenseName = licenseName,
            licenseNo = licenseNo,
            gender = gender,
            nation = nation,
            post = post,
            idCard = idCard,
            birthday = birthday,
            homeAddress = homeAddress
        )
    }
}
