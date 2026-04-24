package com.ruoyi.app.api.repository

import android.content.Context
import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.IndustryCategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 行业分类仓库
 */
class CategoryRepository(private val context: Context) {

    private val categoryDao = AppDatabase.getInstance(context).industryCategoryDao()

    /**
     * 从后端获取行业分类列表并存储到本地
     */
    suspend fun syncCategoriesFromServer(): Result<List<IndustryCategoryEntity>> = withContext(Dispatchers.IO) {
        try {
            val result = Get<CategoryResult>(ConfigApi.baseUrl + ConfigApi.categoryList).await()
            if (result.code == ConfigApi.SUCESSS) {
                val categories = result.data.map { it.toEntity() }
                categoryDao.insertCategories(categories)
                Result.success(categories)
            } else {
                Result.failure(Exception(result.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 从本地获取所有行业分类
     */
    suspend fun getAllCategoriesFromLocal(): List<IndustryCategoryEntity> = withContext(Dispatchers.IO) {
        categoryDao.getAllCategories()
    }

    /**
     * 根据ID获取行业分类
     */
    suspend fun getCategoryById(categoryId: Long): IndustryCategoryEntity? = withContext(Dispatchers.IO) {
        categoryDao.getCategoryById(categoryId)
    }
}

/**
 * API 响应实体
 */
@kotlinx.serialization.Serializable
data class CategoryResult(
    val code: Int,
    val msg: String,
    val data: List<CategoryDTO> = emptyList()
)

@kotlinx.serialization.Serializable
data class CategoryDTO(
    val categoryId: Long,
    val categoryName: String,
    val categoryCode: String?,
    val orderNum: Int?,
    val status: String?,
    val delFlag: String?,
    val createBy: String?,
    val createTime: String?,
    val updateBy: String?,
    val updateTime: String?,
    val remark: String?
) {
    fun toEntity(): IndustryCategoryEntity {
        return IndustryCategoryEntity(
            categoryId = categoryId,
            categoryName = categoryName,
            categoryCode = categoryCode ?: "",
            orderNum = orderNum ?: 0,
            status = status ?: "0",
            delFlag = delFlag ?: "0",
            createBy = createBy,
            createTime = createTime?.toLongOrNull() ?: System.currentTimeMillis(),
            updateBy = updateBy,
            updateTime = updateTime?.toLongOrNull(),
            remark = remark
        )
    }
}
