package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 数据版本实体类
 * 用于增量同步，记录各表的数据版本
 */
@Entity(tableName = "sys_data_version")
data class DataVersionEntity(
    @PrimaryKey val id: Long,
    val tableName: String, // 表名
    val dataVersion: Long, // 数据版本号（时间戳）
    val lastSyncTime: Long, // 最后同步时间
    val syncType: String, // 同步类型：full（全量）, incremental（增量）
    val remark: String?
)
