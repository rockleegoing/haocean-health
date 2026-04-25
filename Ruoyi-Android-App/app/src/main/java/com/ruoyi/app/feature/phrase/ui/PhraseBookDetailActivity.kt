package com.ruoyi.app.feature.phrase.ui

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.toast.ToastUtils
import com.ruoyi.app.databinding.ActivityPhraseBookDetailBinding
import com.ruoyi.app.feature.phrase.ui.adapter.PhraseItemAdapter
import com.ruoyi.app.feature.phrase.viewmodel.PhraseItemViewModel
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.base.activityViewModels
import com.therouter.TheRouter
import com.therouter.router.Route

/**
 * 规范用语书本详情 - 项列表页
 */
@Route(path = Constant.phraseBookDetailRoute)
class PhraseBookDetailActivity : BaseBindingActivity<ActivityPhraseBookDetailBinding>() {

    private val viewModel: PhraseItemViewModel by activityViewModels()
    private lateinit var adapter: PhraseItemAdapter

    private var bookId: Long = 0
    private var bookName: String = ""

    companion object {
        const val EXTRA_BOOK_ID = "bookId"
        const val EXTRA_BOOK_NAME = "bookName"
    }

    override fun initView() {
        bookId = intent.getLongExtra(EXTRA_BOOK_ID, 0)
        bookName = intent.getStringExtra(EXTRA_BOOK_NAME) ?: "项列表"

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupPhaseFilter()
        observeViewModel()
    }

    override fun initData() {
        binding.toolbar.title = bookName
        viewModel.loadItems(bookId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = PhraseItemAdapter { item ->
            // 点击项进入明细列表
            val bundle = Bundle().apply {
                putLong("itemId", item.itemId)
                putString("itemName", item.itemName)
            }
            TheRouter.build(Constant.phraseItemDetailRoute).with(bundle).navigation()
        }

        binding.rvItems.layoutManager = LinearLayoutManager(this)
        binding.rvItems.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            val keyword = editable?.toString() ?: ""
            if (keyword.isBlank()) {
                viewModel.loadItems(bookId, viewModel.selectedPhaseType.value)
            } else {
                viewModel.searchItems(keyword)
            }
        }
    }

    private fun setupPhaseFilter() {
        binding.chipGroupPhase.setOnCheckedStateChangeListener { _, checkedIds ->
            val phaseType = when {
                checkedIds.contains(binding.chipCheckBefore.id) -> "CHECK_BEFORE"
                checkedIds.contains(binding.chipCheckIng.id) -> "CHECK_ING"
                checkedIds.contains(binding.chipCheckAfter.id) -> "CHECK_AFTER"
                else -> null
            }
            viewModel.filterByPhase(phaseType)
        }
    }

    private fun observeViewModel() {
        viewModel.items.observe(this) { items ->
            if (items.isEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvItems.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.rvItems.visibility = View.VISIBLE
                adapter.submitList(items)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading && adapter.itemCount == 0) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        viewModel.error.observe(this) { error ->
            ToastUtils.show(error)
        }
    }
}
