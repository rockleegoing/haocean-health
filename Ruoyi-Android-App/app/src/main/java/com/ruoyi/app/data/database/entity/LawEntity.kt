package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 法律目录实体
 * 对应数据库表：law
 */
@Entity(
    tableName = "law",
    indices = [Index(value = ["name"]), Index(value = ["typeId"])]
)
data class LawEntity(
    @PrimaryKey val id: Long,           // 法律ID
    val name: String,                    // 法律名称
    val releaseTime: Long?,              // 发布日期
    val typeId: Long?                    // 法律法规类型ID
)
