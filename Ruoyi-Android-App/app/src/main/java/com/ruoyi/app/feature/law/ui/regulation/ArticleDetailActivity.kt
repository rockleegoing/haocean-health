package com.ruoyi.app.feature.law.ui.regulation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ruoyi.app.databinding.ActivityArticleDetailBinding
import com.ruoyi.app.feature.law.db.entity.RegulationArticleEntity
import com.ruoyi.app.feature.law.repository.LawRepository
import com.ruoyi.app.model.Constant
import com.therouter.router.Route
import kotlinx.coroutines.launch

@Route(path = Constant.articleDetailRoute)
class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding
    private lateinit var repository: LawRepository
    private var articleId: Long = 0
    private var regulationTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = LawRepository(this)

        // 从 Intent URI 中提取参数
        articleId = extractArticleIdFromIntent()
        regulationTitle = extractRegulationTitleFromIntent()

        android.util.Log.d("ArticleDetail", "onCreate: articleId=$articleId, regulationTitle=$regulationTitle")
        if (articleId > 0) {
            loadArticleDetail()
        }
    }

    private fun extractArticleIdFromIntent(): Long {
        val extras = intent.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras.get(key)
                if (key == "article_id" || key == "articleId") {
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

    private fun extractRegulationTitleFromIntent(): String {
        val extras = intent.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras.get(key)
                if ((key == "regulation_title" || key == "regulationTitle") && value is String && value.isNotEmpty()) {
                    return value
                }
            }
        }
        return ""
    }

    private fun loadArticleDetail() {
        lifecycleScope.launch {
            val article = repository.getArticleById(articleId)
            article?.let { displayArticle(it) }
        }
    }

    private fun displayArticle(article: RegulationArticleEntity) {
        binding.tvArticleNo.text = article.articleNo ?: "条款"
        binding.tvRegulationTitle.text = regulationTitle
        binding.tvContent.text = article.content ?: "暂无内容"
    }
}
