package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 用户信息实体
 */
@kotlinx.serialization.Serializable
data class UserInfoEntidy(
    var sex: String = "",
    var createTime: String = "",
    var email: String = "",
    var nickName: String = "",
    var phonenumber: String = "",
    var avatar: String = "",
    val dept: DeptEntity? = null
)
