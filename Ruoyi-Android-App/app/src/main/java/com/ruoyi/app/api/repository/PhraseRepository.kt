package com.ruoyi.app.api.repository

import android.content.Context
import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.PhraseBookEntity
import com.ruoyi.app.data.database.entity.PhraseDetailEntity
import com.ruoyi.app.data.database.entity.PhraseItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

/**
 * 规范用语仓库
 */
class PhraseRepository(private val context: Context) {

    private val database = AppDatabase.getInstance(context)
    private val bookDao = database.phraseBookDao()
    private val itemDao = database.phraseItemDao()
    private val detailDao = database.phraseDetailDao()

    companion object {
        const val MODULE_PHRASE_BOOK = "phrase_book"
        const val MODULE_PHRASE_ITEM = "phrase_item"
        const val MODULE_PHRASE_DETAIL = "phrase_detail"
    }

    // ==================== 同步相关 ====================

    /**
     * 全量同步 - 从服务器拉取所有数据
     */
    suspend fun syncFullFromServer(): Result<PhraseSyncVO> = withContext(Dispatchers.IO) {
        try {
            val result = Get<PhraseSyncResult>(ConfigApi.baseUrl + "/app/phrase/sync/full").await()
            if (result.code == ConfigApi.SUCCESS) {
                val syncData = result.data
                // 保存到本地数据库
                syncData.books?.let { books ->
                    bookDao.insertBooks(books.map { it.toEntity() })
                }
                syncData.items?.let { items ->
                    itemDao.insertItems(items.map { it.toEntity() })
                }
                syncData.details?.let { details ->
                    detailDao.insertDetails(details.map { it.toEntity() })
                }
                Result.success(syncData)
            } else {
                Result.failure(Exception(result.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 增量同步 - 仅获取变更数据
     */
    suspend fun syncIncrementalFromServer(): Result<PhraseSyncVO> = withContext(Dispatchers.IO) {
        try {
            val bookVersion = bookDao.getMaxVersion() ?: 0
            val itemVersion = itemDao.getMaxVersion() ?: 0
            val detailVersion = detailDao.getMaxVersion() ?: 0

            val url = "${ConfigApi.baseUrl}/app/phrase/sync/incremental?bookVersion=$bookVersion&itemVersion=$itemVersion&detailVersion=$detailVersion"
            val result = Get<PhraseSyncResult>(url).await()

            if (result.code == ConfigApi.SUCCESS) {
                val syncData = result.data
                // 处理增量数据（插入或更新或删除）
                handleIncrementalData(syncData)
                Result.success(syncData)
            } else {
                Result.failure(Exception(result.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun handleIncrementalData(syncData: PhraseSyncVO) {
        // 处理新增/更新的书本
        syncData.books?.forEach { book ->
            if (book.delFlag == "1") {
                bookDao.deleteBook(book.bookId)
            } else {
                bookDao.insertBook(book.toEntity())
            }
        }

        // 处理新增/更新的项
        syncData.items?.forEach { item ->
            if (item.delFlag == "1") {
                itemDao.deleteItemsByBook(item.itemId)
            } else {
                itemDao.insertItem(item.toEntity())
            }
        }

        // 处理新增/更新的明细
        syncData.details?.forEach { detail ->
            if (detail.delFlag == "1") {
                detailDao.deleteDetailsByItem(detail.detailId)
            } else {
                detailDao.insertDetail(detail.toEntity())
            }
        }
    }

    // ==================== 本地查询 ====================

    /**
     * 获取所有书本
     */
    fun getAllBooks(): Flow<List<PhraseBookEntity>> = bookDao.getAllBooks()

    /**
     * 按行业获取书本
     */
    fun getBooksByIndustry(industryCode: String): Flow<List<PhraseBookEntity>> =
        bookDao.getBooksByIndustry(industryCode)

    /**
     * 获取项列表（按书本）
     */
    fun getItemsByBook(bookId: Long): Flow<List<PhraseItemEntity>> =
        itemDao.getItemsByBook(bookId)

    /**
     * 获取项列表（按书本和环节筛选）
     */
    fun getItemsByBookAndPhase(bookId: Long, phaseType: String): Flow<List<PhraseItemEntity>> =
        itemDao.getItemsByBookAndPhase(bookId, phaseType)

    /**
     * 获取明细列表（按项）
     */
    fun getDetailsByItem(itemId: Long): Flow<List<PhraseDetailEntity>> =
        detailDao.getDetailsByItem(itemId)

    /**
     * 获取书本详情
     */
    suspend fun getBookById(bookId: Long): PhraseBookEntity? =
        bookDao.getBookById(bookId)

    /**
     * 获取项详情
     */
    suspend fun getItemById(itemId: Long): PhraseItemEntity? =
        itemDao.getItemById(itemId)

    /**
     * 获取明细详情
     */
    suspend fun getDetailById(detailId: Long): PhraseDetailEntity? =
        detailDao.getDetailById(detailId)

    // ==================== 搜索功能 ====================

    /**
     * 搜索书本
     */
    fun searchBooks(keyword: String): Flow<List<PhraseBookEntity>> =
        bookDao.searchBooks("$keyword*")

    /**
     * 搜索项
     */
    fun searchItems(keyword: String): Flow<List<PhraseItemEntity>> =
        itemDao.searchItems("$keyword*")

    /**
     * 搜索明细
     */
    fun searchDetails(keyword: String): Flow<List<PhraseDetailEntity>> =
        detailDao.searchDetails("$keyword*")

    /**
     * 全局搜索（搜索所有层级）
     */
    fun searchAll(keyword: String): Flow<PhraseSearchResult> {
        return combine(
            searchBooks(keyword),
            searchItems(keyword),
            searchDetails(keyword)
        ) { books, items, details ->
            PhraseSearchResult(books, items, details)
        }
    }

    // ==================== 数据统计 ====================

    /**
     * 获取本地数据统计
     */
    suspend fun getLocalStats(): PhraseLocalStats = withContext(Dispatchers.IO) {
        PhraseLocalStats(
            bookCount = bookDao.getBookCount(),
            itemCount = itemDao.getItemCount(),
            detailCount = detailDao.getDetailCount(),
            bookVersion = bookDao.getMaxVersion() ?: 0,
            itemVersion = itemDao.getMaxVersion() ?: 0,
            detailVersion = detailDao.getMaxVersion() ?: 0
        )
    }
}

// ==================== 数据模型 ====================

/**
 * 搜索结果
 */
data class PhraseSearchResult(
    val books: List<PhraseBookEntity>,
    val items: List<PhraseItemEntity>,
    val details: List<PhraseDetailEntity>
) {
    val isEmpty: Boolean get() = books.isEmpty() && items.isEmpty() && details.isEmpty()
    val totalCount: Int get() = books.size + items.size + details.size
}

/**
 * 本地数据统计
 */
data class PhraseLocalStats(
    val bookCount: Int,
    val itemCount: Int,
    val detailCount: Int,
    val bookVersion: Int,
    val itemVersion: Int,
    val detailVersion: Int
)

/**
 * API 响应
 */
@kotlinx.serialization.Serializable
data class PhraseSyncResult(
    val code: Int,
    val msg: String,
    val data: PhraseSyncVO
)

/**
 * 同步VO
 */
@kotlinx.serialization.Serializable
data class PhraseSyncVO(
    val books: List<PhraseBookDTO>? = null,
    val items: List<PhraseItemDTO>? = null,
    val details: List<PhraseDetailDTO>? = null,
    val bookVersion: Int? = null,
    val itemVersion: Int? = null,
    val detailVersion: Int? = null,
    val syncTime: Long? = null
)

/**
 * 书本DTO
 */
@kotlinx.serialization.Serializable
data class PhraseBookDTO(
    val bookId: Long,
    val bookName: String,
    val bookCode: String,
    val bookDesc: String? = null,
    val industryCode: String? = null,
    val industryName: String? = null,
    val coverUrl: String? = null,
    val sortOrder: Int = 0,
    val status: String = "0",
    val version: Int = 1,
    val delFlag: String = "0",
    val createTime: String? = null,
    val updateTime: String? = null
) {
    fun toEntity(): PhraseBookEntity {
        return PhraseBookEntity(
            bookId = bookId,
            bookName = bookName,
            bookCode = bookCode,
            bookDesc = bookDesc,
            industryCode = industryCode,
            industryName = industryName,
            coverUrl = coverUrl,
            sortOrder = sortOrder,
            status = status,
            version = version,
            createTime = createTime?.toLongOrNull(),
            updateTime = updateTime?.toLongOrNull(),
            delFlag = delFlag
        )
    }
}

/**
 * 项DTO
 */
@kotlinx.serialization.Serializable
data class PhraseItemDTO(
    val itemId: Long,
    val bookId: Long,
    val itemName: String,
    val itemCode: String,
    val itemDesc: String? = null,
    val phaseType: String? = null,
    val sceneType: String? = null,
    val industryCode: String? = null,
    val sortOrder: Int = 0,
    val status: String = "0",
    val version: Int = 1,
    val delFlag: String = "0",
    val createTime: String? = null,
    val updateTime: String? = null,
    val bookName: String? = null
) {
    fun toEntity(): PhraseItemEntity {
        return PhraseItemEntity(
            itemId = itemId,
            bookId = bookId,
            itemName = itemName,
            itemCode = itemCode,
            itemDesc = itemDesc,
            phaseType = phaseType,
            sceneType = sceneType,
            industryCode = industryCode,
            sortOrder = sortOrder,
            status = status,
            version = version,
            createTime = createTime?.toLongOrNull(),
            updateTime = updateTime?.toLongOrNull(),
            delFlag = delFlag,
            bookName = bookName
        )
    }
}

/**
 * 明细DTO
 */
@kotlinx.serialization.Serializable
data class PhraseDetailDTO(
    val detailId: Long,
    val itemId: Long,
    val detailTitle: String,
    val detailContent: String,
    val detailType: String? = null,
    val sortOrder: Int = 0,
    val version: Int = 1,
    val delFlag: String = "0",
    val createTime: String? = null,
    val updateTime: String? = null,
    val itemName: String? = null
) {
    fun toEntity(): PhraseDetailEntity {
        return PhraseDetailEntity(
            detailId = detailId,
            itemId = itemId,
            detailTitle = detailTitle,
            detailContent = detailContent,
            detailType = detailType,
            sortOrder = sortOrder,
            version = version,
            createTime = createTime?.toLongOrNull(),
            updateTime = updateTime?.toLongOrNull(),
            delFlag = delFlag,
            itemName = itemName
        )
    }
}
