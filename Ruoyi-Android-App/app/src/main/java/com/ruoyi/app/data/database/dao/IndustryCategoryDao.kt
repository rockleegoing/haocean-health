package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.IndustryCategoryEntity

/**
 * 行业分类数据访问对象
 */
@Dao
interface IndustryCategoryDao {

    @Query("SELECT * FROM sys_industry_category WHERE categoryId = :categoryId")
    suspend fun getCategoryById(categoryId: Long): IndustryCategoryEntity?

    @Query("SELECT * FROM sys_industry_category WHERE status = '0' ORDER BY categoryCode")
    suspend fun getAllCategories(): List<IndustryCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: IndustryCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<IndustryCategoryEntity>)

    @Update
    suspend fun updateCategory(category: IndustryCategoryEntity)

    @Delete
    suspend fun deleteCategory(category: IndustryCategoryEntity)
}
