package com.ruoyi.app.model.entity

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

@kotlinx.serialization.Serializable
data class DeptEntity(
    val deptName: String = "",
    val leader: String = ""
)

@kotlinx.serialization.Serializable
data class UserResult(
    val code: Int = 0,
    val msg: String = "",
    val data: UserInfoEntidy,
    val postGroup: String = "",
    val roleGroup: String = ""
)