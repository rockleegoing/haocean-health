package com.ruoyi.app.feature.supervision.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.SupervisionCategoryEntity
import com.ruoyi.app.data.database.entity.SupervisionItemEntity
import com.ruoyi.app.feature.supervision.api.SupervisionApi
import com.ruoyi.app.feature.supervision.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * 监管事项数据仓库
 */
class SupervisionRepository(private val context: Context) {

    private val categoryDao = AppDatabase.getInstance(context).supervisionCategoryDao()
    private val itemDao = AppDatabase.getInstance(context).supervisionItemDao()

    // ==================== 远程操作 ====================

    /**
     * 从服务器获取首页数据并缓存
     */
    suspend fun fetchAndCacheHomeData(): SupervisionHomeResponse = withContext(Dispatchers.IO) {
        val response = SupervisionApi.getHomeData()

        // 缓存监管类型
        val categoryEntities = response.categories.map { it.toEntity() }
        categoryDao.insertAll(categoryEntities)

        // 缓存监管事项
        val itemEntities = response.topItems.map { it.toEntity() }
        itemDao.insertAll(itemEntities)

        response
    }

    /**
     * 从服务器获取所有监管类型
     */
    suspend fun fetchCategories(): List<SupervisionCategory> = withContext(Dispatchers.IO) {
        val categories = SupervisionApi.getAllCategories()
        categoryDao.insertAll(categories.map { it.toEntity() })
        categories
    }

    /**
     * 从服务器获取监管事项列表
     */
    suspend fun fetchItems(categoryId: Long? = null): List<SupervisionItem> = withContext(Dispatchers.IO) {
        val items = SupervisionApi.getItemList(categoryId = categoryId)
        itemDao.insertAll(items.map { it.toEntity() })
        items
    }

    /**
     * 从服务器获取事项详情
     */
    suspend fun fetchItemDetail(itemId: Long): SupervisionDetailResponse = withContext(Dispatchers.IO) {
        val response = SupervisionApi.getItemDetail(itemId)

        // 缓存事项详情
        response.item?.let {
            itemDao.insert(it.toEntity())
        }

        response
    }

    /**
     * 从服务器搜索监管事项
     */
    suspend fun searchItems(keyword: String, categoryId: Long? = null): List<SupervisionItem> = withContext(Dispatchers.IO) {
        SupervisionApi.searchItems(keyword, categoryId)
    }

    /**
     * 同步监管数据到本地
     */
    suspend fun syncData() = withContext(Dispatchers.IO) {
        // 获取所有分类
        val categories = SupervisionApi.getAllCategories()
        categoryDao.insertAll(categories.map { it.toEntity() })

        // 获取所有事项（通过各分类）
        for (category in categories) {
            val items = SupervisionApi.getItemsByCategoryId(category.categoryId)
            itemDao.insertAll(items.map { it.toEntity() })
        }
    }

    // ==================== 本地操作 ====================

    /**
     * 获取所有监管类型
     */
    fun getCategories(): Flow<List<SupervisionCategoryEntity>> {
        return categoryDao.getEnabledCategories()
    }

    /**
     * 获取一级监管事项
     */
    fun getTopLevelItems(): Flow<List<SupervisionItemEntity>> {
        return itemDao.getTopLevelItems()
    }

    /**
     * 根据父级ID获取子事项
     */
    fun getItemsByParentId(parentId: Long): Flow<List<SupervisionItemEntity>> {
        return itemDao.getItemsByParentId(parentId)
    }

    /**
     * 根据监管类型获取事项
     */
    fun getItemsByCategoryId(categoryId: Long): Flow<List<SupervisionItemEntity>> {
        return itemDao.getItemsByCategoryId(categoryId)
    }

    /**
     * 根据ID获取事项
     */
    suspend fun getItemById(itemId: Long): SupervisionItemEntity? = withContext(Dispatchers.IO) {
        itemDao.getItemById(itemId)
    }

    /**
     * 搜索监管事项
     */
    fun searchItemsLocal(keyword: String): Flow<List<SupervisionItemEntity>> {
        return itemDao.searchItems(keyword)
    }

    /**
     * 获取收藏的监管事项
     */
    fun getCollectedItems(): Flow<List<SupervisionItemEntity>> {
        return itemDao.getCollectedItems()
    }

    /**
     * 切换收藏状态
     */
    suspend fun toggleCollect(itemId: Long, isCollected: Boolean) = withContext(Dispatchers.IO) {
        itemDao.updateCollectStatus(itemId, isCollected)
    }

    // ==================== 转换方法 ====================

    private fun SupervisionCategory.toEntity() = SupervisionCategoryEntity(
        categoryId = categoryId,
        categoryName = categoryName,
        categoryCode = categoryCode,
        icon = icon,
        sortOrder = sortOrder,
        status = status
    )

    private fun SupervisionItem.toEntity() = SupervisionItemEntity(
        itemId = itemId,
        itemNo = itemNo,
        name = name,
        parentId = parentId,
        categoryId = categoryId,
        categoryName = categoryName,
        description = description,
        legalBasis = legalBasis,
        sortOrder = sortOrder,
        status = status
    )
}
