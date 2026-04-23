package com.ruoyi.app.sync

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.DataVersionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DataVersionManager private constructor(private val context: Context) {

    private val database = AppDatabase.getInstance(context)
    private val dataVersionDao = database.dataVersionDao()

    companion object {
        @Volatile
        private var INSTANCE: DataVersionManager? = null

        fun getInstance(context: Context): DataVersionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataVersionManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        // 模块名称常量
        const val MODULE_USER_PERMISSION = "user_permission"
        const val MODULE_INDUSTRY_CATEGORY = "industry_category"
        const val MODULE_LAW = "law"
        const val MODULE_PHRASE = "phrase"
        const val MODULE_SUPERVISION = "supervision"
        const val MODULE_DOCUMENT_TEMPLATE = "document_template"
        const val MODULE_MEDIA_FILE = "media_file"
    }

    suspend fun getLastSyncTime(moduleName: String): Long = withContext(Dispatchers.IO) {
        dataVersionDao.getDataVersionByTable(moduleName)?.lastSyncTime ?: 0L
    }

    suspend fun updateSyncTime(moduleName: String, lastModified: Long) = withContext(Dispatchers.IO) {
        val existing = dataVersionDao.getDataVersionByTable(moduleName)
        if (existing != null) {
            val updated = existing.copy(lastSyncTime = lastModified, dataVersion = lastModified)
            dataVersionDao.insertDataVersion(updated)
        } else {
            val newVersion = DataVersionEntity(
                id = System.currentTimeMillis(),
                tableName = moduleName,
                dataVersion = lastModified,
                lastSyncTime = lastModified,
                syncType = "incremental",
                remark = null
            )
            dataVersionDao.insertDataVersion(newVersion)
        }
    }

    suspend fun hasUpdate(moduleName: String, serverLastModified: Long): Boolean = withContext(Dispatchers.IO) {
        val localLastSync = getLastSyncTime(moduleName)
        serverLastModified > localLastSync
    }

    suspend fun resetModuleVersion(moduleName: String) = withContext(Dispatchers.IO) {
        updateSyncTime(moduleName, 0L)
    }

    suspend fun resetAll() = withContext(Dispatchers.IO) {
        val allVersions = dataVersionDao.getAllDataVersions()
        allVersions.forEach { version ->
            val reset = version.copy(lastSyncTime = 0L, dataVersion = 0L)
            dataVersionDao.insertDataVersion(reset)
        }
    }
}
