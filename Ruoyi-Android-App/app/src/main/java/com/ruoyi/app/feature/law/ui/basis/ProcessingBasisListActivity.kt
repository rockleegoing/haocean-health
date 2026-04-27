package com.ruoyi.app.feature.law.ui.basis

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruoyi.app.databinding.ActivityLegalBasisListBinding
import com.ruoyi.app.feature.law.db.entity.ProcessingBasisEntity
import com.ruoyi.app.feature.law.repository.LawRepository
import com.ruoyi.app.model.Constant
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Route(path = Constant.processingBasisListRoute)
class ProcessingBasisListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLegalBasisListBinding
    private lateinit var repository: LawRepository
    private lateinit var adapter: ProcessingBasisAdapter

    private var articleId: Long = 0
    private var articleNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLegalBasisListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = LawRepository(this)
        articleId = intent.getLongExtra("article_id", 0)
        articleNo = intent.getStringExtra("article_no")

        if (articleId > 0) {
            setupToolbar()
            setupRecyclerView()
            loadData()
        } else {
            finish()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.title = "处理依据 - $articleNo"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = ProcessingBasisAdapter { basis ->
            // 点击跳转到详情页
            val bundle = Bundle().apply {
                putLong("basis_id", basis.basisId)
            }
            TheRouter.build(Constant.processingBasisDetailRoute).with(bundle).navigation()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun loadData() {
        binding.loadingView.visibility = View.VISIBLE
        lifecycleScope.launch {
            repository.getProcessingBasisesByArticle(articleId).collectLatest { list ->
                binding.loadingView.visibility = View.GONE
                if (list.isEmpty()) {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    adapter.submitList(list)
                }
            }
        }
    }
}
