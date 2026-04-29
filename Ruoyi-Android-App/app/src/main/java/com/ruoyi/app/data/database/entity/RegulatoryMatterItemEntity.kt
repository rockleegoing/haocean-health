package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "regulatory_matter_item",
    indices = [Index(value = ["matterId"])]
)
data class RegulatoryMatterItemEntity(
    @PrimaryKey val itemId: Long,
    val matterId: Long,
    val itemNo: String?,
    val name: String,
    val description: String?,
    val legalBasis: String?
)