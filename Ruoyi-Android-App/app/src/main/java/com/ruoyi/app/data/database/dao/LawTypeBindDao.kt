package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.LawTypeBindEntity

@Dao
interface LawTypeBindDao {
    @Query("SELECT * FROM law_type_bind")
    suspend fun getAll(): List<LawTypeBindEntity>

    @Query("SELECT * FROM law_type_bind WHERE lawId = :lawId")
    suspend fun getByLawId(lawId: Long): List<LawTypeBindEntity>

    @Query("SELECT * FROM law_type_bind WHERE typeId = :typeId")
    suspend fun getByTypeId(typeId: Long): List<LawTypeBindEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(binds: List<LawTypeBindEntity>)

    @Query("DELETE FROM law_type_bind")
    suspend fun deleteAll()

    @Query("DELETE FROM law_type_bind WHERE lawId = :lawId")
    suspend fun deleteByLawId(lawId: Long)

    @Query("DELETE FROM law_type_bind WHERE lawId = :lawId AND typeId = :typeId")
    suspend fun deleteByLawIdAndTypeId(lawId: Long, typeId: Long)
}
