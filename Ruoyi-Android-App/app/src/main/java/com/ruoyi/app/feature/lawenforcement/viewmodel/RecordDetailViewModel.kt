package com.ruoyi.app.feature.lawenforcement.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import com.ruoyi.app.data.database.entity.EvidenceMaterialEntity
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.app.feature.lawenforcement.model.SyncStatus
import com.ruoyi.app.feature.lawenforcement.repository.LawEnforcementRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecordDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LawEnforcementRepository(application)

    // 记录详情
    val record = MutableLiveData<EnforcementRecordEntity?>()

    // 证据材料列表
    val evidenceMaterials = MutableLiveData<List<EvidenceMaterialEntity>>()

    // 加载状态
    val isLoading = MutableLiveData<Boolean>()

    // 错误信息
    val error = MutableLiveData<String>()

    // 操作结果
    val operationResult = MutableLiveData<String>()

    /**
     * 加载记录详情
     */
    fun loadRecord(recordId: Long) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val recordEntity = repository.getRecordById(recordId)
                record.value = recordEntity

                if (recordEntity != null) {
                    repository.getEvidenceMaterials(recordId).collectLatest { materials ->
                        evidenceMaterials.value = materials
                        isLoading.value = false
                    }
                } else {
                    error.value = "记录不存在"
                    isLoading.value = false
                }
            } catch (e: Exception) {
                error.value = "加载失败：${e.message}"
                isLoading.value = false
            }
        }
    }

    /**
     * 上报记录
     */
    fun submitRecord() {
        val currentRecord = record.value ?: return

        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.updateRecordStatus(currentRecord.id, RecordStatus.SUBMITTED)
                record.value = currentRecord.copy(recordStatus = RecordStatus.SUBMITTED)
                operationResult.value = "上报成功"
            } catch (e: Exception) {
                error.value = "上报失败：${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 删除证据材料
     */
    fun deleteEvidence(material: EvidenceMaterialEntity) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.deleteEvidenceMaterial(material)
                operationResult.value = "删除成功"
            } catch (e: Exception) {
                error.value = "删除失败：${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 删除记录
     */
    fun deleteRecord(recordId: Long) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.deleteRecord(recordId)
                operationResult.value = "删除成功"
            } catch (e: Exception) {
                error.value = "删除失败：${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    /**
     * 获取证据统计
     */
    fun getEvidenceStats(): EvidenceStats {
        val materials = evidenceMaterials.value ?: emptyList()
        return EvidenceStats(
            photoCount = materials.count { it.evidenceType == "photo" },
            audioCount = materials.count { it.evidenceType == "audio" },
            videoCount = materials.count { it.evidenceType == "video" }
        )
    }

    data class EvidenceStats(
        val photoCount: Int,
        val audioCount: Int,
        val videoCount: Int
    )
}