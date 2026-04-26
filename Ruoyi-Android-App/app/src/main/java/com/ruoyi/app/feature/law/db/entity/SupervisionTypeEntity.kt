package com.ruoyi.app.feature.law.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sys_supervision_type")
data class SupervisionTypeEntity(
    @PrimaryKey val typeId: Long,
    val typeCode: String,
    val typeName: String,
    val sortOrder: Int,
    val status: String
)
