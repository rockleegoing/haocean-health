package com.ruoyi.app.feature.lawenforcement.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ruoyi.app.data.database.entity.EvidenceMaterialEntity
import com.ruoyi.app.databinding.ItemVideoBinding
import java.io.File
import java.util.Locale

class VideoAdapter(
    private val onItemClick: (EvidenceMaterialEntity) -> Unit
) : ListAdapter<EvidenceMaterialEntity, VideoAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(material: EvidenceMaterialEntity) {
            Glide.with(binding.ivThumbnail)
                .load(File(material.filePath))
                .centerCrop()
                .into(binding.ivThumbnail)
            binding.tvDuration.text = material.duration?.let { formatDuration(it) } ?: "--:--"
        }

        private fun formatDuration(seconds: Int): String {
            val minutes = seconds / 60
            val secs = seconds % 60
            return String.format(Locale.CHINA, "%02d:%02d", minutes, secs)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<EvidenceMaterialEntity>() {
        override fun areItemsTheSame(oldItem: EvidenceMaterialEntity, newItem: EvidenceMaterialEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EvidenceMaterialEntity, newItem: EvidenceMaterialEntity) =
            oldItem == newItem
    }
}
