package com.ruoyi.app.feature.document.api

import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.feature.document.model.DocumentGroup
import com.ruoyi.app.feature.document.model.DocumentTemplate
import com.ruoyi.app.feature.document.model.DocumentVariable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

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

    // ==================== 解析方法 ====================

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
            filePath = obj.optString("filePath", null),
            fileUrl = obj.optString("fileUrl", null),
            version = obj.optInt("version", 1),
            isActive = obj.optString("isActive", "1")
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
}