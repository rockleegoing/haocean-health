package com.ruoyi.app.feature.law.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 章节-依据关联实体
 */
@Entity(tableName = "sys_basis_chapter_link")
data class BasisChapterLinkEntity(
    @PrimaryKey
    @ColumnInfo(name = "link_id")
    val linkId: Long,

    @ColumnInfo(name = "chapter_id")
    val chapterId: Long?,

    @ColumnInfo(name = "article_id")
    val articleId: Long?,

    @ColumnInfo(name = "basis_type")
    val basisType: String?,  // legal: 定性依据, processing: 处理依据

    @ColumnInfo(name = "basis_id")
    val basisId: Long?,

    @ColumnInfo(name = "create_by")
    val createBy: String?,

    @ColumnInfo(name = "create_time")
    val createTime: String?,

    @ColumnInfo(name = "update_by")
    val updateBy: String?,

    @ColumnInfo(name = "update_time")
    val updateTime: String?
)
