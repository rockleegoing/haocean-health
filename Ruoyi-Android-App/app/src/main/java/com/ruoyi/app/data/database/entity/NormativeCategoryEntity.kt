package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 规范用语分类实体
 * 对应数据库表：normative_category
 */
@Entity(
    tableName = "normative_category",
    indices = [Index(value = ["parentCode"]), Index(value = ["name"])]
)
data class NormativeCategoryEntity(
    @PrimaryKey val code: Long,       // 分类编号
    val name: String,                // 分类名称
    val parentCode: Long?,          // 父级分类编号
    val sortOrder: Int?,             // 排序号
    val status: String?             // 状态
)
