package com.ruoyi.app.feature.document.model

/**
 * 文书分组数据模型
 */
data class DocumentGroup(
    val id: Long,                // 分组ID
    val groupCode: String,      // 分组编码
    val groupName: String,      // 分组名称
    val groupType: String?,     // 分组类型
    val templates: String?,     // 关联的模板编码列表
    val isActive: String = "1"  // 是否启用
)

/**
 * 分组关联的模板
 */
data class GroupTemplate(
    val code: String,           // 模板编码
    val required: Boolean,      // 是否必选
    val order: Int              // 排序序号
)
