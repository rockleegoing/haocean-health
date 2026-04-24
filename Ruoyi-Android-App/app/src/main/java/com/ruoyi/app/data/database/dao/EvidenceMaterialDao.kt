package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.EvidenceMaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenceMaterialDao {

    @Query("SELECT * FROM t_evidence_material WHERE recordId = :recordId ORDER BY createTime ASC")
    fun getMaterialsByRecordId(recordId: Long): Flow<List<EvidenceMaterialEntity>>

    @Query("SELECT * FROM t_evidence_material WHERE recordId = :recordId ORDER BY createTime ASC")
    suspend fun getMaterialsByRecordIdSync(recordId: Long): List<EvidenceMaterialEntity>

    @Query("SELECT * FROM t_evidence_material WHERE recordId = :recordId AND evidenceType = :type ORDER BY createTime ASC")
    fun getMaterialsByType(recordId: Long, type: String): Flow<List<EvidenceMaterialEntity>>

    @Query("SELECT * FROM t_evidence_material WHERE id = :id")
    suspend fun getMaterialById(id: Long): EvidenceMaterialEntity?

    @Query("SELECT * FROM t_evidence_material WHERE syncStatus = :syncStatus")
    suspend fun getMaterialsBySyncStatus(syncStatus: String): List<EvidenceMaterialEntity>

    @Query("SELECT COUNT(*) FROM t_evidence_material WHERE recordId = :recordId AND evidenceType = :type")
    suspend fun getMaterialCountByType(recordId: Long, type: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: EvidenceMaterialEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterials(materials: List<EvidenceMaterialEntity>)

    @Update
    suspend fun updateMaterial(material: EvidenceMaterialEntity)

    @Query("UPDATE t_evidence_material SET syncStatus = :syncStatus WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, syncStatus: String)

    @Delete
    suspend fun deleteMaterial(material: EvidenceMaterialEntity)

    @Query("DELETE FROM t_evidence_material WHERE recordId = :recordId")
    suspend fun deleteMaterialsByRecordId(recordId: Long)
}
