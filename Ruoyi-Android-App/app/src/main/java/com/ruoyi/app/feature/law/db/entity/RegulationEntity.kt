package com.ruoyi.app.feature.law.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 法律法规实体类
 */
@Entity(tableName = "sys_regulation")
data class RegulationEntity(
    @PrimaryKey @ColumnInfo(name = "regulation_id") val regulationId: Long,
    val title: String,
    @ColumnInfo(name = "legal_type") val legalType: String,
    @ColumnInfo(name = "supervision_types") val supervisionTypes: String?, // JSON数组存储
    @ColumnInfo(name = "publish_date") val publishDate: String?,
    @ColumnInfo(name = "effective_date") val effectiveDate: String?,
    @ColumnInfo(name = "issuing_authority") val issuingAuthority: String?,
    val content: String?,
    val version: String,
    val status: String,
    @ColumnInfo(name = "del_flag") val delFlag: String,
    @ColumnInfo(name = "create_by") val createBy: String?,
    @ColumnInfo(name = "create_time") val createTime: Long?,
    @ColumnInfo(name = "update_by") val updateBy: String?,
    @ColumnInfo(name = "update_time") val updateTime: Long?,
    val remark: String?,
    val syncStatus: String = "SYNCED" // 同步状态：PENDING=待同步, SYNCED=已同步
)
