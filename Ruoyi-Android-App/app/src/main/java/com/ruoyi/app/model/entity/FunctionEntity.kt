package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 功能实体
 */
@kotlinx.serialization.Serializable
data class FunctionEntity(
    val plug: BasicEntity,
    val basic: BasicEntity
)
