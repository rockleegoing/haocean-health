package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 执法单位实体类
 */
@Entity(tableName = "sys_unit")
data class UnitEntity(
    @PrimaryKey val unitId: Long,
    val unitName: String,
    val industryCategoryId: Long?,   // 行业分类ID（外键）
    val industryCategoryName: String?, // 行业分类名称（来自JOIN查询）
    val region: String?,
    val supervisionType: String?,
    val creditCode: String?,
    val legalPerson: String?,
    val contactPhone: String?,
    val businessAddress: String?,
    val latitude: Double?,
    val longitude: Double?,
    val status: String,
    val delFlag: String,
    val createTime: Long,
    val updateTime: Long?,
    val remark: String?
)
