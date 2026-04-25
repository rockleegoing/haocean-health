package com.ruoyi.app.feature.law.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ruoyi.app.feature.law.db.entity.RegulationEntity
import com.ruoyi.app.feature.law.model.LegalType
import com.ruoyi.app.feature.law.model.SupervisionType
import com.ruoyi.app.feature.law.repository.LawRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 法律模块 ViewModel
 */
class LawViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LawRepository(application)

    // 搜索关键词
    private val _searchKeyword = MutableLiveData<String>()

    // 搜索结果
    private val _searchResults = MutableLiveData<List<RegulationEntity>>()
    val searchResults: LiveData<List<RegulationEntity>> = _searchResults

    // 法律类型列表
    val legalTypes = LegalType.ALL

    // 监管类型列表
    val supervisionTypes = SupervisionType.ALL

    // 搜索法律法规
    fun searchRegulations(keyword: String) {
        if (keyword.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            repository.searchRegulations(keyword).collectLatest { results ->
                _searchResults.value = results
            }
        }
    }

    // 按法律类型获取法规
    fun getRegulationsByLegalType(legalType: String): LiveData<List<RegulationEntity>> {
        val result = MutableLiveData<List<RegulationEntity>>()
        viewModelScope.launch {
            repository.getRegulationsByLegalType(legalType).collectLatest { regulations ->
                result.value = regulations
            }
        }
        return result
    }

    // 按监管类型获取法规
    fun getRegulationsBySupervisionType(supervisionType: String): LiveData<List<RegulationEntity>> {
        val result = MutableLiveData<List<RegulationEntity>>()
        viewModelScope.launch {
            repository.getRegulationsBySupervisionType(supervisionType).collectLatest { regulations ->
                result.value = regulations
            }
        }
        return result
    }

    // 获取所有法规
    fun getAllRegulations(): LiveData<List<RegulationEntity>> {
        val result = MutableLiveData<List<RegulationEntity>>()
        viewModelScope.launch {
            repository.getAllRegulations().collectLatest { regulations ->
                result.value = regulations
            }
        }
        return result
    }
}
