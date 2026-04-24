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
    val remark: String?,
    val personName: String?,           // 当事人
    val registrationAddress: String?,   // 注册地址
    val businessArea: Double?,          // 经营面积
    val licenseName: String?,           // 许可证名称
    val licenseNo: String?,             // 许可证号
    val gender: String?,                // 0男,1女
    val nation: String?,                // 民族
    val post: String?,                  // 职务
    val idCard: String?,               // 身份证
    val birthday: Long?,               // 出生年月
    val homeAddress: String?,           // 家庭住址
    val syncStatus: String = "PENDING"  // 同步状态：PENDING=待同步, SYNCED=已同步
)
