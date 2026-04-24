package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 我的信息响应实体
 */
@kotlinx.serialization.Serializable
data class MineEntity(
    val code: Int,
    val msg: String,
    val permissions: List<String> = arrayListOf(),
    val roles: List<String>?,
    val user: UserEntity?
)
