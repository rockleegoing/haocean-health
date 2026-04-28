package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.BasisChapterLinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BasisChapterLinkDao {

    @Query("SELECT * FROM sys_basis_chapter_link ORDER BY link_id ASC")
    fun getAllLinks(): Flow<List<BasisChapterLinkEntity>>

    @Query("SELECT * FROM sys_basis_chapter_link WHERE link_id = :linkId")
    suspend fun getLinkById(linkId: Long): BasisChapterLinkEntity?

    @Query("SELECT * FROM sys_basis_chapter_link WHERE chapter_id = :chapterId ORDER BY link_id ASC")
    fun getLinksByChapterId(chapterId: Long): Flow<List<BasisChapterLinkEntity>>

    @Query("SELECT * FROM sys_basis_chapter_link WHERE article_id = :articleId ORDER BY link_id ASC")
    fun getLinksByArticleId(articleId: Long): Flow<List<BasisChapterLinkEntity>>

    @Query("SELECT * FROM sys_basis_chapter_link WHERE chapter_id = :chapterId AND basis_type = :basisType ORDER BY link_id ASC")
    fun getLinksByChapterIdAndType(chapterId: Long, basisType: String): Flow<List<BasisChapterLinkEntity>>

    @Query("SELECT * FROM sys_basis_chapter_link WHERE article_id = :articleId AND basis_type = :basisType ORDER BY link_id ASC")
    fun getLinksByArticleIdAndType(articleId: Long, basisType: String): Flow<List<BasisChapterLinkEntity>>

    @Query("SELECT COUNT(*) FROM sys_basis_chapter_link WHERE article_id = :articleId AND basis_type = :basisType")
    suspend fun getCountByArticleIdAndType(articleId: Long, basisType: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinks(entities: List<BasisChapterLinkEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(entity: BasisChapterLinkEntity)

    @Delete
    suspend fun deleteLink(entity: BasisChapterLinkEntity)

    @Query("DELETE FROM sys_basis_chapter_link")
    suspend fun deleteAllLinks()
}
