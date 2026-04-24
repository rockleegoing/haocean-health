package com.ruoyi.app.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.hjq.toast.ToastUtils
import com.ruoyi.app.activity.SelectUnitActivity
import com.ruoyi.app.databinding.FragmentHomeBinding
import com.ruoyi.app.feature.lawenforcement.viewmodel.LawEnforcementViewModel
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.SelectedUnitManager
import com.ruoyi.code.base.BaseBindingFragment
import com.ruoyi.code.base.activityViewModels
import com.therouter.TheRouter
import com.therouter.router.Route
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Route(path = Constant.homeFragment)
class HomeFragment : BaseBindingFragment<FragmentHomeBinding>() {

    private val viewModel: LawEnforcementViewModel by activityViewModels()

    private var currentPhotoPath: String? = null
    private var currentVideoPath: String? = null
    private var currentAudioPath: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false

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

    // 权限请求Launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            recordAudio()
        } else {
            ToastUtils.show("需要相关权限才能使用此功能")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun initView() {
        // 点击单位选择栏
        binding.llUnitSelector.setOnClickListener {
            SelectUnitActivity.startActivity(requireContext())
        }

        // 拍照
        binding.btnCamera.setOnClickListener {
            checkPermissionsAndTakePhoto()
        }

        // 录音
        binding.btnAudio.setOnClickListener {
            checkPermissionsAndRecord()
        }

        // 录像
        binding.btnVideo.setOnClickListener {
            checkPermissionsAndTakeVideo()
        }

        // 导航
        binding.btnNavigation.setOnClickListener {
            openNavigation()
        }

        // 记录列表
        binding.btnRecords.setOnClickListener {
            TheRouter.build(Constant.recordListRoute).navigation()
        }

        observeViewModel()
        loadSelectedUnit()
    }

    override fun initData() {
        updateUnitSelector()
    }

    override fun onResume() {
        super.onResume()
        updateUnitSelector()
    }

    private fun updateUnitSelector() {
        val unitName = SelectedUnitManager.getSelectedUnitName()
        if (unitName != null) {
            binding.tvSelectedUnit.text = "当前单位：$unitName"
        } else {
            binding.tvSelectedUnit.text = "请选择执法单位"
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

    private fun checkPermissionsAndTakePhoto() {
        if (!checkUnitSelected()) return
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
        if (!checkUnitSelected()) return
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
        if (!checkUnitSelected()) return
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
        if (allGranted) {
            recordAudio()
        } else {
            ToastUtils.show("需要相关权限才能使用此功能")
        }
    }

    private fun recordAudio() {
        try {
            val audioFile = createAudioFile()
            currentAudioPath = audioFile.absolutePath

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(requireContext())
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }

            isRecording = true
            ToastUtils.show("录音开始")

            // 停止录音并保存
            binding.root.postDelayed({
                stopRecording()
            }, 10000) // 录音10秒后自动停止

        } catch (e: Exception) {
            ToastUtils.show("录音失败: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        try {
            if (isRecording) {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
                mediaRecorder = null
                isRecording = false

                currentAudioPath?.let { path ->
                    val file = File(path)
                    viewModel.addAudioToRecord(path, file.name, file.length(), null)
                }
                ToastUtils.show("录音已保存")
            }
        } catch (e: Exception) {
            ToastUtils.show("停止录音失败: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun createAudioFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
        val storageDir = requireContext().getExternalFilesDir("audio")
        return File.createTempFile("AAC_${timeStamp}_", ".m4a", storageDir)
    }

    private fun checkUnitSelected(): Boolean {
        val unitId = SelectedUnitManager.getSelectedUnitId()
        if (unitId == null) {
            ToastUtils.show("请先选择执法单位")
            return false
        }
        return true
    }

    private fun openNavigation() {
        if (!checkUnitSelected()) return
        ToastUtils.show("该单位暂无位置信息")
    }
}
