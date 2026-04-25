package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.DocumentVariableEntity

/**
 * 文书模板变量 DAO
 */
@Dao
interface DocumentVariableDao {

    @Query("SELECT * FROM document_variable WHERE templateId = :templateId ORDER BY sortOrder ASC")
    fun getVariablesByTemplateId(templateId: Long): List<DocumentVariableEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(variable: DocumentVariableEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(variables: List<DocumentVariableEntity>)

    @Delete
    suspend fun delete(variable: DocumentVariableEntity)

    @Query("DELETE FROM document_variable WHERE templateId = :templateId")
    suspend fun deleteByTemplateId(templateId: Long)

    @Query("DELETE FROM document_variable")
    suspend fun deleteAll()
}
