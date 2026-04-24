package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "t_evidence_material",
    indices = [Index("recordId")]
)
data class EvidenceMaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recordId: Long,                   // 关联记录 ID
    val evidenceType: String,             // 类型：photo/audio/video
    val filePath: String,                // 文件路径
    val fileName: String?,                // 文件名
    val fileSize: Long?,                 // 文件大小
    val duration: Int?,                   // 时长（音视频）
    val description: String?,              // 描述
    val syncStatus: String = "PENDING",   // 同步状态
    val createTime: Long                  // 创建时间
)