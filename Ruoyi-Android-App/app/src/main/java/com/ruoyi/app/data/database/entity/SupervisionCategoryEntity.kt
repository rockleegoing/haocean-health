package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 监管类型实体
 */
@Entity(tableName = "supervision_category")
data class SupervisionCategoryEntity(
    @PrimaryKey
    val categoryId: Long,
    val categoryName: String,
    val categoryCode: String?,
    val icon: String?,
    val sortOrder: Int = 0,
    val status: String = "0",
    val syncTime: Long = System.currentTimeMillis()
)
