package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index

/**
 * 行业分类与监管事项绑定实体
 * 对应数据库表：regulatory_category_bind
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
    val industryCategoryId: Long,      // 行业分类ID
    val matterId: Long                 // 监管事项ID
)