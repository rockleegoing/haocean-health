package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.adapter.UnitListAdapter
import com.ruoyi.app.api.repository.CategoryRepository
import com.ruoyi.app.data.database.entity.IndustryCategoryEntity
import com.ruoyi.app.databinding.ActivitySelectUnitBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.SelectedUnitManager
import com.ruoyi.app.utils.DistanceUtils
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.clickDelay
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Route(path = Constant.selectUnitRoute)
class SelectUnitActivity : BaseBindingActivity<ActivitySelectUnitBinding>() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, SelectUnitActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private lateinit var unitAdapter: UnitListAdapter
    private val viewModel = SelectUnitViewModel(application)

    private var currentLat: Double? = null
    private var currentLon: Double? = null

    private var categoryJob: Job? = null
    private var categories: List<IndustryCategoryEntity> = emptyList()

    override fun initView() {
        binding.titlebar.setOnTitleBarListener(object : com.hjq.bar.OnTitleBarListener {
            override fun onLeftClick(titleBar: com.hjq.bar.TitleBar?) {
                finish()
            }
        })

        unitAdapter = UnitListAdapter { unit ->
            SelectedUnitManager.saveSelectedUnit(
                unit.unitId,
                unit.unitName,
                unit.industryCategoryId,
                unit.industryCategoryName
            )
            TheRouter.build(Constant.mainRoute).navigation()
            finish()
        }
        binding.rvUnits.apply {
            layoutManager = LinearLayoutManager(this@SelectUnitActivity)
            adapter = unitAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    if (totalItemCount <= lastVisibleItem + 5) {
                        viewModel.loadMore()
                    }
                }
            })
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchUnits(s?.toString() ?: "")
            }
        })

        binding.tvSkip.clickDelay {
            TheRouter.build(Constant.mainRoute).navigation()
            finish()
        }

        // 行业分类筛选
        binding.chipIndustry.setOnClickListener {
            showCategoryFilterDialog()
        }

        // 区域筛选
        binding.chipRegion.setOnClickListener {
            showRegionFilterDialog()
        }

        // 监管类型筛选
        binding.chipSupervision.setOnClickListener {
            showSupervisionTypeFilterDialog()
        }
    }

    override fun initData() {
        viewModel.getCurrentLocation { lat, lon ->
            currentLat = lat
            currentLon = lon
            loadUnits()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading && unitAdapter.itemCount == 0) View.VISIBLE else View.GONE
        }

        viewModel.isEmpty.observe(this) { isEmpty ->
            binding.llEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }

        viewModel.units.observe(this) { units ->
            val sortedUnits = DistanceUtils.sortUnitsByDistance(units, currentLat, currentLon)
            unitAdapter.setCurrentLocation(currentLat, currentLon)
            unitAdapter.submitList(sortedUnits)
        }

        loadCategories()
    }

    private fun loadUnits() {
        viewModel.loadUnits()
    }

    private fun loadCategories() {
        categoryJob?.cancel()
        categoryJob = CoroutineScope(Dispatchers.IO).launch {
            val repository = CategoryRepository(applicationContext)
            categories = repository.getAllCategoriesFromLocal()
        }
    }

    private fun showCategoryFilterDialog() {
        if (categories.isEmpty()) {
            com.hjq.toast.ToastUtils.show("请先同步行业分类数据")
            return
        }

        val items = categories.map { it.categoryName }.toTypedArray()
        var selectedIndex = -1

        AlertDialog.Builder(this)
            .setTitle("选择行业分类")
            .setSingleChoiceItems(items, selectedIndex) { dialog, which ->
                selectedIndex = which
            }
            .setPositiveButton("确定") { dialog, _ ->
                if (selectedIndex >= 0) {
                    val category = categories[selectedIndex]
                    viewModel.filterByCategory(category.categoryId)
                    binding.chipIndustry.text = category.categoryName
                    binding.chipIndustry.isChecked = true
                }
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .setNeutralButton("清除筛选") { dialog, _ ->
                viewModel.clearFilters()
                binding.chipIndustry.text = "行业分类"
                binding.chipIndustry.isChecked = false
                dialog.dismiss()
            }
            .show()
    }

    private fun showRegionFilterDialog() {
        val units = viewModel.units.value ?: emptyList()
        val regions = units.mapNotNull { it.region }.distinct().sorted()

        if (regions.isEmpty()) {
            com.hjq.toast.ToastUtils.show("暂无区域数据")
            return
        }

        val items = regions.toTypedArray()
        var selectedIndex = -1

        AlertDialog.Builder(this)
            .setTitle("选择区域")
            .setSingleChoiceItems(items, selectedIndex) { dialog, which ->
                selectedIndex = which
            }
            .setPositiveButton("确定") { dialog, _ ->
                if (selectedIndex >= 0) {
                    val region = regions[selectedIndex]
                    viewModel.filterByRegion(region)
                    binding.chipRegion.text = region
                    binding.chipRegion.isChecked = true
                }
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .setNeutralButton("清除筛选") { dialog, _ ->
                viewModel.clearFilters()
                binding.chipRegion.text = "区域"
                binding.chipRegion.isChecked = false
                dialog.dismiss()
            }
            .show()
    }

    private fun showSupervisionTypeFilterDialog() {
        val units = viewModel.units.value ?: emptyList()
        val supervisionTypes = units.mapNotNull { it.supervisionType }.distinct().sorted()

        if (supervisionTypes.isEmpty()) {
            com.hjq.toast.ToastUtils.show("暂无监管类型数据")
            return
        }

        val items = supervisionTypes.toTypedArray()
        var selectedIndex = -1

        AlertDialog.Builder(this)
            .setTitle("选择监管类型")
            .setSingleChoiceItems(items, selectedIndex) { dialog, which ->
                selectedIndex = which
            }
            .setPositiveButton("确定") { dialog, _ ->
                if (selectedIndex >= 0) {
                    val type = supervisionTypes[selectedIndex]
                    viewModel.filterBySupervisionType(type)
                    binding.chipSupervision.text = type
                    binding.chipSupervision.isChecked = true
                }
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .setNeutralButton("清除筛选") { dialog, _ ->
                viewModel.clearFilters()
                binding.chipSupervision.text = "监管类型"
                binding.chipSupervision.isChecked = false
                dialog.dismiss()
            }
            .show()
    }
}
