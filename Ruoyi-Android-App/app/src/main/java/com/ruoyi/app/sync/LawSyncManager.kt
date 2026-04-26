package com.ruoyi.app.sync

import android.content.Context
import android.content.SharedPreferences
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.feature.law.api.LawApi
import com.ruoyi.app.feature.law.repository.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LawSyncManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val regulationDao = AppDatabase.getInstance(context).regulationDao()
    private val chapterDao = AppDatabase.getInstance(context).chapterDao()
    private val articleDao = AppDatabase.getInstance(context).articleDao()
    private val legalBasisDao = AppDatabase.getInstance(context).legalBasisDao()

    companion object {
        private const val PREFS_NAME = "law_sync_prefs"
        private const val KEY_LAST_SYNC_TIME = "last_sync_time"
        private const val KEY_HAS_FULL_SYNC = "has_full_sync"
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    }

    /**
     * 执行混合模式同步
     */
    suspend fun sync(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val hasFullSync = prefs.getBoolean(KEY_HAS_FULL_SYNC, false)

            if (!hasFullSync) {
                // 首次全量同步
                syncFull()
                prefs.edit().putBoolean(KEY_HAS_FULL_SYNC, true).apply()
            } else {
                // 增量同步
                syncIncremental()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 全量同步
     */
    private suspend fun syncFull() {
        // 同步法规
        val regulationResponse = LawApi.getRegulationList(pageNum = 1, pageSize = 1000)
        if (regulationResponse.code == 200) {
            val entities = regulationResponse.rows.map { it.toEntity() }
            regulationDao.insertRegulations(entities)
        }

        // 同步章节和条款（每个法规）
        for (regulation in regulationResponse.rows) {
            syncChaptersAndArticles(regulation.regulationId)
        }

        // 同步定性依据
        val basisResponse = LawApi.getLegalBasisList(pageNum = 1, pageSize = 1000)
        if (basisResponse.code == 200) {
            val entities = basisResponse.rows.map { it.toEntity() }
            legalBasisDao.insertLegalBasises(entities)
        }

        // 更新同步时间
        prefs.edit().putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis()).apply()
    }

    /**
     * 增量同步
     */
    private suspend fun syncIncremental() {
        val lastSyncTime = prefs.getLong(KEY_LAST_SYNC_TIME, 0)
        val lastSyncTimeStr = if (lastSyncTime > 0) {
            dateFormat.format(Date(lastSyncTime))
        } else null

        // 增量同步法规
        val regulationResponse = LawApi.getRegulationList(
            pageNum = 1,
            pageSize = 1000,
            updateTimeFrom = lastSyncTimeStr
        )
        if (regulationResponse.code == 200) {
            for (regulation in regulationResponse.rows) {
                val entity = regulation.toEntity()
                regulationDao.insertRegulations(listOf(entity))
                // 同步该法规的章节和条款
                syncChaptersAndArticles(regulation.regulationId)
            }
        }

        // 增量同步定性依据
        val basisResponse = LawApi.getLegalBasisList(
            pageNum = 1,
            pageSize = 1000,
            updateTimeFrom = lastSyncTimeStr
        )
        if (basisResponse.code == 200) {
            val entities = basisResponse.rows.map { it.toEntity() }
            legalBasisDao.insertLegalBasises(entities)
        }

        // 更新同步时间
        prefs.edit().putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis()).apply()
    }

    /**
     * 同步章节和条款
     */
    private suspend fun syncChaptersAndArticles(regulationId: Long) {
        val chapterResponse = LawApi.getChapterList(regulationId, pageSize = 10000)
        if (chapterResponse.code == 200) {
            val chapterEntities = chapterResponse.rows.map { it.toEntity() }
            chapterDao.insertChapters(chapterEntities)
        }

        val articleResponse = LawApi.getArticleList(regulationId, pageSize = 10000)
        if (articleResponse.code == 200) {
            val articleEntities = articleResponse.rows.map { it.toEntity() }
            articleDao.insertArticles(articleEntities)
        }
    }

    /**
     * 获取上次同步时间
     */
    fun getLastSyncTime(): Long = prefs.getLong(KEY_LAST_SYNC_TIME, 0)

    /**
     * 强制全量重同步
     */
    suspend fun forceFullSync() {
        prefs.edit().putBoolean(KEY_HAS_FULL_SYNC, false).apply()
        sync()
    }
}
