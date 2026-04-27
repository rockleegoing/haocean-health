package com.ruoyi.app.feature.law.ui.basis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.databinding.ItemProcessingBasisBinding
import com.ruoyi.app.feature.law.db.entity.ProcessingBasisEntity

class ProcessingBasisAdapter(
    private val onItemClick: (ProcessingBasisEntity) -> Unit
) : ListAdapter<ProcessingBasisEntity, ProcessingBasisAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProcessingBasisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemProcessingBasisBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProcessingBasisEntity) {
            // 格式化编号：001 -> 1.
            val formattedBasisNo = formatBasisNo(item.basisNo)
            binding.tvBasisNo.text = formattedBasisNo
            binding.tvTitle.text = item.title
            binding.tvViolationType.text = item.violationType ?: ""

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }

        private fun formatBasisNo(basisNo: String?): String {
            if (basisNo.isNullOrBlank()) return ""
            val num = basisNo.toLongOrNull() ?: return basisNo
            return "$num."
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ProcessingBasisEntity>() {
        override fun areItemsTheSame(oldItem: ProcessingBasisEntity, newItem: ProcessingBasisEntity): Boolean {
            return oldItem.basisId == newItem.basisId
        }

        override fun areContentsTheSame(oldItem: ProcessingBasisEntity, newItem: ProcessingBasisEntity): Boolean {
            return oldItem == newItem
        }
    }
}
