package com.ruoyi.app.feature.lawenforcement.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.toast.ToastUtils
import com.ruoyi.app.R
import com.ruoyi.app.databinding.ActivityRecordDetailBinding
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.app.feature.lawenforcement.ui.adapter.EvidenceAdapter
import com.ruoyi.app.feature.lawenforcement.viewmodel.RecordDetailViewModel
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.base.activityViewModels
import com.therouter.TheRouter
import com.therouter.router.Route
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Route(path = Constant.recordDetailRoute)
class RecordDetailActivity : BaseBindingActivity<ActivityRecordDetailBinding>() {

    private val viewModel: RecordDetailViewModel by activityViewModels()
    private lateinit var evidenceAdapter: EvidenceAdapter

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    override fun initView() {
        setupTitleBar()
        setupRecyclerView()
        setupButtons()
        observeViewModel()

        // 获取记录ID并加载
        val recordId = intent.getLongExtra("record_id", -1L)
        if (recordId != -1L) {
            viewModel.loadRecord(recordId)
        } else {
            ToastUtils.show("记录不存在")
            finish()
        }
    }

    override fun initData() {
        // 初始化
    }

    private fun setupTitleBar() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
    }

    private fun setupRecyclerView() {
        evidenceAdapter = EvidenceAdapter { material ->
            // 点击证据材料
            when (material.evidenceType) {
                "photo" -> {
                    // 预览照片
                    previewPhoto(material.filePath)
                }
                "audio" -> {
                    // 播放录音
                    playAudio(material.filePath)
                }
                "video" -> {
                    // 播放视频
                    playVideo(material.filePath)
                }
            }
        }

        binding.rvEvidence.adapter = evidenceAdapter
    }

    private fun setupButtons() {
        binding.btnReport.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("确认上报")
                .setMessage("确定要上报这条执法记录吗？")
                .setPositiveButton("确定") { _, _ ->
                    viewModel.submitRecord()
                }
                .setNegativeButton("取消", null)
                .show()
        }

        binding.btnEdit.setOnClickListener {
            val record = viewModel.record.value ?: return@setOnClickListener
            val bundle = Bundle().apply {
                putLong("record_id", record.id)
            }
            TheRouter.build(Constant.recordEditRoute).with(bundle).navigation()
        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除这条执法记录吗？")
                .setPositiveButton("确定") { _, _ ->
                    val record = viewModel.record.value ?: return@setPositiveButton
                    viewModel.deleteRecord(record.id)
                    finish()
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.record.observe(this) { record ->
            if (record != null) {
                binding.tvUnitName.text = record.unitName
                binding.tvIndustry.text = record.industryCode
                binding.tvCreateTime.text = dateFormat.format(Date(record.createTime))
                binding.tvDescription.text = record.description ?: "暂无描述"

                val statusText = when (record.recordStatus) {
                    RecordStatus.DRAFT -> "待上报"
                    RecordStatus.SUBMITTED -> "已上报"
                    RecordStatus.APPROVED -> "已审核"
                    RecordStatus.REJECTED -> "已驳回"
                    else -> record.recordStatus
                }
                binding.tvStatus.text = statusText

                // 上报按钮可见性
                binding.btnReport.visibility = if (record.recordStatus == RecordStatus.DRAFT) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }

        viewModel.evidenceMaterials.observe(this) { materials ->
            evidenceAdapter.submitList(materials)

            // 更新统计
            val stats = viewModel.getEvidenceStats()
            binding.tvPhotoCount.text = "${stats.photoCount} 张照片"
            binding.tvAudioCount.text = "${stats.audioCount} 条录音"
            binding.tvVideoCount.text = "${stats.videoCount} 条录像"
        }

        viewModel.error.observe(this) { error ->
            ToastUtils.show(error)
        }

        viewModel.operationResult.observe(this) { result ->
            ToastUtils.show(result)
            if (result == "上报成功") {
                // 刷新数据
                val recordId = intent.getLongExtra("record_id", -1L)
                viewModel.loadRecord(recordId)
            }
        }
    }

    private fun previewPhoto(filePath: String) {
        // 使用系统图片查看器
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val file = File(filePath)
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this@RecordDetailActivity,
                "${packageName}.fileprovider",
                file
            )
            setDataAndType(uri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    private fun playAudio(filePath: String) {
        // 使用系统音乐播放器
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val file = File(filePath)
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this@RecordDetailActivity,
                "${packageName}.fileprovider",
                file
            )
            setDataAndType(uri, "audio/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    private fun playVideo(filePath: String) {
        // 使用系统视频播放器
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val file = File(filePath)
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this@RecordDetailActivity,
                "${packageName}.fileprovider",
                file
            )
            setDataAndType(uri, "video/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }
}
