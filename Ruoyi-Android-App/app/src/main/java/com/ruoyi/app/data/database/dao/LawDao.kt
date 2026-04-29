package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.LawEntity

@Dao
interface LawDao {
    @Query("SELECT * FROM law ORDER BY name")
    suspend fun getAll(): List<LawEntity>

    @Query("SELECT * FROM law WHERE id = :lawId")
    suspend fun getById(lawId: Long): LawEntity?

    @Query("SELECT * FROM law WHERE name LIKE '%' || :keyword || '%'")
    suspend fun search(keyword: String): List<LawEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(laws: List<LawEntity>)

    @Query("DELETE FROM law")
    suspend fun deleteAll()
}
