package com.ruoyi.app.feature.supervision.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruoyi.app.databinding.ActivitySupervisionDetailBinding
import com.ruoyi.app.feature.supervision.api.SupervisionApi
import com.ruoyi.app.feature.supervision.model.SupervisionDetailResponse
import com.ruoyi.app.feature.supervision.ui.adapter.SupervisionLanguageAdapter
import com.ruoyi.app.feature.supervision.ui.adapter.SupervisionRegulationAdapter
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingActivity
import com.therouter.router.Route
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import kotlinx.coroutines.launch

/**
 * 监管事项详情 Activity
 */
@Route(path = Constant.supervisionDetailRoute)
class SupervisionDetailActivity : BaseBindingActivity<ActivitySupervisionDetailBinding>() {

    private var itemId: Long = 0

    private val languageAdapter = SupervisionLanguageAdapter()
    private val regulationAdapter = SupervisionRegulationAdapter()

    override fun initView() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })

        binding.rvLanguage.apply {
            layoutManager = LinearLayoutManager(this@SupervisionDetailActivity)
            adapter = languageAdapter
        }

        binding.rvRegulation.apply {
            layoutManager = LinearLayoutManager(this@SupervisionDetailActivity)
            adapter = regulationAdapter
        }
    }

    override fun initData() {
        itemId = intent.getLongExtra(EXTRA_ITEM_ID, 0)
        if (itemId == 0L) {
            finish()
            return
        }
        loadData()
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = SupervisionApi.getItemDetail(itemId)
                binding.progressBar.visibility = View.GONE
                displayData(response)
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@SupervisionDetailActivity, "加载失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayData(response: SupervisionDetailResponse) {
        val item = response.item ?: return

        binding.apply {
            tvName.text = item.name
            tvCategory.text = item.categoryName ?: "未分类"
            tvItemNo.text = item.itemNo?.let { "编码: $it" } ?: ""
            tvStatus.text = if (item.status == "0") "正常" else "停用"
            tvStatus.setTextColor(
                if (item.status == "0") getColor(com.ruoyi.app.R.color.success)
                else getColor(com.ruoyi.app.R.color.error)
            )

            tvDescription.text = item.description ?: "暂无"
            tvLegalBasis.text = item.legalBasis ?: "暂无"
        }

        // 规范用语
        val languages = response.languageLinks
        if (languages.isEmpty()) {
            binding.tvNoLanguage.visibility = View.VISIBLE
            binding.rvLanguage.visibility = View.GONE
        } else {
            binding.tvNoLanguage.visibility = View.GONE
            binding.rvLanguage.visibility = View.VISIBLE
            languageAdapter.submitList(languages)
        }

        // 法律法规
        val regulations = response.regulationLinks
        if (regulations.isEmpty()) {
            binding.tvNoRegulation.visibility = View.VISIBLE
            binding.rvRegulation.visibility = View.GONE
        } else {
            binding.tvNoRegulation.visibility = View.GONE
            binding.rvRegulation.visibility = View.VISIBLE
            regulationAdapter.submitList(regulations)
        }
    }

    companion object {
        const val EXTRA_ITEM_ID = "itemId"
    }
}
