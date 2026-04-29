package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.NormativeTermBindEntity

@Dao
interface NormativeTermBindDao {
    @Query("SELECT * FROM normative_term_bind")
    suspend fun getAll(): List<NormativeTermBindEntity>

    @Query("SELECT * FROM normative_term_bind WHERE normativeLanguageId = :languageId")
    suspend fun getByLanguageId(languageId: Long): List<NormativeTermBindEntity>

    @Query("SELECT * FROM normative_term_bind WHERE legalTermId = :termId")
    suspend fun getByTermId(termId: Long): List<NormativeTermBindEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(binds: List<NormativeTermBindEntity>)

    @Query("DELETE FROM normative_term_bind")
    suspend fun deleteAll()
}
