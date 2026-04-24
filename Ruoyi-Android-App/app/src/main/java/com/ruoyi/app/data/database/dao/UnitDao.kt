package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.UnitEntity

/**
 * 执法单位数据访问对象
 */
@Dao
interface UnitDao {

    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' ORDER BY createTime DESC")
    suspend fun getAllUnits(): List<UnitEntity>

    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' ORDER BY createTime DESC LIMIT :limit OFFSET :offset")
    suspend fun getUnitsPaged(limit: Int, offset: Int): List<UnitEntity>

    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' AND unitName LIKE '%' || :keyword || '%' ORDER BY createTime DESC")
    suspend fun searchUnits(keyword: String): List<UnitEntity>

    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' AND industryCategoryId = :categoryId ORDER BY createTime DESC")
    suspend fun getUnitsByCategory(categoryId: Long): List<UnitEntity>

    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' AND region = :region ORDER BY createTime DESC")
    suspend fun getUnitsByRegion(region: String): List<UnitEntity>

    @Query("SELECT * FROM sys_unit WHERE unitId = :unitId")
    suspend fun getUnitById(unitId: Long): UnitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnits(units: List<UnitEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: UnitEntity)

    @Delete
    suspend fun deleteUnit(unit: UnitEntity)

    @Query("DELETE FROM sys_unit")
    suspend fun deleteAllUnits()

    @Query("SELECT COUNT(*) FROM sys_unit")
    suspend fun getUnitCount(): Int
}
