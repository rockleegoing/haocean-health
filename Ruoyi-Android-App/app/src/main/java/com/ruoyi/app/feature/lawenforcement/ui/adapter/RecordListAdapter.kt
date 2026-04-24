package com.ruoyi.app.feature.lawenforcement.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.data.database.entity.EnforcementRecordEntity
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.ruoyi_app.databinding.ItemEnforcementRecordBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordListAdapter(
    private val onItemClick: (EnforcementRecordEntity) -> Unit,
    private val onEditClick: (EnforcementRecordEntity) -> Unit,
    private val onSubmitClick: (EnforcementRecordEntity) -> Unit,
    private val onDeleteClick: (EnforcementRecordEntity) -> Unit
) : ListAdapter<EnforcementRecordEntity, RecordListAdapter.ViewHolder>(DiffCallback()) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEnforcementRecordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemEnforcementRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }

            binding.btnEdit.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClick(getItem(position))
                }
            }

            binding.btnSubmit.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onSubmitClick(getItem(position))
                }
            }

            binding.btnDelete.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(getItem(position))
                }
            }
        }

        fun bind(record: EnforcementRecordEntity) {
            binding.tvUnitName.text = record.unitName
            binding.tvIndustry.text = record.industryCode
            binding.tvCreateTime.text = dateFormat.format(Date(record.createTime))

            // 状态显示
            val statusText = when (record.recordStatus) {
                RecordStatus.DRAFT -> "待上报"
                RecordStatus.SUBMITTED -> "已上报"
                RecordStatus.APPROVED -> "已审核"
                RecordStatus.REJECTED -> "已驳回"
                else -> record.recordStatus
            }
            binding.tvStatus.text = statusText

            // 状态颜色
            val statusColor = when (record.recordStatus) {
                RecordStatus.DRAFT -> android.graphics.Color.parseColor("#FF9800")
                RecordStatus.SUBMITTED -> android.graphics.Color.parseColor("#2196F3")
                RecordStatus.APPROVED -> android.graphics.Color.parseColor("#4CAF50")
                RecordStatus.REJECTED -> android.graphics.Color.parseColor("#F44336")
                else -> android.graphics.Color.GRAY
            }
            binding.statusIndicator.setBackgroundColor(statusColor)

            // 证据统计显示
            binding.tvPhotoCount.text = "${record.photoCount}"
            binding.tvAudioCount.text = "${record.audioCount}"
            binding.tvVideoCount.text = "${record.videoCount}"

            // 操作按钮可见性
            binding.btnSubmit.visibility = if (record.recordStatus == RecordStatus.DRAFT) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<EnforcementRecordEntity>() {
        override fun areItemsTheSame(oldItem: EnforcementRecordEntity, newItem: EnforcementRecordEntity) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EnforcementRecordEntity, newItem: EnforcementRecordEntity) =
            oldItem == newItem
    }
}