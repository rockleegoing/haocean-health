package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 行业分类实体类
 * 一级扁平结构
 */
@Entity(tableName = "sys_industry_category")
data class IndustryCategoryEntity(
    @PrimaryKey val categoryId: Long,
    val categoryName: String,
    val categoryCode: String?,   // 分类编码
    val orderNum: Int,         // 显示顺序
    val status: String,        // 状态(0=正常,1=停用)
    val delFlag: String,       // 删除标志(0=存在,1=删除)
    val createBy: String?,
    val createTime: Long,
    val updateBy: String?,
    val updateTime: Long?,
    val remark: String?
)
