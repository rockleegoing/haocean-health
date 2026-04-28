package com.ruoyi.app.fragment

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.ruoyi.app.databinding.FragmentLawBinding
import com.ruoyi.app.feature.law.repository.LawRepository
import com.ruoyi.app.feature.law.ui.regulation.RegulationListActivity
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingFragment
import com.therouter.TheRouter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LawFragment : BaseBindingFragment<FragmentLawBinding>() {

    private lateinit var repository: LawRepository

    companion object {
        @JvmStatic
        fun newInstance() = LawFragment()
    }

    override fun initView() {
        repository = LawRepository(requireContext())
        setupLegalTypesGrid()
        setupSupervisionTypesGrid()
        setupSearch()
    }

    override fun initData() {
    }

    private fun setupLegalTypesGrid() {
        lifecycleScope.launch {
            repository.getAllLegalTypes().collectLatest { legalTypes ->
                val typeNames = legalTypes.map { it.typeName }
                val adapter = LawTypeAdapter(typeNames) { legalTypeName ->
                    // 点击法律类型，跳转到法规列表
                    val url = "${Constant.regulationListRoute}?filter_type=legal_type&filter_value=$legalTypeName&title=$legalTypeName"
                    android.util.Log.d("LawFragment", "跳转到: $url")
                    TheRouter.build(url).navigation()
                }
                binding.rvLegalTypes.layoutManager = GridLayoutManager(requireContext(), 3)
                binding.rvLegalTypes.adapter = adapter
            }
        }
    }

    private fun setupSupervisionTypesGrid() {
        lifecycleScope.launch {
            repository.getAllSupervisionTypes().collectLatest { supervisionTypes ->
                val typeNames = supervisionTypes.map { it.typeName }
                val adapter = LawTypeAdapter(typeNames) { supervisionTypeName ->
                    // 点击监管类型，跳转到法规列表
                    val url = "${Constant.regulationListRoute}?filter_type=supervision_type&filter_value=$supervisionTypeName&title=$supervisionTypeName"
                    android.util.Log.d("LawFragment", "跳转到: $url")
                    TheRouter.build(url).navigation()
                }
                binding.rvSupervisionTypes.layoutManager = GridLayoutManager(requireContext(), 3)
                binding.rvSupervisionTypes.adapter = adapter
            }
        }
    }

    private fun setupSearch() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val keyword = binding.etSearch.text.toString().trim()
                if (keyword.isNotEmpty()) {
                    val url = "${Constant.regulationListRoute}?filter_type=search&filter_value=$keyword&title=搜索:$keyword"
                    android.util.Log.d("LawFragment", "跳转到: $url")
                    TheRouter.build(url).navigation()
                }
                true
            } else {
                false
            }
        }
    }
}

/**
 * 法律类型/监管类型适配器
 */
class LawTypeAdapter(
    private val items: List<String>,
    private val onItemClick: (String) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<LawTypeAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val tvTitle: android.widget.TextView = itemView.findViewById(com.ruoyi.app.R.id.tv_title)
        val tvIcon: android.widget.TextView = itemView.findViewById(com.ruoyi.app.R.id.tv_icon)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(com.ruoyi.app.R.layout.item_law_type, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item
        // 根据类型选择图标
        val icon = when {
            item.contains("食品") -> "🍎"
            item.contains("药品") -> "💊"
            item.contains("医疗") -> "🏥"
            item.contains("化妆") -> "💄"
            item.contains("特种") -> "⚙️"
            item.contains("工业") -> "🏭"
            item.contains("计量") -> "📏"
            item.contains("认证") -> "✓"
            item.contains("检验") -> "🔬"
            item.contains("广告") -> "📢"
            item.contains("知识") -> "💡"
            item.contains("法律") -> "⚖️"
            item.contains("法规") -> "📖"
            item.contains("规章") -> "📋"
            item.contains("标准") -> "📐"
            else -> "📁"
        }
        holder.tvIcon.text = icon
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size
}
