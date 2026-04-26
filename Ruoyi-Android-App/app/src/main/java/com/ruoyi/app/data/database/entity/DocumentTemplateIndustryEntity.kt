package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 文书模板与行业分类关联实体
 */
@Entity(
    tableName = "document_template_industry",
    primaryKeys = ["templateId", "industryCategoryId"],
    indices = [Index("industryCategoryId")]
)
data class DocumentTemplateIndustryEntity(
    val templateId: Long,
    val industryCategoryId: Long
)
