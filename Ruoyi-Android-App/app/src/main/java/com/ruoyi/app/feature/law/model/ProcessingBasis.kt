package com.ruoyi.app.feature.law.model

/**
 * 处理依据数据模型
 */
data class ProcessingBasis(
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
 * 处理依据列表响应
 */
data class ProcessingBasisListResponse(
    val rows: List<ProcessingBasis>,
    val total: Int,
    val code: Int,
    val msg: String?
)

/**
 * 处理依据详情响应
 */
data class ProcessingBasisDetailResponse(
    val data: ProcessingBasis?,
    val code: Int,
    val msg: String?
)
