package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EnforcementRecordDao {

    @Query("SELECT * FROM t_enforcement_record WHERE delFlag = '0' ORDER BY createTime DESC")
    fun getAllRecords(): Flow<List<EnforcementRecordEntity>>

    @Query("SELECT * FROM t_enforcement_record WHERE delFlag = '0' AND recordStatus = :status ORDER BY createTime DESC")
    fun getRecordsByStatus(status: String): Flow<List<EnforcementRecordEntity>>

    @Query("""
        SELECT * FROM t_enforcement_record
        WHERE delFlag = '0'
        AND (:status IS NULL OR :status = '' OR recordStatus = :status)
        AND (:unitId IS NULL OR unitId = :unitId)
        AND (:industryId IS NULL OR industryId = :industryId)
        AND (:startTime IS NULL OR createTime >= :startTime)
        AND (:endTime IS NULL OR createTime <= :endTime)
        AND (:keyword IS NULL OR :keyword = '' OR unitName LIKE '%' || :keyword || '%' OR industryCode LIKE '%' || :keyword || '%' OR recordNo LIKE '%' || :keyword || '%')
        ORDER BY createTime DESC
    """)
    fun getRecordsFiltered(
        status: String?,
        unitId: Long?,
        industryId: Long?,
        startTime: Long?,
        endTime: Long?,
        keyword: String?
    ): Flow<List<EnforcementRecordEntity>>

    @Query("SELECT * FROM t_enforcement_record WHERE id = :id")
    suspend fun getRecordById(id: Long): EnforcementRecordEntity?

    @Query("SELECT * FROM t_enforcement_record WHERE unitId = :unitId AND delFlag = '0' ORDER BY createTime DESC")
    fun getRecordsByUnitId(unitId: Long): Flow<List<EnforcementRecordEntity>>

    @Query("SELECT * FROM t_enforcement_record WHERE syncStatus = :syncStatus AND delFlag = '0'")
    suspend fun getRecordsBySyncStatus(syncStatus: String): List<EnforcementRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: EnforcementRecordEntity): Long

    @Update
    suspend fun updateRecord(record: EnforcementRecordEntity)

    @Query("UPDATE t_enforcement_record SET recordStatus = :status, updateTime = :updateTime WHERE id = :id")
    suspend fun updateRecordStatus(id: Long, status: String, updateTime: Long)

    @Query("UPDATE t_enforcement_record SET syncStatus = :syncStatus WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, syncStatus: String)

    @Query("UPDATE t_enforcement_record SET delFlag = '1', updateTime = :updateTime WHERE id = :id")
    suspend fun deleteRecord(id: Long, updateTime: Long)

    @Query("SELECT COUNT(*) FROM t_enforcement_record WHERE delFlag = '0'")
    suspend fun getRecordCount(): Int

    @Query("SELECT COUNT(*) FROM t_enforcement_record WHERE delFlag = '0' AND recordStatus = :status")
    suspend fun getRecordCountByStatus(status: String): Int

    @Query("SELECT * FROM t_enforcement_record WHERE unitId = :unitId AND createTime >= :startOfDay AND createTime < :endOfDay AND delFlag = '0' LIMIT 1")
    suspend fun getTodayRecordByUnit(unitId: Long, startOfDay: Long, endOfDay: Long): EnforcementRecordEntity?
}
