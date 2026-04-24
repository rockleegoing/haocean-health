package com.ruoyi.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.databinding.ItemHomeMenuBinding
import com.ruoyi.app.model.MenuItem

class HomeMenuAdapter(
    private val menuItems: List<MenuItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<HomeMenuAdapter.MenuViewHolder>() {

    private var selectedPosition = 0

    inner class MenuViewHolder(val binding: ItemHomeMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val previousPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemHomeMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = menuItems[position]
        holder.binding.ivIcon.setBackgroundResource(item.iconRes)
        holder.binding.tvName.text = item.name
        holder.binding.root.isSelected = position == selectedPosition
    }

    override fun getItemCount(): Int = menuItems.size

    fun setSelectedPosition(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
    }
}
