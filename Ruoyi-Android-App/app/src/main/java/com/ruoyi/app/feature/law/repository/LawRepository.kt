package com.ruoyi.app.feature.law.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.feature.law.api.LawApi
import com.ruoyi.app.feature.law.db.entity.LegalBasisEntity
import com.ruoyi.app.feature.law.db.entity.LegalTypeEntity
import com.ruoyi.app.feature.law.db.entity.ProcessingBasisEntity
import com.ruoyi.app.feature.law.db.entity.BasisChapterLinkEntity
import com.ruoyi.app.feature.law.db.entity.RegulationArticleEntity
import com.ruoyi.app.feature.law.db.entity.RegulationChapterEntity
import com.ruoyi.app.feature.law.db.entity.RegulationEntity
import com.ruoyi.app.feature.law.db.entity.SupervisionTypeEntity
import com.ruoyi.app.feature.law.db.dao.LegalBasisContentDao
import com.ruoyi.app.feature.law.db.dao.ProcessingBasisContentDao
import com.ruoyi.app.feature.law.db.entity.LegalBasisContentEntity
import com.ruoyi.app.feature.law.db.entity.ProcessingBasisContentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray

/**
 * 法律数据仓库
 */
class LawRepository(private val context: Context) {

    private val regulationDao = AppDatabase.getInstance(context).regulationDao()
    private val chapterDao = AppDatabase.getInstance(context).chapterDao()
    private val articleDao = AppDatabase.getInstance(context).articleDao()
    private val legalBasisDao = AppDatabase.getInstance(context).legalBasisDao()
    private val legalTypeDao = AppDatabase.getInstance(context).legalTypeDao()
    private val supervisionTypeDao = AppDatabase.getInstance(context).supervisionTypeDao()
    private val processingBasisDao = AppDatabase.getInstance(context).processingBasisDao()
    private val basisChapterLinkDao = AppDatabase.getInstance(context).basisChapterLinkDao()
    private val legalBasisContentDao = AppDatabase.getInstance(context).legalBasisContentDao()
    private val processingBasisContentDao = AppDatabase.getInstance(context).processingBasisContentDao()

    // ==================== 法律法规 ====================

    /**
     * 获取所有法律法规
     */
    fun getAllRegulations(): Flow<List<RegulationEntity>> {
        return regulationDao.getAllRegulations()
    }

    /**
     * 按法律类型获取法律法规
     */
    fun getRegulationsByLegalType(legalType: String): Flow<List<RegulationEntity>> {
        return regulationDao.getRegulationsByLegalType(legalType)
    }

    /**
     * 按监管类型获取法律法规
     */
    fun getRegulationsBySupervisionType(supervisionType: String): Flow<List<RegulationEntity>> {
        return regulationDao.getRegulationsBySupervisionType(supervisionType)
    }

    /**
     * 搜索法律法规
     */
    fun searchRegulations(keyword: String): Flow<List<RegulationEntity>> {
        return regulationDao.searchRegulations(keyword)
    }

    /**
     * 获取法律法规详情
     */
    suspend fun getRegulationById(regulationId: Long): RegulationEntity? {
        return regulationDao.getRegulationById(regulationId)
    }

    /**
     * 获取章节列表
     */
    fun getChaptersByRegulationId(regulationId: Long): Flow<List<RegulationChapterEntity>> {
        return chapterDao.getChaptersByRegulationId(regulationId)
    }

    /**
     * 获取条款列表
     */
    fun getArticlesByRegulationId(regulationId: Long): Flow<List<RegulationArticleEntity>> {
        return articleDao.getArticlesByRegulationId(regulationId)
    }

    /**
     * 获取条款详情
     */
    suspend fun getArticleById(articleId: Long): RegulationArticleEntity? {
        return articleDao.getArticleById(articleId)
    }

    // ==================== 定性依据 ====================

    /**
     * 获取所有定性依据
     */
    fun getAllLegalBasises(): Flow<List<LegalBasisEntity>> {
        return legalBasisDao.getAllLegalBasises()
    }

    /**
     * 按法规ID获取定性依据
     */
    fun getLegalBasisesByRegulationId(regulationId: Long): Flow<List<LegalBasisEntity>> {
        return legalBasisDao.getLegalBasisesByRegulationId(regulationId)
    }

    /**
     * 搜索定性依据
     */
    fun searchLegalBasises(keyword: String): Flow<List<LegalBasisEntity>> {
        return legalBasisDao.searchLegalBasises(keyword)
    }

    /**
     * 获取定性依据详情
     */
    suspend fun getLegalBasisById(basisId: Long): LegalBasisEntity? {
        return legalBasisDao.getLegalBasisById(basisId)
    }

    /**
     * 获取定性依据内容列表
     */
    fun getLegalBasisContents(basisId: Long): Flow<List<LegalBasisContentEntity>> {
        return legalBasisContentDao.getContentsByBasisId(basisId)
    }

    /**
     * 获取处理依据内容列表
     */
    fun getProcessingBasisContents(basisId: Long): Flow<List<ProcessingBasisContentEntity>> {
        return processingBasisContentDao.getContentsByBasisId(basisId)
    }

    // ==================== 处理依据查询 ====================

    /**
     * 获取所有处理依据
     */
    fun getAllProcessingBasises(): Flow<List<ProcessingBasisEntity>> {
        return processingBasisDao.getAllProcessingBasises()
    }

    /**
     * 根据法规ID获取处理依据列表
     */
    fun getProcessingBasisesByRegulationId(regulationId: Long): Flow<List<ProcessingBasisEntity>> {
        return processingBasisDao.getProcessingBasisesByRegulationId(regulationId)
    }

    /**
     * 搜索处理依据
     */
    fun searchProcessingBasises(keyword: String): Flow<List<ProcessingBasisEntity>> {
        return processingBasisDao.searchProcessingBasises(keyword)
    }

    // ==================== 字典查询 ====================

    /**
     * 获取所有法律类型
     */
    fun getAllLegalTypes(): Flow<List<LegalTypeEntity>> {
        return legalTypeDao.getAllLegalTypes()
    }

    /**
     * 获取所有监管类型
     */
    fun getAllSupervisionTypes(): Flow<List<SupervisionTypeEntity>> {
        return supervisionTypeDao.getAllSupervisionTypes()
    }

    // ==================== 同步方法 ====================

    /**
     * 从服务器同步法律法规
     */
    suspend fun syncRegulationsFromServer(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = LawApi.getRegulationList(pageNum = 1, pageSize = 1000)
            if (response.code == 200) {
                val entities = response.rows.map { it.toEntity() }
                regulationDao.insertRegulations(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.msg ?: "同步失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 同步章节和条款
     */
    suspend fun syncChaptersAndArticles(regulationId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 同步章节
            val chapterResponse = LawApi.getChapterList(regulationId)
            if (chapterResponse.code == 200) {
                val chapterEntities = chapterResponse.rows.map { it.toEntity() }
                chapterDao.insertChapters(chapterEntities)
            }

            // 同步条款
            val articleResponse = LawApi.getArticleList(regulationId)
            if (articleResponse.code == 200) {
                val articleEntities = articleResponse.rows.map { it.toEntity() }
                articleDao.insertArticles(articleEntities)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 从服务器同步定性依据
     */
    suspend fun syncLegalBasisesFromServer(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = LawApi.getLegalBasisList(pageNum = 1, pageSize = 1000)
            if (response.code == 200) {
                val entities = response.rows.map { it.toEntity() }
                legalBasisDao.insertLegalBasises(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.msg ?: "同步失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 从服务器同步处理依据
     */
    suspend fun syncProcessingBasisesFromServer(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = LawApi.getProcessingBasisList(pageNum = 1, pageSize = 1000)
            if (response.code == 200) {
                val entities = response.rows.map { it.toEntity() }
                processingBasisDao.insertProcessingBasises(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.msg ?: "同步失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 从服务器同步章节-依据关联
     */
    suspend fun syncBasisChapterLinksFromServer(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = LawApi.getBasisChapterLinkList(pageNum = 1, pageSize = 5000)
            if (response.code == 200) {
                val entities = response.rows.map { it.toEntity() }
                basisChapterLinkDao.insertLinks(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.msg ?: "同步失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 获取条款的定性依据数量
     */
    suspend fun getLegalBasisCountByArticleId(articleId: Long): Int {
        return basisChapterLinkDao.getCountByArticleIdAndType(articleId, "legal")
    }

    /**
     * 获取条款的处理依据数量
     */
    suspend fun getProcessingBasisCountByArticleId(articleId: Long): Int {
        return basisChapterLinkDao.getCountByArticleIdAndType(articleId, "processing")
    }

    /**
     * 获取条款关联的定性依据列表
     */
    fun getLegalBasisesByArticleId(articleId: Long): Flow<List<LegalBasisEntity>> {
        return legalBasisDao.getLegalBasisesByArticleId(articleId)
    }

    /**
     * 获取条款关联的处理依据列表
     */
    fun getProcessingBasisesByArticleId(articleId: Long): Flow<List<ProcessingBasisEntity>> {
        return processingBasisDao.getProcessingBasisesByArticleId(articleId)
    }

    /**
     * 根据ID获取处理依据详情
     */
    suspend fun getProcessingBasisById(basisId: Long): ProcessingBasisEntity? {
        return processingBasisDao.getProcessingBasisById(basisId)
    }

    // Note: toEntity() 扩展函数定义在文件末尾包级别
}

// 扩展：RegulationEntity 转换回 Model
fun RegulationEntity.toModel(): com.ruoyi.app.feature.law.model.Regulation {
    val supervisionTypesList = try {
        val array = JSONArray(supervisionTypes ?: "[]")
        (0 until array.length()).map { array.getString(it) }
    } catch (e: Exception) {
        emptyList()
    }
    return com.ruoyi.app.feature.law.model.Regulation(
        regulationId = regulationId,
        title = title,
        legalType = legalType,
        supervisionTypes = supervisionTypesList,
        publishDate = publishDate,
        effectiveDate = effectiveDate,
        issuingAuthority = issuingAuthority,
        content = content,
        version = version,
        status = status,
        delFlag = delFlag,
        createBy = createBy,
        createTime = createTime?.toString(),
        updateBy = updateBy,
        updateTime = updateTime?.toString(),
        remark = remark
    )
}

fun LegalBasisEntity.toModel(): com.ruoyi.app.feature.law.model.LegalBasis {
    return com.ruoyi.app.feature.law.model.LegalBasis(
        basisId = basisId,
        basisNo = null,  // 已在内容表中，不再存储在主表
        title = title,
        violationType = null,  // 已在内容表中，不再存储在主表
        issuingAuthority = null,  // 已在内容表中，不再存储在主表
        effectiveDate = null,  // 已在内容表中，不再存储在主表
        legalLevel = null,  // 已在内容表中，不再存储在主表
        clauses = null,  // 已在内容表中，不再存储在主表
        legalLiability = null,  // 已在内容表中，不再存储在主表
        discretionStandard = null,  // 已在内容表中，不再存储在主表
        regulationId = regulationId,
        status = status,
        delFlag = delFlag,
        createBy = createBy,
        createTime = createTime?.toString(),
        updateBy = updateBy,
        updateTime = updateTime?.toString()
    )
}

// ==================== toEntity 扩展函数 ====================

// ProcessingBasis toEntity
fun com.ruoyi.app.feature.law.model.ProcessingBasis.toEntity(): ProcessingBasisEntity {
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
fun com.ruoyi.app.feature.law.model.BasisChapterLink.toEntity(): BasisChapterLinkEntity {
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

fun com.ruoyi.app.feature.law.model.Regulation.toEntity(): RegulationEntity {
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

fun com.ruoyi.app.feature.law.model.RegulationChapter.toEntity(): RegulationChapterEntity {
    return RegulationChapterEntity(
        chapterId = chapterId,
        regulationId = regulationId,
        chapterNo = chapterNo,
        chapterTitle = chapterTitle,
        sortOrder = sortOrder
    )
}

fun com.ruoyi.app.feature.law.model.RegulationArticle.toEntity(): RegulationArticleEntity {
    return RegulationArticleEntity(
        articleId = articleId,
        chapterId = chapterId,
        regulationId = regulationId,
        articleNo = articleNo,
        content = content,
        sortOrder = sortOrder
    )
}

fun com.ruoyi.app.feature.law.model.LegalBasis.toEntity(): LegalBasisEntity {
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

fun com.ruoyi.app.feature.law.api.LegalTypeResponse.toEntity(): com.ruoyi.app.feature.law.db.entity.LegalTypeEntity {
    return com.ruoyi.app.feature.law.db.entity.LegalTypeEntity(
        typeId = typeId,
        typeCode = typeCode,
        typeName = typeName,
        sortOrder = sortOrder,
        status = status
    )
}

fun com.ruoyi.app.feature.law.api.SupervisionTypeResponse.toEntity(): com.ruoyi.app.feature.law.db.entity.SupervisionTypeEntity {
    return com.ruoyi.app.feature.law.db.entity.SupervisionTypeEntity(
        typeId = typeId,
        typeCode = typeCode,
        typeName = typeName,
        sortOrder = sortOrder,
        status = status
    )
}
