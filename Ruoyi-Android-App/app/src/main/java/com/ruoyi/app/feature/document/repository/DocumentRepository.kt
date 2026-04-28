package com.ruoyi.app.feature.document.repository

import android.content.Context
import android.util.Log
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.DocumentCategoryEntity
import com.ruoyi.app.data.database.entity.DocumentGroupEntity
import com.ruoyi.app.data.database.entity.DocumentTemplateEntity
import com.ruoyi.app.data.database.entity.DocumentTemplateIndustryEntity
import com.ruoyi.app.data.database.entity.DocumentVariableEntity
import com.ruoyi.app.feature.document.api.DocumentApi
import com.ruoyi.app.feature.document.model.DocumentCategory
import com.ruoyi.app.feature.document.model.DocumentGroup
import com.ruoyi.app.feature.document.model.DocumentTemplate
import com.ruoyi.app.feature.document.model.DocumentVariable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * 文书模板数据仓库
 */
class DocumentRepository(private val context: Context) {

    private val templateDao = AppDatabase.getInstance(context).documentTemplateDao()
    private val categoryDao = AppDatabase.getInstance(context).documentCategoryDao()
    private val groupDao = AppDatabase.getInstance(context).documentGroupDao()
    private val variableDao = AppDatabase.getInstance(context).documentVariableDao()
    private val industryDao = AppDatabase.getInstance(context).documentTemplateIndustryDao()

    // ==================== 远程操作 ====================

    /**
     * 同步模板到本地
     */
    suspend fun syncTemplates() = withContext(Dispatchers.IO) {
        val templates = DocumentApi.syncTemplates()
        Log.d("DocumentRepository", "=== 文书模板同步 ===")
        Log.d("DocumentRepository", "API 返回模板数量: ${templates.size}")
        templates.forEach { t ->
            Log.d("DocumentRepository", "  模板[id=${t.id}, name=${t.templateName}, categoryId=${t.categoryId}, category=${t.category}]")
        }
        val entities = templates.map { it.toEntity() }
        templateDao.insertAll(entities)
        Log.d("DocumentRepository", "模板已存入本地数据库")

        // 验证插入结果
        val count = templateDao.getActiveTemplates().first().size
        Log.d("DocumentRepository", "验证: 数据库中模板数量 = $count")
    }

    /**
     * 同步分类到本地
     */
    suspend fun syncCategories() = withContext(Dispatchers.IO) {
        val categories = DocumentApi.syncCategories()
        Log.d("DocumentRepository", "=== 文书分类同步 ===")
        Log.d("DocumentRepository", "API 返回分类数量: ${categories.size}")
        categories.forEach { c ->
            Log.d("DocumentRepository", "  分类[id=${c.categoryId}, name=${c.categoryName}, displayType=${c.displayType}]")
        }
        val entities = categories.map { it.toEntity() }
        categoryDao.insertAll(entities)
        Log.d("DocumentRepository", "分类已存入本地数据库")

        // 验证插入结果
        val count = withContext(Dispatchers.IO) { categoryDao.getCategoryById(0) }
        Log.d("DocumentRepository", "验证: categoryDao 查询结果 = $count")
    }

    /**
     * 同步套组到本地
     */
    suspend fun syncGroups() = withContext(Dispatchers.IO) {
        val groups = DocumentApi.syncGroups()
        val entities = groups.map { it.toEntity() }
        groupDao.insertAll(entities)
    }

    /**
     * 同步文书模板与行业分类关联到本地
     */
    suspend fun syncTemplateIndustry() = withContext(Dispatchers.IO) {
        val relations = DocumentApi.syncTemplateIndustry()
        Log.d("DocumentRepository", "=== 文书模板行业关联同步 ===")
        Log.d("DocumentRepository", "API 返回关联数量: ${relations.size}")
        relations.forEach { r ->
            Log.d("DocumentRepository", "  关联[templateId=${r.templateId}, industryCategoryId=${r.industryCategoryId}]")
        }
        // 转换为实体并批量插入
        val entities = relations.map {
            DocumentTemplateIndustryEntity(
                templateId = it.templateId,
                industryCategoryId = it.industryCategoryId
            )
        }
        industryDao.insertAll(entities)
        Log.d("DocumentRepository", "模板行业关联已存入本地数据库")

        // 验证插入结果
        if (entities.isNotEmpty()) {
            val firstTemplateId = entities.first().templateId
            val templateIds = industryDao.getTemplateIdsByIndustryCategory(entities.first().industryCategoryId)
            Log.d("DocumentRepository", "验证: 行业分类[${entities.first().industryCategoryId}]关联的模板数量 = ${templateIds.size}")
        }
    }

    /**
     * 获取模板详情(含变量)
     */
    suspend fun fetchTemplateDetail(id: Long): Pair<DocumentTemplate?, List<DocumentVariable>> {
        return DocumentApi.getTemplateDetail(id)
    }

    // ==================== 本地操作 ====================

    /**
     * 获取本地模板 Flow
     */
    fun getTemplates(): Flow<List<DocumentTemplateEntity>> {
        return templateDao.getActiveTemplates()
    }

    /**
     * 根据分类ID获取模板
     */
    fun getTemplatesByCategory(categoryId: Long): Flow<List<DocumentTemplateEntity>> {
        return templateDao.getTemplatesByCategory(categoryId)
    }

    /**
     * 根据分类名称获取模板
     */
    fun getTemplatesByCategoryName(categoryName: String): Flow<List<DocumentTemplateEntity>> {
        return templateDao.getTemplatesByCategoryName(categoryName)
    }

    /**
     * 获取本地分类 Flow
     */
    fun getCategories(): Flow<List<DocumentCategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    /**
     * 获取本地套组 Flow
     */
    fun getGroups(): Flow<List<DocumentGroupEntity>> {
        return groupDao.getActiveGroups()
    }

    /**
     * 获取本地变量
     */
    suspend fun getVariablesByTemplateId(templateId: Long): List<DocumentVariableEntity> = withContext(Dispatchers.IO) {
        variableDao.getVariablesByTemplateId(templateId)
    }

    /**
     * 根据行业分类ID获取模板ID列表（从中间表查询）
     */
    suspend fun getTemplateIdsByIndustryCategory(industryCategoryId: Long): List<Long> = withContext(Dispatchers.IO) {
        industryDao.getTemplateIdsByIndustryCategory(industryCategoryId)
    }

    // ==================== 转换方法 ====================

    private fun DocumentTemplate.toEntity() = DocumentTemplateEntity(
        id = id,
        templateCode = templateCode,
        templateName = templateName,
        templateType = templateType,
        category = category,
        categoryId = categoryId,
        sort = 0,
        filePath = filePath,
        fileUrl = fileUrl,
        version = version,
        isActive = isActive,
        syncTime = System.currentTimeMillis()
    )

    private fun DocumentVariable.toEntity() = DocumentVariableEntity(
        id = id,
        templateId = templateId,
        variableName = variableName,
        variableLabel = variableLabel,
        variableType = variableType,
        required = required,
        defaultValue = defaultValue,
        options = options,
        sortOrder = sortOrder,
        maxLength = maxLength
    )

    private fun DocumentGroup.toEntity() = DocumentGroupEntity(
        id = id,
        groupCode = groupCode,
        groupName = groupName,
        groupType = groupType,
        templates = templates,
        isActive = isActive,
        syncTime = System.currentTimeMillis()
    )

    private fun DocumentCategory.toEntity() = DocumentCategoryEntity(
        categoryId = categoryId,
        categoryName = categoryName,
        displayType = displayType,
        sort = sort
    )
}