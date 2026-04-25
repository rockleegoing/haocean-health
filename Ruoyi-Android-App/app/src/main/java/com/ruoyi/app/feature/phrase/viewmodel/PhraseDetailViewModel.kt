package com.ruoyi.app.feature.phrase.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ruoyi.app.api.repository.PhraseRepository
import com.ruoyi.app.data.database.entity.PhraseDetailEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 规范用语ViewModel - 明细列表
 */
class PhraseDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PhraseRepository(application)

    // 明细列表
    val details = MutableLiveData<List<PhraseDetailEntity>>()

    // 加载状态
    val isLoading = MutableLiveData<Boolean>()

    // 错误信息
    val error = MutableLiveData<String>()

    // 项ID
    private var currentItemId: Long = 0

    /**
     * 加载明细列表
     */
    fun loadDetails(itemId: Long) {
        currentItemId = itemId

        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.getDetailsByItem(itemId).collectLatest { detailList ->
                    details.value = detailList
                    isLoading.value = false
                }
            } catch (e: Exception) {
                error.value = "加载失败：${e.message}"
                isLoading.value = false
            }
        }
    }

    /**
     * 搜索明细
     */
    fun searchDetails(keyword: String) {
        if (keyword.isBlank()) {
            loadDetails(currentItemId)
            return
        }

        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.searchDetails(keyword).collectLatest { result ->
                    // 过滤出当前项的明细
                    val filtered = result.filter { it.itemId == currentItemId }
                    details.value = filtered
                    isLoading.value = false
                }
            } catch (e: Exception) {
                error.value = "搜索失败：${e.message}"
                isLoading.value = false
            }
        }
    }
}
