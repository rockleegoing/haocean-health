package com.ruoyi.app.fragment

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.toast.ToastUtils
import com.ruoyi.app.databinding.FragmentPhraseBinding
import com.ruoyi.app.feature.phrase.ui.adapter.PhraseBookAdapter
import com.ruoyi.app.feature.phrase.viewmodel.PhraseBookViewModel
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingFragment
import com.ruoyi.code.base.activityViewModels
import com.therouter.TheRouter
import com.therouter.router.Route

@Route(path = Constant.phraseFragmentRoute)
class PhraseFragment : BaseBindingFragment<FragmentPhraseBinding>() {

    private val viewModel: PhraseBookViewModel by activityViewModels()
    private lateinit var adapter: PhraseBookAdapter

    companion object {
        @JvmStatic
        fun newInstance() = PhraseFragment()
    }

    override fun initView() {
        setupRecyclerView()
        setupSearch()
        setupSync()
        setupEmptyState()
        observeViewModel()
    }

    override fun initData() {
        // 首次加载数据
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadBooks()
    }

    private fun setupRecyclerView() {
        adapter = PhraseBookAdapter { book ->
            // 点击书本进入项列表
            val bundle = Bundle().apply {
                putLong("bookId", book.bookId)
                putString("bookName", book.bookName)
            }
            TheRouter.build(Constant.phraseBookDetailRoute).with(bundle).navigation()
        }

        binding.rvBooks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBooks.adapter = adapter

        // 下拉刷新
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadBooks()
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            val keyword = editable?.toString() ?: ""
            if (keyword.isBlank()) {
                viewModel.loadBooks()
            } else {
                viewModel.searchBooks(keyword)
            }
        }
    }

    private fun setupSync() {
        binding.btnSync.setOnClickListener {
            viewModel.syncIncremental()
        }

        binding.btnSyncEmpty.setOnClickListener {
            viewModel.syncFull()
        }
    }

    private fun setupEmptyState() {
        // 空状态点击同步
    }

    private fun observeViewModel() {
        viewModel.books.observe(viewLifecycleOwner) { books ->
            binding.swipeRefresh.isRefreshing = false
            if (books.isEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvBooks.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.rvBooks.visibility = View.VISIBLE
                adapter.submitList(books)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading && adapter.itemCount == 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            ToastUtils.show(error)
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.syncState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PhraseBookViewModel.SyncState.Idle -> {
                    binding.layoutSyncStatus.visibility = View.GONE
                }
                is PhraseBookViewModel.SyncState.Syncing -> {
                    binding.layoutSyncStatus.visibility = View.VISIBLE
                    binding.tvSyncStatus.text = "同步中..."
                }
                is PhraseBookViewModel.SyncState.Success -> {
                    binding.layoutSyncStatus.visibility = View.VISIBLE
                    binding.tvSyncStatus.text = "同步成功"
                    binding.root.postDelayed({
                        binding.layoutSyncStatus.visibility = View.GONE
                    }, 2000)
                }
                is PhraseBookViewModel.SyncState.Error -> {
                    binding.layoutSyncStatus.visibility = View.VISIBLE
                    binding.tvSyncStatus.text = state.message
                }
            }
        }

        viewModel.searchState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PhraseBookViewModel.SearchState.Idle -> {
                    // 正常状态
                }
                is PhraseBookViewModel.SearchState.Searching -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is PhraseBookViewModel.SearchState.Result -> {
                    binding.progressBar.visibility = View.GONE
                    if (state.books.isEmpty()) {
                        binding.layoutEmpty.visibility = View.VISIBLE
                        binding.rvBooks.visibility = View.GONE
                    } else {
                        binding.layoutEmpty.visibility = View.GONE
                        binding.rvBooks.visibility = View.VISIBLE
                        adapter.submitList(state.books)
                    }
                }
                is PhraseBookViewModel.SearchState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    ToastUtils.show(state.message)
                }
            }
        }
    }
}
