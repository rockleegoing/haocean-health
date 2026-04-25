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
            binding.tvBasisNo.text = "编号: ${legalBasis.basisNo ?: "无"}"
            binding.tvViolationType.text = "违法类型: ${legalBasis.violationType ?: "无"}"
            binding.tvIssuingAuthority.text = "发布机构: ${legalBasis.issuingAuthority ?: "未知"}"
            binding.tvEffectiveDate.text = "实施时间: ${legalBasis.effectiveDate ?: "未知"}"
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
