package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 监管事项检查项实体
 * 对应数据库表：regulatory_matter_item
 */
@Entity(
    tableName = "regulatory_matter_item",
    indices = [Index(value = ["matterId"])]
)
data class RegulatoryMatterItemEntity(
    @PrimaryKey val itemId: Long,       // 检查项ID
    val matterId: Long,                // 所属监管事项ID
    val itemNo: String?,              // 监管子项编码
    val name: String,                  // 检查项名称
    val description: String?,           // 描述/检查内容
    val legalBasis: String?            // 法律依据
)