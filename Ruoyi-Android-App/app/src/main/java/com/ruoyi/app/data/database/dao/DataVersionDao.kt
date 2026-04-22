package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.DataVersionEntity

/**
 * 数据版本数据访问对象
 */
@Dao
interface DataVersionDao {

    @Query("SELECT * FROM sys_data_version WHERE tableName = :tableName LIMIT 1")
    suspend fun getDataVersionByTable(tableName: String): DataVersionEntity?

    @Query("SELECT * FROM sys_data_version")
    suspend fun getAllDataVersions(): List<DataVersionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataVersion(version: DataVersionEntity)

    @Update
    suspend fun updateDataVersion(version: DataVersionEntity)

    @Query("UPDATE sys_data_version SET dataVersion = :version, lastSyncTime = :syncTime WHERE tableName = :tableName")
    suspend fun updateDataVersion(tableName: String, version: Long, syncTime: Long)
}
