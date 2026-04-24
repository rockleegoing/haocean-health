package com.ruoyi.app.feature.lawenforcement.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.app.feature.lawenforcement.ui.adapter.RecordListAdapter
import com.ruoyi.app.feature.lawenforcement.viewmodel.RecordListViewModel
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.router.TheRouter
import com.ruoyi.code.utils.ToastUtils
import com.ruoyi.code.widget.OnTitleBarListener
import com.ruoyi.code.widget.TitleBar
import com.ruoyi.ruoyi_app.R
import com.ruoyi.ruoyi_app.databinding.ActivityRecordListBinding
import com.therouter.router.Route
import java.util.Calendar

@Route(path = Constant.recordListRoute)
class RecordListActivity : BaseBindingActivity<ActivityRecordListBinding>() {

    private val viewModel: RecordListViewModel by viewModels()
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
        // 状态下拉框
        val statusOptions = listOf("全部", "待上报", "已上报", "已审核", "已驳回")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusOptions)
        binding.spinnerStatus.adapter = statusAdapter
        binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val status = when (position) {
                    0 -> null
                    1 -> RecordStatus.DRAFT
                    2 -> RecordStatus.SUBMITTED
                    3 -> RecordStatus.APPROVED
                    4 -> RecordStatus.REJECTED
                    else -> null
                }
                viewModel.filterByStatus(status)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 单位下拉框（简化版，暂无单位筛选）
        val unitOptions = listOf("全部单位")
        val unitAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unitOptions)
        binding.spinnerUnit.adapter = unitAdapter

        // 日期筛选
        binding.btnDateFilter.setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun showDateRangePicker() {
        val startCalendar = Calendar.getInstance()
        val endCalendar = Calendar.getInstance()

        // 选择开始日期
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            startCalendar.set(year, month, dayOfMonth, 0, 0, 0)
            // 选择结束日期
            DatePickerDialog(this, { _, year2, month2, dayOfMonth2 ->
                endCalendar.set(year2, month2, dayOfMonth2, 23, 59, 59)
                val startTime = startCalendar.timeInMillis
                val endTime = endCalendar.timeInMillis
                viewModel.filterByDateRange(startTime, endTime)
            }, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show()
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
            ToastUtils.showShort(error)
        }

        viewModel.operationResult.observe(this) { result ->
            ToastUtils.showShort(result)
        }
    }
}