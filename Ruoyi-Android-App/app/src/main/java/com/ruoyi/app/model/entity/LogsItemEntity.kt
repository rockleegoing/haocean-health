package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 日志条目实体
 */
@kotlinx.serialization.Serializable
data class LogsItemEntity(
    val title: String = "",
    val operUrl: String = "",
    val operLocation: String = "",
    val operParam: String = "",
    val jsonResult: String = "",
    val operIp: String = "",
    val operTime: String = "",
    val remark: String = "",
    val errorMsg: String = "",
    val operId: Int = 0,
    val businessType: Int = 0,
    val businessTypes: String = "",
    val method: String = "",
    val requestMethod: String = "",
    val operatorType: Int = 0,
    val operName: String = "",
    val deptName: String = "",
    val status: Int = 0,
    val costTime: Int = 0
)
