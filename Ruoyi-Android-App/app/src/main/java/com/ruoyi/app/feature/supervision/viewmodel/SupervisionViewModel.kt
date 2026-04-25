package com.ruoyi.app.feature.supervision.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ruoyi.app.data.database.entity.SupervisionCategoryEntity
import com.ruoyi.app.data.database.entity.SupervisionItemEntity
import com.ruoyi.app.feature.supervision.model.SupervisionDetailResponse
import com.ruoyi.app.feature.supervision.repository.SupervisionRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 监管事项 ViewModel
 */
class SupervisionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SupervisionRepository(application)

    // 分类列表
    private val _categories = MutableLiveData<List<SupervisionCategoryEntity>>()
    val categories: LiveData<List<SupervisionCategoryEntity>> = _categories

    // 当前选中的分类ID
    private val _selectedCategoryId = MutableLiveData<Long?>()
    val selectedCategoryId: LiveData<Long?> = _selectedCategoryId

    // 事项列表
    private val _items = MutableLiveData<List<SupervisionItemEntity>>()
    val items: LiveData<List<SupervisionItemEntity>> = _items

    // 搜索结果
    private val _searchResults = MutableLiveData<List<SupervisionItemEntity>>()
    val searchResults: LiveData<List<SupervisionItemEntity>> = _searchResults

    // 事项详情
    private val _detail = MutableLiveData<SupervisionDetailResponse?>()
    val detail: LiveData<SupervisionDetailResponse?> = _detail

    // 加载状态
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // 错误信息
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadCategories()
    }

    /**
     * 加载分类列表
     */
    fun loadCategories() {
        viewModelScope.launch {
            try {
                repository.getCategories().collectLatest { list ->
                    _categories.value = list
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * 选择分类
     */
    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
        if (categoryId != null) {
            loadItemsByCategory(categoryId)
        } else {
            loadTopLevelItems()
        }
    }

    /**
     * 加载一级事项
     */
    fun loadTopLevelItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getTopLevelItems().collectLatest { list ->
                    _items.value = list
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * 根据分类加载事项
     */
    fun loadItemsByCategory(categoryId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getItemsByCategoryId(categoryId).collectLatest { list ->
                    _items.value = list
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * 根据父级加载子事项
     */
    fun loadItemsByParentId(parentId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getItemsByParentId(parentId).collectLatest { list ->
                    _items.value = list
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * 搜索事项
     */
    fun searchItems(keyword: String) {
        if (keyword.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.searchItemsLocal(keyword).collectLatest { list ->
                    _searchResults.value = list
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * 搜索事项（支持关键字 + 分类组合）
     */
    fun searchItemsWithCategory(keyword: String, categoryId: Long?) {
        if (keyword.isBlank() && categoryId == null) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.searchItemsLocal(keyword).collectLatest { list ->
                    // 先按关键字搜索，再按分类过滤
                    val filtered = if (categoryId != null) {
                        list.filter { it.categoryId == categoryId }
                    } else {
                        list
                    }
                    _searchResults.value = filtered
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * 获取事项详情
     */
    fun loadItemDetail(itemId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.fetchItemDetail(itemId)
                _detail.value = response
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * 切换收藏状态
     */
    fun toggleCollect(itemId: Long, isCollected: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleCollect(itemId, isCollected)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    /**
     * 从服务器刷新数据
     */
    fun refreshFromServer() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.fetchAndCacheHomeData()
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * 同步数据
     */
    fun syncData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.syncData()
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    /**
     * 清除错误
     */
    fun clearError() {
        _error.value = null
    }
}
