package com.ruoyi.app.feature.lawenforcement.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import com.ruoyi.app.data.database.entity.EvidenceMaterialEntity
import com.ruoyi.app.feature.lawenforcement.model.EvidenceType
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.app.feature.lawenforcement.model.SyncStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LawEnforcementRepository(private val context: Context) {

    private val recordDao = AppDatabase.getInstance(context).enforcementRecordDao()
    private val evidenceDao = AppDatabase.getInstance(context).evidenceMaterialDao()

    // 获取所有记录
    fun getAllRecords(): Flow<List<EnforcementRecordEntity>> {
        return recordDao.getAllRecords()
    }

    // 按状态获取记录
    fun getRecordsByStatus(status: String): Flow<List<EnforcementRecordEntity>> {
        return recordDao.getRecordsByStatus(status)
    }

    // 多条件筛选
    fun getRecordsFiltered(
        status: String?,
        unitId: Long?,
        industryId: Long?,
        startTime: Long?,
        endTime: Long?,
        keyword: String? = null
    ): Flow<List<EnforcementRecordEntity>> {
        return recordDao.getRecordsFiltered(status, unitId, industryId, startTime, endTime, keyword)
    }

    // 获取单条记录
    suspend fun getRecordById(id: Long): EnforcementRecordEntity? {
        return withContext(Dispatchers.IO) {
            recordDao.getRecordById(id)
        }
    }

    // 创建或获取今日记录
    suspend fun getOrCreateTodayRecord(unitId: Long, unitName: String, industryId: Long, industryCode: String, userName: String): EnforcementRecordEntity {
        return withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val startOfDay = getStartOfDay(now)
            val endOfDay = startOfDay + 24 * 60 * 60 * 1000

            // 检查今日是否已有记录
            val existing = recordDao.getTodayRecordByUnit(unitId, startOfDay, endOfDay)
            if (existing != null) {
                return@withContext existing
            }

            // 创建新记录
            val recordNo = generateRecordNo()
            val newRecord = EnforcementRecordEntity(
                recordNo = recordNo,
                unitId = unitId,
                unitName = unitName,
                industryId = industryId,
                industryCode = industryCode,
                recordType = "ROUTINE",  // 例行检查
                recordStatus = RecordStatus.DRAFT,
                description = null,
                longitude = null,
                latitude = null,
                locationName = null,
                syncStatus = SyncStatus.PENDING,
                createBy = userName,
                createTime = now,
                updateBy = null,
                updateTime = null
            )
            val id = recordDao.insertRecord(newRecord)
            newRecord.copy(id = id)
        }
    }

    // 更新记录状态
    suspend fun updateRecordStatus(id: Long, status: String) {
        withContext(Dispatchers.IO) {
            recordDao.updateRecordStatus(id, status, System.currentTimeMillis())
        }
    }

    // 更新同步状态
    suspend fun updateRecordSyncStatus(id: Long, syncStatus: String) {
        withContext(Dispatchers.IO) {
            recordDao.updateSyncStatus(id, syncStatus)
        }
    }

    // 删除记录（软删除）
    suspend fun deleteRecord(id: Long) {
        withContext(Dispatchers.IO) {
            recordDao.deleteRecord(id, System.currentTimeMillis())
        }
    }

    // 获取证据材料
    fun getEvidenceMaterials(recordId: Long): Flow<List<EvidenceMaterialEntity>> {
        return evidenceDao.getMaterialsByRecordId(recordId)
    }

    // 按类型获取证据材料
    fun getEvidenceMaterialsByType(recordId: Long, type: String): Flow<List<EvidenceMaterialEntity>> {
        return evidenceDao.getMaterialsByType(recordId, type)
    }

    // 添加证据材料
    suspend fun addEvidenceMaterial(
        recordId: Long,
        type: String,
        filePath: String,
        fileName: String?,
        fileSize: Long?,
        duration: Int?,
        description: String?
    ): Long {
        return withContext(Dispatchers.IO) {
            val material = EvidenceMaterialEntity(
                recordId = recordId,
                evidenceType = type,
                filePath = filePath,
                fileName = fileName,
                fileSize = fileSize,
                duration = duration,
                description = description,
                syncStatus = SyncStatus.PENDING,
                createTime = System.currentTimeMillis()
            )
            evidenceDao.insertMaterial(material)
        }
    }

    // 删除证据材料
    suspend fun deleteEvidenceMaterial(material: EvidenceMaterialEntity) {
        withContext(Dispatchers.IO) {
            evidenceDao.deleteMaterial(material)
        }
    }

    // 获取待同步记录
    suspend fun getPendingRecords(): List<EnforcementRecordEntity> {
        return withContext(Dispatchers.IO) {
            recordDao.getRecordsBySyncStatus(SyncStatus.PENDING)
        }
    }

    // 获取待同步证据
    suspend fun getPendingEvidences(): List<EvidenceMaterialEntity> {
        return withContext(Dispatchers.IO) {
            evidenceDao.getMaterialsBySyncStatus(SyncStatus.PENDING)
        }
    }

    // 生成记录编号
    private fun generateRecordNo(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
        val dateStr = dateFormat.format(Date())
        val random = (1000..9999).random()
        return "ER$dateStr$random"
    }

    // 获取当天开始时间戳
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
