package com.ruoyi.app.feature.law.model

import com.ruoyi.app.feature.law.db.entity.LegalBasisContentEntity

/**
 * 定性依据数据模型
 */
data class LegalBasis(
    val basisId: Long,
    val basisNo: String?,
    val title: String,
    val violationType: String?,
    val issuingAuthority: String?,
    val effectiveDate: String?,
    val legalLevel: String?,
    val clauses: String?,
    val legalLiability: String?,
    val discretionStandard: String?,
    val regulationId: Long?,
    val status: String,
    val delFlag: String,
    val createBy: String?,
    val createTime: String?,
    val updateBy: String?,
    val updateTime: String?
)

/**
 * 定性依据列表响应
 */
data class LegalBasisListResponse(
    val rows: List<LegalBasis>,
    val total: Int,
    val code: Int,
    val msg: String?
)

/**
 * 定性依据详情响应
 */
data class LegalBasisDetailResponse(
    val code: Int,
    val msg: String?,
    val data: LegalBasisDetailData?
)

data class LegalBasisDetailData(
    val basis: LegalBasis,
    val contents: List<LegalBasisContent>
)

/**
 * 定性依据内容
 */
data class LegalBasisContent(
    val contentId: Long,
    val basisId: Long,
    val label: String,
    val content: String?,
    val sortOrder: Int
)

/**
 * LegalBasisContent 转换为 LegalBasisContentEntity
 */
fun LegalBasisContent.toEntity(): LegalBasisContentEntity {
    return LegalBasisContentEntity(
        contentId = contentId,
        basisId = basisId,
        label = label,
        content = content,
        sortOrder = sortOrder,
        createBy = null,
        createTime = null,
        updateBy = null,
        updateTime = null
    )
}
