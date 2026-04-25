package com.ruoyi.app.feature.supervision.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.data.database.entity.SupervisionItemEntity
import com.ruoyi.app.databinding.ItemSupervisionBinding

/**
 * 监管事项列表适配器
 */
class SupervisionItemAdapter(
    private val onItemClick: (SupervisionItemEntity) -> Unit,
    private val onCollectClick: (SupervisionItemEntity) -> Unit
) : ListAdapter<SupervisionItemEntity, SupervisionItemAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSupervisionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemSupervisionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
            binding.ivCollect.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onCollectClick(getItem(position))
                }
            }
        }

        fun bind(item: SupervisionItemEntity) {
            binding.apply {
                tvName.text = item.name
                tvCategory.text = item.categoryName ?: "未分类"
                tvItemNo.text = item.itemNo?.let { "编码: $it" } ?: ""

                // 状态
                if (item.status == "0") {
                    tvStatus.text = "正常"
                    tvStatus.setTextColor(root.context.getColor(com.ruoyi.app.R.color.success))
                } else {
                    tvStatus.text = "停用"
                    tvStatus.setTextColor(root.context.getColor(com.ruoyi.app.R.color.error))
                }

                // 描述
                if (!item.description.isNullOrBlank()) {
                    tvDescription.text = item.description
                    tvDescription.visibility = View.VISIBLE
                } else {
                    tvDescription.visibility = View.GONE
                }

                // 法律依据
                if (!item.legalBasis.isNullOrBlank()) {
                    tvLegalBasis.text = item.legalBasis
                    tvLegalBasis.visibility = View.VISIBLE
                } else {
                    tvLegalBasis.visibility = View.GONE
                }

                // 收藏状态
                if (item.isCollected) {
                    ivCollect.setImageResource(com.ruoyi.app.R.drawable.ic_collect_selected)
                    ivCollect.visibility = View.VISIBLE
                } else {
                    ivCollect.setImageResource(com.ruoyi.app.R.drawable.ic_collect)
                    ivCollect.visibility = View.VISIBLE
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SupervisionItemEntity>() {
        override fun areItemsTheSame(
            oldItem: SupervisionItemEntity,
            newItem: SupervisionItemEntity
        ): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        override fun areContentsTheSame(
            oldItem: SupervisionItemEntity,
            newItem: SupervisionItemEntity
        ): Boolean {
            return oldItem == newItem
        }
    }
}
