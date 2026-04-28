package com.ruoyi.app.feature.law.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 定性依据内容实体
 */
@Entity(
    tableName = "sys_legal_basis_content",
    foreignKeys = [
        ForeignKey(
            entity = LegalBasisEntity::class,
            parentColumns = ["basis_id"],
            childColumns = ["basis_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LegalBasisContentEntity(
    @PrimaryKey @ColumnInfo(name = "content_id") val contentId: Long,
    @ColumnInfo(name = "basis_id") val basisId: Long,
    @ColumnInfo(name = "label") val label: String,
    @ColumnInfo(name = "content") val content: String?,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
    @ColumnInfo(name = "create_by") val createBy: String?,
    @ColumnInfo(name = "create_time") val createTime: Long?,
    @ColumnInfo(name = "update_by") val updateBy: String?,
    @ColumnInfo(name = "update_time") val updateTime: Long?
)
