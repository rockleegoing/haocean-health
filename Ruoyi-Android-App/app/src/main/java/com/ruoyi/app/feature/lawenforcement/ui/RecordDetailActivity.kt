package com.ruoyi.app.feature.lawenforcement.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.toast.ToastUtils
import com.ruoyi.app.R
import com.ruoyi.app.data.database.entity.EvidenceMaterialEntity
import com.ruoyi.app.databinding.ActivityRecordDetailBinding
import com.ruoyi.app.feature.lawenforcement.model.EvidenceType
import com.ruoyi.app.feature.lawenforcement.model.RecordStatus
import com.ruoyi.app.feature.lawenforcement.ui.adapter.AudioAdapter
import com.ruoyi.app.feature.lawenforcement.ui.adapter.PhotoAdapter
import com.ruoyi.app.feature.lawenforcement.ui.adapter.VideoAdapter
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
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var audioAdapter: AudioAdapter
    private lateinit var videoAdapter: VideoAdapter

    private var isPhotoExpanded = false
    private var isAudioExpanded = false
    private var isVideoExpanded = false
    private var recordId: Long = 0

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    override fun initView() {
        setupTitleBar()
        setupRecyclerViews()
        setupExpandableHeaders()
        setupButtons()
        observeViewModel()

        // 获取记录ID并加载
        recordId = extractRecordIdFromIntent()
        if (recordId > 0) {
            viewModel.loadRecord(recordId)
        } else {
            ToastUtils.show("记录不存在")
            finish()
        }
    }

    private fun extractRecordIdFromIntent(): Long {
        val extras = intent.extras
        if (extras != null) {
            for (key in extras.keySet()) {
                val value = extras.get(key)
                if (key == "record_id" || key == "recordId") {
                    return when (value) {
                        is Int -> if (value > 0) value.toLong() else 0L
                        is Long -> if (value > 0) value else 0L
                        is Integer -> {
                            val intValue = value.toInt()
                            if (intValue > 0) intValue.toLong() else 0L
                        }
                        is String -> value.toLongOrNull() ?: 0L
                        else -> 0L
                    }
                }
            }
        }
        return 0
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

    private fun setupRecyclerViews() {
        // 照片适配器
        photoAdapter = PhotoAdapter { material ->
            previewPhoto(material.filePath)
        }
        binding.rvPhoto.apply {
            layoutManager = GridLayoutManager(this@RecordDetailActivity, 3)
            adapter = photoAdapter
        }

        // 录音适配器
        audioAdapter = AudioAdapter { material ->
            playAudio(material.filePath)
        }
        binding.rvAudio.apply {
            layoutManager = LinearLayoutManager(this@RecordDetailActivity)
            adapter = audioAdapter
        }

        // 录像适配器
        videoAdapter = VideoAdapter { material ->
            playVideo(material.filePath)
        }
        binding.rvVideo.apply {
            layoutManager = GridLayoutManager(this@RecordDetailActivity, 3)
            adapter = videoAdapter
        }
    }

    private fun setupExpandableHeaders() {
        binding.layoutPhotoHeader.setOnClickListener {
            isPhotoExpanded = !isPhotoExpanded
            binding.rvPhoto.visibility = if (isPhotoExpanded) View.VISIBLE else View.GONE
            binding.ivPhotoExpand.setImageResource(
                if (isPhotoExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
            )
        }

        binding.layoutAudioHeader.setOnClickListener {
            isAudioExpanded = !isAudioExpanded
            binding.rvAudio.visibility = if (isAudioExpanded) View.VISIBLE else View.GONE
            binding.ivAudioExpand.setImageResource(
                if (isAudioExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
            )
        }

        binding.layoutVideoHeader.setOnClickListener {
            isVideoExpanded = !isVideoExpanded
            binding.rvVideo.visibility = if (isVideoExpanded) View.VISIBLE else View.GONE
            binding.ivVideoExpand.setImageResource(
                if (isVideoExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
            )
        }
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
                binding.tvRecordNo.text = record.recordNo
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
            val stats = viewModel.getEvidenceStats()

            // 照片
            val photos = materials.filter { it.evidenceType == EvidenceType.PHOTO }
            photoAdapter.submitList(photos)
            binding.tvPhotoCount.text = "(${photos.size})"

            // 录音
            val audios = materials.filter { it.evidenceType == EvidenceType.AUDIO }
            audioAdapter.submitList(audios)
            binding.tvAudioCount.text = "(${audios.size})"

            // 录像
            val videos = materials.filter { it.evidenceType == EvidenceType.VIDEO }
            videoAdapter.submitList(videos)
            binding.tvVideoCount.text = "(${videos.size})"
        }

        viewModel.error.observe(this) { error ->
            ToastUtils.show(error)
        }

        viewModel.operationResult.observe(this) { result ->
            ToastUtils.show(result)
            if (result == "上报成功" && recordId > 0) {
                // 刷新数据
                viewModel.loadRecord(recordId)
            }
        }
    }

    private fun previewPhoto(filePath: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val file = File(filePath)
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this@RecordDetailActivity,
                "${packageName}.provider",
                file
            )
            setDataAndType(uri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    private fun playAudio(filePath: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val file = File(filePath)
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this@RecordDetailActivity,
                "${packageName}.provider",
                file
            )
            setDataAndType(uri, "audio/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    private fun playVideo(filePath: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            val file = File(filePath)
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this@RecordDetailActivity,
                "${packageName}.provider",
                file
            )
            setDataAndType(uri, "video/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }
}
