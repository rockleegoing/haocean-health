package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.RegulationArticleEntity
import kotlinx.coroutines.flow.Flow

/**
 * 法规条款DAO
 */
@Dao
interface ArticleDao {

    @Query("SELECT * FROM sys_regulation_article WHERE regulationId = :regulationId ORDER BY sortOrder ASC")
    fun getArticlesByRegulationId(regulationId: Long): Flow<List<RegulationArticleEntity>>

    @Query("SELECT * FROM sys_regulation_article WHERE regulationId = :regulationId ORDER BY sortOrder ASC")
    suspend fun getArticlesByRegulationIdList(regulationId: Long): List<RegulationArticleEntity>

    @Query("SELECT * FROM sys_regulation_article WHERE chapterId = :chapterId ORDER BY sortOrder ASC")
    fun getArticlesByChapterId(chapterId: Long): Flow<List<RegulationArticleEntity>>

    @Query("SELECT * FROM sys_regulation_article WHERE articleId = :articleId")
    suspend fun getArticleById(articleId: Long): RegulationArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: RegulationArticleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<RegulationArticleEntity>)

    @Update
    suspend fun updateArticle(article: RegulationArticleEntity)

    @Delete
    suspend fun deleteArticle(article: RegulationArticleEntity)

    @Query("DELETE FROM sys_regulation_article WHERE regulationId = :regulationId")
    suspend fun deleteByRegulationId(regulationId: Long)

    @Query("DELETE FROM sys_regulation_article")
    suspend fun deleteAll()
}
