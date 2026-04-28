package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.ProcessingBasisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProcessingBasisDao {

    @Query("SELECT * FROM sys_processing_basis WHERE del_flag = '0' ORDER BY basis_id ASC")
    fun getAllProcessingBasises(): Flow<List<ProcessingBasisEntity>>

    @Query("SELECT * FROM sys_processing_basis WHERE basis_id = :basisId AND del_flag = '0'")
    suspend fun getProcessingBasisById(basisId: Long): ProcessingBasisEntity?

    @Query("SELECT * FROM sys_processing_basis WHERE regulation_id = :regulationId AND del_flag = '0' ORDER BY basis_id ASC")
    fun getProcessingBasisesByRegulationId(regulationId: Long): Flow<List<ProcessingBasisEntity>>

    @Query("SELECT * FROM sys_processing_basis WHERE (title LIKE '%' || :keyword || '%' OR violation_type LIKE '%' || :keyword || '%') AND del_flag = '0' ORDER BY basis_id ASC")
    fun searchProcessingBasises(keyword: String): Flow<List<ProcessingBasisEntity>>

    @Query("""
        SELECT pb.* FROM sys_processing_basis pb
        INNER JOIN sys_basis_chapter_link bcl ON pb.basis_id = bcl.basis_id
        WHERE bcl.article_id = :articleId AND bcl.basis_type = 'processing' AND pb.del_flag = '0'
        ORDER BY pb.basis_id ASC
    """)
    fun getProcessingBasisesByArticleId(articleId: Long): Flow<List<ProcessingBasisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessingBasises(entities: List<ProcessingBasisEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessingBasis(entity: ProcessingBasisEntity)

    @Delete
    suspend fun deleteProcessingBasis(entity: ProcessingBasisEntity)

    @Query("DELETE FROM sys_processing_basis")
    suspend fun deleteAllProcessingBasises()
}
