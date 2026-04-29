package com.ruoyi.app.feature.normative.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.NormativeCategoryEntity
import com.ruoyi.app.data.database.entity.NormativeLanguageEntity
import com.ruoyi.app.data.database.entity.NormativeMatterBindEntity
import com.ruoyi.app.data.database.entity.NormativeTermBindEntity
import com.ruoyi.app.feature.normative.api.CategoryDto
import com.ruoyi.app.feature.normative.api.LanguageDto
import com.ruoyi.app.feature.normative.api.MatterBindDto
import com.ruoyi.app.feature.normative.api.NormativeApi
import com.ruoyi.app.feature.normative.api.TermBindDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NormativeRepository(private val context: Context) {

    private val categoryDao = AppDatabase.getInstance(context).normativeCategoryDao()
    private val languageDao = AppDatabase.getInstance(context).normativeLanguageDao()
    private val matterBindDao = AppDatabase.getInstance(context).normativeMatterBindDao()
    private val termBindDao = AppDatabase.getInstance(context).normativeTermBindDao()

    /**
     * 同步分类列表
     */
    suspend fun syncCategories(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = NormativeApi.getCategoryList()
                if (response.code == 200) {
                    val entities = response.rows.map { it.toEntity() }
                    categoryDao.insertAll(entities)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 同步规范用语列表
     */
    suspend fun syncLanguages(categoryId: Long? = null): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = NormativeApi.getLanguageList(categoryId)
                if (response.code == 200) {
                    val entities = response.rows.map { it.toEntity() }
                    languageDao.insertAll(entities)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 同步规范用语监管事项关联
     */
    suspend fun syncMatterBinds(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = NormativeApi.getMatterBindList()
                if (response.code == 200) {
                    val entities = response.rows.map { it.toEntity() }
                    matterBindDao.insertAll(entities)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 同步规范用语法律条款关联
     */
    suspend fun syncTermBinds(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = NormativeApi.getTermBindList()
                if (response.code == 200) {
                    val entities = response.rows.map { it.toEntity() }
                    termBindDao.insertAll(entities)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 获取本地所有分类
     */
    suspend fun getAllCategories(): List<NormativeCategoryEntity> {
        return withContext(Dispatchers.IO) {
            categoryDao.getAll()
        }
    }

    /**
     * 获取本地所有规范用语
     */
    suspend fun getAllLanguages(): List<NormativeLanguageEntity> {
        return withContext(Dispatchers.IO) {
            languageDao.getAll()
        }
    }

    /**
     * 获取按分类的规范用语
     */
    suspend fun getLanguagesByCategory(categoryId: Long): List<NormativeLanguageEntity> {
        return withContext(Dispatchers.IO) {
            languageDao.getByCategory(categoryId)
        }
    }
}

/**
 * CategoryDto 转换为 Entity
 */
fun CategoryDto.toEntity() = NormativeCategoryEntity(
    code = code,
    name = name,
    parentCode = parentCode,
    sortOrder = sortOrder ?: 0,
    status = status ?: "0"
)

/**
 * LanguageDto 转换为 Entity
 */
fun LanguageDto.toEntity() = NormativeLanguageEntity(
    id = id,
    standardCode = standardCode,
    standardPhrase = standardPhrase,
    supervisoryOpinion = supervisoryOpinion,
    basisType = null,
    primaryCategory = primaryCategory
)

/**
 * MatterBindDto 转换为 Entity
 */
fun MatterBindDto.toEntity() = NormativeMatterBindEntity(
    normativeLanguageId = normativeId,
    regulatoryMatterId = matterId,
    basisType = basisType
)

/**
 * TermBindDto 转换为 Entity
 */
fun TermBindDto.toEntity() = NormativeTermBindEntity(
    legalTermId = legalTermId,
    normativeLanguageId = normativeLanguageId,
    basisType = basisType
)
