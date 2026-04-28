package com.ruoyi.app.feature.law.ui.regulation

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruoyi.app.databinding.ActivityRegulationListBinding
import com.ruoyi.app.feature.law.db.entity.RegulationEntity
import com.ruoyi.app.feature.law.repository.LawRepository
import com.ruoyi.app.feature.law.ui.RegulationAdapter
import com.ruoyi.app.model.Constant
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Route(path = Constant.regulationListRoute)
class RegulationListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegulationListBinding
    private lateinit var adapter: RegulationAdapter
    private lateinit var repository: LawRepository

    private var filterType: String = ""
    private var filterValue: String = ""
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegulationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filterType = intent.getStringExtra("filter_type") ?: ""
        filterValue = intent.getStringExtra("filter_value") ?: ""
        title = intent.getStringExtra("title") ?: "法律法规"

        android.util.Log.d("RegulationList", "onCreate: filterType=$filterType, filterValue=$filterValue, title=$title")

        setupRecyclerView()
        setupSearch()
        initData()
    }

    private fun initData() {
        repository = LawRepository(this)
        loadRegulations()
    }

    private fun setupRecyclerView() {
        adapter = RegulationAdapter { regulation ->
            // 跳转到法规详情
            val url = "${Constant.regulationDetailRoute}?regulation_id=${regulation.regulationId}"
            TheRouter.build(url).navigation()
        }
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val keyword = binding.etSearch.text.toString().trim()
                if (keyword.isNotEmpty()) {
                    searchRegulations(keyword)
                }
                true
            } else {
                false
            }
        }
    }

    private fun loadRegulations() {
        android.util.Log.d("RegulationList", "loadRegulations: filterType=$filterType, filterValue=$filterValue")
        when (filterType) {
            "legal_type" -> {
                android.util.Log.d("RegulationList", "调用 loadByLegalType: $filterValue")
                loadByLegalType(filterValue)
            }
            "supervision_type" -> {
                android.util.Log.d("RegulationList", "调用 loadBySupervisionType: $filterValue")
                loadBySupervisionType(filterValue)
            }
            "search" -> {
                android.util.Log.d("RegulationList", "调用 searchRegulations: $filterValue")
                searchRegulations(filterValue)
            }
            else -> {
                android.util.Log.d("RegulationList", "调用 loadAll (未知 filterType)")
                loadAll()
            }
        }
    }

    private fun loadAll() {
        lifecycleScope.launch {
            repository.getAllRegulations().collectLatest { regulations ->
                updateUI(regulations)
            }
        }
    }

    private fun loadByLegalType(legalType: String) {
        android.util.Log.d("RegulationList", "loadByLegalType 开始查询: legalType=$legalType")
        lifecycleScope.launch {
            repository.getRegulationsByLegalType(legalType).collectLatest { regulations ->
                android.util.Log.d("RegulationList", "loadByLegalType 查询结果: ${regulations.size} 条")
                regulations.forEach {
                    android.util.Log.d("RegulationList", "  - ${it.title}, legalType=${it.legalType}")
                }
                updateUI(regulations)
            }
        }
    }

    private fun loadBySupervisionType(supervisionType: String) {
        lifecycleScope.launch {
            repository.getRegulationsBySupervisionType(supervisionType).collectLatest { regulations ->
                updateUI(regulations)
            }
        }
    }

    private fun searchRegulations(keyword: String) {
        lifecycleScope.launch {
            repository.searchRegulations(keyword).collectLatest { regulations ->
                updateUI(regulations)
            }
        }
    }

    private fun updateUI(regulations: List<RegulationEntity>) {
        adapter.submitList(regulations)
        binding.emptyView.visibility = if (regulations.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerview.visibility = if (regulations.isEmpty()) View.GONE else View.VISIBLE
    }
}
