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
import com.ruoyi.app.data.database.entity.RoleEntity
import com.ruoyi.app.data.database.entity.SyncQueueEntity
import com.ruoyi.app.data.database.entity.UnitEntity
import com.ruoyi.app.data.database.entity.UserEntity

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
        EvidenceMaterialEntity::class
    ],
    version = 4,
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
