package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi @kotlinx.serialization.Serializable
data class CaptchaImageEntity(
    val code: Int = 0,
    val msg: String = "",
    val captchaEnabled: Boolean = false,
    val img: String = "",
    val uuid: String = ""
)