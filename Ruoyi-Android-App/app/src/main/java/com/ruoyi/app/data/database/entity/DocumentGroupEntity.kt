package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 文书套组实体
 */
@Entity(tableName = "document_group")
data class DocumentGroupEntity(
    @PrimaryKey
    val id: Long,
    val groupCode: String,
    val groupName: String,
    val groupType: String?,
    val templates: String?,
    val isActive: String = "1",
    val syncTime: Long = System.currentTimeMillis()
)
