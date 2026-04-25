package com.ruoyi.app.feature.document.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.databinding.ItemDocumentGroupBinding
import com.ruoyi.app.feature.document.model.DocumentGroup
import com.ruoyi.app.feature.document.model.DocumentTemplate
import org.json.JSONArray

/**
 * 文书套组适配器
 */
class DocumentGroupAdapter(
    private val onGroupClick: (DocumentGroup, List<DocumentTemplate>) -> Unit
) : ListAdapter<DocumentGroup, DocumentGroupAdapter.ViewHolder>(DiffCallback()) {

    private var templateMap: Map<Long, List<DocumentTemplate>> = emptyMap()

    fun setTemplates(templates: List<DocumentTemplate>) {
        templateMap = templates.groupBy { it.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDocumentGroupBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemDocumentGroupBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val templateAdapter = DocumentTemplateAdapter()

        init {
            binding.rvTemplates.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = templateAdapter
            }
        }

        fun bind(group: DocumentGroup) {
            binding.tvGroupName.text = group.groupName
            binding.tvGroupType.text = group.groupType ?: "默认类型"

            // 解析模板列表
            val templates = parseTemplates(group.templates)
            templateAdapter.submitList(templates)

            binding.layoutGroupHeader.setOnClickListener {
                onGroupClick(group, templates)
            }

            binding.tvSelectTemplate.setOnClickListener {
                onGroupClick(group, templates)
            }
        }

        private fun parseTemplates(templatesJson: String?): List<DocumentTemplate> {
            if (templatesJson.isNullOrEmpty()) return emptyList()
            return try {
                val jsonArray = JSONArray(templatesJson)
                val templates = mutableListOf<DocumentTemplate>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    templates.add(
                        DocumentTemplate(
                            id = obj.optLong("id", 0),
                            templateCode = obj.optString("code", ""),
                            templateName = obj.optString("name", ""),
                            templateType = obj.optString("type", null),
                            category = null,
                            filePath = null,
                            fileUrl = null
                        )
                    )
                }
                templates
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DocumentGroup>() {
        override fun areItemsTheSame(oldItem: DocumentGroup, newItem: DocumentGroup): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DocumentGroup, newItem: DocumentGroup): Boolean {
            return oldItem == newItem
        }
    }
}
