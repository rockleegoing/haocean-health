package com.ruoyi.app.feature.document.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.R

data class CategoryWithTemplates(
    val categoryId: Long,
    val categoryName: String,
    val displayType: String,
    val templates: List<TemplateItem>
)

data class TemplateItem(
    val id: Long,
    val templateName: String
)

class DocumentCategoryAdapter(
    private val onTemplateClick: (Long, String) -> Unit
) : RecyclerView.Adapter<DocumentCategoryAdapter.CategoryViewHolder>() {

    private var categories: List<CategoryWithTemplates> = emptyList()

    fun submitList(list: List<CategoryWithTemplates>) {
        categories = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.tv_category_title)
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.rv_templates)

        fun bind(category: CategoryWithTemplates) {
            titleText.text = category.categoryName

            val spanCount = when (category.displayType) {
                "grid" -> 3
                "table" -> 2
                else -> 1
            }

            recyclerView.layoutManager = if (category.displayType == "list") {
                LinearLayoutManager(itemView.context)
            } else {
                GridLayoutManager(itemView.context, spanCount)
            }

            val adapter = TemplateAdapter(category.templates) { id, name ->
                onTemplateClick(id, name)
            }
            recyclerView.adapter = adapter
        }
    }

    class TemplateAdapter(
        private val templates: List<TemplateItem>,
        private val onClick: (Long, String) -> Unit
    ) : RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_document_template_simple, parent, false)
            return TemplateViewHolder(view)
        }

        override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
            holder.bind(templates[position])
        }

        override fun getItemCount() = templates.size

        inner class TemplateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nameText: TextView = itemView.findViewById(R.id.tv_template_name)

            fun bind(template: TemplateItem) {
                nameText.text = template.templateName
                itemView.setOnClickListener { onClick(template.id, template.templateName) }
            }
        }
    }
}
