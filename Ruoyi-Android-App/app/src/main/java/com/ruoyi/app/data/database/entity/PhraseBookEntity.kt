package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

/**
 * 规范用语书本实体类
 */
@Entity(tableName = "t_phrase_book")
data class PhraseBookEntity(
    @PrimaryKey val bookId: Long,
    val bookName: String,
    val bookCode: String,
    val bookDesc: String?,
    val industryCode: String?,
    val industryName: String?,
    val coverUrl: String?,
    val sortOrder: Int,
    val status: String,
    val version: Int,
    val createTime: Long?,
    val updateTime: Long?,
    val delFlag: String
)

/**
 * 规范用语书本 FTS4 虚拟表（用于全文搜索）
 */
@Entity(tableName = "t_phrase_book_fts")
@Fts4(contentEntity = PhraseBookEntity::class)
data class PhraseBookFtsEntity(
    val bookName: String,
    val bookDesc: String
)
