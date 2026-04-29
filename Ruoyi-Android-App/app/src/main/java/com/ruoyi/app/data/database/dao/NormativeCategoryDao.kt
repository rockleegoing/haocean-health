package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.NormativeCategoryEntity

@Dao
interface NormativeCategoryDao {
    @Query("SELECT * FROM normative_category ORDER BY sortOrder")
    suspend fun getAll(): List<NormativeCategoryEntity>

    @Query("SELECT * FROM normative_category WHERE code = :code")
    suspend fun getById(code: Long): NormativeCategoryEntity?

    @Query("SELECT * FROM normative_category WHERE parentCode = :parentCode ORDER BY sortOrder")
    suspend fun getByParentCode(parentCode: Long?): List<NormativeCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<NormativeCategoryEntity>)

    @Query("DELETE FROM normative_category")
    suspend fun deleteAll()
}
