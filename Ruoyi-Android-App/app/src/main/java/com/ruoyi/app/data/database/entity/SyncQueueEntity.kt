package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 同步队列实体类
 * 用于记录需要同步到服务端的数据
 */
@Entity(tableName = "sys_sync_queue")
data class SyncQueueEntity(
    @PrimaryKey val queueId: Long,
    val syncType: String, // 同步类型：user_data, business_data
    val targetTable: String, // 目标表名
    val operation: String, // 操作类型：insert, update, delete
    val recordId: Long, // 记录 ID
    val jsonData: String, // 同步的数据（JSON 格式）
    val syncStatus: String, // 同步状态：pending, success, failed
    val retryCount: Int, // 重试次数
    val errorMessage: String?, // 错误信息
    val createTime: Long,
    val updateTime: Long,
    val remark: String?
)
