package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.RegulationEntity
import kotlinx.coroutines.flow.Flow

/**
 * 法律法规DAO
 */
@Dao
interface RegulationDao {

    @Query("SELECT * FROM sys_regulation WHERE del_flag = '0' ORDER BY regulation_id DESC")
    fun getAllRegulations(): Flow<List<RegulationEntity>>

    @Query("SELECT * FROM sys_regulation WHERE regulation_id = :id")
    suspend fun getRegulationById(id: Long): RegulationEntity?

    @Query("SELECT * FROM sys_regulation WHERE legal_type = :legalType AND del_flag = '0' ORDER BY regulation_id DESC")
    fun getRegulationsByLegalType(legalType: String): Flow<List<RegulationEntity>>

    @Query("SELECT * FROM sys_regulation WHERE supervision_types LIKE '%' || :supervisionType || '%' AND del_flag = '0' ORDER BY regulation_id DESC")
    fun getRegulationsBySupervisionType(supervisionType: String): Flow<List<RegulationEntity>>

    @Query("SELECT * FROM sys_regulation WHERE title LIKE '%' || :keyword || '%' AND del_flag = '0' ORDER BY regulation_id DESC")
    fun searchRegulations(keyword: String): Flow<List<RegulationEntity>>

    @Query("SELECT * FROM sys_regulation WHERE del_flag = '0' ORDER BY regulation_id DESC")
    suspend fun getAllRegulationsList(): List<RegulationEntity>

    @Query("SELECT regulation_id FROM sys_regulation WHERE del_flag = '0'")
    suspend fun getAllRegulationIds(): List<Long>

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

    @Query("SELECT COUNT(*) FROM sys_regulation WHERE del_flag = '0'")
    suspend fun getCount(): Int
}
