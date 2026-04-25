package com.ruoyi.app.feature.law.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.databinding.ItemRegulationBinding
import com.ruoyi.app.feature.law.db.entity.RegulationEntity

/**
 * 法律法规列表适配器
 */
class RegulationAdapter(
    private val onItemClick: (RegulationEntity) -> Unit
) : ListAdapter<RegulationEntity, RegulationAdapter.ViewHolder>(RegulationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRegulationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemRegulationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(regulation: RegulationEntity) {
            binding.tvTitle.text = regulation.title
            binding.tvLegalType.text = regulation.legalType
            binding.tvIssuingAuthority.text = regulation.issuingAuthority ?: "未知机构"
            binding.tvPublishDate.text = regulation.publishDate ?: ""

            // 解析监管类型
            val supervisionTypes = try {
                val jsonArray = org.json.JSONArray(regulation.supervisionTypes ?: "[]")
                (0 until jsonArray.length()).map { jsonArray.getString(it) }
            } catch (e: Exception) {
                emptyList()
            }
            binding.tvSupervisionTypes.text = if (supervisionTypes.isNotEmpty()) {
                supervisionTypes.take(3).joinToString(", ") + if (supervisionTypes.size > 3) "..." else ""
            } else {
                "无"
            }
        }
    }

    class RegulationDiffCallback : DiffUtil.ItemCallback<RegulationEntity>() {
        override fun areItemsTheSame(oldItem: RegulationEntity, newItem: RegulationEntity): Boolean {
            return oldItem.regulationId == newItem.regulationId
        }

        override fun areContentsTheSame(oldItem: RegulationEntity, newItem: RegulationEntity): Boolean {
            return oldItem == newItem
        }
    }
}
