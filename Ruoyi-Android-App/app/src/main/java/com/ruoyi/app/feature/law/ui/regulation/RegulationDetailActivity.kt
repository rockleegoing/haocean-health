package com.ruoyi.app.feature.law.ui.regulation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruoyi.app.databinding.ActivityRegulationDetailBinding
import com.ruoyi.app.feature.law.db.entity.RegulationArticleEntity
import com.ruoyi.app.feature.law.db.entity.RegulationEntity
import com.ruoyi.app.feature.law.repository.LawRepository
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingActivity
import com.hjq.toast.ToastUtils
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Route(path = Constant.regulationDetailRoute)
class RegulationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegulationDetailBinding
    private lateinit var repository: LawRepository
    private var regulationId: Long = 0
    private var currentRegulation: RegulationEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegulationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = LawRepository(this)
        regulationId = intent.getLongExtra("regulation_id", 0)
        if (regulationId > 0) {
            loadRegulationDetail()
            loadArticles()
        }
    }

    private fun loadRegulationDetail() {
        lifecycleScope.launch {
            val regulation = repository.getRegulationById(regulationId)
            currentRegulation = regulation
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

    private fun loadArticles() {
        lifecycleScope.launch {
            repository.getArticlesByRegulationId(regulationId).collectLatest { articles ->
                if (articles.isNotEmpty()) {
                    updateArticlesList(articles)
                }
            }
        }
    }

    private fun updateArticlesList(articles: List<RegulationArticleEntity>) {
        val adapter = ArticleAdapter(articles.sortedBy { it.sortOrder }) { article ->
            val bundle = Bundle().apply {
                putLong("article_id", article.articleId)
                putString("regulation_title", currentRegulation?.title)
            }
            TheRouter.build(Constant.articleDetailRoute).with(bundle).navigation()
        }
        binding.recyclerView.adapter = adapter
    }
}
