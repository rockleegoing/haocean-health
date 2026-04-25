package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.RegulationEntity
import kotlinx.coroutines.flow.Flow

/**
 * 法律法规DAO
 */
@Dao
interface RegulationDao {

    @Query("SELECT * FROM sys_regulation WHERE delFlag = '0' ORDER BY regulationId DESC")
    fun getAllRegulations(): Flow<List<RegulationEntity>>

    @Query("SELECT * FROM sys_regulation WHERE regulationId = :id")
    suspend fun getRegulationById(id: Long): RegulationEntity?

    @Query("SELECT * FROM sys_regulation WHERE legalType = :legalType AND delFlag = '0' ORDER BY regulationId DESC")
    fun getRegulationsByLegalType(legalType: String): Flow<List<RegulationEntity>>

    @Query("SELECT * FROM sys_regulation WHERE supervisionTypes LIKE '%' || :supervisionType || '%' AND delFlag = '0' ORDER BY regulationId DESC")
    fun getRegulationsBySupervisionType(supervisionType: String): Flow<List<RegulationEntity>>

    @Query("SELECT * FROM sys_regulation WHERE title LIKE '%' || :keyword || '%' AND delFlag = '0' ORDER BY regulationId DESC")
    fun searchRegulations(keyword: String): Flow<List<RegulationEntity>>

    @Query("SELECT * FROM sys_regulation WHERE delFlag = '0' ORDER BY regulationId DESC")
    suspend fun getAllRegulationsList(): List<RegulationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegulation(regulation: RegulationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegulations(regulations: List<RegulationEntity>)

    @Update
    suspend fun updateRegulation(regulation: RegulationEntity)

    @Delete
    suspend fun deleteRegulation(regulation: RegulationEntity)

    @Query("DELETE FROM sys_regulation")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM sys_regulation WHERE delFlag = '0'")
    suspend fun getCount(): Int
}
