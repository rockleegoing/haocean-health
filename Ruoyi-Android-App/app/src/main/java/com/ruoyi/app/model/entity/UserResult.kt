package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 用户详情响应实体
 */
@kotlinx.serialization.Serializable
data class UserResult(
    val code: Int = 0,
    val msg: String = "",
    val data: UserInfoEntidy,
    val postGroup: String = "",
    val roleGroup: String = ""
)
