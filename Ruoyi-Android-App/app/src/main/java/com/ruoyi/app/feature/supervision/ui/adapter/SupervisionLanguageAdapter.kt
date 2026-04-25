package com.ruoyi.app.feature.supervision.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.databinding.ItemSupervisionLanguageBinding
import com.ruoyi.app.feature.supervision.model.SupervisionLanguageLink

/**
 * 规范用语关联适配器
 */
class SupervisionLanguageAdapter :
    RecyclerView.Adapter<SupervisionLanguageAdapter.ViewHolder>() {

    private val items = mutableListOf<SupervisionLanguageLink>()

    fun submitList(list: List<SupervisionLanguageLink>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSupervisionLanguageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(
        private val binding: ItemSupervisionLanguageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SupervisionLanguageLink) {
            binding.tvLanguageName.text = item.languageName ?: "未命名"
            binding.tvLanguageContent.text = item.languageContent ?: ""
        }
    }
}
