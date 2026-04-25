package com.ruoyi.app.feature.law.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.feature.law.api.LawApi
import com.ruoyi.app.feature.law.db.entity.LegalBasisEntity
import com.ruoyi.app.feature.law.db.entity.RegulationArticleEntity
import com.ruoyi.app.feature.law.db.entity.RegulationChapterEntity
import com.ruoyi.app.feature.law.db.entity.RegulationEntity
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

    // ==================== 数据转换 ====================

    private fun com.ruoyi.app.feature.law.model.Regulation.toEntity(): RegulationEntity {
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
            createTime = createTime?.toLongOrNull(),
            updateBy = updateBy,
            updateTime = updateTime?.toLongOrNull(),
            remark = remark
        )
    }

    private fun com.ruoyi.app.feature.law.model.RegulationChapter.toEntity(): RegulationChapterEntity {
        return RegulationChapterEntity(
            chapterId = chapterId,
            regulationId = regulationId,
            chapterNo = chapterNo,
            chapterTitle = chapterTitle,
            sortOrder = sortOrder
        )
    }

    private fun com.ruoyi.app.feature.law.model.RegulationArticle.toEntity(): RegulationArticleEntity {
        return RegulationArticleEntity(
            articleId = articleId,
            chapterId = chapterId,
            regulationId = regulationId,
            articleNo = articleNo,
            content = content,
            sortOrder = sortOrder
        )
    }

    private fun com.ruoyi.app.feature.law.model.LegalBasis.toEntity(): LegalBasisEntity {
        return LegalBasisEntity(
            basisId = basisId,
            basisNo = basisNo,
            title = title,
            violationType = violationType,
            issuingAuthority = issuingAuthority,
            effectiveDate = effectiveDate,
            legalLevel = legalLevel,
            clauses = clauses,
            legalLiability = legalLiability,
            discretionStandard = discretionStandard,
            regulationId = regulationId,
            status = status,
            delFlag = delFlag,
            createBy = createBy,
            createTime = createTime?.toLongOrNull(),
            updateBy = updateBy,
            updateTime = updateTime?.toLongOrNull()
        )
    }
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
        basisNo = basisNo,
        title = title,
        violationType = violationType,
        issuingAuthority = issuingAuthority,
        effectiveDate = effectiveDate,
        legalLevel = legalLevel,
        clauses = clauses,
        legalLiability = legalLiability,
        discretionStandard = discretionStandard,
        regulationId = regulationId,
        status = status,
        delFlag = delFlag,
        createBy = createBy,
        createTime = createTime?.toString(),
        updateBy = updateBy,
        updateTime = updateTime?.toString()
    )
}
