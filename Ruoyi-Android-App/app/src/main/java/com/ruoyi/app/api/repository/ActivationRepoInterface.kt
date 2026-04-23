package com.ruoyi.app.api.repository

import com.ruoyi.app.data.database.entity.ActivationCodeEntity

/**
 * 激活码仓库接口
 */
interface ActivationRepoInterface {
    /**
     * 验证激活码并保存到 Room
     * @param codeValue 激活码值
     * @param deviceUuid 设备 UUID
     * @param deviceName 设备名称
     * @param deviceModel 设备型号
     * @param deviceOs 操作系统
     * @param appVersion App 版本
     * @return 验证结果
     */
    suspend fun validateAndSave(
        codeValue: String,
        deviceUuid: String,
        deviceName: String,
        deviceModel: String,
        deviceOs: String,
        appVersion: String
    ): Result<ActivationResponse>

    /**
     * 获取本地激活状态
     */
    suspend fun getActivationStatus(): ActivationStatus?

    /**
     * 同步离线数据到后端
     */
    suspend fun syncOfflineData(): List<ActivationCodeEntity>
}

/**
 * 激活响应数据
 */
data class ActivationResponse(
    val success: Boolean,
    val message: String,
    val activationCodeId: Long,
    val deviceCount: Int,
    val maxDeviceCount: Int,
    val expiryTime: Long
)

/**
 * 激活状态
 */
data class ActivationStatus(
    val isActivated: Boolean,
    val activationCodeId: Long?,
    val activationCodeValue: String?,
    val expiryTime: Long?
)
