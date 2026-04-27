package com.ruoyi.app.feature.law.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 依据章节关联实体类
 */
@Entity(tableName = "sys_basis_chapter_link")
data class BasisChapterLinkEntity(
    @PrimaryKey val linkId: Long,
    val basisType: String,  // 'legal' 或 'processing'
    val basisId: Long,
    val chapterId: Long,
    val articleId: Long?,
    val createBy: String?,
    val createTime: Long?,
    val syncStatus: String = "SYNCED"
)
