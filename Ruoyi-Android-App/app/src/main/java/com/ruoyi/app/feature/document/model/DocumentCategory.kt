package com.ruoyi.app.feature.document.model

/**
 * 文书分类数据模型
 */
data class DocumentCategory(
    val categoryId: Long,           // 分类ID
    val categoryName: String,       // 分类名称
    val displayType: String,       // 显示类型
    val sort: Int,                 // 排序号
    val status: String,            // 状态
    val createTime: String?         // 创建时间
)
