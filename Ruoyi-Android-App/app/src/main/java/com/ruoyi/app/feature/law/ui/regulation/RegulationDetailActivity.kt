package com.ruoyi.app.feature.law.ui.regulation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruoyi.app.databinding.ActivityRegulationDetailBinding
import com.ruoyi.app.databinding.ItemCatalogBinding
import com.ruoyi.app.feature.law.db.entity.RegulationArticleEntity
import com.ruoyi.app.feature.law.db.entity.RegulationChapterEntity
import com.ruoyi.app.feature.law.db.entity.RegulationEntity
import com.ruoyi.app.feature.law.repository.LawRepository
import com.ruoyi.app.model.Constant
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Route(path = Constant.regulationDetailRoute)
class RegulationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegulationDetailBinding
    private lateinit var adapter: ChapterTreeAdapter
    private lateinit var repository: LawRepository

    private val expandedChapters = mutableSetOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegulationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = LawRepository(this)
        val regulationId = intent.getLongExtra("regulation_id", 0)
        if (regulationId > 0) {
            loadRegulationDetail()
            setupRecyclerView()
            loadData(regulationId)
        }
    }

    private fun loadRegulationDetail() {
        lifecycleScope.launch {
            val regulationId = intent.getLongExtra("regulation_id", 0)
            val regulation = repository.getRegulationById(regulationId)
            regulation?.let {
                binding.tvTitle.text = it.title
                binding.tvLegalType.text = it.legalType
                binding.tvIssuingAuthority.text = "发布机构: ${it.issuingAuthority ?: "未知"}"
                binding.tvPublishDate.text = "发布日期: ${it.publishDate ?: "未知"}"
                binding.tvEffectiveDate.text = "实施日期: ${it.effectiveDate ?: "未知"}"

                val supervisionTypes = try {
                    val jsonArray = org.json.JSONArray(it.supervisionTypes ?: "[]")
                    (0 until jsonArray.length()).map { i -> jsonArray.getString(i) }
                } catch (e: Exception) {
                    emptyList()
                }
                binding.tvSupervisionTypes.text = "监管类型: ${supervisionTypes.joinToString(", ")}"

                binding.tvContent.text = it.content?.take(200) ?: "暂无内容"
                binding.tvContent.visibility = if (it.content.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ChapterTreeAdapter(
            onChapterClick = { chapter ->
                if (expandedChapters.contains(chapter.chapterId)) {
                    expandedChapters.remove(chapter.chapterId)
                } else {
                    expandedChapters.add(chapter.chapterId)
                }
                rebuildTreeItems()
            },
            onArticleClick = { article ->
                val bundle = Bundle().apply {
                    putLong("article_id", article.articleId)
                }
                TheRouter.build(Constant.articleDetailRoute).with(bundle).navigation()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 设置目录 RecyclerView
        binding.rvCatalog.layoutManager = LinearLayoutManager(this)
    }

    private var chaptersCache: List<RegulationChapterEntity> = emptyList()
    private var articlesCache: List<RegulationArticleEntity> = emptyList()
    private var articleBasisCounts: MutableMap<Long, Pair<Int, Int>> = mutableMapOf() // articleId to (legalCount, processingCount)

    private fun loadData(regulationId: Long) {
        lifecycleScope.launch {
            val chaptersDeferred = async { repository.getChaptersByRegulationId(regulationId).first() }
            val articlesDeferred = async { repository.getArticlesByRegulationId(regulationId).first() }

            chaptersCache = chaptersDeferred.await()
            articlesCache = articlesDeferred.await()

            // 加载关联统计
            loadArticleBasisCounts()

            rebuildTreeItems()
        }
    }

    private suspend fun loadArticleBasisCounts() {
        for (article in articlesCache) {
            val legalCount = repository.getLegalBasisCountByArticle(article.articleId)
            val processingCount = repository.getProcessingBasisCountByArticle(article.articleId)
            articleBasisCounts[article.articleId] = Pair(legalCount, processingCount)
        }
    }

    private fun rebuildTreeItems() {
        val treeItems = mutableListOf<ChapterTreeItem>()

        for (chapter in chaptersCache) {
            val chapterItem = ChapterTreeItem.Chapter(
                chapterId = chapter.chapterId,
                chapterNo = chapter.chapterNo,
                chapterTitle = chapter.chapterTitle,
                hasArticles = articlesCache.any { it.chapterId == chapter.chapterId },
                isExpanded = expandedChapters.contains(chapter.chapterId)
            )
            treeItems.add(chapterItem)

            if (expandedChapters.contains(chapter.chapterId)) {
                val chapterArticles = articlesCache.filter { it.chapterId == chapter.chapterId }
                for (article in chapterArticles) {
                    val counts = articleBasisCounts[article.articleId] ?: Pair(0, 0)
                    treeItems.add(
                        ChapterTreeItem.Article(
                            articleId = article.articleId,
                            chapterId = article.chapterId,
                            articleNo = article.articleNo,
                            content = article.content,
                            legalBasisCount = counts.first,
                            processingBasisCount = counts.second
                        )
                    )
                }
            }
        }

        adapter.submitList(treeItems)

        // 显示目录
        if (chaptersCache.isNotEmpty()) {
            binding.tvChaptersLabel.visibility = View.VISIBLE
            binding.rvCatalog.visibility = View.VISIBLE
            setupCatalog()
        }
    }

    private fun setupCatalog() {
        val catalogItems = chaptersCache.map { chapter ->
            CatalogItem(chapter.chapterId, "${chapter.chapterNo ?: ""} ${chapter.chapterTitle ?: ""}")
        }
        binding.rvCatalog.adapter = CatalogAdapter(catalogItems) { chapterId ->
            // 点击目录项，滚动到对应章节并展开
            if (!expandedChapters.contains(chapterId)) {
                expandedChapters.add(chapterId)
            }
            rebuildTreeItems()
            // 滚动到对应位置
            val position = adapter.currentList.indexOfFirst {
                it is ChapterTreeItem.Chapter && it.chapterId == chapterId
            }
            if (position >= 0) {
                binding.recyclerView.scrollToPosition(position)
            }
        }
    }
}

data class CatalogItem(
    val chapterId: Long,
    val title: String
)

class CatalogAdapter(
    private val items: List<CatalogItem>,
    private val onItemClick: (Long) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCatalogBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCatalogBinding.inflate(android.view.LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvCatalogTitle.text = item.title
        holder.binding.root.setOnClickListener {
            onItemClick(item.chapterId)
        }
    }

    override fun getItemCount() = items.size