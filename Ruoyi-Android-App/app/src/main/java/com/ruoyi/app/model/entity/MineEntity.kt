package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 我的信息响应实体
 */
@kotlinx.serialization.Serializable
data class MineEntity(
    val code: Int,
    val msg: String,
    val permissions: List<String> = emptyList(),
    val roles: List<String>? = null,
    val user: UserEntity? = null,
    val pwdChrtype: Int = 0,
    val isDefaultModifyPwd: Boolean = false,
    val isPasswordExpired: Boolean = false
)
