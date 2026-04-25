package com.ruoyi.app.feature.law.ui.basis

import android.os.Bundle
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
    private var title: String = "定性依据"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLegalBasisListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = LawRepository(this)
        regulationId = intent.getLongExtra("regulation_id", 0)
        title = intent.getStringExtra("title") ?: "定性依据"

        setupRecyclerView()
        setupSearch()
        initData()
    }

    private fun initData() {
        if (regulationId > 0) {
            loadByRegulationId(regulationId)
        } else {
            loadAll()
        }
    }

    private fun setupRecyclerView() {
        adapter = LegalBasisAdapter { legalBasis ->
            val bundle = Bundle().apply {
                putLong("basis_id", legalBasis.basisId)
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
