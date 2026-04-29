package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "law",
    indices = [Index(value = ["name"])]
)
data class LawEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val releaseTime: Long?  // 时间戳，毫秒
)
