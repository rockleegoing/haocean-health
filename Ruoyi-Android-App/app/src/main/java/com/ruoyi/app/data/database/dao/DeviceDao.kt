package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.DeviceEntity

/**
 * 设备数据访问对象
 */
@Dao
interface DeviceDao {

    @Query("SELECT * FROM sys_device WHERE deviceId = :deviceId")
    suspend fun getDeviceById(deviceId: Long): DeviceEntity?

    @Query("SELECT * FROM sys_device WHERE deviceUuid = :deviceUuid LIMIT 1")
    suspend fun getDeviceByUuid(deviceUuid: String): DeviceEntity?

    @Query("SELECT * FROM sys_device ORDER BY createTime DESC")
    suspend fun getAllDevices(): List<DeviceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity)

    @Update
    suspend fun updateDevice(device: DeviceEntity)

    @Delete
    suspend fun deleteDevice(device: DeviceEntity)

    @Query("UPDATE sys_device SET current_user_id = :currentUserId, current_user_name = :currentUserName WHERE deviceUuid = :deviceUuid")
    suspend fun updateDeviceCurrentUser(deviceUuid: String, currentUserId: Long?, currentUserName: String?)

    @Query("UPDATE sys_device SET current_user_id = NULL, current_user_name = NULL, activation_code_id = NULL WHERE deviceId = :deviceId")
    suspend fun unbindDevice(deviceId: Long)
}
