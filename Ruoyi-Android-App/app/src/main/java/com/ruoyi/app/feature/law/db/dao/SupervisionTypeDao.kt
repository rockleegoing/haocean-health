package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.SupervisionTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SupervisionTypeDao {
    @Query("SELECT * FROM sys_supervision_type WHERE status = '0' ORDER BY sort_order ASC, type_id ASC")
    fun getAllSupervisionTypes(): Flow<List<SupervisionTypeEntity>>

    @Query("SELECT * FROM sys_supervision_type ORDER BY sort_order ASC, type_id ASC")
    fun getAllSupervisionTypesIncludeDisabled(): Flow<List<SupervisionTypeEntity>>

    @Query("SELECT * FROM sys_supervision_type WHERE type_id = :typeId")
    suspend fun getSupervisionTypeById(typeId: Long): SupervisionTypeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(types: List<SupervisionTypeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(type: SupervisionTypeEntity)

    @Delete
    suspend fun delete(type: SupervisionTypeEntity)

    @Query("DELETE FROM sys_supervision_type")
    suspend fun deleteAll()
}
