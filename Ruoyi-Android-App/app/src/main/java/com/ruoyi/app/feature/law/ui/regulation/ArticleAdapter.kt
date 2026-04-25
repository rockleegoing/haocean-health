package com.ruoyi.app.feature.law.ui.regulation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.databinding.ItemArticleBinding
import com.ruoyi.app.feature.law.db.entity.RegulationArticleEntity

/**
 * 条款列表适配器
 */
class ArticleAdapter(
    private val articles: List<RegulationArticleEntity>,
    private val onItemClick: (RegulationArticleEntity) -> Unit
) : ListAdapter<RegulationArticleEntity, ArticleAdapter.ViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount() = articles.size

    inner class ViewHolder(
        private val binding: ItemArticleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(articles[position])
                }
            }
        }

        fun bind(article: RegulationArticleEntity) {
            binding.tvArticleNo.text = article.articleNo ?: "条款"
            binding.tvContent.text = article.content?.take(150) ?: "暂无内容"
            binding.tvContent.visibility = if (article.content.isNullOrEmpty()) ViewGroup.GONE else ViewGroup.VISIBLE
        }
    }

    class ArticleDiffCallback : DiffUtil.ItemCallback<RegulationArticleEntity>() {
        override fun areItemsTheSame(oldItem: RegulationArticleEntity, newItem: RegulationArticleEntity): Boolean {
            return oldItem.articleId == newItem.articleId
        }

        override fun areContentsTheSame(oldItem: RegulationArticleEntity, newItem: RegulationArticleEntity): Boolean {
            return oldItem == newItem
        }
    }
}
