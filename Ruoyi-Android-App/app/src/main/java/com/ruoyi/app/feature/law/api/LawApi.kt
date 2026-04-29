package com.ruoyi.app.feature.law.api

import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

object LawApi {

    suspend fun getLawList(): LawListResponse = withContext(Dispatchers.IO) {
        Get<LawListResponse>("${ConfigApi.baseUrl}/app/law/list").await()
    }

    suspend fun getLawById(lawId: Long): LawResponse = withContext(Dispatchers.IO) {
        Get<LawResponse>("${ConfigApi.baseUrl}/app/law/$lawId").await()
    }

    suspend fun getTermListByLaw(lawId: Long): TermListResponse = withContext(Dispatchers.IO) {
        Get<TermListResponse>("${ConfigApi.baseUrl}/app/law/$lawId/term/list").await()
    }
}

@Serializable
data class LawListResponse(
    val code: Int,                     // 响应码
    val msg: String,                   // 消息
    val rows: List<LawDto> = emptyList()  // 法律列表
)

@Serializable
data class LawResponse(
    val code: Int,                     // 响应码
    val msg: String,                   // 消息
    val data: LawDto? = null          // 法律详情
)

@Serializable
data class TermListResponse(
    val code: Int,                     // 响应码
    val msg: String,                   // 消息
    val rows: List<LegalTermDto> = emptyList()  // 条款列表
)

/** 法律目录DTO */
@Serializable
data class LawDto(
    val id: Long,                    // 法律ID
    val name: String                 // 法律名称
)

/** 法律条款DTO */
@Serializable
data class LegalTermDto(
    val id: Long,                    // 条款ID
    val lawId: Long,                 // 法律编号
    val article: Int?,              // 条
    val zhCode: String?,             // 中文条款编码
    val content: String?             // 条款内容
)
