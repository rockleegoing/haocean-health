package com.ruoyi.app.feature.phrase.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.data.database.entity.PhraseItemEntity
import com.ruoyi.app.databinding.ItemPhraseItemBinding

/**
 * 规范用语句项列表适配器
 */
class PhraseItemAdapter(
    private val onItemClick: (PhraseItemEntity) -> Unit
) : ListAdapter<PhraseItemEntity, PhraseItemAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhraseItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPhraseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(item: PhraseItemEntity) {
            binding.tvItemName.text = item.itemName
            binding.tvItemCode.text = item.itemCode
            binding.tvItemDesc.text = item.itemDesc ?: "暂无描述"

            // 环节类型显示
            val phaseText = when (item.phaseType) {
                "CHECK_BEFORE" -> "检查前"
                "CHECK_ING" -> "检查中"
                "CHECK_AFTER" -> "检查后"
                else -> item.phaseType ?: "通用"
            }
            binding.tvPhaseType.text = phaseText

            // 状态显示
            binding.tvStatus.text = if (item.status == "0") "启用" else "禁用"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PhraseItemEntity>() {
        override fun areItemsTheSame(oldItem: PhraseItemEntity, newItem: PhraseItemEntity) =
            oldItem.itemId == newItem.itemId

        override fun areContentsTheSame(oldItem: PhraseItemEntity, newItem: PhraseItemEntity) =
            oldItem == newItem
    }
}
