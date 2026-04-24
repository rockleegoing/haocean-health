package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi @kotlinx.serialization.Serializable
class AvatarEntity(
    val code: Int = 0,
    val msg: String = "",
    val imgUrl: String = ""
)