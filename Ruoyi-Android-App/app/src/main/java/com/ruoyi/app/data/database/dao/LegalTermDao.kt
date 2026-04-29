package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.LegalTermEntity

@Dao
interface LegalTermDao {
    @Query("SELECT * FROM legal_term WHERE lawId = :lawId ORDER BY zhCode")
    suspend fun getByLawId(lawId: Long): List<LegalTermEntity>

    @Query("SELECT * FROM legal_term WHERE id = :termId")
    suspend fun getById(termId: Long): LegalTermEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(terms: List<LegalTermEntity>)

    @Query("DELETE FROM legal_term WHERE lawId = :lawId")
    suspend fun deleteByLawId(lawId: Long)

    @Query("DELETE FROM legal_term")
    suspend fun deleteAll()
}
