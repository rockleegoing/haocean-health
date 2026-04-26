package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.DocumentTemplateEntity
import kotlinx.coroutines.flow.Flow

/**
 * 文书模板 DAO
 */
@Dao
interface DocumentTemplateDao {

    @Query("SELECT * FROM document_template WHERE isActive = '1' ORDER BY id ASC")
    fun getActiveTemplates(): Flow<List<DocumentTemplateEntity>>

    @Query("SELECT * FROM document_template WHERE isActive = '1' AND categoryId = :categoryId ORDER BY sort ASC, id ASC")
    fun getTemplatesByCategory(categoryId: Long): Flow<List<DocumentTemplateEntity>>

    @Query("SELECT * FROM document_template WHERE isActive = '1' AND category = :categoryName ORDER BY sort ASC, id ASC")
    fun getTemplatesByCategoryName(categoryName: String): Flow<List<DocumentTemplateEntity>>

    @Query("SELECT * FROM document_template WHERE isActive = '1' AND categoryId != 0 ORDER BY categoryId ASC, sort ASC, id ASC")
    fun getTemplatesWithCategory(): Flow<List<DocumentTemplateEntity>>

    @Query("SELECT * FROM document_template WHERE isActive = '1' AND industryCategoryId = :industryCategoryId ORDER BY sort ASC, id ASC")
    fun getTemplatesByIndustryCategory(industryCategoryId: Long): Flow<List<DocumentTemplateEntity>>

    @Query("SELECT * FROM document_template WHERE id = :id")
    suspend fun getTemplateById(id: Long): DocumentTemplateEntity?

    @Query("SELECT * FROM document_template WHERE templateCode = :code")
    suspend fun getTemplateByCode(code: String): DocumentTemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: DocumentTemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<DocumentTemplateEntity>)

    @Delete
    suspend fun delete(template: DocumentTemplateEntity)

    @Query("DELETE FROM document_template")
    suspend fun deleteAll()
}
