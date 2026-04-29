package com.ruoyi.app.feature.regulatory.api

import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

object RegulatoryApi {

    suspend fun getMatterList(): MatterListResponse = withContext(Dispatchers.IO) {
        Get<MatterListResponse>("${ConfigApi.baseUrl}/app/regulatory/matter/list").await()
    }

    suspend fun getMatterById(matterId: Long): MatterResponse = withContext(Dispatchers.IO) {
        Get<MatterResponse>("${ConfigApi.baseUrl}/app/regulatory/matter/$matterId").await()
    }

    suspend fun getItemListByMatter(matterId: Long): ItemListResponse = withContext(Dispatchers.IO) {
        Get<ItemListResponse>("${ConfigApi.baseUrl}/app/regulatory/matter/$matterId/item/list").await()
    }

    suspend fun getCategoryBindList(): CategoryBindListResponse = withContext(Dispatchers.IO) {
        Get<CategoryBindListResponse>("${ConfigApi.baseUrl}/app/regulatory/categorybind/list").await()
    }
}

@Serializable
data class MatterListResponse(
    val code: Int,                              // 响应码
    val msg: String,                            // 消息
    val rows: List<MatterDto> = emptyList()    // 监管事项列表
)

@Serializable
data class MatterResponse(
    val code: Int,                              // 响应码
    val msg: String,                            // 消息
    val data: MatterDto? = null                // 监管事项详情
)

@Serializable
data class ItemListResponse(
    val code: Int,                              // 响应码
    val msg: String,                            // 消息
    val rows: List<ItemDto> = emptyList()       // 检查项列表
)

@Serializable
data class CategoryBindListResponse(
    val code: Int,                              // 响应码
    val msg: String,                            // 消息
    val rows: List<CategoryBindDto> = emptyList() // 行业分类关联列表
)

/** 监管事项DTO */
@Serializable
data class MatterDto(
    val matterId: Long,       // 监管事项ID
    val matterName: String,  // 监管事项名称
    val categoryId: Long?,   // 行业分类ID
    val description: String?, // 描述
    val status: String?     // 状态
)

/** 监管事项检查项DTO */
@Serializable
data class ItemDto(
    val itemId: Long,         // 检查项ID
    val matterId: Long,       // 所属监管事项ID
    val itemNo: String?,     // 监管子项编码
    val name: String,        // 检查项名称
    val description: String?,// 描述/检查内容
    val legalBasis: String?  // 法律依据
)

/** 行业分类与监管事项关联DTO */
@Serializable
data class CategoryBindDto(
    val id: Long,                // 关联ID
    val industryCategoryId: Long, // 行业分类ID
    val matterId: Long          // 监管事项ID
)
