package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.LegalBasisContentEntity
import kotlinx.coroutines.flow.Flow

/**
 * 定性依据内容DAO
 */
@Dao
interface LegalBasisContentDao {

    @Query("SELECT * FROM sys_legal_basis_content WHERE basis_id = :basisId ORDER BY sort_order ASC")
    fun getContentsByBasisId(basisId: Long): Flow<List<LegalBasisContentEntity>>

    @Query("SELECT * FROM sys_legal_basis_content WHERE basis_id = :basisId ORDER BY sort_order ASC")
    suspend fun getContentsByBasisIdList(basisId: Long): List<LegalBasisContentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContents(contents: List<LegalBasisContentEntity>)

    @Query("DELETE FROM sys_legal_basis_content WHERE basis_id = :basisId")
    suspend fun deleteByBasisId(basisId: Long)

    @Query("DELETE FROM sys_legal_basis_content")
    suspend fun deleteAll()
}