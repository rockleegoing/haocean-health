package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 用户实体
 */
@kotlinx.serialization.Serializable
data class UserEntity(
    val userId: Long = 0,
    val userName: String = "",
    val nickName: String = "",
    val email: String = "",
    val phonenumber: String = "",
    val avatar: String = "",
    val createTime: String = "",
    val sex: String = "",
    val status: String = "0",
    val password: String = "", // BCrypt 哈希（仅用于在线登录验证）
    val plainPassword: String = "", // 明文密码，用于离线登录验证
    val roles: List<String> = emptyList(), // 角色列表
    val permissions: List<String> = emptyList(), // 权限列表
    val pwdChrtype: Int = 0, // 密码强度
    val isDefaultModifyPwd: Boolean = false, // 是否默认修改密码
    val isPasswordExpired: Boolean = false // 密码是否过期
)
