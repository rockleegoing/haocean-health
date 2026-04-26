package com.ruoyi.app.feature.document.api

import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.feature.document.model.DocumentCategory
import com.ruoyi.app.feature.document.model.DocumentGroup
import com.ruoyi.app.feature.document.model.DocumentTemplate
import com.ruoyi.app.feature.document.model.DocumentVariable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * 文书提交请求
 */
data class DocumentSubmitRequest(
    val templateId: Long,
    val recordId: Long? = null,
    val unitId: Long? = null,
    val variables: String,
    val signatures: String,
    val status: String = "1"
)

/**
 * 文书提交响应
 */
data class DocumentSubmitResponse(
    val code: Int,
    val msg: String,
    val data: DocumentRecord?
)

/**
 * 文书记录（用于接收提交后的返回数据）
 */
data class DocumentRecord(
    val id: Long,
    val documentNo: String?,
    val templateId: Long?,
    val templateName: String?,
    val recordId: Long?,
    val unitId: Long?,
    val status: String?,
    val syncStatus: String?,
    val createTime: String?
)

/**
 * 文书模板与行业分类关联
 */
data class DocumentTemplateIndustry(
    val templateId: Long,
    val industryCategoryId: Long
)

/**
 * 文书模板 API 接口
 */
object DocumentApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * 同步文书模板(下行)
     */
    suspend fun syncTemplates(): List<DocumentTemplate> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/admin/document/sync/template")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseTemplateListResponse(response.body?.string() ?: "")
    }

    /**
     * 同步文书套组(下行)
     */
    suspend fun syncGroups(): List<DocumentGroup> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/admin/document/sync/group")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseGroupListResponse(response.body?.string() ?: "")
    }

    /**
     * 同步文书分类(下行)
     */
    suspend fun syncCategories(): List<DocumentCategory> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/admin/document/category/sync")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseCategoryListResponse(response.body?.string() ?: "")
    }

    /**
     * 同步文书模板与行业分类关联(下行)
     */
    suspend fun syncTemplateIndustry(): List<DocumentTemplateIndustry> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/admin/document/template/industry/sync")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseTemplateIndustryResponse(response.body?.string() ?: "")
    }

    /**
     * 获取模板详情(含变量)
     */
    suspend fun getTemplateDetail(id: Long): Pair<DocumentTemplate?, List<DocumentVariable>> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/admin/document/mobile/template/$id")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseTemplateDetailResponse(response.body?.string() ?: "")
    }

    /**
     * 提交生成的文书
     */
    suspend fun submitDocument(request: DocumentSubmitRequest): DocumentSubmitResponse = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("templateId", request.templateId)
            request.recordId?.let { put("recordId", it) }
            request.unitId?.let { put("unitId", it) }
            put("variables", request.variables)
            put("signatures", request.signatures)
            put("status", request.status)
        }

        val req = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/document/record")
            .post(json.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(req).execute()
        parseSubmitResponse(response.body?.string() ?: "")
    }

    // ==================== 解析方法 ====================

    private fun parseCategoryListResponse(json: String): List<DocumentCategory> {
        return try {
            val obj = JSONObject(json)
            val dataArray = obj.optJSONArray("data") ?: JSONArray()
            val categories = mutableListOf<DocumentCategory>()
            for (i in 0 until dataArray.length()) {
                val itemObj = dataArray.getJSONObject(i)
                categories.add(DocumentCategory(
                    categoryId = itemObj.optLong("categoryId", 0),
                    categoryName = itemObj.optString("categoryName", ""),
                    displayType = itemObj.optString("displayType", "grid"),
                    sort = itemObj.optInt("sort", 0),
                    status = itemObj.optString("status", "0"),
                    createTime = itemObj.optString("createTime", null)
                ))
            }
            categories
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseTemplateIndustryResponse(json: String): List<DocumentTemplateIndustry> {
        return try {
            val obj = JSONObject(json)
            val dataArray = obj.optJSONArray("data") ?: JSONArray()
            val relations = mutableListOf<DocumentTemplateIndustry>()
            for (i in 0 until dataArray.length()) {
                val itemObj = dataArray.getJSONObject(i)
                relations.add(DocumentTemplateIndustry(
                    templateId = itemObj.optLong("templateId", 0),
                    industryCategoryId = itemObj.optLong("industryCategoryId", 0)
                ))
            }
            relations
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseTemplateListResponse(json: String): List<DocumentTemplate> {
        return try {
            val obj = JSONObject(json)
            val dataArray = obj.optJSONArray("data") ?: JSONArray()
            val templates = mutableListOf<DocumentTemplate>()
            for (i in 0 until dataArray.length()) {
                templates.add(parseTemplate(dataArray.getJSONObject(i)))
            }
            templates
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseGroupListResponse(json: String): List<DocumentGroup> {
        return try {
            val obj = JSONObject(json)
            val dataArray = obj.optJSONArray("data") ?: JSONArray()
            val groups = mutableListOf<DocumentGroup>()
            for (i in 0 until dataArray.length()) {
                groups.add(parseGroup(dataArray.getJSONObject(i)))
            }
            groups
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseTemplateDetailResponse(json: String): Pair<DocumentTemplate?, List<DocumentVariable>> {
        return try {
            val obj = JSONObject(json)
            val data = obj.optJSONObject("data") ?: JSONObject()

            val template = if (data.has("template") && !data.isNull("template")) {
                parseTemplate(data.getJSONObject("template"))
            } else null

            val variables = mutableListOf<DocumentVariable>()
            val variablesArray = data.optJSONArray("variables") ?: JSONArray()
            for (i in 0 until variablesArray.length()) {
                variables.add(parseVariable(variablesArray.getJSONObject(i)))
            }

            Pair(template, variables)
        } catch (e: Exception) {
            Pair(null, emptyList())
        }
    }

    private fun parseTemplate(obj: JSONObject): DocumentTemplate {
        return DocumentTemplate(
            id = obj.optLong("id", 0),
            templateCode = obj.optString("templateCode", ""),
            templateName = obj.optString("templateName", ""),
            templateType = obj.optString("templateType", null),
            category = obj.optString("category", null),
            categoryId = obj.optLong("categoryId", 0),
            filePath = obj.optString("filePath", null),
            fileUrl = obj.optString("fileUrl", null),
            version = obj.optInt("version", 1),
            isActive = obj.optString("isActive", "1"),
            industryCategoryId = if (obj.has("industryCategoryId") && !obj.isNull("industryCategoryId")) obj.optLong("industryCategoryId") else null,
            industryCategoryName = obj.optString("industryCategoryName", null)
        )
    }

    private fun parseGroup(obj: JSONObject): DocumentGroup {
        return DocumentGroup(
            id = obj.optLong("id", 0),
            groupCode = obj.optString("groupCode", ""),
            groupName = obj.optString("groupName", ""),
            groupType = obj.optString("groupType", null),
            templates = obj.optString("templates", null),
            isActive = obj.optString("isActive", "1")
        )
    }

    private fun parseVariable(obj: JSONObject): DocumentVariable {
        return DocumentVariable(
            id = obj.optLong("id", 0),
            templateId = obj.optLong("templateId", 0),
            variableName = obj.optString("variableName", ""),
            variableLabel = obj.optString("variableLabel", null),
            variableType = obj.optString("variableType", "TEXT"),
            required = obj.optString("required", "1"),
            defaultValue = obj.optString("defaultValue", null),
            options = obj.optString("options", null),
            sortOrder = obj.optInt("sortOrder", 0),
            maxLength = if (obj.has("maxLength") && !obj.isNull("maxLength")) obj.optInt("maxLength") else null
        )
    }

    private fun parseSubmitResponse(json: String): DocumentSubmitResponse {
        return try {
            val obj = JSONObject(json)
            val data = if (obj.has("data") && !obj.isNull("data")) obj.getJSONObject("data") else null
            DocumentSubmitResponse(
                code = obj.optInt("code", 500),
                msg = obj.optString("msg", ""),
                data = data?.let {
                    DocumentRecord(
                        id = it.optLong("id", 0),
                        documentNo = it.optString("documentNo", null),
                        templateId = if (it.has("templateId") && !it.isNull("templateId")) it.optLong("templateId") else null,
                        templateName = it.optString("templateName", null),
                        recordId = if (it.has("recordId") && !it.isNull("recordId")) it.optLong("recordId") else null,
                        unitId = if (it.has("unitId") && !it.isNull("unitId")) it.optLong("unitId") else null,
                        status = it.optString("status", null),
                        syncStatus = it.optString("syncStatus", null),
                        createTime = it.optString("createTime", null)
                    )
                }
            )
        } catch (e: Exception) {
            DocumentSubmitResponse(code = 500, msg = e.message ?: "解析失败", data = null)
        }
    }
}