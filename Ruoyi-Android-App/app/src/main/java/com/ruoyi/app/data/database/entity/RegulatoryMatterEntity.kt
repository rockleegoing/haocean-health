package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 监管事项实体
 */
@Entity(
    tableName = "regulatory_matter",
    indices = [Index(value = ["categoryId"])]
)
data class RegulatoryMatterEntity(
    @PrimaryKey val matterId: Long,
    val matterName: String,
    val categoryId: Long?,
    val description: String?,
    val status: String
)