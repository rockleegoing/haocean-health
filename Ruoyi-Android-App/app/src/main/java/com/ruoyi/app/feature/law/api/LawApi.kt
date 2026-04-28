package com.ruoyi.app.feature.law.api

import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.feature.law.model.*
import com.ruoyi.app.feature.law.model.LegalBasisListResponse
import com.ruoyi.app.feature.law.model.LegalBasisDetailResponse
import com.ruoyi.app.feature.law.model.LegalBasisDetailData
import com.ruoyi.app.feature.law.model.LegalBasisContent
import com.ruoyi.app.feature.law.model.ProcessingBasisListResponse
import com.ruoyi.app.feature.law.model.ProcessingBasisDetailResponse
import com.ruoyi.app.feature.law.model.BasisChapterLinkListResponse
import com.ruoyi.app.feature.law.model.BasisChapterLinkDetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

// 字典类型响应
data class LegalTypeResponse(
    val typeId: Long,
    val typeCode: String,
    val typeName: String,
    val sortOrder: Int,
    val status: String
)

data class SupervisionTypeResponse(
    val typeId: Long,
    val typeCode: String,
    val typeName: String,
    val sortOrder: Int,
    val status: String
)

/**
 * 法律API接口
 */
object LawApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    // ==================== 法律法规API ====================

    /**
     * 获取法律法规列表（支持增量同步）
     */
    suspend fun getRegulationList(
        pageNum: Int = 1,
        pageSize: Int = 20,
        title: String? = null,
        legalType: String? = null,
        status: String? = null,
        updateTimeFrom: String? = null
    ): RegulationListResponse = withContext(Dispatchers.IO) {
        val urlBuilder = StringBuilder("${ConfigApi.baseUrl}/system/regulation/list?")
            .append("pageNum=$pageNum")
            .append("&pageSize=$pageSize")
        title?.let { urlBuilder.append("&title=$it") }
        legalType?.let { urlBuilder.append("&legalType=$it") }
        status?.let { urlBuilder.append("&status=$it") }
        updateTimeFrom?.let { urlBuilder.append("&updateTimeFrom=$it") }

        val request = Request.Builder()
            .url(urlBuilder.toString())
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseRegulationListResponse(response.body?.string() ?: "")
    }

    /**
     * 获取法律法规详情
     */
    suspend fun getRegulationDetail(regulationId: Long): RegulationDetailResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/system/regulation/$regulationId")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseRegulationDetailResponse(response.body?.string() ?: "")
    }

    /**
     * 获取章节列表
     */
    suspend fun getChapterList(
        regulationId: Long,
        pageNum: Int = 1,
        pageSize: Int = 1000,
        updateTimeFrom: String? = null
    ): ChapterListResponse = withContext(Dispatchers.IO) {
        val urlBuilder = StringBuilder("${ConfigApi.baseUrl}/system/regulation/chapters/$regulationId?")
            .append("pageNum=$pageNum")
            .append("&pageSize=$pageSize")
        updateTimeFrom?.let { urlBuilder.append("&updateTimeFrom=$it") }

        val request = Request.Builder()
            .url(urlBuilder.toString())
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseChapterListResponse(response.body?.string() ?: "")
    }

    /**
     * 获取条款列表
     */
    suspend fun getArticleList(
        regulationId: Long,
        chapterId: Long? = null,
        pageNum: Int = 1,
        pageSize: Int = 1000,
        updateTimeFrom: String? = null
    ): ArticleListResponse = withContext(Dispatchers.IO) {
        val urlBuilder = StringBuilder("${ConfigApi.baseUrl}/system/regulation/articles/$regulationId?")
            .append("pageNum=$pageNum")
            .append("&pageSize=$pageSize")
        chapterId?.let { urlBuilder.append("&chapterId=$it") }
        updateTimeFrom?.let { urlBuilder.append("&updateTimeFrom=$it") }

        val request = Request.Builder()
            .url(urlBuilder.toString())
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseArticleListResponse(response.body?.string() ?: "")
    }

    // ==================== 定性依据API ====================

    /**
     * 获取定性依据列表（支持增量同步）
     */
    suspend fun getLegalBasisList(
        pageNum: Int = 1,
        pageSize: Int = 20,
        title: String? = null,
        violationType: String? = null,
        status: String? = null,
        updateTimeFrom: String? = null
    ): LegalBasisListResponse = withContext(Dispatchers.IO) {
        val urlBuilder = StringBuilder("${ConfigApi.baseUrl}/system/legalBasis/list?")
            .append("pageNum=$pageNum")
            .append("&pageSize=$pageSize")
        title?.let { urlBuilder.append("&title=$it") }
        violationType?.let { urlBuilder.append("&violationType=$it") }
        status?.let { urlBuilder.append("&status=$it") }
        updateTimeFrom?.let { urlBuilder.append("&updateTimeFrom=$it") }

        val request = Request.Builder()
            .url(urlBuilder.toString())
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseLegalBasisListResponse(response.body?.string() ?: "")
    }

    /**
     * 获取定性依据详情
     */
    suspend fun getLegalBasisDetail(basisId: Long): LegalBasisDetailResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/system/legalBasis/$basisId")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseLegalBasisDetailResponse(response.body?.string() ?: "")
    }

    /**
     * 获取某法规关联的定性依据
     */
    suspend fun getLegalBasisByRegulation(regulationId: Long): LegalBasisListResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/system/legalBasis/regulation/$regulationId")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseLegalBasisListResponse(response.body?.string() ?: "")
    }

    // ==================== 处理依据API ====================

    /**
     * 获取处理依据列表（支持增量同步）
     */
    suspend fun getProcessingBasisList(
        pageNum: Int = 1,
        pageSize: Int = 20,
        title: String? = null,
        violationType: String? = null,
        status: String? = null,
        updateTimeFrom: String? = null
    ): ProcessingBasisListResponse = withContext(Dispatchers.IO) {
        val urlBuilder = StringBuilder("${ConfigApi.baseUrl}/system/processingBasis/list?")
            .append("pageNum=$pageNum")
            .append("&pageSize=$pageSize")
        title?.let { urlBuilder.append("&title=$it") }
        violationType?.let { urlBuilder.append("&violationType=$it") }
        status?.let { urlBuilder.append("&status=$it") }
        updateTimeFrom?.let { urlBuilder.append("&updateTimeFrom=$it") }

        val request = Request.Builder()
            .url(urlBuilder.toString())
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseProcessingBasisListResponse(response.body?.string() ?: "")
    }

    /**
     * 获取处理依据详情
     */
    suspend fun getProcessingBasisDetail(basisId: Long): ProcessingBasisDetailResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/system/processingBasis/$basisId")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseProcessingBasisDetailResponse(response.body?.string() ?: "")
    }

    /**
     * 获取某法规关联的处理依据
     */
    suspend fun getProcessingBasisByRegulation(regulationId: Long): ProcessingBasisListResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/system/processingBasis/regulation/$regulationId")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseProcessingBasisListResponse(response.body?.string() ?: "")
    }

    // ==================== 依据关联API ====================

    /**
     * 获取依据关联列表
     */
    suspend fun getBasisChapterLinkList(
        pageNum: Int = 1,
        pageSize: Int = 1000
    ): BasisChapterLinkListResponse = withContext(Dispatchers.IO) {
        val urlBuilder = StringBuilder("${ConfigApi.baseUrl}/system/basisLink/list?")
            .append("pageNum=$pageNum")
            .append("&pageSize=$pageSize")

        val request = Request.Builder()
            .url(urlBuilder.toString())
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseBasisChapterLinkListResponse(response.body?.string() ?: "")
    }

    /**
     * 根据章节ID获取关联列表
     */
    suspend fun getBasisChapterLinkByChapterId(chapterId: Long): BasisChapterLinkListResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/system/basisLink/chapter/$chapterId")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseBasisChapterLinkListResponse(response.body?.string() ?: "")
    }

    /**
     * 根据章节ID和依据类型获取关联列表
     */
    suspend fun getBasisChapterLinkByChapterIdAndType(chapterId: Long, basisType: String): BasisChapterLinkListResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/system/basisLink/chapter/$chapterId/$basisType")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseBasisChapterLinkListResponse(response.body?.string() ?: "")
    }

    // ==================== 字典API ====================

    /**
     * 获取法律类型列表
     */
    suspend fun getLegalTypeList(): List<LegalTypeResponse> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/system/legal/type/list")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseLegalTypeListResponse(response.body?.string() ?: "")
    }

    /**
     * 获取监管类型列表
     */
    suspend fun getSupervisionTypeList(): List<SupervisionTypeResponse> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/system/supervision/type/list")
            .get()
            .build()

        val response = client.newCall(request).execute()
        parseSupervisionTypeListResponse(response.body?.string() ?: "")
    }

    // ==================== 解析方法 ====================

    private fun parseRegulationListResponse(json: String): RegulationListResponse {
        return try {
            val obj = JSONObject(json)
            // 后端返回 rows 字段
            val rowsArray = obj.optJSONArray("rows") ?: JSONArray()
            val rows = mutableListOf<Regulation>()
            for (i in 0 until rowsArray.length()) {
                val item = rowsArray.getJSONObject(i)
                rows.add(parseRegulation(item))
            }
            RegulationListResponse(
                rows = rows,
                total = obj.optInt("total", rows.size),
                code = obj.optInt("code", 0),
                msg = obj.optString("msg", null)
            )
        } catch (e: Exception) {
            RegulationListResponse(rows = emptyList(), total = 0, code = 500, msg = e.message)
        }
    }

    private fun parseRegulationDetailResponse(json: String): RegulationDetailResponse {
        return try {
            val obj = JSONObject(json)
            val data = obj.optJSONObject("data")?.let { parseRegulation(it) }
            RegulationDetailResponse(
                data = data,
                code = obj.optInt("code", 0),
                msg = obj.optString("msg", null)
            )
        } catch (e: Exception) {
            RegulationDetailResponse(data = null, code = 500, msg = e.message)
        }
    }

    private fun parseRegulation(obj: JSONObject): Regulation {
        val supervisionTypesStr = obj.optString("supervisionTypes", "[]")
        val supervisionTypes = try {
            val array = JSONArray(supervisionTypesStr)
            (0 until array.length()).map { array.getString(it) }
        } catch (e: Exception) {
            emptyList()
        }
        return Regulation(
            regulationId = obj.optLong("regulationId", 0),
            title = obj.optString("title", ""),
            legalType = obj.optString("legalType", ""),
            supervisionTypes = supervisionTypes,
            publishDate = obj.optString("publishDate", null),
            effectiveDate = obj.optString("effectiveDate", null),
            issuingAuthority = obj.optString("issuingAuthority", null),
            content = obj.optString("content", null),
            version = obj.optString("version", "1.0"),
            status = obj.optString("status", "0"),
            delFlag = obj.optString("delFlag", "0"),
            createBy = obj.optString("createBy", null),
            createTime = obj.optString("createTime", null),
            updateBy = obj.optString("updateBy", null),
            updateTime = obj.optString("updateTime", null),
            remark = obj.optString("remark", null)
        )
    }

    private fun parseChapterListResponse(json: String): ChapterListResponse {
        return try {
            val obj = JSONObject(json)
            // 后端返回 rows 字段
            val rowsArray = obj.optJSONArray("rows") ?: JSONArray()
            val rows = mutableListOf<RegulationChapter>()
            for (i in 0 until rowsArray.length()) {
                val item = rowsArray.getJSONObject(i)
                rows.add(
                    RegulationChapter(
                        chapterId = item.optLong("chapterId", 0),
                        regulationId = item.optLong("regulationId", 0),
                        chapterNo = item.optString("chapterNo", null),
                        chapterTitle = item.optString("chapterTitle", null),
                        sortOrder = item.optInt("sortOrder", 0)
                    )
                )
            }
            ChapterListResponse(
                rows = rows,
                total = obj.optInt("total", rows.size),
                code = obj.optInt("code", 0),
                msg = obj.optString("msg", null)
            )
        } catch (e: Exception) {
            ChapterListResponse(rows = emptyList(), total = 0, code = 500, msg = e.message)
        }
    }

    private fun parseArticleListResponse(json: String): ArticleListResponse {
        return try {
            val obj = JSONObject(json)
            // 后端返回 rows 字段
            val rowsArray = obj.optJSONArray("rows") ?: JSONArray()
            val rows = mutableListOf<RegulationArticle>()
            for (i in 0 until rowsArray.length()) {
                val item = rowsArray.getJSONObject(i)
                rows.add(
                    RegulationArticle(
                        articleId = item.optLong("articleId", 0),
                        chapterId = if (item.has("chapterId") && !item.isNull("chapterId")) item.optLong("chapterId") else null,
                        regulationId = item.optLong("regulationId", 0),
                        articleNo = item.optString("articleNo", null),
                        content = item.optString("content", null),
                        sortOrder = item.optInt("sortOrder", 0)
                    )
                )
            }
            ArticleListResponse(
                rows = rows,
                total = obj.optInt("total", rows.size),
                code = obj.optInt("code", 0),
                msg = obj.optString("msg", null)
            )
        } catch (e: Exception) {
            ArticleListResponse(rows = emptyList(), total = 0, code = 500, msg = e.message)
        }
    }

    private fun parseLegalBasisListResponse(json: String): LegalBasisListResponse {
        return try {
            val obj = JSONObject(json)
            // 后端返回 rows 字段
            val rowsArray = obj.optJSONArray("rows") ?: JSONArray()
            val rows = mutableListOf<LegalBasis>()
            for (i in 0 until rowsArray.length()) {
                val item = rowsArray.getJSONObject(i)
                rows.add(parseLegalBasis(item))
            }
            LegalBasisListResponse(
                rows = rows,
                total = obj.optInt("total", rows.size),
                code = obj.optInt("code", 0),
                msg = obj.optString("msg", null)
            )
        } catch (e: Exception) {
            LegalBasisListResponse(rows = emptyList(), total = 0, code = 500, msg = e.message)
        }
    }

    private fun parseLegalBasisDetailResponse(json: String): LegalBasisDetailResponse {
        return try {
            val obj = JSONObject(json)
            val dataObj = obj.optJSONObject("data")
            val basisObj = dataObj?.optJSONObject("basis")
            val contentsArray = dataObj?.optJSONArray("contents") ?: JSONArray()

            val contents = mutableListOf<LegalBasisContent>()
            for (i in 0 until contentsArray.length()) {
                val item = contentsArray.getJSONObject(i)
                contents.add(
                    LegalBasisContent(
                        contentId = item.optLong("contentId", 0),
                        basisId = item.optLong("basisId", 0),
                        label = item.optString("label", ""),
                        content = item.optString("content", null),
                        sortOrder = item.optInt("sortOrder", 0)
                    )
                )
            }

            LegalBasisDetailResponse(
                code = obj.optInt("code", 0),
                msg = obj.optString("msg", null),
                data = if (basisObj != null) LegalBasisDetailData(
                    basis = parseLegalBasis(basisObj),
                    contents = contents
                ) else null
            )
        } catch (e: Exception) {
            LegalBasisDetailResponse(code = 500, msg = e.message, data = null)
        }
    }

    private fun parseLegalBasis(obj: JSONObject): LegalBasis {
        return LegalBasis(
            basisId = obj.optLong("basisId", 0),
            basisNo = obj.optString("basisNo", null),
            title = obj.optString("title", ""),
            violationType = obj.optString("violationType", null),
            issuingAuthority = obj.optString("issuingAuthority", null),
            effectiveDate = obj.optString("effectiveDate", null),
            legalLevel = obj.optString("legalLevel", null),
            clauses = obj.optString("clauses", null),
            legalLiability = obj.optString("legalLiability", null),
            discretionStandard = obj.optString("discretionStandard", null),
            regulationId = if (obj.has("regulationId") && !obj.isNull("regulationId")) obj.optLong("regulationId") else null,
            status = obj.optString("status", "0"),
            delFlag = obj.optString("delFlag", "0"),
            createBy = obj.optString("createBy", null),
            createTime = obj.optString("createTime", null),
            updateBy = obj.optString("updateBy", null),
            updateTime = obj.optString("updateTime", null)
        )
    }

    private fun parseLegalTypeListResponse(json: String): List<LegalTypeResponse> {
        return try {
            val obj = JSONObject(json)
            val rowsArray = obj.optJSONArray("rows") ?: JSONArray()
            val list = mutableListOf<LegalTypeResponse>()
            for (i in 0 until rowsArray.length()) {
                val item = rowsArray.getJSONObject(i)
                list.add(
                    LegalTypeResponse(
                        typeId = item.optLong("typeId", 0),
                        typeCode = item.optString("typeCode", ""),
                        typeName = item.optString("typeName", ""),
                        sortOrder = item.optInt("sortOrder", 0),
                        status = item.optString("status", "0")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseSupervisionTypeListResponse(json: String): List<SupervisionTypeResponse> {
        return try {
            val obj = JSONObject(json)
            val rowsArray = obj.optJSONArray("rows") ?: JSONArray()
            val list = mutableListOf<SupervisionTypeResponse>()
            for (i in 0 until rowsArray.length()) {
                val item = rowsArray.getJSONObject(i)
                list.add(
                    SupervisionTypeResponse(
                        typeId = item.optLong("typeId", 0),
                        typeCode = item.optString("typeCode", ""),
                        typeName = item.optString("typeName", ""),
                        sortOrder = item.optInt("sortOrder", 0),
                        status = item.optString("status", "0")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ==================== 处理依据解析方法 ====================

    private fun parseProcessingBasisListResponse(json: String): ProcessingBasisListResponse {
        return try {
            val obj = JSONObject(json)
            val rowsArray = obj.optJSONArray("rows") ?: JSONArray()
            val rows = mutableListOf<ProcessingBasis>()
            for (i in 0 until rowsArray.length()) {
                val item = rowsArray.getJSONObject(i)
                rows.add(parseProcessingBasis(item))
            }
            ProcessingBasisListResponse(
                rows = rows,
                total = obj.optInt("total", rows.size),
                code = obj.optInt("code", 0),
                msg = obj.optString("msg", null)
            )
        } catch (e: Exception) {
            ProcessingBasisListResponse(rows = emptyList(), total = 0, code = 500, msg = e.message)
        }
    }

    private fun parseProcessingBasisDetailResponse(json: String): ProcessingBasisDetailResponse {
        return try {
            val obj = JSONObject(json)
            val dataObj = obj.optJSONObject("data")
            val basisObj = dataObj?.optJSONObject("basis")
            val contentsArray = dataObj?.optJSONArray("contents") ?: JSONArray()

            val contents = mutableListOf<ProcessingBasisContent>()
            for (i in 0 until contentsArray.length()) {
                val item = contentsArray.getJSONObject(i)
                contents.add(
                    ProcessingBasisContent(
                        contentId = item.optLong("contentId", 0),
                        basisId = item.optLong("basisId", 0),
                        label = item.optString("label", ""),
                        content = item.optString("content", null),
                        sortOrder = item.optInt("sortOrder", 0)
                    )
                )
            }

            ProcessingBasisDetailResponse(
                code = obj.optInt("code", 0),
                msg = obj.optString("msg", null),
                data = if (basisObj != null) ProcessingBasisDetailData(
                    basis = parseProcessingBasis(basisObj),
                    contents = contents
                ) else null
            )
        } catch (e: Exception) {
            ProcessingBasisDetailResponse(data = null, code = 500, msg = e.message)
        }
    }

    private fun parseProcessingBasis(obj: JSONObject): ProcessingBasis {
        return ProcessingBasis(
            basisId = obj.optLong("basisId", 0),
            basisNo = obj.optString("basisNo", null),
            title = obj.optString("title", null),
            violationType = obj.optString("violationType", null),
            issuingAuthority = obj.optString("issuingAuthority", null),
            effectiveDate = obj.optString("effectiveDate", null),
            legalLevel = obj.optString("legalLevel", null),
            clauses = obj.optString("clauses", null),
            legalLiability = obj.optString("legalLiability", null),
            discretionStandard = obj.optString("discretionStandard", null),
            regulationId = if (obj.has("regulationId") && !obj.isNull("regulationId")) obj.optLong("regulationId") else null,
            status = obj.optString("status", null),
            delFlag = obj.optString("delFlag", null),
            createBy = obj.optString("createBy", null),
            createTime = obj.optString("createTime", null),
            updateBy = obj.optString("updateBy", null),
            updateTime = obj.optString("updateTime", null),
            remark = obj.optString("remark", null)
        )
    }

    // ==================== 依据关联解析方法 ====================

    private fun parseBasisChapterLinkListResponse(json: String): BasisChapterLinkListResponse {
        return try {
            val obj = JSONObject(json)
            // 后端返回 rows 字段
            val rowsArray = obj.optJSONArray("rows") ?: JSONArray()
            val rows = mutableListOf<BasisChapterLink>()
            for (i in 0 until rowsArray.length()) {
                val item = rowsArray.getJSONObject(i)
                rows.add(parseBasisChapterLink(item))
            }
            BasisChapterLinkListResponse(
                rows = rows,
                total = obj.optInt("total", rows.size),
                code = obj.optInt("code", 0),
                msg = obj.optString("msg", null)
            )
        } catch (e: Exception) {
            BasisChapterLinkListResponse(rows = emptyList(), total = 0, code = 500, msg = e.message)
        }
    }

    private fun parseBasisChapterLink(obj: JSONObject): BasisChapterLink {
        return BasisChapterLink(
            linkId = obj.optLong("linkId", 0),
            chapterId = if (obj.has("chapterId") && !obj.isNull("chapterId")) obj.optLong("chapterId") else null,
            articleId = if (obj.has("articleId") && !obj.isNull("articleId")) obj.optLong("articleId") else null,
            basisType = obj.optString("basisType", null),
            basisId = if (obj.has("basisId") && !obj.isNull("basisId")) obj.optLong("basisId") else null,
            createBy = obj.optString("createBy", null),
            createTime = obj.optString("createTime", null),
            updateBy = obj.optString("updateBy", null),
            updateTime = obj.optString("updateTime", null)
        )
    }
}
