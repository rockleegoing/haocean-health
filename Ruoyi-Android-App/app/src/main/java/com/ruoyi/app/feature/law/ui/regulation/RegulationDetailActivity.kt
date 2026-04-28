package com.ruoyi.app.feature.law.ui.regulation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruoyi.app.databinding.ActivityRegulationDetailBinding
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

        val regulationId = extractRegulationIdFromIntent()
        if (regulationId > 0) {
            loadRegulationDetail()
            setupRecyclerView()
            loadData(regulationId)
        }
    }

    private fun extractRegulationIdFromIntent(): Long {
        val extras = intent.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras.get(key)
                if (key == "regulation_id" || key == "regulationId") {
                    return when (value) {
                        is Int -> if (value > 0) value.toLong() else 0L
                        is Long -> if (value > 0) value else 0L
                        is Integer -> {
                            val intValue = value.toInt()
                            if (intValue > 0) intValue.toLong() else 0L
                        }
                        is String -> value.toLongOrNull() ?: 0L
                        else -> 0L
                    }
                }
            }
        }
        return 0
    }

    private fun loadRegulationDetail() {
        lifecycleScope.launch {
            val regulationId = extractRegulationIdFromIntent()
            android.util.Log.d("RegulationDetail", "loadRegulationDetail: regulationId=$regulationId")
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

                // 加载章节目录
                loadChaptersCatalog(regulationId)
            }
        }
    }

    private fun loadChaptersCatalog(regulationId: Long) {
        lifecycleScope.launch {
            val chapters = repository.getChaptersByRegulationId(regulationId).first()
            val catalogText = chapters.mapNotNull { chapter ->
                chapter.chapterNo?.let { no ->
                    chapter.chapterTitle?.let { title ->
                        "$no $title"
                    }
                }
            }.joinToString("\n")
            binding.tvChapters.text = catalogText.ifEmpty { "暂无章节" }
            binding.tvChapters.visibility = if (catalogText.isEmpty()) View.GONE else View.VISIBLE
            binding.tvChaptersLabel.visibility = if (catalogText.isEmpty()) View.GONE else View.VISIBLE
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
                val url = "${Constant.articleDetailRoute}?article_id=${article.articleId}"
                TheRouter.build(url).navigation()
            },
            onLegalBasisClick = { article ->
                val url = "${Constant.legalBasisListRoute}?article_id=${article.articleId}&article_no=${article.articleNo ?: ""}"
                TheRouter.build(url).navigation()
            },
            onProcessingBasisClick = { article ->
                val url = "${Constant.processingBasisListRoute}?article_id=${article.articleId}&article_no=${article.articleNo ?: ""}"
                TheRouter.build(url).navigation()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private var chaptersCache: List<RegulationChapterEntity> = emptyList()
    private var articlesCache: List<RegulationArticleEntity> = emptyList()
    private var articleCountsCache: Map<Long, Pair<Int, Int>> = emptyMap() // articleId -> (legalCount, processingCount)

    private fun loadData(regulationId: Long) {
        android.util.Log.d("RegulationDetail", "loadData: regulationId=$regulationId")
        lifecycleScope.launch {
            val chaptersDeferred = async { repository.getChaptersByRegulationId(regulationId).first() }
            val articlesDeferred = async { repository.getArticlesByRegulationId(regulationId).first() }

            chaptersCache = chaptersDeferred.await()
            articlesCache = articlesDeferred.await()

            android.util.Log.d("RegulationDetail", "loadData: chaptersCache.size=${chaptersCache.size}, articlesCache.size=${articlesCache.size}")

            // 默认展开所有章节，让条款直接显示
            expandedChapters.clear()
            chaptersCache.forEach { chapter ->
                expandedChapters.add(chapter.chapterId)
            }

            // 加载每个条款的关联依据数量
            val countDeferreds = articlesCache.map { article ->
                async {
                    val legalCount = repository.getLegalBasisCountByArticleId(article.articleId)
                    val processingCount = repository.getProcessingBasisCountByArticleId(article.articleId)
                    article.articleId to (legalCount to processingCount)
                }
            }
            val counts = countDeferreds.map { it.await() }.toMap()
            articleCountsCache = counts

            android.util.Log.d("RegulationDetail", "loadData: articleCountsCache.size=${articleCountsCache.size}")

            rebuildTreeItems()
        }
    }

    private fun rebuildTreeItems() {
        val treeItems = mutableListOf<ChapterTreeItem>()

        android.util.Log.d("RegulationDetail", "rebuildTreeItems: chaptersCache=${chaptersCache.size}, articlesCache=${articlesCache.size}, expandedChapters=${expandedChapters.size}")
        android.util.Log.d("RegulationDetail", "  Chapter IDs in cache: ${chaptersCache.map { it.chapterId }}")
        android.util.Log.d("RegulationDetail", "  Article chapterIds in cache: ${articlesCache.map { it.chapterId }}")

        for (chapter in chaptersCache) {
            val hasArticles = articlesCache.any { it.chapterId == chapter.chapterId }
            val isExpanded = expandedChapters.contains(chapter.chapterId)
            android.util.Log.d("RegulationDetail", "  Chapter ${chapter.chapterId}: hasArticles=$hasArticles, isExpanded=$isExpanded, articleCount=${articlesCache.count { it.chapterId == chapter.chapterId }}")

            val chapterItem = ChapterTreeItem.Chapter(
                chapterId = chapter.chapterId,
                chapterNo = chapter.chapterNo,
                chapterTitle = chapter.chapterTitle,
                hasArticles = hasArticles,
                isExpanded = isExpanded
            )
            treeItems.add(chapterItem)

            if (isExpanded) {
                val chapterArticles = articlesCache.filter { it.chapterId == chapter.chapterId }
                android.util.Log.d("RegulationDetail", "    Adding ${chapterArticles.size} articles for chapter ${chapter.chapterId}")
                for (article in chapterArticles) {
                    val counts = articleCountsCache[article.articleId] ?: (0 to 0)
                    android.util.Log.d("RegulationDetail", "      Article ${article.articleId}: content=${article.content?.take(30)}...")
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

        android.util.Log.d("RegulationDetail", "rebuildTreeItems: totalItems=${treeItems.size}")
        adapter.submitList(treeItems)
    }
}