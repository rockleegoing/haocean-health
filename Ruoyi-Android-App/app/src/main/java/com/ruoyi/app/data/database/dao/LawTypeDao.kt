package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.LawTypeEntity

@Dao
interface LawTypeDao {
    @Query("SELECT * FROM law_type ORDER BY sort")
    suspend fun getAll(): List<LawTypeEntity>

    @Query("SELECT * FROM law_type WHERE parentId = :parentId ORDER BY sort")
    suspend fun getByParentId(parentId: Long): List<LawTypeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lawTypes: List<LawTypeEntity>)

    @Query("DELETE FROM law_type")
    suspend fun deleteAll()
}