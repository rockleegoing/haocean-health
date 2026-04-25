package com.ruoyi.app.feature.phrase.ui.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.data.database.entity.PhraseDetailEntity
import com.ruoyi.app.databinding.ItemPhraseDetailBinding

/**
 * 规范用语明细列表适配器
 */
class PhraseDetailAdapter(
    private val onItemClick: (PhraseDetailEntity) -> Unit
) : ListAdapter<PhraseDetailEntity, PhraseDetailAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhraseDetailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPhraseDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(detail: PhraseDetailEntity) {
            binding.tvDetailTitle.text = detail.detailTitle

            // 处理内容显示，支持HTML格式
            val content = if (detail.detailType == "HTML") {
                Html.fromHtml(detail.detailContent, Html.FROM_HTML_MODE_COMPACT).toString()
            } else {
                detail.detailContent
            }
            binding.tvDetailContent.text = content

            // 类型标签
            binding.tvDetailType.text = when (detail.detailType) {
                "HTML" -> "HTML"
                else -> "文本"
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PhraseDetailEntity>() {
        override fun areItemsTheSame(oldItem: PhraseDetailEntity, newItem: PhraseDetailEntity) =
            oldItem.detailId == newItem.detailId

        override fun areContentsTheSame(oldItem: PhraseDetailEntity, newItem: PhraseDetailEntity) =
            oldItem == newItem
    }
}
