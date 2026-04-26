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
    }

    private var chaptersCache: List<RegulationChapterEntity> = emptyList()
    private var articlesCache: List<RegulationArticleEntity> = emptyList()

    private fun loadData(regulationId: Long) {
        lifecycleScope.launch {
            val chaptersDeferred = async { repository.getChaptersByRegulationId(regulationId).first() }
            val articlesDeferred = async { repository.getArticlesByRegulationId(regulationId).first() }

            chaptersCache = chaptersDeferred.await()
            articlesCache = articlesDeferred.await()

            rebuildTreeItems()
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
                    treeItems.add(
                        ChapterTreeItem.Article(
                            articleId = article.articleId,
                            chapterId = article.chapterId,
                            articleNo = article.articleNo,
                            content = article.content
                        )
                    )
                }
            }
        }

        adapter.submitList(treeItems)
    }
}