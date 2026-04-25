package com.ruoyi.app.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.ruoyi.app.R
import com.ruoyi.app.data.database.entity.SupervisionCategoryEntity
import com.ruoyi.app.data.database.entity.SupervisionItemEntity
import com.ruoyi.app.databinding.FragmentSupervisionBinding
import com.ruoyi.app.feature.supervision.ui.SupervisionDetailActivity
import com.ruoyi.app.feature.supervision.ui.adapter.SupervisionItemAdapter
import com.ruoyi.app.feature.supervision.viewmodel.SupervisionViewModel
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.activityViewModels
import com.therouter.TheRouter
import com.therouter.router.Route

/**
 * 监管事项 Fragment
 */
@Route(path = "/app/supervision")
class SupervisionFragment : Fragment() {

    private var _binding: FragmentSupervisionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SupervisionViewModel by activityViewModels()

    private lateinit var adapter: SupervisionItemAdapter
    private var categories: List<SupervisionCategoryEntity> = emptyList()
    private var currentCategoryId: Long? = null
    private var currentKeyword: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupervisionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
        viewModel.refreshFromServer()
    }

    private fun initView() {
        // 初始化 RecyclerView
        adapter = SupervisionItemAdapter(
            onItemClick = { item -> navigateToDetail(item) },
            onCollectClick = { item -> toggleCollect(item) }
        )
        binding.rvItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SupervisionFragment.adapter
        }

        // 搜索框
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                currentKeyword = s?.toString() ?: ""
                performSearch()
            }
        })

        // TabLayout 选择监听
        binding.tabCategory.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                if (position == 0) {
                    currentCategoryId = null
                } else {
                    currentCategoryId = categories.getOrNull(position - 1)?.categoryId
                }
                // 同步下拉框
                binding.spinnerCategory.setSelection(position)
                performSearch()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 分类下拉框
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    currentCategoryId = null
                } else {
                    currentCategoryId = categories.getOrNull(position - 1)?.categoryId
                }
                // 同步 TabLayout
                if (position != binding.tabCategory.selectedTabPosition) {
                    binding.tabCategory.getTabAt(position)?.select()
                }
                performSearch()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /**
     * 执行搜索（支持关键字 + 分类组合搜索）
     */
    private fun performSearch() {
        if (currentKeyword.isNotBlank()) {
            viewModel.searchItemsWithCategory(currentKeyword, currentCategoryId)
        } else if (currentCategoryId != null) {
            viewModel.loadItemsByCategory(currentCategoryId!!)
        } else {
            viewModel.loadTopLevelItems()
        }
    }

    private fun initObserver() {
        viewModel.categories.observe(viewLifecycleOwner) { list ->
            categories = list
            updateTabs(list)
            updateSpinner(list)
        }

        viewModel.items.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            binding.rvItems.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            binding.rvItems.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun updateTabs(list: List<SupervisionCategoryEntity>) {
        binding.tabCategory.removeAllTabs()
        binding.tabCategory.addTab(binding.tabCategory.newTab().setText("全部"))
        list.forEach { category ->
            binding.tabCategory.addTab(
                binding.tabCategory.newTab().setText(category.categoryName)
            )
        }
    }

    private fun updateSpinner(list: List<SupervisionCategoryEntity>) {
        val items = mutableListOf("全部")
        items.addAll(list.map { it.categoryName })

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = spinnerAdapter
    }

    private fun navigateToDetail(item: SupervisionItemEntity) {
        val bundle = Bundle().apply {
            putLong(SupervisionDetailActivity.EXTRA_ITEM_ID, item.itemId)
        }
        TheRouter.build(Constant.supervisionDetailRoute).with(bundle).navigation()
    }

    private fun toggleCollect(item: SupervisionItemEntity) {
        viewModel.toggleCollect(item.itemId, !item.isCollected)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = SupervisionFragment()
    }
}
