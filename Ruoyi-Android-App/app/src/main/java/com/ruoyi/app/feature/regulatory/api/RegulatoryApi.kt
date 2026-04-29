package com.ruoyi.app.feature.regulatory.api

import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi

object RegulatoryApi {

    suspend fun getMatterList(): MatterListResponse {
        return Get("${ConfigApi.baseUrl}/app/regulatory/matter/list").await()
    }

    suspend fun getMatterById(matterId: Long): MatterResponse {
        return Get("${ConfigApi.baseUrl}/app/regulatory/matter/$matterId").await()
    }

    suspend fun getItemListByMatter(matterId: Long): ItemListResponse {
        return Get("${ConfigApi.baseUrl}/app/regulatory/matter/$matterId/item/list").await()
    }

    suspend fun getCategoryBindList(): CategoryBindListResponse {
        return Get("${ConfigApi.baseUrl}/app/regulatory/categorybind/list").await()
    }
}

@kotlinx.serialization.Serializable
data class MatterListResponse(
    val code: Int,
    val msg: String,
    val rows: List<MatterDto> = emptyList()
)

@kotlinx.serialization.Serializable
data class MatterResponse(
    val code: Int,
    val msg: String,
    val data: MatterDto? = null
)

@kotlinx.serialization.Serializable
data class ItemListResponse(
    val code: Int,
    val msg: String,
    val rows: List<ItemDto> = emptyList()
)

@kotlinx.serialization.Serializable
data class CategoryBindListResponse(
    val code: Int,
    val msg: String,
    val rows: List<CategoryBindDto> = emptyList()
)

@kotlinx.serialization.Serializable
data class MatterDto(
    val matterId: Long,
    val matterName: String,
    val categoryId: Long?,
    val description: String?,
    val status: String?
)

@kotlinx.serialization.Serializable
data class ItemDto(
    val itemId: Long,
    val matterId: Long,
    val itemNo: String?,
    val name: String,
    val description: String?,
    val legalBasis: String?
)

@kotlinx.serialization.Serializable
data class CategoryBindDto(
    val id: Long,
    val industryCategoryId: Long,
    val matterId: Long
)
