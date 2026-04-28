package com.ruoyi.app.feature.law.ui.basis

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.databinding.ItemLegalBasisBinding
import com.ruoyi.app.feature.law.db.entity.LegalBasisEntity

/**
 * 定性依据列表适配器
 */
class LegalBasisAdapter(
    private val onItemClick: (LegalBasisEntity) -> Unit
) : ListAdapter<LegalBasisEntity, LegalBasisAdapter.ViewHolder>(LegalBasisDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLegalBasisBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemLegalBasisBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(legalBasis: LegalBasisEntity) {
            binding.tvTitle.text = legalBasis.title
            // 内容详情已移至内容表，列表只显示标题
            binding.tvBasisNo.visibility = android.view.View.GONE
            binding.tvViolationType.visibility = android.view.View.GONE
            binding.tvIssuingAuthority.visibility = android.view.View.GONE
            binding.tvEffectiveDate.visibility = android.view.View.GONE
        }
    }

    class LegalBasisDiffCallback : DiffUtil.ItemCallback<LegalBasisEntity>() {
        override fun areItemsTheSame(oldItem: LegalBasisEntity, newItem: LegalBasisEntity): Boolean {
            return oldItem.basisId == newItem.basisId
        }

        override fun areContentsTheSame(oldItem: LegalBasisEntity, newItem: LegalBasisEntity): Boolean {
            return oldItem == newItem
        }
    }
}
