package com.ruoyi.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ruoyi.app.data.database.dao.ActivationCodeDao
import com.ruoyi.app.data.database.dao.DataVersionDao
import com.ruoyi.app.data.database.dao.DeviceDao
import com.ruoyi.app.data.database.dao.DeptDao
import com.ruoyi.app.data.database.dao.EnforcementRecordDao
import com.ruoyi.app.data.database.dao.EvidenceMaterialDao
import com.ruoyi.app.data.database.dao.IndustryCategoryDao
import com.ruoyi.app.data.database.dao.LegalTermDao
import com.ruoyi.app.data.database.dao.NormativeCategoryDao
import com.ruoyi.app.data.database.dao.NormativeLanguageDao
import com.ruoyi.app.data.database.dao.NormativeMatterBindDao
import com.ruoyi.app.data.database.dao.NormativeTermBindDao
import com.ruoyi.app.data.database.dao.RegulatoryCategoryBindDao
import com.ruoyi.app.data.database.dao.RegulatoryMatterDao
import com.ruoyi.app.data.database.dao.RegulatoryMatterItemDao
import com.ruoyi.app.data.database.dao.RoleDao
import com.ruoyi.app.data.database.dao.SyncQueueDao
import com.ruoyi.app.data.database.dao.UnitDao
import com.ruoyi.app.data.database.dao.UserDao
import com.ruoyi.app.data.database.dao.DocumentTemplateDao
import com.ruoyi.app.data.database.dao.DocumentVariableDao
import com.ruoyi.app.data.database.dao.DocumentCategoryDao
import com.ruoyi.app.data.database.dao.DocumentGroupDao
import com.ruoyi.app.data.database.dao.DocumentTemplateIndustryDao
import com.ruoyi.app.data.database.dao.LawDao
import com.ruoyi.app.data.database.dao.LawTypeDao
import com.ruoyi.app.data.database.entity.ActivationCodeEntity
import com.ruoyi.app.data.database.entity.DataVersionEntity
import com.ruoyi.app.data.database.entity.DeviceEntity
import com.ruoyi.app.data.database.entity.DeptEntity
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import com.ruoyi.app.data.database.entity.EvidenceMaterialEntity
import com.ruoyi.app.data.database.entity.IndustryCategoryEntity
import com.ruoyi.app.data.database.entity.LegalTermEntity
import com.ruoyi.app.data.database.entity.NormativeCategoryEntity
import com.ruoyi.app.data.database.entity.NormativeLanguageEntity
import com.ruoyi.app.data.database.entity.NormativeMatterBindEntity
import com.ruoyi.app.data.database.entity.NormativeTermBindEntity
import com.ruoyi.app.data.database.entity.RegulatoryCategoryBindEntity
import com.ruoyi.app.data.database.entity.RegulatoryMatterEntity
import com.ruoyi.app.data.database.entity.RegulatoryMatterItemEntity
import com.ruoyi.app.data.database.entity.RoleEntity
import com.ruoyi.app.data.database.entity.SyncQueueEntity
import com.ruoyi.app.data.database.entity.UnitEntity
import com.ruoyi.app.data.database.entity.UserEntity
import com.ruoyi.app.data.database.entity.DocumentTemplateEntity
import com.ruoyi.app.data.database.entity.DocumentVariableEntity
import com.ruoyi.app.data.database.entity.DocumentCategoryEntity
import com.ruoyi.app.data.database.entity.DocumentGroupEntity
import com.ruoyi.app.data.database.entity.DocumentTemplateIndustryEntity
import com.ruoyi.app.data.database.entity.LawEntity
import com.ruoyi.app.data.database.entity.LawTypeEntity

/**
 * App 数据库
 * 使用 Room 持久化库
 */
@Database(
    entities = [
        UserEntity::class,
        RoleEntity::class,
        DeptEntity::class,
        IndustryCategoryEntity::class,
        ActivationCodeEntity::class,
        DeviceEntity::class,
        SyncQueueEntity::class,
        DataVersionEntity::class,
        UnitEntity::class,
        EnforcementRecordEntity::class,
        EvidenceMaterialEntity::class,
        LawEntity::class,
        LegalTermEntity::class,
        NormativeCategoryEntity::class,
        NormativeLanguageEntity::class,
        NormativeMatterBindEntity::class,
        NormativeTermBindEntity::class,
        RegulatoryMatterEntity::class,
        RegulatoryMatterItemEntity::class,
        RegulatoryCategoryBindEntity::class,
        DocumentTemplateEntity::class,
        DocumentVariableEntity::class,
        DocumentCategoryEntity::class,
        DocumentGroupEntity::class,
        DocumentTemplateIndustryEntity::class,
        LawTypeEntity::class
    ],
    version = 17,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun roleDao(): RoleDao
    abstract fun deptDao(): DeptDao
    abstract fun industryCategoryDao(): IndustryCategoryDao
    abstract fun activationCodeDao(): ActivationCodeDao
    abstract fun deviceDao(): DeviceDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun dataVersionDao(): DataVersionDao
    abstract fun unitDao(): UnitDao
    abstract fun enforcementRecordDao(): EnforcementRecordDao
    abstract fun evidenceMaterialDao(): EvidenceMaterialDao
    abstract fun lawDao(): LawDao
    abstract fun lawTypeDao(): LawTypeDao
    abstract fun legalTermDao(): LegalTermDao
    abstract fun normativeCategoryDao(): NormativeCategoryDao
    abstract fun normativeLanguageDao(): NormativeLanguageDao
    abstract fun normativeMatterBindDao(): NormativeMatterBindDao
    abstract fun normativeTermBindDao(): NormativeTermBindDao
    abstract fun regulatoryMatterDao(): RegulatoryMatterDao
    abstract fun regulatoryMatterItemDao(): RegulatoryMatterItemDao
    abstract fun regulatoryCategoryBindDao(): RegulatoryCategoryBindDao
    abstract fun documentTemplateDao(): DocumentTemplateDao
    abstract fun documentVariableDao(): DocumentVariableDao
    abstract fun documentCategoryDao(): DocumentCategoryDao
    abstract fun documentGroupDao(): DocumentGroupDao
    abstract fun documentTemplateIndustryDao(): DocumentTemplateIndustryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // 支持破坏性迁移（清空数据重建表）
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // 兼容旧方法名
        fun getDatabase(context: Context): AppDatabase = getInstance(context)
    }
}
