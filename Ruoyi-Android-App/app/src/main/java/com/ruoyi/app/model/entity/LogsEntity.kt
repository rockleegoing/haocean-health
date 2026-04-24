package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 日志列表响应实体
 */
@kotlinx.serialization.Serializable
data class LogsEntity(
    val total: Int = 0,
    val code: Int = 0,
    val msg: String = "",
    val rows: List<LogsItemEntity> = arrayListOf()
)
