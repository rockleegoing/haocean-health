package com.ruoyi.app.feature.lawenforcement.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.app.feature.lawenforcement.repository.LawEnforcementRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecordListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LawEnforcementRepository(application)

    // 记录列表
    val records = MutableLiveData<List<EnforcementRecordEntity>>()

    // 筛选条件
    data class FilterParams(
        val status: String? = null,
        val unitId: Long? = null,
        val industryId: Long? = null,
        val startTime: Long? = null,
        val endTime: Long? = null
    )
    val filterParams = MutableLiveData(FilterParams())

    // 加载状态
    val isLoading = MutableLiveData<Boolean>()

    // 错误信息
    val error = MutableLiveData<String>()

    // 操作结果
    val operationResult = MutableLiveData<String>()

    init {
        loadRecords()
    }

    /**
     * 加载记录列表
     */
    fun loadRecords() {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val params = filterParams.value ?: FilterParams()

                repository.getRecordsFiltered(
                    status = params.status,
                    unitId = params.unitId,
                    industryId = params.industryId,
                    startTime = params.startTime,
                    endTime = params.endTime
                ).collectLatest { recordList ->
                    records.value = recordList
                    isLoading.value = false
                }
            } catch (e: Exception) {
                error.value = "加载失败：${e.message}"
                isLoading.value = false
            }
        }
    }

    /**
     * 按状态筛选
     */
    fun filterByStatus(status: String?) {
        val current = filterParams.value ?: FilterParams()
        filterParams.value = current.copy(status = status)
        loadRecords()
    }

    /**
     * 按单位筛选
     */
    fun filterByUnit(unitId: Long?) {
        val current = filterParams.value ?: FilterParams()
        filterParams.value = current.copy(unitId = unitId)
        loadRecords()
    }

    /**
     * 按行业筛选
     */
    fun filterByIndustry(industryId: Long?) {
        val current = filterParams.value ?: FilterParams()
        filterParams.value = current.copy(industryId = industryId)
        loadRecords()
    }

    /**
     * 按日期范围筛选
     */
    fun filterByDateRange(startTime: Long?, endTime: Long?) {
        val current = filterParams.value ?: FilterParams()
        filterParams.value = current.copy(startTime = startTime, endTime = endTime)
        loadRecords()
    }

    /**
     * 清空筛选条件
     */
    fun clearFilters() {
        filterParams.value = FilterParams()
        loadRecords()
    }

    /**
     * 上报记录
     */
    fun submitRecord(recordId: Long) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.updateRecordStatus(recordId, RecordStatus.SUBMITTED)
                operationResult.value = "上报成功"
                loadRecords()
            } catch (e: Exception) {
                error.value = "上报失败：${e.message}"
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
                loadRecords()
            } catch (e: Exception) {
                error.value = "删除失败：${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}