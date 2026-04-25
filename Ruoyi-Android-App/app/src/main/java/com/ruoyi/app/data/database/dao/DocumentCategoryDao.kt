package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.DocumentCategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * 文书分类 DAO
 */
@Dao
interface DocumentCategoryDao {

    @Query("SELECT * FROM document_category ORDER BY sort ASC, categoryId ASC")
    fun getAllCategories(): Flow<List<DocumentCategoryEntity>>

    @Query("SELECT * FROM document_category WHERE categoryId = :categoryId")
    suspend fun getCategoryById(categoryId: Long): DocumentCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: DocumentCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<DocumentCategoryEntity>)

    @Delete
    suspend fun delete(category: DocumentCategoryEntity)

    @Query("DELETE FROM document_category")
    suspend fun deleteAll()
}