package com.ruoyi.app.feature.lawenforcement.ui

import android.Manifest
import android.content.ContentObserver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.ruoyi.app.feature.lawenforcement.viewmodel.LawEnforcementViewModel
import com.ruoyi.code.base.BaseBindingFragment
import com.ruoyi.code.router.TheRouter
import com.ruoyi.code.utils.SelectedUnitManager
import com.ruoyi.code.utils.ToastUtils
import com.ruoyi.code.utils.ActivationManager
import com.ruoyi.ruoyi_app.R
import com.ruoyi.ruoyi_app.databinding.FragmentLawEnforcementBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LawEnforcementFragment : BaseBindingFragment<FragmentLawEnforcementBinding>() {

    private val viewModel: LawEnforcementViewModel by viewModels()

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
            ToastUtils.showShort("需要相关权限才能使用此功能")
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
        viewModel.error.observe(this) { error ->
            ToastUtils.showShort(error)
        }

        viewModel.operationResult.observe(this) { result ->
            when (result) {
                is LawEnforcementViewModel.OperationResult.Success -> {
                    ToastUtils.showShort(result.message)
                }
                is LawEnforcementViewModel.OperationResult.Error -> {
                    ToastUtils.showShort(result.message)
                }
            }
        }
    }

    private fun loadSelectedUnit() {
        val unit = SelectedUnitManager.getSelectedUnit()
        if (unit != null) {
            viewModel.setSelectedUnit(
                unitId = unit.unitId,
                unitName = unit.unitName,
                industryId = unit.industryCategoryId ?: 0,
                industryCode = unit.industryCategoryName ?: ""
            )
        }
    }

    private fun checkActivation(): Boolean {
        // 检查设备激活状态
        val activationManager = ActivationManager.getInstance()
        if (!activationManager.isActivated()) {
            ToastUtils.showShort("请先激活设备")
            // 可选：跳转到激活页面
            // TheRouter.build("/activation").navigation()
            return false
        }
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
            "${requireContext().packageName}.fileprovider",
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
            "${requireContext().packageName}.fileprovider",
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
            ToastUtils.showShort("未找到录音应用")
        }
        // 注意：系统录音机无法返回文件路径
        // 替代方案：监听 ContentObserver 检测新录音文件（见下方代码）
    }

    // ContentObserver 用于监听新录音文件
    private var audioObserver: ContentObserver? = null

    private fun registerAudioObserver() {
        audioObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                // 查询最新创建的录音文件
                queryLatestAudioFile()?.let { filePath ->
                    viewModel.addAudioToRecord(filePath, File(filePath).name, File(filePath).length(), null)
                }
            }
        }
        requireContext().contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            audioObserver!!
        )
    }

    private fun queryLatestAudioFile(): String? {
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        val cursor = requireContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.Audio.Media.DATA)
                if (columnIndex != -1) {
                    return it.getString(columnIndex)
                }
            }
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        audioObserver?.let {
            requireContext().contentResolver.unregisterContentObserver(it)
        }
    }

    private fun openNavigation() {
        val unit = SelectedUnitManager.getSelectedUnit()
        if (unit == null) {
            ToastUtils.showShort("请先选择执法单位")
            return
        }

        // 检查经纬度
        if (unit.latitude == null || unit.longitude == null) {
            ToastUtils.showShort("该单位暂无位置信息")
            return
        }

        // 检测高德地图
        val gaodeInstalled = isAppInstalled("com.autonavi.minimap")
        if (gaodeInstalled) {
            // 调用高德地图导航
            val uri = Uri.parse("androidamap://route?sourceApplication=appname&dlat=${unit.latitude}&dlon=${unit.longitude}&dname=${Uri.encode(unit.unitName)}&dev=0&t=1")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.autonavi.minimap")
            startActivity(intent)
        } else {
            // 使用浏览器打开
            val url = "https://uri.amap.com/navigation?to=${unit.longitude},${unit.latitude},${Uri.encode(unit.unitName)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            requireContext().packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        const val TAG = "LawEnforcementFragment"
    }
}