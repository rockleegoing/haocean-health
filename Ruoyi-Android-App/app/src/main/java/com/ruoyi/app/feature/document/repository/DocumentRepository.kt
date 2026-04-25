package com.ruoyi.app.feature.document.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.DocumentGroupEntity
import com.ruoyi.app.data.database.entity.DocumentTemplateEntity
import com.ruoyi.app.data.database.entity.DocumentVariableEntity
import com.ruoyi.app.feature.document.api.DocumentApi
import com.ruoyi.app.feature.document.model.DocumentGroup
import com.ruoyi.app.feature.document.model.DocumentTemplate
import com.ruoyi.app.feature.document.model.DocumentVariable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * 文书模板数据仓库
 */
class DocumentRepository(private val context: Context) {

    private val templateDao = AppDatabase.getInstance(context).documentTemplateDao()
    private val groupDao = AppDatabase.getInstance(context).documentGroupDao()
    private val variableDao = AppDatabase.getInstance(context).documentVariableDao()

    // ==================== 远程操作 ====================

    /**
     * 同步模板到本地
     */
    suspend fun syncTemplates() = withContext(Dispatchers.IO) {
        val templates = DocumentApi.syncTemplates()
        val entities = templates.map { it.toEntity() }
        templateDao.insertAll(entities)
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

    // ==================== 转换方法 ====================

    private fun DocumentTemplate.toEntity() = DocumentTemplateEntity(
        id = id,
        templateCode = templateCode,
        templateName = templateName,
        templateType = templateType,
        category = category,
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
}