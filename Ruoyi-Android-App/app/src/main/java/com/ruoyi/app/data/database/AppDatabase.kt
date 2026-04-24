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
import com.ruoyi.app.data.database.dao.PhraseBookDao
import com.ruoyi.app.data.database.dao.PhraseDetailDao
import com.ruoyi.app.data.database.dao.PhraseItemDao
import com.ruoyi.app.data.database.dao.RoleDao
import com.ruoyi.app.data.database.dao.SyncQueueDao
import com.ruoyi.app.data.database.dao.UnitDao
import com.ruoyi.app.data.database.dao.UserDao
import com.ruoyi.app.data.database.entity.ActivationCodeEntity
import com.ruoyi.app.data.database.entity.DataVersionEntity
import com.ruoyi.app.data.database.entity.DeviceEntity
import com.ruoyi.app.data.database.entity.DeptEntity
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import com.ruoyi.app.data.database.entity.EvidenceMaterialEntity
import com.ruoyi.app.data.database.entity.IndustryCategoryEntity
import com.ruoyi.app.data.database.entity.PhraseBookEntity
import com.ruoyi.app.data.database.entity.PhraseBookFtsEntity
import com.ruoyi.app.data.database.entity.PhraseDetailEntity
import com.ruoyi.app.data.database.entity.PhraseDetailFtsEntity
import com.ruoyi.app.data.database.entity.PhraseItemEntity
import com.ruoyi.app.data.database.entity.PhraseItemFtsEntity
import com.ruoyi.app.data.database.entity.RoleEntity
import com.ruoyi.app.data.database.entity.SyncQueueEntity
import com.ruoyi.app.data.database.entity.UnitEntity
import com.ruoyi.app.data.database.entity.UserEntity
import com.ruoyi.app.feature.law.db.dao.ArticleDao
import com.ruoyi.app.feature.law.db.dao.ChapterDao
import com.ruoyi.app.feature.law.db.dao.LegalBasisDao
import com.ruoyi.app.feature.law.db.dao.RegulationDao
import com.ruoyi.app.feature.law.db.entity.LegalBasisEntity
import com.ruoyi.app.feature.law.db.entity.RegulationArticleEntity
import com.ruoyi.app.feature.law.db.entity.RegulationChapterEntity
import com.ruoyi.app.feature.law.db.entity.RegulationEntity

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
        RegulationEntity::class,
        RegulationChapterEntity::class,
        RegulationArticleEntity::class,
        LegalBasisEntity::class,
        PhraseBookEntity::class,
        PhraseBookFtsEntity::class,
        PhraseItemEntity::class,
        PhraseItemFtsEntity::class,
        PhraseDetailEntity::class,
        PhraseDetailFtsEntity::class
    ],
    version = 6,
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
    abstract fun regulationDao(): RegulationDao
    abstract fun chapterDao(): ChapterDao
    abstract fun articleDao(): ArticleDao
    abstract fun legalBasisDao(): LegalBasisDao
    abstract fun phraseBookDao(): PhraseBookDao
    abstract fun phraseItemDao(): PhraseItemDao
    abstract fun phraseDetailDao(): PhraseDetailDao

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
