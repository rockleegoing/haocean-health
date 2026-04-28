package com.ruoyi.app.feature.law.ui.basis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.databinding.ItemProcessingBasisBinding
import com.ruoyi.app.feature.law.db.entity.ProcessingBasisEntity

/**
 * 处理依据列表适配器
 */
class ProcessingBasisAdapter(
    private val onItemClick: (ProcessingBasisEntity) -> Unit
) : ListAdapter<ProcessingBasisEntity, ProcessingBasisAdapter.ViewHolder>(ProcessingBasisDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProcessingBasisBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemProcessingBasisBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(processingBasis: ProcessingBasisEntity) {
            binding.tvTitle.text = processingBasis.title ?: "无标题"
            // 内容详情已移至内容表，列表只显示标题
            binding.tvBasisNo.visibility = android.view.View.GONE
            binding.tvViolationType.visibility = android.view.View.GONE
            binding.tvIssuingAuthority.visibility = android.view.View.GONE
            binding.tvEffectiveDate.visibility = android.view.View.GONE
        }
    }

    class ProcessingBasisDiffCallback : DiffUtil.ItemCallback<ProcessingBasisEntity>() {
        override fun areItemsTheSame(oldItem: ProcessingBasisEntity, newItem: ProcessingBasisEntity): Boolean {
            return oldItem.basisId == newItem.basisId
        }

        override fun areContentsTheSame(oldItem: ProcessingBasisEntity, newItem: ProcessingBasisEntity): Boolean {
            return oldItem == newItem
        }
    }
}
