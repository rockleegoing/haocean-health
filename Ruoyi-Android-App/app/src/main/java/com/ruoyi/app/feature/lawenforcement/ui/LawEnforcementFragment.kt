package com.ruoyi.app.feature.lawenforcement.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.hjq.toast.ToastUtils
import com.ruoyi.app.databinding.FragmentLawEnforcementBinding
import com.ruoyi.app.feature.lawenforcement.viewmodel.LawEnforcementViewModel
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.SelectedUnitManager
import com.ruoyi.code.base.BaseBindingFragment
import com.ruoyi.code.base.activityViewModels
import com.therouter.TheRouter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LawEnforcementFragment : BaseBindingFragment<FragmentLawEnforcementBinding>() {

    private val viewModel: LawEnforcementViewModel by activityViewModels()

    private var currentPhotoPath: String? = null
    private var currentVideoPath: String? = null
    private var currentAudioPath: String? = null

    // 照片拍摄Launcher
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoPath != null) {
            viewModel.addPhotoToRecord(currentPhotoPath!!, File(currentPhotoPath!!).name, File(currentPhotoPath!!).length())
        }
    }

    // 视频拍摄Launcher
    private val takeVideoLauncher = registerForActivityResult(
        ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && currentVideoPath != null) {
            viewModel.addVideoToRecord(currentVideoPath!!, File(currentVideoPath!!).name, File(currentVideoPath!!).length(), null)
        }
    }

    // 音频录制Launcher (使用系统录音机)
    private val recordAudioLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 录音结果由录音App处理，临时方案
    }

    // 权限请求Launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (!allGranted) {
            ToastUtils.show("需要相关权限才能使用此功能")
        }
    }

    override fun initView() {
        setupClickListeners()
        observeViewModel()
        loadSelectedUnit()
    }

    override fun initData() {
        // 初始化
    }

    private fun setupClickListeners() {
        // 拍照
        binding.btnCamera.setOnClickListener {
            if (checkActivation()) {
                checkPermissionsAndTakePhoto()
            }
        }

        // 录音
        binding.btnAudio.setOnClickListener {
            if (checkActivation()) {
                checkPermissionsAndRecord()
            }
        }

        // 录像
        binding.btnVideo.setOnClickListener {
            if (checkActivation()) {
                checkPermissionsAndTakeVideo()
            }
        }

        // 导航
        binding.btnNavigation.setOnClickListener {
            if (checkActivation()) {
                openNavigation()
            }
        }

        // 记录列表
        binding.btnRecords.setOnClickListener {
            if (checkActivation()) {
                // 跳转到记录列表
                TheRouter.build(Constant.recordListRoute).navigation()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            ToastUtils.show(error)
        }

        viewModel.operationResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LawEnforcementViewModel.OperationResult.Success -> {
                    ToastUtils.show(result.message)
                }
                is LawEnforcementViewModel.OperationResult.Error -> {
                    ToastUtils.show(result.message)
                }
            }
        }
    }

    private fun loadSelectedUnit() {
        val unitId = SelectedUnitManager.getSelectedUnitId()
        val unitName = SelectedUnitManager.getSelectedUnitName()
        if (unitId != null && unitName != null) {
            viewModel.setSelectedUnit(
                unitId = unitId,
                unitName = unitName,
                industryId = SelectedUnitManager.getSelectedCategoryId() ?: 0,
                industryCode = SelectedUnitManager.getSelectedCategoryName() ?: ""
            )
        }
    }

    private fun checkActivation(): Boolean {
        // TODO: 检查设备激活状态（ActivationManager 未实现）
        // 暂时跳过激活检查
        return true
    }

    private fun checkPermissionsAndTakePhoto() {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            takePhoto()
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    private fun takePhoto() {
        val photoFile = createImageFile()
        currentPhotoPath = photoFile.absolutePath

        val photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )

        takePictureLauncher.launch(photoUri)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
        val storageDir = requireContext().getExternalFilesDir("photos")
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun checkPermissionsAndTakeVideo() {
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            takeVideo()
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    private fun takeVideo() {
        val videoFile = createVideoFile()
        currentVideoPath = videoFile.absolutePath

        val videoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            videoFile
        )

        takeVideoLauncher.launch(videoUri)
    }

    private fun createVideoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
        val storageDir = requireContext().getExternalFilesDir("videos")
        return File.createTempFile("MP4_${timeStamp}_", ".mp4", storageDir)
    }

    private fun checkPermissionsAndRecord() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            recordAudio()
        } else {
            permissionLauncher.launch(permissions)
        }
    }

    private fun recordAudio() {
        // 启动系统录音机
        val intent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            recordAudioLauncher.launch(intent)
        } else {
            ToastUtils.show("未找到录音应用")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun openNavigation() {
        val unitId = SelectedUnitManager.getSelectedUnitId()
        val unitName = SelectedUnitManager.getSelectedUnitName()
        if (unitId == null || unitName == null) {
            ToastUtils.show("请先选择执法单位")
            return
        }

        // 注意：SelectedUnitManager 只存储了 ID 和名称，没有经纬度信息
        // 如果需要导航功能，需要扩展 SelectedUnitManager 或从 UnitEntity 获取经纬度
        ToastUtils.show("该单位暂无位置信息")
    }

    companion object {
        const val TAG = "LawEnforcementFragment"
    }
}
