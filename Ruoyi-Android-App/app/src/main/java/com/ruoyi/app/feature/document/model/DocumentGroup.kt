package com.ruoyi.app.feature.document.model

/**
 * 文书分组数据模型
 */
data class DocumentGroup(
    val id: Long,
    val groupCode: String,
    val groupName: String,
    val groupType: String?,
    val templates: String?,
    val isActive: String = "1"
)

/**
 * 分组关联的模板
 */
data class GroupTemplate(
    val code: String,
    val required: Boolean,
    val order: Int
)
