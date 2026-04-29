package com.ruoyi.app.feature.normative.api

import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import kotlinx.serialization.Serializable

object NormativeApi {

    suspend fun getCategoryList(): CategoryListResponse {
        return Get("${ConfigApi.baseUrl}/app/normative/category/list").await()
    }

    suspend fun getLanguageList(categoryId: Long? = null): LanguageListResponse {
        val url = if (categoryId != null) {
            "${ConfigApi.baseUrl}/app/normative/language/list?categoryId=$categoryId"
        } else {
            "${ConfigApi.baseUrl}/app/normative/language/list"
        }
        return Get(url).await()
    }

    suspend fun getMatterBindList(): MatterBindListResponse {
        return Get("${ConfigApi.baseUrl}/app/normative/matterbind/list").await()
    }

    suspend fun getTermBindList(legalTermId: Long? = null): TermBindListResponse {
        val url = if (legalTermId != null) {
            "${ConfigApi.baseUrl}/app/normative/termbind/list?legalTermId=$legalTermId"
        } else {
            "${ConfigApi.baseUrl}/app/normative/termbind/list"
        }
        return Get(url).await()
    }
}

@Serializable
data class CategoryListResponse(
    val code: Int,
    val msg: String,
    val rows: List<CategoryDto> = emptyList()
)

@Serializable
data class LanguageListResponse(
    val code: Int,
    val msg: String,
    val rows: List<LanguageDto> = emptyList()
)

@Serializable
data class MatterBindListResponse(
    val code: Int,
    val msg: String,
    val rows: List<MatterBindDto> = emptyList()
)

@Serializable
data class TermBindListResponse(
    val code: Int,
    val msg: String,
    val rows: List<TermBindDto> = emptyList()
)

@Serializable
data class CategoryDto(
    val code: Long,
    val name: String,
    val parentCode: Long?,
    val sortOrder: Int?,
    val status: String?
)

@Serializable
data class LanguageDto(
    val id: Long,
    val standardCode: String?,
    val standardPhrase: String,
    val supervisoryOpinion: String?,
    val basisType: Int?,
    val primaryCategory: Long?
)

@Serializable
data class MatterBindDto(
    val id: Long,
    val normativeLanguageId: Long,
    val regulatoryMatterId: Long,
    val basisType: Int?
)

@Serializable
data class TermBindDto(
    val id: Long,
    val legalTermId: Long,
    val normativeLanguageId: Long,
    val basisType: Int?
)