package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 通知条目实体
 */
@kotlinx.serialization.Serializable
data class NoticeItemEntity(
    val createBy: String = "",
    val createTime: String = "",
    val noticeContent: String = "",
    val noticeId: Int = 0,
    val noticeTitle: String = "",
    val noticeType: String = "",
    val remark: String = "",
    val status: String = "",
    val updateBy: String = "",
    val updateTime: String = ""
)
