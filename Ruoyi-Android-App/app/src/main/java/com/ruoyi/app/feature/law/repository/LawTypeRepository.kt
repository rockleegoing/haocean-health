package com.ruoyi.app.feature.law.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.LawTypeEntity
import com.ruoyi.app.feature.law.api.LawTypeApi
import com.ruoyi.app.feature.law.api.LawTypeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LawTypeRepository(private val context: Context) {

    private val lawTypeDao = AppDatabase.getInstance(context).lawTypeDao()

    /**
     * 同步法律类型
     */
    suspend fun syncLawTypes(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = LawTypeApi.getLawTypeTree()
                if (response.code == 200) {
                    val entities = response.data.flatMap { flattenTree(it) }
                    lawTypeDao.insertAll(entities)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 扁平化树形结构
     */
    private fun flattenTree(dto: LawTypeDto): List<LawTypeEntity> {
        val result = mutableListOf(toEntity(dto))
        dto.children.forEach { child ->
            result.addAll(flattenTree(child))
        }
        return result
    }

    /**
     * 转换为 Entity
     */
    private fun toEntity(dto: LawTypeDto) = LawTypeEntity(
        id = dto.id,
        parentId = dto.parentId,
        ancestors = dto.ancestors,
        name = dto.name,
        icon = dto.icon,
        sort = dto.sort,
        status = dto.status
    )

    /**
     * 获取本地所有法律类型
     */
    suspend fun getAllLawTypes(): List<LawTypeEntity> {
        return withContext(Dispatchers.IO) {
            lawTypeDao.getAll()
        }
    }

    /**
     * 获取顶级类型
     */
    suspend fun getRootTypes(): List<LawTypeEntity> {
        return withContext(Dispatchers.IO) {
            lawTypeDao.getByParentId(0)
        }
    }
}
