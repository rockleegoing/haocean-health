package com.ruoyi.app.feature.supervision.api

import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.feature.supervision.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * 监管事项 API 接口
 */
object SupervisionApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * 获取监管事项首页数据
     */
    suspend fun getHomeData(): SupervisionHomeResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/supervision/home")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseHomeDataResponse(response.body?.string() ?: "")
    }

    /**
     * 获取所有监管类型
     */
    suspend fun getAllCategories(): List<SupervisionCategory> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/supervision/category/all")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseCategoryListResponse(response.body?.string() ?: "")
    }

    /**
     * 获取监管事项列表
     */
    suspend fun getItemList(
        categoryId: Long? = null,
        keyword: String? = null
    ): List<SupervisionItem> = withContext(Dispatchers.IO) {
        val urlBuilder = StringBuilder("${ConfigApi.baseUrl}/api/supervision/item-list?")
        categoryId?.let { urlBuilder.append("categoryId=$it&") }
        keyword?.let { urlBuilder.append("name=$it&") }

        val request = Request.Builder()
            .url(urlBuilder.toString())
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseItemListResponse(response.body?.string() ?: "")
    }

    /**
     * 根据父级ID获取子事项
     */
    suspend fun getItemsByParentId(parentId: Long): List<SupervisionItem> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/supervision/item-children/$parentId")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseItemListResponse(response.body?.string() ?: "")
    }

    /**
     * 根据监管类型获取事项列表
     */
    suspend fun getItemsByCategoryId(categoryId: Long): List<SupervisionItem> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/supervision/item-list/category/$categoryId")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseItemListResponse(response.body?.string() ?: "")
    }

    /**
     * 获取监管事项详情
     */
    suspend fun getItemDetail(itemId: Long): SupervisionDetailResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/supervision/item-detail/$itemId")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseDetailResponse(response.body?.string() ?: "")
    }

    /**
     * 搜索监管事项
     */
    suspend fun searchItems(keyword: String, categoryId: Long? = null): List<SupervisionItem> = withContext(Dispatchers.IO) {
        val urlBuilder = StringBuilder("${ConfigApi.baseUrl}/api/supervision/search?keyword=$keyword")
        categoryId?.let { urlBuilder.append("&categoryId=$it") }

        val request = Request.Builder()
            .url(urlBuilder.toString())
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseItemListResponse(response.body?.string() ?: "")
    }

    // ==================== 解析方法 ====================

    private fun parseHomeDataResponse(json: String): SupervisionHomeResponse {
        return try {
            val obj = JSONObject(json)
            val categoriesArray = obj.optJSONArray("categories") ?: JSONArray()
            val topItemsArray = obj.optJSONArray("topItems") ?: JSONArray()

            val categories = mutableListOf<SupervisionCategory>()
            for (i in 0 until categoriesArray.length()) {
                categories.add(parseCategory(categoriesArray.getJSONObject(i)))
            }

            val topItems = mutableListOf<SupervisionItem>()
            for (i in 0 until topItemsArray.length()) {
                topItems.add(parseItem(topItemsArray.getJSONObject(i)))
            }

            SupervisionHomeResponse(categories = categories, topItems = topItems)
        } catch (e: Exception) {
            SupervisionHomeResponse()
        }
    }

    private fun parseCategoryListResponse(json: String): List<SupervisionCategory> {
        return try {
            val obj = JSONObject(json)
            val dataArray = obj.optJSONArray("data") ?: JSONArray()
            val categories = mutableListOf<SupervisionCategory>()
            for (i in 0 until dataArray.length()) {
                categories.add(parseCategory(dataArray.getJSONObject(i)))
            }
            categories
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseItemListResponse(json: String): List<SupervisionItem> {
        return try {
            val obj = JSONObject(json)
            val dataArray = obj.optJSONArray("data") ?: JSONArray()
            val items = mutableListOf<SupervisionItem>()
            for (i in 0 until dataArray.length()) {
                items.add(parseItem(dataArray.getJSONObject(i)))
            }
            items
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseDetailResponse(json: String): SupervisionDetailResponse {
        return try {
            val obj = JSONObject(json)
            val data = obj.optJSONObject("data") ?: JSONObject()

            val item = if (data.has("item") && !data.isNull("item")) {
                parseItem(data.getJSONObject("item"))
            } else null

            val languageLinks = mutableListOf<SupervisionLanguageLink>()
            val langArray = data.optJSONArray("languageLinks") ?: JSONArray()
            for (i in 0 until langArray.length()) {
                languageLinks.add(parseLanguageLink(langArray.getJSONObject(i)))
            }

            val regulationLinks = mutableListOf<SupervisionRegulationLink>()
            val regArray = data.optJSONArray("regulationLinks") ?: JSONArray()
            for (i in 0 until regArray.length()) {
                regulationLinks.add(parseRegulationLink(regArray.getJSONObject(i)))
            }

            SupervisionDetailResponse(
                item = item,
                languageLinks = languageLinks,
                regulationLinks = regulationLinks
            )
        } catch (e: Exception) {
            SupervisionDetailResponse()
        }
    }

    private fun parseCategory(obj: JSONObject): SupervisionCategory {
        return SupervisionCategory(
            categoryId = obj.optLong("categoryId", 0),
            categoryName = obj.optString("categoryName", ""),
            categoryCode = obj.optString("categoryCode", null),
            icon = obj.optString("icon", null),
            sortOrder = obj.optInt("sortOrder", 0),
            status = obj.optString("status", "0")
        )
    }

    private fun parseItem(obj: JSONObject): SupervisionItem {
        return SupervisionItem(
            itemId = obj.optLong("itemId", 0),
            itemNo = obj.optString("itemNo", null),
            name = obj.optString("name", ""),
            parentId = obj.optLong("parentId", 0),
            categoryId = if (obj.has("categoryId") && !obj.isNull("categoryId")) obj.optLong("categoryId") else null,
            categoryName = obj.optString("categoryName", null),
            description = obj.optString("description", null),
            legalBasis = obj.optString("legalBasis", null),
            sortOrder = obj.optInt("sortOrder", 0),
            status = obj.optString("status", "0")
        )
    }

    private fun parseLanguageLink(obj: JSONObject): SupervisionLanguageLink {
        return SupervisionLanguageLink(
            linkId = obj.optLong("linkId", 0),
            itemId = obj.optLong("itemId", 0),
            languageId = obj.optLong("languageId", 0),
            languageName = obj.optString("languageName", null),
            languageContent = obj.optString("languageContent", null),
            sortOrder = obj.optInt("sortOrder", 0)
        )
    }

    private fun parseRegulationLink(obj: JSONObject): SupervisionRegulationLink {
        return SupervisionRegulationLink(
            linkId = obj.optLong("linkId", 0),
            itemId = obj.optLong("itemId", 0),
            regulationId = obj.optLong("regulationId", 0),
            regulationName = obj.optString("regulationName", null),
            lawCode = obj.optString("lawCode", null),
            sortOrder = obj.optInt("sortOrder", 0)
        )
    }
}
