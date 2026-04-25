package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 文书模板实体
 */
@Entity(tableName = "document_template")
data class DocumentTemplateEntity(
    @PrimaryKey
    val id: Long,
    val templateCode: String,
    val templateName: String,
    val templateType: String?,
    val category: String?,
    val filePath: String?,
    val fileUrl: String?,
    val version: Int = 1,
    val isActive: String = "1",
    val syncTime: Long = System.currentTimeMillis()
)
