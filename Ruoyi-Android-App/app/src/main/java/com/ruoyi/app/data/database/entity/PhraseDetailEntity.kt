package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

/**
 * 规范用语项明细实体类
 */
@Entity(tableName = "t_phrase_detail")
data class PhraseDetailEntity(
    @PrimaryKey val detailId: Long,
    val itemId: Long,
    val detailTitle: String,
    val detailContent: String,
    val detailType: String?,    // TEXT, HTML
    val sortOrder: Int,
    val version: Int,
    val createTime: Long?,
    val updateTime: Long?,
    val delFlag: String,
    val itemName: String?      // 项名称（来自JOIN查询）
)

/**
 * 规范用语项明细 FTS4 虚拟表（用于全文搜索）
 */
@Entity(tableName = "t_phrase_detail_fts")
@Fts4(contentEntity = PhraseDetailEntity::class)
data class PhraseDetailFtsEntity(
    val detailTitle: String,
    val detailContent: String
)
