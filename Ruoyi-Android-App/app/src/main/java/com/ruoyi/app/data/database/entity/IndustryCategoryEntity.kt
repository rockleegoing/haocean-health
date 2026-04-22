package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 行业分类实体类
 */
@Entity(tableName = "sys_industry_category")
data class IndustryCategoryEntity(
    @PrimaryKey val categoryId: Long,
    val categoryName: String,
    val parentCode: String,
    val categoryCode: String,
    val level: Int,
    val status: String,
    val createBy: String?,
    val createTime: Long,
    val updateBy: String?,
    val updateTime: Long?,
    val remark: String?
)
