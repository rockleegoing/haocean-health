package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.RegulatoryMatterItemEntity

@Dao
interface RegulatoryMatterItemDao {
    @Query("SELECT * FROM regulatory_matter_item WHERE matterId = :matterId ORDER BY itemNo")
    suspend fun getByMatterId(matterId: Long): List<RegulatoryMatterItemEntity>

    @Query("SELECT * FROM regulatory_matter_item WHERE itemId = :itemId")
    suspend fun getById(itemId: Long): RegulatoryMatterItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<RegulatoryMatterItemEntity>)

    @Query("DELETE FROM regulatory_matter_item WHERE matterId = :matterId")
    suspend fun deleteByMatterId(matterId: Long)

    @Query("DELETE FROM regulatory_matter_item")
    suspend fun deleteAll()
}