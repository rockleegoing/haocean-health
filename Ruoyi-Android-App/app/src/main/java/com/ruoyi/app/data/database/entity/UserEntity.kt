package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户实体类
 */
@Entity(tableName = "sys_user")
data class UserEntity(
    @PrimaryKey val userId: Long,
    val deptId: Long?,
    val userName: String,
    val nickName: String,
    val userType: String?,
    val email: String?,
    val phonenumber: String?,
    val sex: String?,
    val avatar: String?,
    val password: String, // bcrypt 加密后的密码
    val plainPassword: String = "", // 明文密码，用于离线登录验证
    val status: String,
    val delFlag: String,
    val loginIp: String?,
    val loginDate: Long?,
    val createBy: String?,
    val createTime: Long,
    val updateBy: String?,
    val updateTime: Long?,
    val remark: String?
)
