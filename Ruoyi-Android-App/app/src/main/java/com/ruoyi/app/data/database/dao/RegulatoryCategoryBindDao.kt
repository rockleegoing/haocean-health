package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.RegulatoryCategoryBindEntity

@Dao
interface RegulatoryCategoryBindDao {
    @Query("SELECT * FROM regulatory_category_bind")
    suspend fun getAll(): List<RegulatoryCategoryBindEntity>

    @Query("SELECT * FROM regulatory_category_bind WHERE matterId = :matterId")
    suspend fun getByMatterId(matterId: Long): List<RegulatoryCategoryBindEntity>

    @Query("SELECT * FROM regulatory_category_bind WHERE industryCategoryId = :categoryId")
    suspend fun getByCategoryId(categoryId: Long): List<RegulatoryCategoryBindEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(binds: List<RegulatoryCategoryBindEntity>)

    @Query("DELETE FROM regulatory_category_bind")
    suspend fun deleteAll()
}