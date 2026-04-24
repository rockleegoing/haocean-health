package com.ruoyi.app.feature.lawenforcement.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import com.ruoyi.app.feature.lawenforcement.model.EvidenceType
import com.ruoyi.app.feature.lawenforcement.repository.LawEnforcementRepository
import kotlinx.coroutines.launch

class LawEnforcementViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LawEnforcementRepository(application)

    // 当前选中的单位
    val selectedUnit = MutableLiveData<UnitInfo?>()
    val selectedUnitLiveData = selectedUnit

    // 当前执法记录
    val currentRecord = MutableLiveData<EnforcementRecordEntity?>()

    // 操作结果
    val operationResult = MutableLiveData<OperationResult>()

    // 加载状态
    val isLoading = MutableLiveData<Boolean>()

    // 错误信息
    val error = MutableLiveData<String>()

    data class UnitInfo(
        val unitId: Long,
        val unitName: String,
        val industryId: Long,
        val industryCode: String
    )

    sealed class OperationResult {
        data class Success(val message: String) : OperationResult()
        data class Error(val message: String) : OperationResult()
    }

    /**
     * 快速拍照后添加到记录
     */
    fun addPhotoToRecord(filePath: String, fileName: String?, fileSize: Long?) {
        val unit = selectedUnit.value
        if (unit == null) {
            error.value = "请先选择执法单位"
            return
        }

        viewModelScope.launch {
            try {
                isLoading.value = true
                val record = repository.getOrCreateTodayRecord(
                    unitId = unit.unitId,
                    unitName = unit.unitName,
                    industryId = unit.industryId,
                    industryCode = unit.industryCode,
                    userName = getCurrentUserName()
                )
                repository.addEvidenceMaterial(
                    recordId = record.id,
                    type = EvidenceType.PHOTO,
                    filePath = filePath,
                    fileName = fileName,
                    fileSize = fileSize,
                    duration = null,
                    description = null
                )
                currentRecord.value = record
                operationResult.value = OperationResult.Success("照片已保存")
            } catch (e: Exception) {
                error.value = "保存失败：${e.message}"
                operationResult.value = OperationResult.Error("保存失败：${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 快速录音后添加到记录
     */
    fun addAudioToRecord(filePath: String, fileName: String?, fileSize: Long?, duration: Int?) {
        val unit = selectedUnit.value
        if (unit == null) {
            error.value = "请先选择执法单位"
            return
        }

        viewModelScope.launch {
            try {
                isLoading.value = true
                val record = repository.getOrCreateTodayRecord(
                    unitId = unit.unitId,
                    unitName = unit.unitName,
                    industryId = unit.industryId,
                    industryCode = unit.industryCode,
                    userName = getCurrentUserName()
                )
                repository.addEvidenceMaterial(
                    recordId = record.id,
                    type = EvidenceType.AUDIO,
                    filePath = filePath,
                    fileName = fileName,
                    fileSize = fileSize,
                    duration = duration,
                    description = null
                )
                currentRecord.value = record
                operationResult.value = OperationResult.Success("录音已保存")
            } catch (e: Exception) {
                error.value = "保存失败：${e.message}"
                operationResult.value = OperationResult.Error("保存失败：${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 快速录像后添加到记录
     */
    fun addVideoToRecord(filePath: String, fileName: String?, fileSize: Long?, duration: Int?) {
        val unit = selectedUnit.value
        if (unit == null) {
            error.value = "请先选择执法单位"
            return
        }

        viewModelScope.launch {
            try {
                isLoading.value = true
                val record = repository.getOrCreateTodayRecord(
                    unitId = unit.unitId,
                    unitName = unit.unitName,
                    industryId = unit.industryId,
                    industryCode = unit.industryCode,
                    userName = getCurrentUserName()
                )
                repository.addEvidenceMaterial(
                    recordId = record.id,
                    type = EvidenceType.VIDEO,
                    filePath = filePath,
                    fileName = fileName,
                    fileSize = fileSize,
                    duration = duration,
                    description = null
                )
                currentRecord.value = record
                operationResult.value = OperationResult.Success("录像已保存")
            } catch (e: Exception) {
                error.value = "保存失败：${e.message}"
                operationResult.value = OperationResult.Error("保存失败：${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 设置当前选中的单位
     */
    fun setSelectedUnit(unitId: Long, unitName: String, industryId: Long, industryCode: String) {
        selectedUnit.value = UnitInfo(unitId, unitName, industryId, industryCode)
    }

    /**
     * 获取当前用户名
     */
    private fun getCurrentUserName(): String {
        // 从本地数据库获取当前登录用户
        return try {
            val token = com.tencent.mmkv.MMKV.defaultMMKV().decodeString("token")
            val userId = token?.toLongOrNull()
            if (userId != null) {
                // 使用 runBlocking 在非协程上下文中获取用户
                val user = kotlinx.coroutines.runBlocking {
                    com.ruoyi.app.data.database.AppDatabase.getInstance(getApplication())
                        .userDao().getUserById(userId)
                }
                user?.userName ?: "unknown"
            } else {
                "unknown"
            }
        } catch (e: Exception) {
            "unknown"
        }
    }
}