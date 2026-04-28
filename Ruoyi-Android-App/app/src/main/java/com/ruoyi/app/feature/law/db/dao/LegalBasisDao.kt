package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.LegalBasisEntity
import kotlinx.coroutines.flow.Flow

/**
 * 定性依据DAO
 */
@Dao
interface LegalBasisDao {

    @Query("SELECT * FROM sys_legal_basis WHERE del_flag = '0' ORDER BY basis_id DESC")
    fun getAllLegalBasises(): Flow<List<LegalBasisEntity>>

    @Query("SELECT * FROM sys_legal_basis WHERE basis_id = :basisId AND del_flag = '0'")
    suspend fun getLegalBasisById(basisId: Long): LegalBasisEntity?

    @Query("SELECT * FROM sys_legal_basis WHERE regulation_id = :regulationId AND del_flag = '0' ORDER BY basis_id DESC")
    fun getLegalBasisesByRegulationId(regulationId: Long): Flow<List<LegalBasisEntity>>

    @Query("SELECT * FROM sys_legal_basis WHERE regulation_id = :regulationId AND del_flag = '0'")
    suspend fun getLegalBasisesByRegulationIdList(regulationId: Long): List<LegalBasisEntity>

    @Query("SELECT * FROM sys_legal_basis WHERE title LIKE '%' || :keyword || '%' AND del_flag = '0' ORDER BY basis_id DESC")
    fun searchLegalBasises(keyword: String): Flow<List<LegalBasisEntity>>

    @Query("SELECT * FROM sys_legal_basis WHERE violation_type LIKE '%' || :violationType || '%' AND del_flag = '0' ORDER BY basis_id DESC")
    fun getLegalBasisesByViolationType(violationType: String): Flow<List<LegalBasisEntity>>

    @Query("SELECT * FROM sys_legal_basis WHERE del_flag = '0' ORDER BY basis_id DESC")
    suspend fun getAllLegalBasisesList(): List<LegalBasisEntity>

    @Query("""
        SELECT lb.* FROM sys_legal_basis lb
        INNER JOIN sys_basis_chapter_link bcl ON lb.basis_id = bcl.basis_id
        WHERE bcl.article_id = :articleId AND bcl.basis_type = 'legal' AND lb.del_flag = '0'
        ORDER BY lb.basis_id DESC
    """)
    fun getLegalBasisesByArticleId(articleId: Long): Flow<List<LegalBasisEntity>>

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

    @Query("SELECT COUNT(*) FROM sys_legal_basis WHERE del_flag = '0'")
    suspend fun getCount(): Int
}
