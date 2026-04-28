package com.ruoyi.app.feature.law.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sys_supervision_type")
data class SupervisionTypeEntity(
    @PrimaryKey @ColumnInfo(name = "type_id") val typeId: Long,
    @ColumnInfo(name = "type_code") val typeCode: String,
    @ColumnInfo(name = "type_name") val typeName: String,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
    val status: String
)
