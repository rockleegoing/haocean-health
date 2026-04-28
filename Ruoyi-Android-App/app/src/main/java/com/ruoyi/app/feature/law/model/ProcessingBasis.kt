package com.ruoyi.app.feature.law.model

/**
 * 处理依据数据模型
 */
data class ProcessingBasis(
    val basisId: Long,
    val basisNo: String?,
    val title: String?,
    val violationType: String?,
    val issuingAuthority: String?,
    val effectiveDate: String?,
    val legalLevel: String?,
    val clauses: String?,
    val legalLiability: String?,
    val discretionStandard: String?,
    val regulationId: Long?,
    val status: String?,
    val delFlag: String?,
    val createBy: String?,
    val createTime: String?,
    val updateBy: String?,
    val updateTime: String?,
    val remark: String?
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
    val data: ProcessingBasisDetailData?,
    val code: Int,
    val msg: String?
)

data class ProcessingBasisDetailData(
    val basis: ProcessingBasis,
    val contents: List<ProcessingBasisContent>
)

/**
 * 处理依据内容
 */
data class ProcessingBasisContent(
    val contentId: Long,
    val basisId: Long,
    val label: String,
    val content: String?,
    val sortOrder: Int
)

/**
 * ProcessingBasisContent 转换为 ProcessingBasisContentEntity
 */
fun ProcessingBasisContent.toEntity(): com.ruoyi.app.feature.law.db.entity.ProcessingBasisContentEntity {
    return com.ruoyi.app.feature.law.db.entity.ProcessingBasisContentEntity(
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

/**
 * 章节-依据关联数据模型
 */
data class BasisChapterLink(
    val linkId: Long,
    val chapterId: Long?,
    val articleId: Long?,
    val basisType: String?,  // legal: 定性依据, processing: 处理依据
    val basisId: Long?,
    val createBy: String?,
    val createTime: String?,
    val updateBy: String?,
    val updateTime: String?
)

/**
 * 章节-依据关联列表响应
 */
data class BasisChapterLinkListResponse(
    val rows: List<BasisChapterLink>,
    val total: Int,
    val code: Int,
    val msg: String?
)

/**
 * 章节-依据关联详情响应
 */
data class BasisChapterLinkDetailResponse(
    val data: BasisChapterLink?,
    val code: Int,
    val msg: String?
)
