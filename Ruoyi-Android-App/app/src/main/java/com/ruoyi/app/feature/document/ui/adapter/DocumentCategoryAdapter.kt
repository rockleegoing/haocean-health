package com.ruoyi.app.feature.document.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        android.util.Log.d("DocumentCategoryAdapter", "submitList called, size=${list.size}")
        categories = list
        android.util.Log.d("DocumentCategoryAdapter", "categories.size=${categories.size}, itemCount=${itemCount}")
        notifyDataSetChanged()
        android.util.Log.d("DocumentCategoryAdapter", "notifyDataSetChanged called")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document_category, parent, false)
        android.util.Log.d("DocumentCategoryAdapter", "onCreateViewHolder, view=$view, view.layoutParams=${view.layoutParams}")
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        android.util.Log.d("DocumentCategoryAdapter", "onBindViewHolder position=$position")
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.tv_category_title)
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.rv_templates)

        // 使用稳定的adapter引用
        private val templateAdapter: TemplateAdapter

        init {
            // 初始化时创建adapter，避免重复创建
            templateAdapter = TemplateAdapter { id, name ->
                onTemplateClick(id, name)
            }
            recyclerView.adapter = templateAdapter
        }

        fun bind(category: CategoryWithTemplates) {
            android.util.Log.d("DocumentCategoryAdapter", "CategoryViewHolder.bind called, category=${category.categoryName}")
            titleText.text = category.categoryName

            val spanCount = when (category.displayType) {
                "grid" -> 3
                "table" -> 2
                else -> 1
            }

            // 只在第一次或displayType变化时设置layoutManager
            val layoutManager = recyclerView.layoutManager
            if (layoutManager == null || layoutManager is GridLayoutManager && layoutManager.spanCount != spanCount) {
                recyclerView.layoutManager = GridLayoutManager(itemView.context, spanCount)
            }

            // 更新adapter数据
            templateAdapter.submitList(category.templates)
        }
    }

    class TemplateAdapter(
        private val onClick: (Long, String) -> Unit
    ) : RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>() {

        private var templates: List<TemplateItem> = emptyList()

        fun submitList(list: List<TemplateItem>) {
            android.util.Log.d("DocumentCategoryAdapter", "TemplateAdapter.submitList size=${list.size}")
            templates = list
            notifyDataSetChanged()
        }

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
            private val iconImage: ImageView = itemView.findViewById(R.id.iv_template_icon)

            fun bind(template: TemplateItem) {
                nameText.text = template.templateName
                iconImage.setImageResource(R.drawable.ic_document)
                itemView.setOnClickListener { onClick(template.id, template.templateName) }
            }
        }
    }
}
