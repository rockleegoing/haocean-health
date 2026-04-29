package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.NormativeMatterBindEntity

@Dao
interface NormativeMatterBindDao {
    @Query("SELECT * FROM normative_matter_bind")
    suspend fun getAll(): List<NormativeMatterBindEntity>

    @Query("SELECT * FROM normative_matter_bind WHERE normativeLanguageId = :languageId")
    suspend fun getByLanguageId(languageId: Long): List<NormativeMatterBindEntity>

    @Query("SELECT * FROM normative_matter_bind WHERE regulatoryMatterId = :matterId")
    suspend fun getByMatterId(matterId: Long): List<NormativeMatterBindEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(binds: List<NormativeMatterBindEntity>)

    @Query("DELETE FROM normative_matter_bind")
    suspend fun deleteAll()
}
