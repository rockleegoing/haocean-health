package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 工作台响应实体
 */
@kotlinx.serialization.Serializable
data class WorkIndexEntity(
    val msg: String,
    val code: Int,
    val data: WorkIndexDataEntity
)
