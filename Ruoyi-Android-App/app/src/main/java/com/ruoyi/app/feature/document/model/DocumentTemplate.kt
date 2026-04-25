package com.ruoyi.app.feature.document.model

/**
 * 文书模板数据模型
 */
data class DocumentTemplate(
    val id: Long,
    val templateCode: String,
    val templateName: String,
    val templateType: String?,
    val category: String?,
    val filePath: String?,
    val fileUrl: String?,
    val version: Int = 1,
    val isActive: String = "1"
)
