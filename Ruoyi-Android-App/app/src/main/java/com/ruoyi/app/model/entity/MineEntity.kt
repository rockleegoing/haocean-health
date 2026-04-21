package com.ruoyi.app.model.entity


@kotlinx.serialization.Serializable
data class MineEntity(
    val code: Int,
    val msg: String,
    val permissions: List<String> = arrayListOf(),
    val roles: List<String>?,
    val user: UserEntity?
)

@kotlinx.serialization.Serializable
data class UserEntity(
    val avatar: String,
    val createTime: String,
    val email: String,
    val nickName: String,
    val phonenumber: String,
    val userName: String
)
