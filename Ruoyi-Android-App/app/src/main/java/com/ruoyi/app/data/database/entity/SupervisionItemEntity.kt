package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 监管事项实体
 */
@Entity(
    tableName = "supervision_item",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["name"])
    ]
)
data class SupervisionItemEntity(
    @PrimaryKey
    val itemId: Long,
    val itemNo: String?,
    val name: String,
    val parentId: Long = 0,
    val categoryId: Long?,
    val categoryName: String?,
    val description: String?,
    val legalBasis: String?,
    val sortOrder: Int = 0,
    val status: String = "0",
    val isCollected: Boolean = false,
    val syncTime: Long = System.currentTimeMillis()
)
