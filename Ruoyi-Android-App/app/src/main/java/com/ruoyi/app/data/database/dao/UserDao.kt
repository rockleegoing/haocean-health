package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.UserEntity

/**
 * 用户数据访问对象
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM sys_user WHERE userId = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    @Query("SELECT * FROM sys_user WHERE userName = :userName LIMIT 1")
    suspend fun getUserByUserName(userName: String): UserEntity?

    @Query("SELECT * FROM sys_user WHERE delFlag = '0'")
    suspend fun getAllUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM sys_user WHERE delFlag = '1'")
    suspend fun deleteDeletedUsers()

    @Query("SELECT COUNT(*) FROM sys_user")
    suspend fun getUserCount(): Int
}
