package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "t_enforcement_record")
data class EnforcementRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recordNo: String,                  // 记录编号
    val unitId: Long,                     // 单位 ID
    val unitName: String,                 // 单位名称
    val industryId: Long,                // 行业 ID
    val industryCode: String,             // 行业代码
    val recordType: String,               // 记录类型
    val recordStatus: String,             // 记录状态
    val description: String?,             // 备注说明
    val longitude: Double?,              // 经度
    val latitude: Double?,                // 纬度
    val locationName: String?,            // 位置名称
    val syncStatus: String = "PENDING",  // 同步状态
    val createBy: String,                 // 创建人
    val createTime: Long,                 // 创建时间
    val updateBy: String?,                // 更新人
    val updateTime: Long?,                // 更新时间
    val delFlag: String = "0",           // 删除标志
    // 以下为非持久化字段，用于列表展示
    @Ignore
    val photoCount: Int = 0,              // 照片数量（非持久化）
    @Ignore
    val audioCount: Int = 0,              // 录音数量（非持久化）
    @Ignore
    val videoCount: Int = 0               // 录像数量（非持久化）
)