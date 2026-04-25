package com.ruoyi.app.feature.law.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 法律法规实体类
 */
@Entity(tableName = "sys_regulation")
data class RegulationEntity(
    @PrimaryKey val regulationId: Long,
    val title: String,
    val legalType: String,
    val supervisionTypes: String?, // JSON数组存储
    val publishDate: String?,
    val effectiveDate: String?,
    val issuingAuthority: String?,
    val content: String?,
    val version: String,
    val status: String,
    val delFlag: String,
    val createBy: String?,
    val createTime: Long?,
    val updateBy: String?,
    val updateTime: Long?,
    val remark: String?,
    val syncStatus: String = "SYNCED" // 同步状态：PENDING=待同步, SYNCED=已同步
)
