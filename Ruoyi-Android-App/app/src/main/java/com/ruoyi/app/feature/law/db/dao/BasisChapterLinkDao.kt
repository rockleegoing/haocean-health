package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.BasisChapterLinkEntity
import kotlinx.coroutines.flow.Flow

/**
 * 依据章节关联DAO
 */
@Dao
interface BasisChapterLinkDao {

    @Query("SELECT * FROM sys_basis_chapter_link")
    fun getAllLinks(): Flow<List<BasisChapterLinkEntity>>

    @Query("SELECT * FROM sys_basis_chapter_link")
    suspend fun getAllLinksList(): List<BasisChapterLinkEntity>

    @Query("SELECT COUNT(*) FROM sys_basis_chapter_link WHERE articleId = :articleId AND basisType = 'legal'")
    suspend fun getLegalBasisCountByArticle(articleId: Long): Int

    @Query("SELECT COUNT(*) FROM sys_basis_chapter_link WHERE articleId = :articleId AND basisType = 'processing'")
    suspend fun getProcessingBasisCountByArticle(articleId: Long): Int

    @Query("SELECT lb.* FROM sys_legal_basis lb INNER JOIN sys_basis_chapter_link l ON lb.basisId = l.basisId WHERE l.articleId = :articleId AND l.basisType = 'legal' AND lb.delFlag = '0'")
    fun getLegalBasisesByArticle(articleId: Long): Flow<List<com.ruoyi.app.feature.law.db.entity.LegalBasisEntity>>

    @Query("SELECT pb.* FROM sys_processing_basis pb INNER JOIN sys_basis_chapter_link l ON pb.basisId = l.basisId WHERE l.articleId = :articleId AND l.basisType = 'processing' AND pb.delFlag = '0'")
    fun getProcessingBasisesByArticle(articleId: Long): Flow<List<com.ruoyi.app.feature.law.db.entity.ProcessingBasisEntity>>

    @Query("SELECT * FROM sys_basis_chapter_link WHERE articleId = :articleId AND basisType = :basisType")
    fun getLinksByArticleAndType(articleId: Long, basisType: String): Flow<List<BasisChapterLinkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: BasisChapterLinkEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinks(links: List<BasisChapterLinkEntity>)

    @Delete
    suspend fun deleteLink(link: BasisChapterLinkEntity)

    @Query("DELETE FROM sys_basis_chapter_link")
    suspend fun deleteAll()
}
