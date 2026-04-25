package com.ruoyi.app.feature.document.model

/**
 * 文书分类数据模型
 */
data class DocumentCategory(
    val categoryId: Long,
    val categoryName: String,
    val displayType: String,
    val sort: Int,
    val status: String,
    val createTime: String?
)
