package com.ruoyi.app.model.entity

import com.ruoyi.app.api.ConfigApi
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi @kotlinx.serialization.Serializable
data class LoginEntity(
    val code: Int = 0,
    val msg: String = "",
    val token: String = ""
){
    fun isSuceess(): Boolean {
        return code == ConfigApi.SUCCESS
    }

}