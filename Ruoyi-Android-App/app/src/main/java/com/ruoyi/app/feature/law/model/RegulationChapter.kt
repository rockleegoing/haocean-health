package com.ruoyi.app.feature.law.model

/**
 * 法规章节数据模型
 */
data class RegulationChapter(
    val chapterId: Long,
    val regulationId: Long,
    val chapterNo: String?,
    val chapterTitle: String?,
    val sortOrder: Int
)

/**
 * 章节列表响应
 */
data class ChapterListResponse(
    val rows: List<RegulationChapter>,
    val total: Int,
    val code: Int,
    val msg: String?
)
