package com.ruoyi.app.feature.supervision.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.databinding.ItemSupervisionRegulationBinding
import com.ruoyi.app.feature.supervision.model.SupervisionRegulationLink

/**
 * 法律法规关联适配器
 */
class SupervisionRegulationAdapter :
    RecyclerView.Adapter<SupervisionRegulationAdapter.ViewHolder>() {

    private val items = mutableListOf<SupervisionRegulationLink>()

    fun submitList(list: List<SupervisionRegulationLink>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSupervisionRegulationBinding.inflate(
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
        private val binding: ItemSupervisionRegulationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SupervisionRegulationLink) {
            binding.tvRegulationName.text = item.regulationName ?: "未命名"
            binding.tvLawCode.text = item.lawCode ?: ""
        }
    }
}
