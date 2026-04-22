package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.RoleEntity

/**
 * 角色数据访问对象
 */
@Dao
interface RoleDao {

    @Query("SELECT * FROM sys_role WHERE roleId = :roleId")
    suspend fun getRoleById(roleId: Long): RoleEntity?

    @Query("SELECT * FROM sys_role WHERE delFlag = '0'")
    suspend fun getAllRoles(): List<RoleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRole(role: RoleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoles(roles: List<RoleEntity>)

    @Update
    suspend fun updateRole(role: RoleEntity)

    @Delete
    suspend fun deleteRole(role: RoleEntity)

    @Query("DELETE FROM sys_role WHERE delFlag = '1'")
    suspend fun deleteDeletedRoles()
}
