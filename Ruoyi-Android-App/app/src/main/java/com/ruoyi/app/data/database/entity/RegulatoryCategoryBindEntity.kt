package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "regulatory_category_bind",
    primaryKeys = ["industryCategoryId", "matterId"],
    indices = [Index(value = ["matterId"])]
)
data class RegulatoryCategoryBindEntity(
    val industryCategoryId: Long,
    val matterId: Long
)