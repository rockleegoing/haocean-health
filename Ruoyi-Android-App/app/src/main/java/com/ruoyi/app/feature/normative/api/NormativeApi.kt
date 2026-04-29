package com.ruoyi.app.feature.normative.api

import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

object NormativeApi {

    suspend fun getCategoryList(): CategoryListResponse = withContext(Dispatchers.IO) {
        Get<CategoryListResponse>("${ConfigApi.baseUrl}/app/normative/category/list").await()
    }

    suspend fun getLanguageList(categoryId: Long? = null): LanguageListResponse = withContext(Dispatchers.IO) {
        val url = if (categoryId != null) {
            "${ConfigApi.baseUrl}/app/normative/language/list?categoryId=$categoryId"
        } else {
            "${ConfigApi.baseUrl}/app/normative/language/list"
        }
        Get<LanguageListResponse>(url).await()
    }

    suspend fun getMatterBindList(): MatterBindListResponse = withContext(Dispatchers.IO) {
        Get<MatterBindListResponse>("${ConfigApi.baseUrl}/app/normative/matterbind/list").await()
    }

    suspend fun getTermBindList(legalTermId: Long? = null): TermBindListResponse = withContext(Dispatchers.IO) {
        val url = if (legalTermId != null) {
            "${ConfigApi.baseUrl}/app/normative/termbind/list?legalTermId=$legalTermId"
        } else {
            "${ConfigApi.baseUrl}/app/normative/termbind/list"
        }
        Get<TermBindListResponse>(url).await()
    }
}

@Serializable
data class CategoryListResponse(
    val code: Int,                              // 响应码
    val msg: String,                            // 消息
    val rows: List<CategoryDto> = emptyList()   // 分类列表
)

@Serializable
data class LanguageListResponse(
    val code: Int,                              // 响应码
    val msg: String,                            // 消息
    val rows: List<LanguageDto> = emptyList()  // 用语列表
)

@Serializable
data class MatterBindListResponse(
    val code: Int,                              // 响应码
    val msg: String,                            // 消息
    val rows: List<MatterBindDto> = emptyList() // 监管事项关联列表
)

@Serializable
data class TermBindListResponse(
    val code: Int,                              // 响应码
    val msg: String,                            // 消息
    val rows: List<TermBindDto> = emptyList()  // 法律条款关联列表
)

/** 规范用语分类DTO */
@Serializable
data class CategoryDto(
    val code: Long,            // 分类编号
    val name: String,         // 分类名称
    val parentCode: Long?,    // 父级分类编号
    val sortOrder: Int?,     // 排序号
    val status: String?       // 状态
)

/** 规范用语DTO */
@Serializable
data class LanguageDto(
    val id: Long,                    // 主键ID
    val standardCode: String?,       // 规范用语代码
    val standardPhrase: String,      // 规范用语（违法事实）
    val supervisoryOpinion: String?,  // 监督意见
    val primaryCategory: Long?       // 一级分类编号
)

/** 规范用语与监管事项关联DTO */
@Serializable
data class MatterBindDto(
    val matterId: Long,      // 监管事项编号
    val normativeId: Long,   // 规范用语编号
    val basisType: Int?      // 依据类型(1定性依据/0处理依据)
)

/** 规范用语与法律条款关联DTO */
@Serializable
data class TermBindDto(
    val legalTermId: Long,          // 法律条款ID
    val normativeLanguageId: Long,   // 规范用语编号
    val basisType: Int?             // 依据类型(1定性依据/0处理依据)
)