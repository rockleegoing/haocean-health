package com.ruoyi.app.feature.law.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 法规条款实体类
 */
@Entity(tableName = "sys_regulation_article")
data class RegulationArticleEntity(
    @PrimaryKey val articleId: Long,
    val chapterId: Long?,
    val regulationId: Long,
    val articleNo: String?,
    val content: String?,
    val sortOrder: Int
)
