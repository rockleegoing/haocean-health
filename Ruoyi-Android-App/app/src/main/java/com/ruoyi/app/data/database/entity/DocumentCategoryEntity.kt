package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 文书分类实体
 */
@Entity(tableName = "document_category")
data class DocumentCategoryEntity(
    @PrimaryKey
    val categoryId: Long,
    val categoryName: String,
    val displayType: String,
    val sort: Int
)
