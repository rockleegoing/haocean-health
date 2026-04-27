package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.ProcessingBasisEntity
import kotlinx.coroutines.flow.Flow

/**
 * 处理依据DAO
 */
@Dao
interface ProcessingBasisDao {

    @Query("SELECT * FROM sys_processing_basis WHERE delFlag = '0' ORDER BY basisId DESC")
    fun getAllProcessingBasises(): Flow<List<ProcessingBasisEntity>>

    @Query("SELECT * FROM sys_processing_basis WHERE basisId = :basisId")
    suspend fun getProcessingBasisById(basisId: Long): ProcessingBasisEntity?

    @Query("SELECT * FROM sys_processing_basis WHERE regulationId = :regulationId AND delFlag = '0' ORDER BY basisId DESC")
    fun getProcessingBasisesByRegulationId(regulationId: Long): Flow<List<ProcessingBasisEntity>>

    @Query("SELECT * FROM sys_processing_basis WHERE regulationId = :regulationId AND delFlag = '0'")
    suspend fun getProcessingBasisesByRegulationIdList(regulationId: Long): List<ProcessingBasisEntity>

    @Query("SELECT * FROM sys_processing_basis WHERE title LIKE '%' || :keyword || '%' AND delFlag = '0' ORDER BY basisId DESC")
    fun searchProcessingBasises(keyword: String): Flow<List<ProcessingBasisEntity>>

    @Query("SELECT * FROM sys_processing_basis WHERE violationType LIKE '%' || :violationType || '%' AND delFlag = '0' ORDER BY basisId DESC")
    fun getProcessingBasisesByViolationType(violationType: String): Flow<List<ProcessingBasisEntity>>

    @Query("SELECT * FROM sys_processing_basis WHERE delFlag = '0' ORDER BY basisId DESC")
    suspend fun getAllProcessingBasisesList(): List<ProcessingBasisEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessingBasis(processingBasis: ProcessingBasisEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessingBasises(processingBasises: List<ProcessingBasisEntity>)

    @Update
    suspend fun updateProcessingBasis(processingBasis: ProcessingBasisEntity)

    @Delete
    suspend fun deleteProcessingBasis(processingBasis: ProcessingBasisEntity)

    @Query("DELETE FROM sys_processing_basis")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM sys_processing_basis WHERE delFlag = '0'")
    suspend fun getCount(): Int
}
