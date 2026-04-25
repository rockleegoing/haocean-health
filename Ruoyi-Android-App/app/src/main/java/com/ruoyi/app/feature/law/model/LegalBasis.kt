package com.ruoyi.app.feature.law.model

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
    val data: LegalBasis?,
    val code: Int,
    val msg: String?
)
