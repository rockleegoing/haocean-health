package com.ruoyi.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.ruoyi.app.databinding.FragmentLawBinding
import com.ruoyi.app.feature.law.adapter.LawListAdapter
import com.ruoyi.app.feature.law.model.LawListItem
import com.ruoyi.app.feature.law.model.LawType
import com.ruoyi.app.feature.law.repository.LawTypeRepository
import com.ruoyi.app.feature.law.repository.LawTypeBindRepository
import com.ruoyi.code.base.BaseBindingFragment
import kotlinx.coroutines.launch

class LawFragment : BaseBindingFragment<FragmentLawBinding>() {

    private lateinit var lawTypeRepository: LawTypeRepository
    private lateinit var adapter: LawListAdapter

    companion object {
        @JvmStatic
        fun newInstance() = LawFragment()
    }

    override fun initView() {
        lawTypeRepository = LawTypeRepository(requireContext())
        setupRecyclerView()
        setupSearch()
    }

    override fun initData() {
        loadLawTypes()
    }

    private fun setupRecyclerView() {
        adapter = LawListAdapter(mutableListOf())
        binding.rvLawTypes.layoutManager = GridLayoutManager(context, 4).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (adapter.getItemViewType(position)) {
                        LawListAdapter.VIEW_TYPE_HEADER -> 4  // header 占整行
                        else -> 1  // item 占 1 列
                    }
                }
            }
        }
        binding.rvLawTypes.adapter = adapter
    }

    private fun setupSearch() {
        // 搜索图标点击事件（暂不实现搜索逻辑）
        binding.ivSearch.setOnClickListener {
            // TODO: 实现搜索功能
        }
    }

    private fun loadLawTypes() {
        lifecycleScope.launch {
            val rootTypes = lawTypeRepository.getRootTypes()
            val listItems = mutableListOf<LawListItem>()

            for (rootType in rootTypes) {
                // 添加分组标题
                listItems.add(LawListItem.GroupHeader(rootType.name))

                // 根据顶级类型确定 LawType
                val lawType = when (rootType.id) {
                    1L -> LawType.COMPREHENSIVE  // 综合法律条例
                    8L -> LawType.SUPERVISION   // 监管类型
                    else -> LawType.COMPREHENSIVE
                }

                // 获取子类型
                val children = lawTypeRepository.getChildrenByParentId(rootType.id)
                for (child in children) {
                    listItems.add(LawListItem.LawItem(
                        id = child.id,
                        name = child.name,
                        type = lawType
                    ))
                }
            }

            adapter = LawListAdapter(listItems)
            binding.rvLawTypes.adapter = adapter
        }
    }
}
