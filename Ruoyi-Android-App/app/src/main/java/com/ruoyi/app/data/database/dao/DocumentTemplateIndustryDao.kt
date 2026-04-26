package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.DocumentTemplateIndustryEntity

/**
 * 文书模板与行业分类关联 DAO
 */
@Dao
interface DocumentTemplateIndustryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<DocumentTemplateIndustryEntity>)

    @Query("DELETE FROM document_template_industry")
    suspend fun deleteAll()

    @Query("SELECT templateId FROM document_template_industry WHERE industryCategoryId = :industryCategoryId")
    suspend fun getTemplateIdsByIndustryCategory(industryCategoryId: Long): List<Long>
}
