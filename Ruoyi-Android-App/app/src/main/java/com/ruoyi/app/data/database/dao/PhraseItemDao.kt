package com.ruoyi.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ruoyi.app.data.database.entity.PhraseItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * 规范用语项 DAO
 */
@Dao
interface PhraseItemDao {

    /**
     * 获取所有项
     */
    @Query("SELECT * FROM t_phrase_item WHERE delFlag = '0' ORDER BY sortOrder ASC, itemId ASC")
    fun getAllItems(): Flow<List<PhraseItemEntity>>

    /**
     * 按书本获取项
     */
    @Query("SELECT * FROM t_phrase_item WHERE bookId = :bookId AND delFlag = '0' ORDER BY sortOrder ASC, itemId ASC")
    fun getItemsByBook(bookId: Long): Flow<List<PhraseItemEntity>>

    /**
     * 按书本和环节获取项
     */
    @Query("SELECT * FROM t_phrase_item WHERE bookId = :bookId AND phaseType = :phaseType AND delFlag = '0' ORDER BY sortOrder ASC, itemId ASC")
    fun getItemsByBookAndPhase(bookId: Long, phaseType: String): Flow<List<PhraseItemEntity>>

    /**
     * 按ID获取项
     */
    @Query("SELECT * FROM t_phrase_item WHERE itemId = :itemId")
    suspend fun getItemById(itemId: Long): PhraseItemEntity?

    /**
     * FTS搜索项
     */
    @Query("""
        SELECT t_phrase_item.* FROM t_phrase_item
        JOIN t_phrase_item_fts ON t_phrase_item.rowid = t_phrase_item_fts.rowid
        WHERE t_phrase_item_fts MATCH :keyword AND t_phrase_item.delFlag = '0'
    """)
    fun searchItems(keyword: String): Flow<List<PhraseItemEntity>>

    /**
     * 批量插入项
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<PhraseItemEntity>)

    /**
     * 插入单个项
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PhraseItemEntity)

    /**
     * 按书本删除项
     */
    @Query("DELETE FROM t_phrase_item WHERE bookId = :bookId")
    suspend fun deleteItemsByBook(bookId: Long)

    /**
     * 删除所有项
     */
    @Query("DELETE FROM t_phrase_item")
    suspend fun deleteAllItems()

    /**
     * 获取最大版本号
     */
    @Query("SELECT MAX(version) FROM t_phrase_item")
    suspend fun getMaxVersion(): Int?

    /**
     * 获取项数量
     */
    @Query("SELECT COUNT(*) FROM t_phrase_item WHERE delFlag = '0'")
    suspend fun getItemCount(): Int
}
