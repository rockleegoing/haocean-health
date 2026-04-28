package com.ruoyi.app.feature.law.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 法规条款实体类
 */
@Entity(tableName = "sys_regulation_article")
data class RegulationArticleEntity(
    @PrimaryKey @ColumnInfo(name = "article_id") val articleId: Long,
    @ColumnInfo(name = "chapter_id") val chapterId: Long?,
    @ColumnInfo(name = "regulation_id") val regulationId: Long,
    @ColumnInfo(name = "article_no") val articleNo: String?,
    val content: String?,
    @ColumnInfo(name = "sort_order") val sortOrder: Int
)
