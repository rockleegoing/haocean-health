package com.ruoyi.app.feature.phrase.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ruoyi.app.api.repository.PhraseRepository
import com.ruoyi.app.data.database.entity.PhraseBookEntity
import com.ruoyi.app.data.database.entity.PhraseItemEntity
import com.ruoyi.app.data.database.entity.PhraseDetailEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 规范用ViewModel - 书本列表
 */
class PhraseBookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PhraseRepository(application)

    // 书本列表
    val books = MutableLiveData<List<PhraseBookEntity>>()

    // 加载状态
    val isLoading = MutableLiveData<Boolean>()

    // 错误信息
    val error = MutableLiveData<String>()

    // 同步状态
    val syncState = MutableLiveData<SyncState>(SyncState.Idle)

    // 搜索状态
    val searchState = MutableLiveData<SearchState>(SearchState.Idle)

    // 当前选中的书本
    val selectedBook = MutableLiveData<PhraseBookEntity?>()

    init {
        loadBooks()
    }

    /**
     * 加载书本列表
     */
    fun loadBooks(industryCode: String? = null) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                val booksFlow = if (industryCode != null) {
                    repository.getBooksByIndustry(industryCode)
                } else {
                    repository.getAllBooks()
                }

                booksFlow.collectLatest { bookList ->
                    books.value = bookList
                    isLoading.value = false
                }
            } catch (e: Exception) {
                error.value = "加载失败：${e.message}"
                isLoading.value = false
            }
        }
    }

    /**
     * 搜索书本
     */
    fun searchBooks(keyword: String) {
        if (keyword.isBlank()) {
            searchState.value = SearchState.Idle
            loadBooks()
            return
        }

        viewModelScope.launch {
            try {
                searchState.value = SearchState.Searching
                repository.searchBooks(keyword).collectLatest { result ->
                    searchState.value = SearchState.Result(result)
                }
            } catch (e: Exception) {
                searchState.value = SearchState.Error(e.message ?: "搜索失败")
            }
        }
    }

    /**
     * 执行全量同步
     */
    fun syncFull() {
        viewModelScope.launch {
            try {
                syncState.value = SyncState.Syncing
                val result = repository.syncFullFromServer()
                syncState.value = result.fold(
                    onSuccess = { SyncState.Success },
                    onFailure = { SyncState.Error(it.message ?: "同步失败") }
                )
                loadBooks()
            } catch (e: Exception) {
                syncState.value = SyncState.Error(e.message ?: "同步失败")
            }
        }
    }

    /**
     * 执行增量同步
     */
    fun syncIncremental() {
        viewModelScope.launch {
            try {
                syncState.value = SyncState.Syncing
                val result = repository.syncIncrementalFromServer()
                syncState.value = result.fold(
                    onSuccess = { SyncState.Success },
                    onFailure = { SyncState.Error(it.message ?: "同步失败") }
                )
                loadBooks()
            } catch (e: Exception) {
                syncState.value = SyncState.Error(e.message ?: "同步失败")
            }
        }
    }

    /**
     * 选择书本
     */
    fun selectBook(book: PhraseBookEntity) {
        selectedBook.value = book
    }

    // 同步状态
    sealed class SyncState {
        object Idle : SyncState()
        object Syncing : SyncState()
        object Success : SyncState()
        data class Error(val message: String) : SyncState()
    }

    // 搜索状态
    sealed class SearchState {
        object Idle : SearchState()
        object Searching : SearchState()
        data class Result(val books: List<PhraseBookEntity>) : SearchState()
        data class Error(val message: String) : SearchState()
    }
}
