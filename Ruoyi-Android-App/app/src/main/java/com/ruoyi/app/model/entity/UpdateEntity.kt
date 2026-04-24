package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 更新响应实体
 */
@kotlinx.serialization.Serializable
data class UpdateEntity(
    val code: Int,
    val data: UpdateDataEntity,
    val msg: String
)
