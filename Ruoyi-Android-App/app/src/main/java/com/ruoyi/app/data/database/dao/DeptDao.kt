package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.DeptEntity

/**
 * 部门数据访问对象
 */
@Dao
interface DeptDao {

    @Query("SELECT * FROM sys_dept WHERE deptId = :deptId")
    suspend fun getDeptById(deptId: Long): DeptEntity?

    @Query("SELECT * FROM sys_dept WHERE delFlag = '0' ORDER BY orderNum")
    suspend fun getAllDepts(): List<DeptEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDept(dept: DeptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepts(depts: List<DeptEntity>)

    @Update
    suspend fun updateDept(dept: DeptEntity)

    @Delete
    suspend fun deleteDept(dept: DeptEntity)

    @Query("DELETE FROM sys_dept WHERE delFlag = '1'")
    suspend fun deleteDeletedDepts()
}
