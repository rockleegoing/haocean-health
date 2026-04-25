package com.ruoyi.app.feature.law.model

/**
 * 法规条款数据模型
 */
data class RegulationArticle(
    val articleId: Long,
    val chapterId: Long?,
    val regulationId: Long,
    val articleNo: String?,
    val content: String?,
    val sortOrder: Int
)

/**
 * 条款列表响应
 */
data class ArticleListResponse(
    val rows: List<RegulationArticle>,
    val total: Int,
    val code: Int,
    val msg: String?
)
