package com.ruoyi.app.feature.lawenforcement.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ruoyi.app.R
import com.ruoyi.app.data.database.entity.EvidenceMaterialEntity
import com.ruoyi.app.databinding.ItemEvidenceMaterialBinding
import com.ruoyi.app.feature.lawenforcement.model.EvidenceType
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

class EvidenceAdapter(
    private val onItemClick: (EvidenceMaterialEntity) -> Unit
) : ListAdapter<EvidenceMaterialEntity, EvidenceAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEvidenceMaterialBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemEvidenceMaterialBinding) :
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
            when (material.evidenceType) {
                EvidenceType.PHOTO -> {
                    // 照片显示缩略图
                    Glide.with(binding.ivThumbnail)
                        .load(File(material.filePath))
                        .centerCrop()
                        .into(binding.ivThumbnail)
                    binding.playOverlay.visibility = View.GONE
                    binding.tvDuration.visibility = View.GONE
                }
                EvidenceType.AUDIO -> {
                    // 录音显示播放图标
                    binding.ivThumbnail.setImageResource(R.drawable.ic_audio_placeholder)
                    binding.playOverlay.visibility = View.VISIBLE
                    material.duration?.let { duration ->
                        binding.tvDuration.visibility = View.VISIBLE
                        binding.tvDuration.text = formatDuration(duration)
                    }
                }
                EvidenceType.VIDEO -> {
                    // 视频显示缩略图和播放图标
                    Glide.with(binding.ivThumbnail)
                        .load(File(material.filePath))
                        .centerCrop()
                        .into(binding.ivThumbnail)
                    binding.playOverlay.visibility = View.VISIBLE
                    material.duration?.let { duration ->
                        binding.tvDuration.visibility = View.VISIBLE
                        binding.tvDuration.text = formatDuration(duration)
                    }
                }
            }
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