package com.ruoyi.app.feature.phrase.ui

import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.toast.ToastUtils
import com.ruoyi.app.databinding.ActivityPhraseItemDetailBinding
import com.ruoyi.app.feature.phrase.ui.adapter.PhraseDetailAdapter
import com.ruoyi.app.feature.phrase.viewmodel.PhraseDetailViewModel
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.base.activityViewModels
import com.therouter.router.Route

/**
 * 规范用语项详情 - 明细列表页
 */
@Route(path = Constant.phraseItemDetailRoute)
class PhraseItemDetailActivity : BaseBindingActivity<ActivityPhraseItemDetailBinding>() {

    private val viewModel: PhraseDetailViewModel by activityViewModels()
    private lateinit var adapter: PhraseDetailAdapter

    private var itemId: Long = 0
    private var itemName: String = ""

    companion object {
        const val EXTRA_ITEM_ID = "itemId"
        const val EXTRA_ITEM_NAME = "itemName"
    }

    override fun initView() {
        itemId = intent.getLongExtra(EXTRA_ITEM_ID, 0)
        itemName = intent.getStringExtra(EXTRA_ITEM_NAME) ?: "明细列表"

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    override fun initData() {
        binding.toolbar.title = itemName
        viewModel.loadDetails(itemId)
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = PhraseDetailAdapter { detail ->
            // 点击明细可以复制或查看完整内容
            // 目前只是显示，暂时不跳转到详情页
        }

        binding.rvDetails.layoutManager = LinearLayoutManager(this)
        binding.rvDetails.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { editable ->
            val keyword = editable?.toString() ?: ""
            if (keyword.isBlank()) {
                viewModel.loadDetails(itemId)
            } else {
                viewModel.searchDetails(keyword)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.details.observe(this) { details ->
            if (details.isEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.rvDetails.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.rvDetails.visibility = View.VISIBLE
                adapter.submitList(details)
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
