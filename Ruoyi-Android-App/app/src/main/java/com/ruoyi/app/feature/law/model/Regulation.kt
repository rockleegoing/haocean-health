package com.ruoyi.app.feature.law.model

/**
 * 法律法规数据模型
 */
data class Regulation(
    val regulationId: Long,
    val title: String,
    val legalType: String,
    val supervisionTypes: List<String>,
    val publishDate: String?,
    val effectiveDate: String?,
    val issuingAuthority: String?,
    val content: String?,
    val version: String,
    val status: String,
    val delFlag: String,
    val createBy: String?,
    val createTime: String?,
    val updateBy: String?,
    val updateTime: String?,
    val remark: String?
)

/**
 * 法律法规列表响应
 */
data class RegulationListResponse(
    val rows: List<Regulation>,
    val total: Int,
    val code: Int,
    val msg: String?
)

/**
 * 法律法规详情响应
 */
data class RegulationDetailResponse(
    val data: Regulation?,
    val code: Int,
    val msg: String?
)

/**
 * 法律类型枚举
 */
object LegalType {
    const val LAW = "法律"
    const val REGULATION = "法规"
    const val RULE = "规章"
    const val NORMATIVE_DOCUMENT = "规范性文件"
    const val APPROVAL_DOCUMENT = "批复文件"
    const val STANDARD = "标准"

    val ALL = listOf(LAW, REGULATION, RULE, NORMATIVE_DOCUMENT, APPROVAL_DOCUMENT, STANDARD)
}

/**
 * 监管类型
 */
object SupervisionType {
    const val FOOD_PRODUCTION = "食品生产"
    const val FOOD_SALES = "食品销售"
    const val CATERING_SERVICE = "餐饮服务"
    const val DRUG_OPERATION = "药品经营"
    const val MEDICAL_DEVICE = "医疗器械"
    const val COSMETICS = "化妆品"
    const val SPECIAL_EQUIPMENT = "特种设备"
    const val INDUSTRIAL_PRODUCT = "工业产品"
    const val METROLOGY_STANDARD = "计量标准"
    const val CERTIFICATION = "认证认可"
    const val INSPECTION_TESTING = "检验检测"
    const val ADVERTISING_SUPERVISION = "广告监管"
    const val INTELLECTUAL_PROPERTY = "知识产权"

    val ALL = listOf(
        FOOD_PRODUCTION, FOOD_SALES, CATERING_SERVICE, DRUG_OPERATION,
        MEDICAL_DEVICE, COSMETICS, SPECIAL_EQUIPMENT, INDUSTRIAL_PRODUCT,
        METROLOGY_STANDARD, CERTIFICATION, INSPECTION_TESTING,
        ADVERTISING_SUPERVISION, INTELLECTUAL_PROPERTY
    )
}
