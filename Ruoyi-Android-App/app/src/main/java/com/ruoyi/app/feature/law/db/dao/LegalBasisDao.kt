package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.LegalBasisEntity
import kotlinx.coroutines.flow.Flow

/**
 * 定性依据DAO
 */
@Dao
interface LegalBasisDao {

    @Query("SELECT * FROM sys_legal_basis WHERE delFlag = '0' ORDER BY basisId DESC")
    fun getAllLegalBasises(): Flow<List<LegalBasisEntity>>

    @Query("SELECT * FROM sys_legal_basis WHERE basisId = :basisId")
    suspend fun getLegalBasisById(basisId: Long): LegalBasisEntity?

    @Query("SELECT * FROM sys_legal_basis WHERE regulationId = :regulationId AND delFlag = '0' ORDER BY basisId DESC")
    fun getLegalBasisesByRegulationId(regulationId: Long): Flow<List<LegalBasisEntity>>

    @Query("SELECT * FROM sys_legal_basis WHERE regulationId = :regulationId AND delFlag = '0'")
    suspend fun getLegalBasisesByRegulationIdList(regulationId: Long): List<LegalBasisEntity>

    @Query("SELECT * FROM sys_legal_basis WHERE title LIKE '%' || :keyword || '%' AND delFlag = '0' ORDER BY basisId DESC")
    fun searchLegalBasises(keyword: String): Flow<List<LegalBasisEntity>>

    @Query("SELECT * FROM sys_legal_basis WHERE violationType LIKE '%' || :violationType || '%' AND delFlag = '0' ORDER BY basisId DESC")
    fun getLegalBasisesByViolationType(violationType: String): Flow<List<LegalBasisEntity>>

    @Query("SELECT * FROM sys_legal_basis WHERE delFlag = '0' ORDER BY basisId DESC")
    suspend fun getAllLegalBasisesList(): List<LegalBasisEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLegalBasis(legalBasis: LegalBasisEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLegalBasises(legalBasises: List<LegalBasisEntity>)

    @Update
    suspend fun updateLegalBasis(legalBasis: LegalBasisEntity)

    @Delete
    suspend fun deleteLegalBasis(legalBasis: LegalBasisEntity)

    @Query("DELETE FROM sys_legal_basis")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM sys_legal_basis WHERE delFlag = '0'")
    suspend fun getCount(): Int
}
