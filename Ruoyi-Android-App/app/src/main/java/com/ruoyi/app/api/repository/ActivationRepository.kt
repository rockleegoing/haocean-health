package com.ruoyi.app.api.repository

import android.content.Context
import com.drake.net.Post
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.api.OKHttpUtils
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.dao.ActivationCodeDao
import com.ruoyi.app.data.database.dao.DeviceDao
import com.ruoyi.app.data.database.entity.ActivationCodeEntity
import com.ruoyi.app.data.database.entity.DeviceEntity
import com.ruoyi.app.model.entity.ResultEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 激活码仓库实现类
 */
class ActivationRepository(
    private val context: Context
) : ActivationRepoInterface {

    companion object {
        private const val TAG = "ActivationRepository"
    }

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    private val activationCodeDao: ActivationCodeDao by lazy {
        database.activationCodeDao()
    }

    private val deviceDao: DeviceDao by lazy {
        database.deviceDao()
    }

    override suspend fun validateAndSave(
        codeValue: String,
        deviceUuid: String,
        deviceName: String,
        deviceModel: String,
        deviceOs: String,
        appVersion: String
    ): Result<ActivationResponse> = withContext(Dispatchers.IO) {
        try {
            // 1. 调用后端 API 验证激活码
            val params = mapOf(
                "codeValue" to codeValue,
                "deviceUuid" to deviceUuid,
                "deviceName" to deviceName,
                "deviceModel" to deviceModel,
                "deviceOs" to deviceOs,
                "appVersion" to appVersion
            )

            val response = Post<ResultEntity<ActivationApiResponse>>(
                ConfigApi.baseUrl + "/device/activationCode/validate"
            ) {
                body = OKHttpUtils.getRequestBody(params)
            }.await()

            if (response.code == 200 && response.data != null) {
                val apiData = response.data

                // 后端返回 success=false 表示业务逻辑失败（如激活码不存在、已使用等）
                if (!apiData.success) {
                    return@withContext Result.failure(Exception(apiData.message))
                }

                // success=true，验证成功，保存到 Room
                val codeEntity = ActivationCodeEntity(
                    codeId = apiData.activationCodeId,
                    codeValue = codeValue,
                    status = "1", // 已使用
                    expireTime = apiData.expiryTime,
                    bindDeviceId = deviceUuid,
                    bindUserId = null,
                    remark = "",
                    createBy = "system",
                    createTime = System.currentTimeMillis(),
                    updateBy = null,
                    updateTime = null,
                    validDays = apiData.validDays
                )
                activationCodeDao.insertActivationCode(codeEntity)

                // 3. 保存或更新设备信息
                val deviceEntity = DeviceEntity(
                    deviceId = apiData.activationCodeId, // 临时使用激活码 ID
                    deviceUuid = deviceUuid,
                    deviceName = deviceName,
                    deviceModel = deviceModel,
                    deviceOs = deviceOs,
                    appVersion = appVersion,
                    currentUserId = null,
                    currentUserName = null,
                    activationCodeId = apiData.activationCodeId,
                    lastSyncTime = System.currentTimeMillis(),
                    lastLoginTime = System.currentTimeMillis(),
                    lastLoginIp = null,
                    status = "0", // 离线
                    remark = "",
                    createBy = "system",
                    createTime = System.currentTimeMillis(),
                    updateBy = null,
                    updateTime = null
                )
                deviceDao.insertDevice(deviceEntity)

                Result.success(
                    ActivationResponse(
                        success = true,
                        message = apiData.message,
                        activationCodeId = apiData.activationCodeId,
                        deviceCount = apiData.deviceCount,
                        maxDeviceCount = apiData.maxDeviceCount,
                        expiryTime = apiData.expiryTime,
                        validDays = apiData.validDays
                    )
                )
            } else {
                Result.failure(Exception(response.msg ?: "激活码验证失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getActivationStatus(): ActivationStatus? = withContext(Dispatchers.IO) {
        // 获取最新的已使用激活码
        val codes = activationCodeDao.getAllActivationCodes()
        val usedCode = codes.firstOrNull { it.status == "1" }

        if (usedCode != null) {
            ActivationStatus(
                isActivated = true,
                activationCodeId = usedCode.codeId,
                activationCodeValue = usedCode.codeValue,
                expiryTime = usedCode.expireTime
            )
        } else {
            null
        }
    }

    override suspend fun syncOfflineData(): List<ActivationCodeEntity> = withContext(Dispatchers.IO) {
        // 获取所有需要同步的激活码
        val codes = activationCodeDao.getAllActivationCodes()
        // 后续实现：筛选未同步到后端的记录并上传
        return@withContext codes
    }

    /**
     * 离线模式下预加载激活码
     */
    suspend fun preloadActivationCode(code: ActivationCodeEntity) {
        activationCodeDao.insertActivationCode(code)
    }

    /**
     * 检查激活码是否在本地有效
     */
    suspend fun isActivationCodeValidLocally(codeValue: String): Boolean {
        val code = activationCodeDao.getActivationCodeByValue(codeValue)
        return code != null && code.status == "0" &&
               (code.expireTime == null || code.expireTime > System.currentTimeMillis())
    }

    /**
     * 后端 API 响应数据结构
     */
    @kotlinx.serialization.Serializable
    data class ActivationApiResponse(
        val success: Boolean,
        val message: String,
        val codeId: Long = 0,
        val activatedCount: Int = 0,
        val maxDeviceCount: Int = 0,
        val expiryTime: Long = 0,
        val activateTime: Long = 0,
        val activateDevice: String = "",
        val validDays: Int = 0
    ) {
        val activationCodeId: Long get() = codeId
        val deviceCount: Int get() = activatedCount
    }
}
