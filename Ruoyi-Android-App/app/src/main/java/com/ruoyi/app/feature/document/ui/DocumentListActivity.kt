package com.ruoyi.app.feature.document.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.databinding.ActivityDocumentListBinding
import com.ruoyi.app.feature.document.api.DocumentApi
import com.ruoyi.app.feature.document.model.DocumentGroup
import com.ruoyi.app.feature.document.model.DocumentTemplate
import com.ruoyi.app.feature.document.ui.adapter.DocumentGroupAdapter
import com.ruoyi.app.feature.document.ui.adapter.DocumentTemplateAdapter
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingActivity
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.launch

/**
 * 文书列表 Activity - 从执法记录进入
 */
@Route(path = Constant.documentListRoute)
class DocumentListActivity : BaseBindingActivity<ActivityDocumentListBinding>() {

    private var recordId: Long = 0
    private lateinit var groupAdapter: DocumentGroupAdapter
    private var selectedTemplate: DocumentTemplate? = null

    override fun initView() {
        setupTitleBar()
        setupRecyclerView()
        setupFab()
    }

    override fun initData() {
        recordId = intent.getLongExtra(EXTRA_RECORD_ID, 0)
        if (recordId == 0L) {
            Toast.makeText(this, "执法记录不存在", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        loadGroups()
    }

    private fun setupTitleBar() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
    }

    private fun setupRecyclerView() {
        groupAdapter = DocumentGroupAdapter { group, templates ->
            showTemplateDialog(group, templates)
        }
        binding.rvGroups.apply {
            layoutManager = LinearLayoutManager(this@DocumentListActivity)
            adapter = groupAdapter
        }
    }

    private fun setupFab() {
        binding.fabCreateDocument.setOnClickListener {
            if (selectedTemplate != null) {
                navigateToFillActivity(selectedTemplate!!)
            } else {
                Toast.makeText(this, "请先选择文书模板", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadGroups() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val groups = DocumentApi.syncGroups()
                binding.progressBar.visibility = View.GONE
                if (groups.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvGroups.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvGroups.visibility = View.VISIBLE
                    groupAdapter.submitList(groups)
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@DocumentListActivity, "加载失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showTemplateDialog(group: DocumentGroup, templates: List<DocumentTemplate>) {
        if (templates.isEmpty()) {
            Toast.makeText(this, "该套组下暂无模板", Toast.LENGTH_SHORT).show()
            return
        }

        val templateNames = templates.map { it.templateName }.toTypedArray()
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("选择模板 - ${group.groupName}")
            .setItems(templateNames) { _, which ->
                selectedTemplate = templates[which]
                binding.tvSelectedTemplate.text = "已选: ${selectedTemplate?.templateName}"
                binding.fabCreateDocument.visibility = View.VISIBLE
            }
            .show()
    }

    private fun navigateToFillActivity(template: DocumentTemplate) {
        val bundle = Bundle().apply {
            putLong(EXTRA_TEMPLATE_ID, template.id)
            putLong(EXTRA_RECORD_ID, recordId)
            putString(EXTRA_TEMPLATE_NAME, template.templateName)
        }
        TheRouter.build(Constant.documentFillRoute).with(bundle).navigation()
    }

    companion object {
        const val EXTRA_RECORD_ID = "recordId"
        const val EXTRA_TEMPLATE_ID = "templateId"
        const val EXTRA_TEMPLATE_NAME = "templateName"
    }
}
