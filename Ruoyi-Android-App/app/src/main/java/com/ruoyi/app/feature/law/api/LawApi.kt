package com.ruoyi.app.feature.law.api

import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import kotlinx.serialization.Serializable

object LawApi {

    suspend fun getLawList(): LawListResponse {
        return Get("${ConfigApi.baseUrl}/app/law/list").await()
    }

    suspend fun getLawById(lawId: Long): LawResponse {
        return Get("${ConfigApi.baseUrl}/app/law/$lawId").await()
    }

    suspend fun getTermListByLaw(lawId: Long): TermListResponse {
        return Get("${ConfigApi.baseUrl}/app/law/$lawId/term/list").await()
    }
}

@Serializable
data class LawListResponse(
    val code: Int,
    val msg: String,
    val rows: List<LawDto> = emptyList()
)

@Serializable
data class LawResponse(
    val code: Int,
    val msg: String,
    val data: LawDto? = null
)

@Serializable
data class TermListResponse(
    val code: Int,
    val msg: String,
    val rows: List<LegalTermDto> = emptyList()
)

@Serializable
data class LawDto(
    val id: Long,
    val name: String
)

@Serializable
data class LegalTermDto(
    val id: Long,
    val lawId: Long,
    val article: Int?,
    val zhCode: String?,
    val content: String?
)
