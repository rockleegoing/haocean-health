package com.ruoyi.app.feature.law.ui.basis

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruoyi.app.databinding.ActivityLegalBasisListBinding
import com.ruoyi.app.feature.law.db.entity.LegalBasisEntity
import com.ruoyi.app.feature.law.repository.LawRepository
import com.ruoyi.app.model.Constant
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Route(path = Constant.legalBasisListRoute)
class LegalBasisListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLegalBasisListBinding
    private lateinit var repository: LawRepository
    private lateinit var adapter: LegalBasisAdapter
    private var regulationId: Long = 0
    private var articleId: Long = 0
    private var articleNo: String = ""
    private var title: String = "定性依据"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLegalBasisListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = LawRepository(this)

        // 解析参数
        val extras = intent.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras.get(key)
                when (key) {
                    "regulation_id", "regulationId" -> {
                        regulationId = when (value) {
                            is Int -> value.toLong()
                            is Long -> value
                            is String -> value.toLongOrNull() ?: 0L
                            else -> 0L
                        }
                    }
                    "article_id", "articleId" -> {
                        articleId = when (value) {
                            is Int -> value.toLong()
                            is Long -> value
                            is String -> value.toLongOrNull() ?: 0L
                            else -> 0L
                        }
                    }
                    "article_no", "articleNo" -> {
                        articleNo = value?.toString() ?: ""
                    }
                    "title" -> {
                        title = value?.toString() ?: "定性依据"
                    }
                }
            }
        }

        // 如果没有传入title，用articleNo构建
        if (title == "定性依据" && articleNo.isNotEmpty()) {
            title = "定性依据 - $articleNo"
        }

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        initData()
    }

    private fun setupToolbar() {
        binding.toolbar.title = title
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initData() {
        when {
            articleId > 0 -> loadByArticleId(articleId)
            regulationId > 0 -> loadByRegulationId(regulationId)
            else -> loadAll()
        }
    }

    private fun setupRecyclerView() {
        adapter = LegalBasisAdapter { legalBasis ->
            val bundle = Bundle().apply {
                putLong(LegalBasisDetailActivity.EXTRA_BASIS_ID, legalBasis.basisId)
            }
            TheRouter.build(Constant.legalBasisDetailRoute).with(bundle).navigation()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val keyword = binding.etSearch.text.toString().trim()
                if (keyword.isNotEmpty()) {
                    search(keyword)
                }
                true
            } else {
                false
            }
        }

        // 监听搜索框文本变化，清空时恢复原始列表
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val keyword = s?.toString()?.trim() ?: ""
                if (keyword.isEmpty()) {
                    // 搜索框清空时，恢复原始列表
                    initData()
                }
            }
        })
    }

    private fun loadAll() {
        lifecycleScope.launch {
            repository.getAllLegalBasises().collectLatest { list ->
                updateUI(list)
            }
        }
    }

    private fun loadByRegulationId(regulationId: Long) {
        lifecycleScope.launch {
            repository.getLegalBasisesByRegulationId(regulationId).collectLatest { list ->
                updateUI(list)
            }
        }
    }

    private fun loadByArticleId(articleId: Long) {
        lifecycleScope.launch {
            repository.getLegalBasisesByArticleId(articleId).collectLatest { list ->
                updateUI(list)
            }
        }
    }

    private fun search(keyword: String) {
        lifecycleScope.launch {
            repository.searchLegalBasises(keyword).collectLatest { list ->
                updateUI(list)
            }
        }
    }

    private fun updateUI(list: List<LegalBasisEntity>) {
        adapter.submitList(list)
        binding.emptyView.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
    }
}
