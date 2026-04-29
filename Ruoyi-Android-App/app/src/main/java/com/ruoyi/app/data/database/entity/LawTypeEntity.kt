package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 法律类型实体
 * 对应数据库表：law_type
 */
@Entity(
    tableName = "law_type",
    indices = [Index(value = ["parentId"])]
)
data class LawTypeEntity(
    @PrimaryKey val id: Long,           // 主键
    val parentId: Long,                 // 父类型ID（0为顶级）
    val ancestors: String,              // 祖先路径（格式：0,1,2）
    val name: String,                   // 类型名称
    val icon: String?,                  // 图标
    val sort: Int,                     // 排序
    val status: String                 // 状态（0正常 1停用）
)
