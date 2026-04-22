package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.SyncQueueEntity

/**
 * 同步队列数据访问对象
 */
@Dao
interface SyncQueueDao {

    @Query("SELECT * FROM sys_sync_queue WHERE queueId = :queueId")
    suspend fun getSyncQueueById(queueId: Long): SyncQueueEntity?

    @Query("SELECT * FROM sys_sync_queue WHERE syncStatus = 'pending' ORDER BY createTime ASC")
    suspend fun getPendingSyncItems(): List<SyncQueueEntity>

    @Query("SELECT * FROM sys_sync_queue WHERE syncStatus = 'failed' ORDER BY updateTime ASC")
    suspend fun getFailedSyncItems(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncQueue(item: SyncQueueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSyncQueueList(items: List<SyncQueueEntity>)

    @Update
    suspend fun updateSyncQueue(item: SyncQueueEntity)

    @Delete
    suspend fun deleteSyncQueue(item: SyncQueueEntity)

    @Query("DELETE FROM sys_sync_queue WHERE syncStatus = 'success'")
    suspend fun deleteSuccessfulSyncItems()

    @Query("SELECT COUNT(*) FROM sys_sync_queue WHERE syncStatus = 'pending'")
    suspend fun getPendingCount(): Int
}
