package com.ruoyi.app.model.entity

import com.ruoyi.app.api.ConfigApi
import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi @kotlinx.serialization.Serializable
data class ResultEntity<T>(
    val code: Int,
    val msg: String,
    val data: T
) {
    fun isSuceess(): Boolean {
        return code == ConfigApi.SUCESSS
    }
}
