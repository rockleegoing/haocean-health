package com.ruoyi.app.feature.law.api

import com.drake.net.Get
import com.drake.net.Post
import com.drake.net.Delete
import com.ruoyi.app.api.ConfigApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

object LawTypeBindApi {

    suspend fun getAllBinds(): LawTypeBindListResponse = withContext(Dispatchers.IO) {
        Get<LawTypeBindListResponse>("${ConfigApi.baseUrl}/app/lawtype/bind/list").await()
    }

    suspend fun getBindsByLawId(lawId: Long): LawTypeBindListResponse = withContext(Dispatchers.IO) {
        Get<LawTypeBindListResponse>("${ConfigApi.baseUrl}/app/lawtype/bind/$lawId").await()
    }

    suspend fun bind(lawId: Long, typeIds: List<Long>): BaseResponse = withContext(Dispatchers.IO) {
        Post<BaseResponse>("${ConfigApi.baseUrl}/app/lawtype/bind/$lawId") {
            setBody(typeIds)
        }.await()
    }

    suspend fun deleteByLawId(lawId: Long): BaseResponse = withContext(Dispatchers.IO) {
        Delete<BaseResponse>("${ConfigApi.baseUrl}/app/lawtype/bind/$lawId").await()
    }

    suspend fun deleteBind(lawId: Long, typeId: Long): BaseResponse = withContext(Dispatchers.IO) {
        Delete<BaseResponse>("${ConfigApi.baseUrl}/app/lawtype/bind/$lawId/$typeId").await()
    }
}

@Serializable
data class LawTypeBindListResponse(
    val code: Int,
    val msg: String,
    val data: List<LawTypeBindDto> = emptyList()
)

@Serializable
data class LawTypeBindDto(
    val lawId: Long,
    val typeId: Long
)

@Serializable
data class BaseResponse(
    val code: Int,
    val msg: String
)