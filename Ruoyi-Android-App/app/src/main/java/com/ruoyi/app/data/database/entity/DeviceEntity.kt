package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 设备实体类
 */
@Entity(tableName = "sys_device")
data class DeviceEntity(
    @PrimaryKey val deviceId: Long,
    val deviceUuid: String,
    val deviceName: String?,
    val deviceModel: String?,
    val deviceOs: String?,
    val appVersion: String?,
    val currentUserId: Long?,
    val currentUserName: String?,
    val activationCodeId: Long?,
    val lastSyncTime: Long?,
    val lastLoginTime: Long?,
    val lastLoginIp: String?,
    val status: String,
    val remark: String?,
    val createBy: String?,
    val createTime: Long,
    val updateBy: String?,
    val updateTime: Long?
)
