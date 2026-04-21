package com.ruoyi.app.model.request
@kotlinx.serialization.Serializable
data class EditInfoRequest(
    val nickName:String,
    val email:String,
    val phonenumber:String,
    val sex:String
)