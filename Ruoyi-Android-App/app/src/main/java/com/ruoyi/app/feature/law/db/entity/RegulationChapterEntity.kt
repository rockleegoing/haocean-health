package com.ruoyi.app.feature.law.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 法规章节实体类
 */
@Entity(tableName = "sys_regulation_chapter")
data class RegulationChapterEntity(
    @PrimaryKey val chapterId: Long,
    val regulationId: Long,
    val chapterNo: String?,
    val chapterTitle: String?,
    val sortOrder: Int
)
