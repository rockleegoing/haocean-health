package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index

/**
 * 监管事项分类绑定实体
 */
@Entity(
    tableName = "regulatory_category_bind",
    primaryKeys = ["industryCategoryId", "matterId"],
    indices = [
        Index(value = ["matterId"]),
        Index(value = ["industryCategoryId"])
    ]
)
data class RegulatoryCategoryBindEntity(
    val industryCategoryId: Long,
    val matterId: Long
)