package com.ruoyi.app.feature.law.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 定性依据实体类
 */
@Entity(tableName = "sys_legal_basis")
data class LegalBasisEntity(
    @PrimaryKey @ColumnInfo(name = "basis_id") val basisId: Long,
    val title: String,
    @ColumnInfo(name = "regulation_id") val regulationId: Long?,
    val status: String,
    @ColumnInfo(name = "del_flag") val delFlag: String,
    @ColumnInfo(name = "create_by") val createBy: String?,
    @ColumnInfo(name = "create_time") val createTime: Long?,
    @ColumnInfo(name = "update_by") val updateBy: String?,
    @ColumnInfo(name = "update_time") val updateTime: Long?,
    val remark: String?,
    val syncStatus: String = "SYNCED"
)
