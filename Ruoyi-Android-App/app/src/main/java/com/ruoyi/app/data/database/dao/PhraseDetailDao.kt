package com.ruoyi.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ruoyi.app.data.database.entity.PhraseDetailEntity
import kotlinx.coroutines.flow.Flow

/**
 * 规范用语项明细 DAO
 */
@Dao
interface PhraseDetailDao {

    /**
     * 获取所有明细
     */
    @Query("SELECT * FROM t_phrase_detail WHERE delFlag = '0' ORDER BY sortOrder ASC, detailId ASC")
    fun getAllDetails(): Flow<List<PhraseDetailEntity>>

    /**
     * 按项获取明细
     */
    @Query("SELECT * FROM t_phrase_detail WHERE itemId = :itemId AND delFlag = '0' ORDER BY sortOrder ASC, detailId ASC")
    fun getDetailsByItem(itemId: Long): Flow<List<PhraseDetailEntity>>

    /**
     * 按ID获取明细
     */
    @Query("SELECT * FROM t_phrase_detail WHERE detailId = :detailId")
    suspend fun getDetailById(detailId: Long): PhraseDetailEntity?

    /**
     * FTS搜索明细
     */
    @Query("""
        SELECT t_phrase_detail.* FROM t_phrase_detail
        JOIN t_phrase_detail_fts ON t_phrase_detail.rowid = t_phrase_detail_fts.rowid
        WHERE t_phrase_detail_fts MATCH :keyword AND t_phrase_detail.delFlag = '0'
    """)
    fun searchDetails(keyword: String): Flow<List<PhraseDetailEntity>>

    /**
     * 批量插入明细
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(details: List<PhraseDetailEntity>)

    /**
     * 插入单个明细
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetail(detail: PhraseDetailEntity)

    /**
     * 按项删除明细
     */
    @Query("DELETE FROM t_phrase_detail WHERE itemId = :itemId")
    suspend fun deleteDetailsByItem(itemId: Long)

    /**
     * 删除所有明细
     */
    @Query("DELETE FROM t_phrase_detail")
    suspend fun deleteAllDetails()

    /**
     * 获取最大版本号
     */
    @Query("SELECT MAX(version) FROM t_phrase_detail")
    suspend fun getMaxVersion(): Int?

    /**
     * 获取明细数量
     */
    @Query("SELECT COUNT(*) FROM t_phrase_detail WHERE delFlag = '0'")
    suspend fun getDetailCount(): Int
}
