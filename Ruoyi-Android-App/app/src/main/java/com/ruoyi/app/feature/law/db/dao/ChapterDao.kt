package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.RegulationChapterEntity
import kotlinx.coroutines.flow.Flow

/**
 * 法规章节DAO
 */
@Dao
interface ChapterDao {

    @Query("SELECT * FROM sys_regulation_chapter WHERE regulationId = :regulationId ORDER BY sortOrder ASC")
    fun getChaptersByRegulationId(regulationId: Long): Flow<List<RegulationChapterEntity>>

    @Query("SELECT * FROM sys_regulation_chapter WHERE regulationId = :regulationId ORDER BY sortOrder ASC")
    suspend fun getChaptersByRegulationIdList(regulationId: Long): List<RegulationChapterEntity>

    @Query("SELECT * FROM sys_regulation_chapter WHERE chapterId = :chapterId")
    suspend fun getChapterById(chapterId: Long): RegulationChapterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: RegulationChapterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<RegulationChapterEntity>)

    @Update
    suspend fun updateChapter(chapter: RegulationChapterEntity)

    @Delete
    suspend fun deleteChapter(chapter: RegulationChapterEntity)

    @Query("DELETE FROM sys_regulation_chapter WHERE regulationId = :regulationId")
    suspend fun deleteByRegulationId(regulationId: Long)

    @Query("DELETE FROM sys_regulation_chapter")
    suspend fun deleteAll()
}
