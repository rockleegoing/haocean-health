package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

/**
 * 规范用语项实体类
 */
@Entity(tableName = "t_phrase_item")
data class PhraseItemEntity(
    @PrimaryKey val itemId: Long,
    val bookId: Long,
    val itemName: String,
    val itemCode: String,
    val itemDesc: String?,
    val phaseType: String?,        // CHECK_BEFORE, CHECK_ING, CHECK_AFTER
    val sceneType: String?,
    val industryCode: String?,
    val sortOrder: Int,
    val status: String,
    val version: Int,
    val createTime: Long?,
    val updateTime: Long?,
    val delFlag: String,
    val bookName: String?         // 书本名称（来自JOIN查询）
)

/**
 * 规范用语项 FTS4 虚拟表（用于全文搜索）
 */
@Entity(tableName = "t_phrase_item_fts")
@Fts4(contentEntity = PhraseItemEntity::class)
data class PhraseItemFtsEntity(
    val itemName: String,
    val itemDesc: String?
)
