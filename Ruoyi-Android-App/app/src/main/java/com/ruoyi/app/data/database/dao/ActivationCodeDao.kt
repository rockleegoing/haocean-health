package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.ActivationCodeEntity

/**
 * 激活码数据访问对象
 */
@Dao
interface ActivationCodeDao {

    @Query("SELECT * FROM sys_activation_code WHERE codeId = :codeId")
    suspend fun getActivationCodeById(codeId: Long): ActivationCodeEntity?

    @Query("SELECT * FROM sys_activation_code WHERE codeValue = :codeValue LIMIT 1")
    suspend fun getActivationCodeByValue(codeValue: String): ActivationCodeEntity?

    @Query("SELECT * FROM sys_activation_code ORDER BY createTime DESC")
    suspend fun getAllActivationCodes(): List<ActivationCodeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivationCode(code: ActivationCodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivationCodes(codes: List<ActivationCodeEntity>)

    @Update
    suspend fun updateActivationCode(code: ActivationCodeEntity)

    @Delete
    suspend fun deleteActivationCode(code: ActivationCodeEntity)
}
