package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.DocumentGroupEntity
import kotlinx.coroutines.flow.Flow

/**
 * 文书套组 DAO
 */
@Dao
interface DocumentGroupDao {

    @Query("SELECT * FROM document_group WHERE isActive = '1' ORDER BY id ASC")
    fun getActiveGroups(): Flow<List<DocumentGroupEntity>>

    @Query("SELECT * FROM document_group WHERE id = :id")
    suspend fun getGroupById(id: Long): DocumentGroupEntity?

    @Query("SELECT * FROM document_group WHERE groupCode = :code")
    suspend fun getGroupByCode(code: String): DocumentGroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: DocumentGroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groups: List<DocumentGroupEntity>)

    @Delete
    suspend fun delete(group: DocumentGroupEntity)

    @Query("DELETE FROM document_group")
    suspend fun deleteAll()
}
