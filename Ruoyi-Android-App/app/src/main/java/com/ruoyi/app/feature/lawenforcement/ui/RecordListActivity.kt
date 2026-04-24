package com.ruoyi.app.feature.lawenforcement.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import com.google.android.material.tabs.TabLayout
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.app.feature.lawenforcement.ui.adapter.RecordListAdapter
import com.ruoyi.app.feature.lawenforcement.viewmodel.RecordListViewModel
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.base.activityViewModels
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.toast.ToastUtils
import com.ruoyi.app.databinding.ActivityRecordListBinding
import com.therouter.TheRouter
import com.therouter.router.Route

@Route(path = Constant.recordListRoute)
class RecordListActivity : BaseBindingActivity<ActivityRecordListBinding>() {

    private val viewModel: RecordListViewModel by activityViewModels()
    private lateinit var adapter: RecordListAdapter

    override fun initView() {
        setupTitleBar()
        setupRecyclerView()
        setupFilters()
        observeViewModel()
    }

    override fun initData() {
        viewModel.loadRecords()
    }

    private fun setupTitleBar() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = RecordListAdapter(
            onItemClick = { record ->
                // 跳转到详情
                val bundle = Bundle().apply {
                    putLong("record_id", record.id)
                }
                TheRouter.build(Constant.recordDetailRoute).with(bundle).navigation()
            },
            onEditClick = { record ->
                // 编辑
                val bundle = Bundle().apply {
                    putLong("record_id", record.id)
                }
                TheRouter.build(Constant.recordEditRoute).with(bundle).navigation()
            },
            onSubmitClick = { record ->
                // 上报确认
                AlertDialog.Builder(this)
                    .setTitle("确认上报")
                    .setMessage("确定要上报这条执法记录吗？")
                    .setPositiveButton("确定") { _, _ ->
                        viewModel.submitRecord(record.id)
                    }
                    .setNegativeButton("取消", null)
                    .show()
            },
            onDeleteClick = { record ->
                // 删除确认
                AlertDialog.Builder(this)
                    .setTitle("确认删除")
                    .setMessage("确定要删除这条执法记录吗？")
                    .setPositiveButton("确定") { _, _ ->
                        viewModel.deleteRecord(record.id)
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        )

        binding.recyclerview.adapter = adapter
    }

    private fun setupFilters() {
        // TabLayout 状态切换
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("全部"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("待上报"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("已上报"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val status = when (tab?.position) {
                    0 -> null // 全部
                    1 -> RecordStatus.DRAFT // 待上报
                    2 -> RecordStatus.SUBMITTED // 已上报
                    else -> null
                }
                viewModel.filterByStatus(status)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 搜索功能
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val keyword = binding.etSearch.text.toString().trim()
                viewModel.searchRecords(keyword)
                true
            } else {
                false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.records.observe(this) { records ->
            adapter.submitList(records)
            binding.emptyView.visibility = if (records.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerview.visibility = if (records.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            ToastUtils.show(error)
        }

        viewModel.operationResult.observe(this) { result ->
            ToastUtils.show(result)
        }
    }
}