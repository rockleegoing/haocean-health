package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "normative_category",
    indices = [Index(value = ["parentCode"])]
)
data class NormativeCategoryEntity(
    @PrimaryKey val code: Long,
    val name: String,
    val parentCode: Long?,
    val sortOrder: Int,
    val status: String
)
