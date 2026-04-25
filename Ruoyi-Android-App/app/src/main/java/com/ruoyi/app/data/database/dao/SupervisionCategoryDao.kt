package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.SupervisionCategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * 监管类型 DAO
 */
@Dao
interface SupervisionCategoryDao {

    @Query("SELECT * FROM supervision_category ORDER BY sortOrder ASC")
    fun getAllCategories(): Flow<List<SupervisionCategoryEntity>>

    @Query("SELECT * FROM supervision_category WHERE status = '0' ORDER BY sortOrder ASC")
    fun getEnabledCategories(): Flow<List<SupervisionCategoryEntity>>

    @Query("SELECT * FROM supervision_category WHERE categoryId = :categoryId")
    suspend fun getCategoryById(categoryId: Long): SupervisionCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: SupervisionCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<SupervisionCategoryEntity>)

    @Delete
    suspend fun delete(category: SupervisionCategoryEntity)

    @Query("DELETE FROM supervision_category")
    suspend fun deleteAll()
}
