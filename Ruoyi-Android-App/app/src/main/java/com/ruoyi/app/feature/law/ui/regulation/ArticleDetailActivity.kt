package com.ruoyi.app.feature.law.ui.regulation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ruoyi.app.databinding.ActivityArticleDetailBinding
import com.ruoyi.app.feature.law.db.entity.RegulationArticleEntity
import com.ruoyi.app.feature.law.repository.LawRepository
import kotlinx.coroutines.launch

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
        articleId = intent.getLongExtra("article_id", 0)
        regulationTitle = intent.getStringExtra("regulation_title") ?: ""
        if (articleId > 0) {
            loadArticleDetail()
        }
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
