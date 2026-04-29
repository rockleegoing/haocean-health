package com.ruoyi.app.feature.law.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.LawEntity
import com.ruoyi.app.data.database.entity.LegalTermEntity
import com.ruoyi.app.feature.law.api.LawApi
import com.ruoyi.app.feature.law.api.LawDto
import com.ruoyi.app.feature.law.api.LegalTermDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LawRepository(private val context: Context) {

    private val lawDao = AppDatabase.getInstance(context).lawDao()
    private val termDao = AppDatabase.getInstance(context).legalTermDao()

    /**
     * 同步法律列表
     */
    suspend fun syncLaws(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = LawApi.getLawList()
                if (response.code == 200) {
                    val entities = response.rows.map { it.toEntity() }
                    lawDao.insertAll(entities)
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
     * 同步法律条款
     */
    suspend fun syncTerms(lawId: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = LawApi.getTermListByLaw(lawId)
                if (response.code == 200) {
                    val entities = response.rows.map { it.toEntity(lawId) }
                    termDao.insertAll(entities)
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
     * 获取本地所有法律
     */
    suspend fun getAllLaws(): List<LawEntity> {
        return withContext(Dispatchers.IO) {
            lawDao.getAll()
        }
    }

    /**
     * 搜索法律
     */
    suspend fun searchLaws(keyword: String): List<LawEntity> {
        return withContext(Dispatchers.IO) {
            lawDao.search(keyword)
        }
    }

    /**
     * 获取法律条款
     */
    suspend fun getTermsByLawId(lawId: Long): List<LegalTermEntity> {
        return withContext(Dispatchers.IO) {
            termDao.getByLawId(lawId)
        }
    }
}

/**
 * LawDto 转换为 Entity
 */
fun LawDto.toEntity() = LawEntity(
    id = id,
    name = name,
    releaseTime = null
)

/**
 * LegalTermDto 转换为 Entity
 */
fun LegalTermDto.toEntity(lawId: Long) = LegalTermEntity(
    id = id,
    lawId = lawId,
    part = null,
    partBranch = null,
    chapter = null,
    quarter = null,
    article = article,
    section = null,
    subparagraph = null,
    item = null,
    zhCode = zhCode,
    content = content
)
