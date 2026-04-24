package com.ruoyi.app.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ruoyi.app.data.database.entity.PhraseBookEntity
import kotlinx.coroutines.flow.Flow

/**
 * 规范用语书本 DAO
 */
@Dao
interface PhraseBookDao {

    /**
     * 获取所有书本
     */
    @Query("SELECT * FROM t_phrase_book WHERE delFlag = '0' ORDER BY sortOrder ASC, bookId ASC")
    fun getAllBooks(): Flow<List<PhraseBookEntity>>

    /**
     * 按行业获取书本
     */
    @Query("SELECT * FROM t_phrase_book WHERE delFlag = '0' AND industryCode = :industryCode ORDER BY sortOrder ASC, bookId ASC")
    fun getBooksByIndustry(industryCode: String): Flow<List<PhraseBookEntity>>

    /**
     * 按ID获取书本
     */
    @Query("SELECT * FROM t_phrase_book WHERE bookId = :bookId")
    suspend fun getBookById(bookId: Long): PhraseBookEntity?

    /**
     * FTS搜索书本
     */
    @Query("""
        SELECT t_phrase_book.* FROM t_phrase_book
        JOIN t_phrase_book_fts ON t_phrase_book.rowid = t_phrase_book_fts.rowid
        WHERE t_phrase_book_fts MATCH :keyword AND t_phrase_book.delFlag = '0'
    """)
    fun searchBooks(keyword: String): Flow<List<PhraseBookEntity>>

    /**
     * 批量插入书本
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<PhraseBookEntity>)

    /**
     * 插入单个书本
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: PhraseBookEntity)

    /**
     * 删除所有书本
     */
    @Query("DELETE FROM t_phrase_book")
    suspend fun deleteAllBooks()

    /**
     * 删除单个书本
     */
    @Query("DELETE FROM t_phrase_book WHERE bookId = :bookId")
    suspend fun deleteBook(bookId: Long)

    /**
     * 获取最大版本号
     */
    @Query("SELECT MAX(version) FROM t_phrase_book")
    suspend fun getMaxVersion(): Int?

    /**
     * 获取书本数量
     */
    @Query("SELECT COUNT(*) FROM t_phrase_book WHERE delFlag = '0'")
    suspend fun getBookCount(): Int
}
