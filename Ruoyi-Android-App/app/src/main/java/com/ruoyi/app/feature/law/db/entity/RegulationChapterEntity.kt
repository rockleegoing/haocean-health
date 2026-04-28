package com.ruoyi.app.feature.law.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 法规章节实体类
 */
@Entity(tableName = "sys_regulation_chapter")
data class RegulationChapterEntity(
    @PrimaryKey @ColumnInfo(name = "chapter_id") val chapterId: Long,
    @ColumnInfo(name = "regulation_id") val regulationId: Long,
    @ColumnInfo(name = "chapter_no") val chapterNo: String?,
    @ColumnInfo(name = "chapter_title") val chapterTitle: String?,
    @ColumnInfo(name = "sort_order") val sortOrder: Int
)
