package com.ruoyi.app.feature.phrase.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ruoyi.app.api.repository.PhraseRepository
import com.ruoyi.app.data.database.entity.PhraseItemEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 规范用语ViewModel - 项列表
 */
class PhraseItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PhraseRepository(application)

    // 项列表
    val items = MutableLiveData<List<PhraseItemEntity>>()

    // 加载状态
    val isLoading = MutableLiveData<Boolean>()

    // 错误信息
    val error = MutableLiveData<String>()

    // 当前选中的环节筛选
    val selectedPhaseType = MutableLiveData<String?>(null)

    // 书本ID
    private var currentBookId: Long = 0

    /**
     * 加载项列表
     */
    fun loadItems(bookId: Long, phaseType: String? = null) {
        currentBookId = bookId
        selectedPhaseType.value = phaseType

        viewModelScope.launch {
            try {
                isLoading.value = true
                val itemsFlow = if (phaseType != null) {
                    repository.getItemsByBookAndPhase(bookId, phaseType)
                } else {
                    repository.getItemsByBook(bookId)
                }

                itemsFlow.collectLatest { itemList ->
                    items.value = itemList
                    isLoading.value = false
                }
            } catch (e: Exception) {
                error.value = "加载失败：${e.message}"
                isLoading.value = false
            }
        }
    }

    /**
     * 按环节筛选
     */
    fun filterByPhase(phaseType: String?) {
        if (currentBookId > 0) {
            loadItems(currentBookId, phaseType)
        }
    }

    /**
     * 搜索项
     */
    fun searchItems(keyword: String) {
        if (keyword.isBlank()) {
            loadItems(currentBookId, selectedPhaseType.value)
            return
        }

        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.searchItems(keyword).collectLatest { result ->
                    // 过滤出当前书本的项
                    val filtered = result.filter { it.bookId == currentBookId }
                    items.value = filtered
                    isLoading.value = false
                }
            } catch (e: Exception) {
                error.value = "搜索失败：${e.message}"
                isLoading.value = false
            }
        }
    }
}
