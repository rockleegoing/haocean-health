package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.RegulatoryMatterEntity

@Dao
interface RegulatoryMatterDao {
    @Query("SELECT * FROM regulatory_matter ORDER BY matterName")
    suspend fun getAll(): List<RegulatoryMatterEntity>

    @Query("SELECT * FROM regulatory_matter WHERE matterId = :matterId")
    suspend fun getById(matterId: Long): RegulatoryMatterEntity?

    @Query("SELECT * FROM regulatory_matter WHERE categoryId = :categoryId ORDER BY matterName")
    suspend fun getByCategoryId(categoryId: Long): List<RegulatoryMatterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(matters: List<RegulatoryMatterEntity>)

    @Query("DELETE FROM regulatory_matter")
    suspend fun deleteAll()
}