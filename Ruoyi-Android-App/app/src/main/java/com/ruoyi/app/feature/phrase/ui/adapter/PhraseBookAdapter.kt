package com.ruoyi.app.feature.phrase.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.data.database.entity.PhraseBookEntity
import com.ruoyi.app.databinding.ItemPhraseBookBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 规范用语书本列表适配器
 */
class PhraseBookAdapter(
    private val onItemClick: (PhraseBookEntity) -> Unit
) : ListAdapter<PhraseBookEntity, PhraseBookAdapter.ViewHolder>(DiffCallback()) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPhraseBookBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPhraseBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(book: PhraseBookEntity) {
            binding.tvBookName.text = book.bookName
            binding.tvBookCode.text = book.bookCode
            binding.tvIndustryName.text = book.industryName ?: "通用"
            binding.tvBookDesc.text = book.bookDesc ?: "暂无描述"

            // 更新时间
            book.updateTime?.let {
                binding.tvUpdateTime.text = dateFormat.format(Date(it))
            } ?: run {
                binding.tvUpdateTime.text = "-"
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PhraseBookEntity>() {
        override fun areItemsTheSame(oldItem: PhraseBookEntity, newItem: PhraseBookEntity) =
            oldItem.bookId == newItem.bookId

        override fun areContentsTheSame(oldItem: PhraseBookEntity, newItem: PhraseBookEntity) =
            oldItem == newItem
    }
}
