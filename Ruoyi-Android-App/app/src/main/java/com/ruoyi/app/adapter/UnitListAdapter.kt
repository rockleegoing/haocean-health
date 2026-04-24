package com.ruoyi.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.data.database.entity.UnitEntity
import com.ruoyi.app.databinding.ItemUnitBinding
import com.ruoyi.app.utils.DistanceUtils

class UnitListAdapter(
    private val onItemClick: (UnitEntity) -> Unit
) : ListAdapter<UnitEntity, UnitListAdapter.UnitViewHolder>(UnitDiffCallback()) {

    private var currentLat: Double? = null
    private var currentLon: Double? = null

    fun setCurrentLocation(lat: Double?, lon: Double?) {
        currentLat = lat
        currentLon = lon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        val binding = ItemUnitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UnitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UnitViewHolder(
        private val binding: ItemUnitBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(unit: UnitEntity) {
            binding.tvUnitName.text = unit.unitName
            binding.tvAddress.text = unit.businessAddress ?: "暂无地址"
            binding.tvIndustry.text = unit.industryCategoryName ?: "未知分类"

            // 显示距离
            if (currentLat != null && currentLon != null && unit.latitude != null && unit.longitude != null) {
                val distance = DistanceUtils.calculateDistance(currentLat!!, currentLon!!, unit.latitude!!, unit.longitude!!)
                binding.tvDistance.text = DistanceUtils.formatDistance(distance)
                binding.tvDistance.visibility = View.VISIBLE
            } else {
                binding.tvDistance.visibility = View.GONE
            }
        }
    }

    class UnitDiffCallback : DiffUtil.ItemCallback<UnitEntity>() {
        override fun areItemsTheSame(oldItem: UnitEntity, newItem: UnitEntity): Boolean {
            return oldItem.unitId == newItem.unitId
        }

        override fun areContentsTheSame(oldItem: UnitEntity, newItem: UnitEntity): Boolean {
            return oldItem == newItem
        }
    }
}
