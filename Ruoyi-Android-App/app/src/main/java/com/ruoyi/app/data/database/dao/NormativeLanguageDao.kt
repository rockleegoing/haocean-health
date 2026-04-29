package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.NormativeLanguageEntity

@Dao
interface NormativeLanguageDao {
    @Query("SELECT * FROM normative_language ORDER BY standardPhrase")
    suspend fun getAll(): List<NormativeLanguageEntity>

    @Query("SELECT * FROM normative_language WHERE id = :id")
    suspend fun getById(id: Long): NormativeLanguageEntity?

    @Query("SELECT * FROM normative_language WHERE primaryCategory = :categoryCode ORDER BY standardPhrase")
    suspend fun getByCategory(categoryCode: Long?): List<NormativeLanguageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(languages: List<NormativeLanguageEntity>)

    @Query("DELETE FROM normative_language")
    suspend fun deleteAll()
}
