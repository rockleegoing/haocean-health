package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 监管事项实体
 * 对应数据库表：regulatory_matter
 */
@Entity(
    tableName = "regulatory_matter",
    indices = [Index(value = ["categoryId"])]
)
data class RegulatoryMatterEntity(
    @PrimaryKey val matterId: Long,      // 监管事项ID
    val matterName: String,             // 监管事项名称
    val categoryId: Long?,              // 行业分类ID
    val description: String?,           // 描述
    val status: String?                // 状态
)