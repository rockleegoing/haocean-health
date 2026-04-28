package com.ruoyi.app.sync

import com.ruoyi.app.feature.law.db.entity.*
import com.ruoyi.app.feature.law.model.*
import com.ruoyi.app.feature.law.api.*
import org.json.JSONArray

/**
 * sync 包扩展函数 - 提供 model 到 entity 的转换
 * 解决 LawSyncManager 无法导入 repository 包扩展函数的问题
 */

// Regulation toEntity
fun Regulation.toEntity(): RegulationEntity {
    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.CHINA)
    return RegulationEntity(
        regulationId = regulationId,
        title = title,
        legalType = legalType,
        supervisionTypes = JSONArray(supervisionTypes).toString(),
        publishDate = publishDate,
        effectiveDate = effectiveDate,
        issuingAuthority = issuingAuthority,
        content = content,
        version = version,
        status = status,
        delFlag = delFlag,
        createBy = createBy,
        createTime = createTime?.let { try { dateFormat.parse(it)?.time } catch (e: Exception) { null } },
        updateBy = updateBy,
        updateTime = updateTime?.let { try { dateFormat.parse(it)?.time } catch (e: Exception) { null } },
        remark = remark
    )
}

// RegulationChapter toEntity
fun RegulationChapter.toEntity(): RegulationChapterEntity {
    return RegulationChapterEntity(
        chapterId = chapterId,
        regulationId = regulationId,
        chapterNo = chapterNo,
        chapterTitle = chapterTitle,
        sortOrder = sortOrder
    )
}

// RegulationArticle toEntity
fun RegulationArticle.toEntity(): RegulationArticleEntity {
    return RegulationArticleEntity(
        articleId = articleId,
        chapterId = chapterId,
        regulationId = regulationId,
        articleNo = articleNo,
        content = content,
        sortOrder = sortOrder
    )
}

// LegalBasis toEntity
fun LegalBasis.toEntity(): LegalBasisEntity {
    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.CHINA)
    return LegalBasisEntity(
        basisId = basisId,
        title = title,
        regulationId = regulationId,
        status = status,
        delFlag = delFlag,
        createBy = createBy,
        createTime = createTime?.let { try { dateFormat.parse(it)?.time } catch (e: Exception) { null } },
        updateBy = updateBy,
        updateTime = updateTime?.let { try { dateFormat.parse(it)?.time } catch (e: Exception) { null } },
        remark = null
    )
}

// ProcessingBasis toEntity
fun ProcessingBasis.toEntity(): ProcessingBasisEntity {
    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.CHINA)
    return ProcessingBasisEntity(
        basisId = basisId,
        title = title,
        regulationId = regulationId,
        status = status,
        delFlag = delFlag,
        createBy = createBy,
        createTime = createTime?.let { try { dateFormat.parse(it)?.time } catch (e: Exception) { null } },
        updateBy = updateBy,
        updateTime = updateTime?.let { try { dateFormat.parse(it)?.time } catch (e: Exception) { null } },
        remark = remark
    )
}

// BasisChapterLink toEntity
fun BasisChapterLink.toEntity(): BasisChapterLinkEntity {
    return BasisChapterLinkEntity(
        linkId = linkId,
        chapterId = chapterId,
        articleId = articleId,
        basisType = basisType,
        basisId = basisId,
        createBy = createBy,
        createTime = createTime,
        updateBy = updateBy,
        updateTime = updateTime
    )
}

// LegalTypeResponse toEntity
fun LegalTypeResponse.toEntity(): LegalTypeEntity {
    return LegalTypeEntity(
        typeId = typeId,
        typeCode = typeCode,
        typeName = typeName,
        sortOrder = sortOrder,
        status = status
    )
}

// SupervisionTypeResponse toEntity
fun SupervisionTypeResponse.toEntity(): SupervisionTypeEntity {
    return SupervisionTypeEntity(
        typeId = typeId,
        typeCode = typeCode,
        typeName = typeName,
        sortOrder = sortOrder,
        status = status
    )
}

// LegalBasisContent toEntity
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

// ProcessingBasisContent toEntity
fun ProcessingBasisContent.toEntity(): ProcessingBasisContentEntity {
    return ProcessingBasisContentEntity(
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
