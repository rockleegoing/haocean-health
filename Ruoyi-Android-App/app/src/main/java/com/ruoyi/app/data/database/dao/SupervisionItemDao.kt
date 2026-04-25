package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.SupervisionItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * 监管事项 DAO
 */
@Dao
interface SupervisionItemDao {

    @Query("SELECT * FROM supervision_item ORDER BY sortOrder ASC")
    fun getAllItems(): Flow<List<SupervisionItemEntity>>

    @Query("SELECT * FROM supervision_item WHERE status = '0' ORDER BY sortOrder ASC")
    fun getEnabledItems(): Flow<List<SupervisionItemEntity>>

    @Query("SELECT * FROM supervision_item WHERE parentId = :parentId AND status = '0' ORDER BY sortOrder ASC")
    fun getItemsByParentId(parentId: Long): Flow<List<SupervisionItemEntity>>

    @Query("SELECT * FROM supervision_item WHERE parentId = 0 AND status = '0' ORDER BY sortOrder ASC")
    fun getTopLevelItems(): Flow<List<SupervisionItemEntity>>

    @Query("SELECT * FROM supervision_item WHERE categoryId = :categoryId AND status = '0' ORDER BY sortOrder ASC")
    fun getItemsByCategoryId(categoryId: Long): Flow<List<SupervisionItemEntity>>

    @Query("SELECT * FROM supervision_item WHERE itemId = :itemId")
    suspend fun getItemById(itemId: Long): SupervisionItemEntity?

    @Query("SELECT * FROM supervision_item WHERE (name LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%') AND status = '0' ORDER BY sortOrder ASC")
    fun searchItems(keyword: String): Flow<List<SupervisionItemEntity>>

    @Query("SELECT * FROM supervision_item WHERE isCollected = 1 ORDER BY sortOrder ASC")
    fun getCollectedItems(): Flow<List<SupervisionItemEntity>>

    @Update
    suspend fun update(item: SupervisionItemEntity)

    @Query("UPDATE supervision_item SET isCollected = :isCollected WHERE itemId = :itemId")
    suspend fun updateCollectStatus(itemId: Long, isCollected: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SupervisionItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SupervisionItemEntity>)

    @Delete
    suspend fun delete(item: SupervisionItemEntity)

    @Query("DELETE FROM supervision_item")
    suspend fun deleteAll()
}
