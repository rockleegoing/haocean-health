package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 激活码实体类
 */
@Entity(tableName = "sys_activation_code")
data class ActivationCodeEntity(
    @PrimaryKey val codeId: Long,
    val codeValue: String,
    val status: String,
    val expireTime: Long?,
    val bindDeviceId: String?,
    val bindUserId: Long?,
    val remark: String?,
    val createBy: String?,
    val createTime: Long,
    val updateBy: String?,
    val updateTime: Long?
)
