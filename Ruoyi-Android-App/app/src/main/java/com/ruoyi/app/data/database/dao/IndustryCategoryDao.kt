package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.IndustryCategoryEntity

/**
 * 行业分类数据访问对象
 */
@Dao
interface IndustryCategoryDao {

    @Query("SELECT * FROM sys_industry_category WHERE delFlag = '0' AND status = '0' ORDER BY orderNum")
    suspend fun getAllCategories(): List<IndustryCategoryEntity>

    @Query("SELECT * FROM sys_industry_category WHERE categoryId = :categoryId")
    suspend fun getCategoryById(categoryId: Long): IndustryCategoryEntity?

    @Query("SELECT * FROM sys_industry_category WHERE categoryId IN (:ids)")
    suspend fun getCategoriesByIds(ids: List<Long>): List<IndustryCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<IndustryCategoryEntity>)

    @Query("DELETE FROM sys_industry_category")
    suspend fun deleteAllCategories()
}
