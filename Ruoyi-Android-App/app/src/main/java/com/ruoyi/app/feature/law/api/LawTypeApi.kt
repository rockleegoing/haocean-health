package com.ruoyi.app.feature.law.api

import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

object LawTypeApi {

    suspend fun getLawTypeTree(): LawTypeTreeResponse = withContext(Dispatchers.IO) {
        Get<LawTypeTreeResponse>("${ConfigApi.baseUrl}/app/lawtype/treeList").await()
    }
}

@Serializable
data class LawTypeTreeResponse(
    val code: Int,                              // 响应码
    val msg: String,                            // 消息
    val data: List<LawTypeDto> = emptyList()    // 法律类型列表
)

/** 法律类型DTO */
@Serializable
data class LawTypeDto(
    val id: Long,                    // 主键
    val parentId: Long,              // 父类型ID
    val ancestors: String,            // 祖先路径
    val name: String,                // 类型名称
    val icon: String?,               // 图标
    val sort: Int,                   // 排序
    val status: String,              // 状态
    val children: List<LawTypeDto> = emptyList()  // 子节点
)
