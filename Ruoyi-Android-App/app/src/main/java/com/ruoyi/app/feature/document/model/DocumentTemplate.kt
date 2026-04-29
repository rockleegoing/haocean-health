package com.ruoyi.app.feature.document.model

/**
 * 文书模板数据模型
 */
data class DocumentTemplate(
    val id: Long,                    // 模板ID
    val templateCode: String,        // 模板编码
    val templateName: String,       // 模板名称
    val templateType: String?,       // 模板类型
    val category: String?,          // 所属分类
    val categoryId: Long = 0,       // 分类ID
    val filePath: String?,          // 本地文件路径
    val fileUrl: String?,           // 远程文件URL
    val version: Int = 1,           // 版本号
    val isActive: String = "1"      // 是否启用
)
