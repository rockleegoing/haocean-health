package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 法律类型绑定实体
 * 对应数据库表：law_type_bind
 */
@Entity(
    tableName = "law_type_bind",
    primaryKeys = ["lawId", "typeId"]
)
data class LawTypeBindEntity(
    val lawId: Long,          // 法律ID
    val typeId: Long,         // 类型ID
    val createTime: Long?     // 创建时间
)
