package com.ruoyi.app.feature.document.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.databinding.ItemDocumentTemplateBinding
import com.ruoyi.app.feature.document.model.DocumentTemplate

/**
 * 文书模板适配器
 */
class DocumentTemplateAdapter : ListAdapter<DocumentTemplate, DocumentTemplateAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDocumentTemplateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemDocumentTemplateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(template: DocumentTemplate) {
            binding.tvTemplateName.text = template.templateName
            binding.tvTemplateType.text = template.templateType ?: "标准文书"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DocumentTemplate>() {
        override fun areItemsTheSame(oldItem: DocumentTemplate, newItem: DocumentTemplate): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DocumentTemplate, newItem: DocumentTemplate): Boolean {
            return oldItem == newItem
        }
    }
}
