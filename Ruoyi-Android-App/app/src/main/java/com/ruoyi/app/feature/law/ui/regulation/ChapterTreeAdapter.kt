package com.ruoyi.app.feature.law.ui.regulation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.databinding.ItemChapterBinding
import com.ruoyi.app.databinding.ItemArticleBinding

/**
 * 章节-条款树状结构适配器
 */
class ChapterTreeAdapter(
    private val onChapterClick: (ChapterTreeItem.Chapter) -> Unit,
    private val onArticleClick: (ChapterTreeItem.Article) -> Unit,
    private val onLegalBasisClick: (ChapterTreeItem.Article) -> Unit,
    private val onProcessingBasisClick: (ChapterTreeItem.Article) -> Unit
) : ListAdapter<ChapterTreeItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_CHAPTER = 0
        private const val TYPE_ARTICLE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChapterTreeItem.Chapter -> TYPE_CHAPTER
            is ChapterTreeItem.Article -> TYPE_ARTICLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CHAPTER -> {
                val binding = ItemChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChapterViewHolder(binding)
            }
            TYPE_ARTICLE -> {
                val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ArticleViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ChapterTreeItem.Chapter -> (holder as ChapterViewHolder).bind(item)
            is ChapterTreeItem.Article -> (holder as ArticleViewHolder).bind(item)
        }
    }

    inner class ChapterViewHolder(private val binding: ItemChapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChapterTreeItem.Chapter) {
            binding.tvChapterNo.text = item.chapterNo
            binding.tvChapterTitle.text = item.chapterTitle
            binding.ivExpand.visibility = View.GONE // 隐藏展开箭头，禁用展开/收起

            // 章节不可点击，移除点击事件
        }
    }

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChapterTreeItem.Article) {
            binding.tvArticleNo.text = item.articleNo
            binding.tvArticleContent.text = item.content

            // 设置定性依据数量（始终显示，始终可点击）
            binding.tvLegalBasisCount.text = "定性依据 ${item.legalBasisCount}"
            binding.layoutLegalBasis.visibility = View.VISIBLE
            binding.layoutLegalBasis.setOnClickListener {
                onLegalBasisClick(item)
            }

            // 设置处理依据数量（始终显示，始终可点击）
            binding.tvProcessingBasisCount.text = "处理依据 ${item.processingBasisCount}"
            binding.layoutProcessingBasis.visibility = View.VISIBLE
            binding.layoutProcessingBasis.setOnClickListener {
                onProcessingBasisClick(item)
            }

            binding.root.setOnClickListener {
                onArticleClick(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChapterTreeItem>() {
        override fun areItemsTheSame(oldItem: ChapterTreeItem, newItem: ChapterTreeItem): Boolean {
            return when {
                oldItem is ChapterTreeItem.Chapter && newItem is ChapterTreeItem.Chapter ->
                    oldItem.chapterId == newItem.chapterId
                oldItem is ChapterTreeItem.Article && newItem is ChapterTreeItem.Article ->
                    oldItem.articleId == newItem.articleId
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ChapterTreeItem, newItem: ChapterTreeItem): Boolean {
            return oldItem == newItem
        }
    }
}

sealed class ChapterTreeItem {
    data class Chapter(
        val chapterId: Long,
        val chapterNo: String?,
        val chapterTitle: String?,
        val hasArticles: Boolean = false,
        var isExpanded: Boolean = false
    ) : ChapterTreeItem()

    data class Article(
        val articleId: Long,
        val chapterId: Long?,
        val articleNo: String?,
        val content: String?,
        val legalBasisCount: Int = 0,
        val processingBasisCount: Int = 0
    ) : ChapterTreeItem()
}