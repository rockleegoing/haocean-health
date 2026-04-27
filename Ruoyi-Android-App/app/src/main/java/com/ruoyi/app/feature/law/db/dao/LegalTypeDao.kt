package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.LegalTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LegalTypeDao {
    @Query("SELECT * FROM sys_legal_type WHERE status = '0' ORDER BY sortOrder ASC, typeId ASC")
    fun getAllLegalTypes(): Flow<List<LegalTypeEntity>>

    @Query("SELECT * FROM sys_legal_type ORDER BY sortOrder ASC, typeId ASC")
    fun getAllLegalTypesIncludeDisabled(): Flow<List<LegalTypeEntity>>

    @Query("SELECT * FROM sys_legal_type WHERE typeId = :typeId")
    suspend fun getLegalTypeById(typeId: Long): LegalTypeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(types: List<LegalTypeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(type: LegalTypeEntity)

    @Delete
    suspend fun delete(type: LegalTypeEntity)

    @Query("DELETE FROM sys_legal_type")
    suspend fun deleteAll()
}
