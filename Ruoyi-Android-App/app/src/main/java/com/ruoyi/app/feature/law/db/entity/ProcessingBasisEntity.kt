package com.ruoyi.app.feature.law.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 处理依据实体类
 */
@Entity(tableName = "sys_processing_basis")
data class ProcessingBasisEntity(
    @PrimaryKey val basisId: Long,
    val basisNo: String?,
    val title: String,
    val violationType: String?,
    val issuingAuthority: String?,
    val effectiveDate: String?,
    val legalLevel: String?,
    val clauses: String?,
    val legalLiability: String?,
    val discretionStandard: String?,
    val regulationId: Long?,
    val status: String,
    val delFlag: String,
    val createBy: String?,
    val createTime: Long?,
    val updateBy: String?,
    val updateTime: Long?,
    val syncStatus: String = "SYNCED"
)
