package com.ruoyi.app.feature.law.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.R
import com.ruoyi.app.feature.law.model.LawListItem
import com.ruoyi.app.feature.law.model.LawType

class LawListAdapter(
    private val dataList: List<LawListItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (dataList[position]) {
            is LawListItem.GroupHeader -> VIEW_TYPE_HEADER
            is LawListItem.LawItem -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_law_group_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_law_type_book, parent, false)
                ItemViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = dataList[position]) {
            is LawListItem.GroupHeader -> {
                (holder as HeaderViewHolder).bind(item)
            }
            is LawListItem.LawItem -> {
                (holder as ItemViewHolder).bind(item)
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_group_title)

        fun bind(item: LawListItem.GroupHeader) {
            tvTitle.text = item.name
        }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTypeName: TextView = itemView.findViewById(R.id.tv_type_name)
        private val bookBackground: View = itemView.findViewById(R.id.book_background)

        fun bind(item: LawListItem.LawItem) {
            tvTypeName.text = item.name
            val backgroundRes = when (item.type) {
                LawType.COMPREHENSIVE -> R.drawable.bg_book_comprehensive
                LawType.SUPERVISION -> R.drawable.bg_book_supervision
            }
            tvTypeName.setBackgroundResource(backgroundRes)
            bookBackground.setBackgroundResource(backgroundRes)
        }
    }

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ITEM = 1
    }
}
