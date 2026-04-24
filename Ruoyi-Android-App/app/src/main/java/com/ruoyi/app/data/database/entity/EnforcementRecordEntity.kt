package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "t_enforcement_record")
data class EnforcementRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recordNo: String,
    val unitId: Long,
    val unitName: String,
    val industryId: Long,
    val industryCode: String,
    val recordType: String,
    val recordStatus: String,
    val description: String?,
    val longitude: Double?,
    val latitude: Double?,
    val locationName: String?,
    val syncStatus: String = "PENDING",
    val createBy: String,
    val createTime: Long,
    val updateBy: String?,
    val updateTime: Long?,
    val delFlag: String = "0"
) {
    // 以下为非持久化字段，用于列表展示
    @Ignore
    var photoCount: Int = 0

    @Ignore
    var audioCount: Int = 0

    @Ignore
    var videoCount: Int = 0
}
