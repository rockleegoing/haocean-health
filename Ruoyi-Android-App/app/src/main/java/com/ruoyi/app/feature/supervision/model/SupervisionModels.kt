package com.ruoyi.app.feature.supervision.model

/**
 * 监管事项数据模型
 */
data class SupervisionItem(
    val itemId: Long = 0,
    val itemNo: String? = null,
    val name: String,
    val parentId: Long = 0,
    val categoryId: Long? = null,
    val categoryName: String? = null,
    val description: String? = null,
    val legalBasis: String? = null,
    val sortOrder: Int = 0,
    val status: String = "0"
)

/**
 * 监管类型数据模型
 */
data class SupervisionCategory(
    val categoryId: Long = 0,
    val categoryName: String,
    val categoryCode: String? = null,
    val icon: String? = null,
    val sortOrder: Int = 0,
    val status: String = "0"
)

/**
 * 规范用语关联
 */
data class SupervisionLanguageLink(
    val linkId: Long = 0,
    val itemId: Long = 0,
    val languageId: Long = 0,
    val languageName: String? = null,
    val languageContent: String? = null,
    val sortOrder: Int = 0
)

/**
 * 法律法规关联
 */
data class SupervisionRegulationLink(
    val linkId: Long = 0,
    val itemId: Long = 0,
    val regulationId: Long = 0,
    val regulationName: String? = null,
    val lawCode: String? = null,
    val sortOrder: Int = 0
)

/**
 * 首页数据响应
 */
data class SupervisionHomeResponse(
    val categories: List<SupervisionCategory> = emptyList(),
    val topItems: List<SupervisionItem> = emptyList()
)

/**
 * 事项详情响应
 */
data class SupervisionDetailResponse(
    val item: SupervisionItem? = null,
    val languageLinks: List<SupervisionLanguageLink> = emptyList(),
    val regulationLinks: List<SupervisionRegulationLink> = emptyList()
)

/**
 * API响应基类
 */
data class SupervisionApiResponse(
    val code: Int = 0,
    val msg: String? = null,
    val data: Any? = null
)
