package com.ruoyi.app.model.request
@kotlinx.serialization.Serializable
data class LoginRequest(
    val code: String,
    val password: String,
    val username: String,
    var uuid: String
)
@kotlinx.serialization.Serializable
data class registerRequest(
    val code: String,
    var uuid: String,
    val password: String,
    val confirmPassword: String,
    val username: String
)