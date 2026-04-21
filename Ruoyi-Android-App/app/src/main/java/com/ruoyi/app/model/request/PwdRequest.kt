package com.ruoyi.app.model.request
@kotlinx.serialization.Serializable
data class PwdRequest(
    val newPassword: String,
    val oldPassword: String
)