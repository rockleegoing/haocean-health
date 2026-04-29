package com.ruoyi.app.feature.regulatory.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.RegulatoryMatterEntity
import com.ruoyi.app.data.database.entity.RegulatoryMatterItemEntity
import com.ruoyi.app.data.database.entity.RegulatoryCategoryBindEntity
import com.ruoyi.app.feature.regulatory.api.RegulatoryApi
import com.ruoyi.app.feature.regulatory.api.CategoryBindDto
import com.ruoyi.app.feature.regulatory.api.ItemDto
import com.ruoyi.app.feature.regulatory.api.MatterDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegulatoryRepository(private val context: Context) {

    private val matterDao = AppDatabase.getInstance(context).regulatoryMatterDao()
    private val itemDao = AppDatabase.getInstance(context).regulatoryMatterItemDao()
    private val categoryBindDao = AppDatabase.getInstance(context).regulatoryCategoryBindDao()

    /**
     * 同步监管事项列表
     */
    suspend fun syncMatters(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RegulatoryApi.getMatterList()
                if (response.code == 200) {
                    val entities = response.rows.map { it.toEntity() }
                    matterDao.insertAll(entities)
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
     * 同步监管事项明细
     */
    suspend fun syncItems(matterId: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RegulatoryApi.getItemListByMatter(matterId)
                if (response.code == 200) {
                    val entities = response.rows.map { it.toEntity() }
                    itemDao.insertAll(entities)
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
     * 同步行业分类监管事项关联
     */
    suspend fun syncCategoryBinds(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RegulatoryApi.getCategoryBindList()
                if (response.code == 200) {
                    val entities = response.rows.map { it.toEntity() }
                    categoryBindDao.insertAll(entities)
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
     * 获取本地所有监管事项
     */
    suspend fun getAllMatters(): List<RegulatoryMatterEntity> {
        return withContext(Dispatchers.IO) {
            matterDao.getAll()
        }
    }

    /**
     * 获取本地监管事项明细
     */
    suspend fun getItemsByMatterId(matterId: Long): List<RegulatoryMatterItemEntity> {
        return withContext(Dispatchers.IO) {
            itemDao.getByMatterId(matterId)
        }
    }

    /**
     * 获取本地行业分类关联
     */
    suspend fun getCategoryBinds(): List<RegulatoryCategoryBindEntity> {
        return withContext(Dispatchers.IO) {
            categoryBindDao.getAll()
        }
    }

    /**
     * 获取按行业分类的监管事项
     */
    suspend fun getMattersByCategoryId(categoryId: Long): List<RegulatoryMatterEntity> {
        return withContext(Dispatchers.IO) {
            matterDao.getByCategoryId(categoryId)
        }
    }
}

/**
 * MatterDto 转换为 Entity
 */
fun MatterDto.toEntity() = RegulatoryMatterEntity(
    matterId = matterId,
    matterName = matterName,
    categoryId = categoryId,
    description = description,
    status = status ?: "0"
)

/**
 * ItemDto 转换为 Entity
 */
fun ItemDto.toEntity() = RegulatoryMatterItemEntity(
    itemId = itemId,
    matterId = matterId,
    itemNo = itemNo,
    name = name,
    description = description,
    legalBasis = legalBasis
)

/**
 * CategoryBindDto 转换为 Entity
 */
fun CategoryBindDto.toEntity() = RegulatoryCategoryBindEntity(
    id = id,
    industryCategoryId = industryCategoryId,
    matterId = matterId
)
