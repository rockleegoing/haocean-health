package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 通知响应实体
 */
@kotlinx.serialization.Serializable
data class NoticeEntity(
    val code: Int = 0,
    val msg: String = "",
    val rows: List<NoticeItemEntity> = arrayListOf(),
    val total: Int = 0
)
