package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.ProcessingBasisContentEntity
import kotlinx.coroutines.flow.Flow

/**
 * 处理依据内容DAO
 */
@Dao
interface ProcessingBasisContentDao {

    @Query("SELECT * FROM sys_processing_basis_content WHERE basis_id = :basisId ORDER BY sort_order ASC")
    fun getContentsByBasisId(basisId: Long): Flow<List<ProcessingBasisContentEntity>>

    @Query("SELECT * FROM sys_processing_basis_content WHERE basis_id = :basisId ORDER BY sort_order ASC")
    suspend fun getContentsByBasisIdList(basisId: Long): List<ProcessingBasisContentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContents(contents: List<ProcessingBasisContentEntity>)

    @Query("DELETE FROM sys_processing_basis_content WHERE basis_id = :basisId")
    suspend fun deleteByBasisId(basisId: Long)

    @Query("DELETE FROM sys_processing_basis_content")
    suspend fun deleteAll()
}