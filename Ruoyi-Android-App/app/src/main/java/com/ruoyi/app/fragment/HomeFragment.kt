package com.ruoyi.app.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.hjq.toast.ToastUtils
import com.ruoyi.app.activity.SelectUnitActivity
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.databinding.FragmentHomeBinding
import com.ruoyi.app.feature.lawenforcement.viewmodel.LawEnforcementViewModel
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.SelectedUnitManager
import com.ruoyi.app.utils.NavigationUtils
import com.ruoyi.code.base.BaseBindingFragment
import com.ruoyi.code.base.activityViewModels
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.lifecycleScope
import com.ruoyi.app.feature.document.model.DocumentCategory
import com.ruoyi.app.feature.document.repository.DocumentRepository
import com.ruoyi.app.feature.document.ui.adapter.CategoryWithTemplates
import com.ruoyi.app.feature.document.ui.adapter.TemplateItem
import com.ruoyi.app.feature.document.ui.adapter.DocumentCategoryAdapter

@Route(path = Constant.homeFragment)
class HomeFragment : BaseBindingFragment<FragmentHomeBinding>() {

    private val viewModel: LawEnforcementViewModel by activityViewModels()

    private var currentPhotoPath: String? = null
    private var currentVideoPath: String? = null
    private var currentAudioPath: String? = null
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private lateinit var categoryAdapter: DocumentCategoryAdapter
    private val documentRepository by lazy { DocumentRepository(requireContext()) }

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
        android.util.Log.d("HomeFragment", "permissionLauncher callback: allGranted=$allGranted, permissions=$permissions")
        if (allGranted) {
            // 应该根据请求的权限类型决定调用哪个方法，这里暂时记录日志
            android.util.Log.d("HomeFragment", "All permissions granted, but no action taken in callback")
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
        setupDocumentCategories()
    }

    override fun initData() {
        updateUnitSelector()
    }

    override fun onResume() {
        super.onResume()
        updateUnitSelector()
        loadSelectedUnit()
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
        android.util.Log.d("HomeFragment", "checkPermissionsAndTakePhoto called")
        if (!checkUnitSelected()) return
        val permissions = arrayOf(Manifest.permission.CAMERA)
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
        android.util.Log.d("HomeFragment", "Camera permission check: allGranted=$allGranted")
        if (allGranted) {
            takePhoto()
        } else {
            android.util.Log.d("HomeFragment", "Launching camera permission request")
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
        android.util.Log.d("HomeFragment", "checkPermissionsAndRecord called")
        if (!checkUnitSelected()) return
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
        android.util.Log.d("HomeFragment", "Audio permission check: allGranted=$allGranted")
        if (allGranted) {
            recordAudio()
        } else {
            android.util.Log.d("HomeFragment", "Launching audio permission request")
            permissionLauncher.launch(permissions)
        }
    }

    private fun recordAudio() {
        android.util.Log.d("HomeFragment", "recordAudio called")
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
            android.util.Log.e("HomeFragment", "recordAudio failed: ${e.message}", e)
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

        val unitId = SelectedUnitManager.getSelectedUnitId() ?: return
        val unitName = SelectedUnitManager.getSelectedUnitName() ?: return

        CoroutineScope(Dispatchers.Main).launch {
            val unitEntity = withContext(Dispatchers.IO) {
                AppDatabase.getInstance(requireContext()).unitDao().getUnitById(unitId)
            }

            if (unitEntity == null) {
                ToastUtils.show("无法获取单位信息")
                return@launch
            }

            val latitude = unitEntity.latitude
            val longitude = unitEntity.longitude

            if (latitude == null || longitude == null) {
                ToastUtils.show("该单位暂无位置信息")
                return@launch
            }

            NavigationUtils.navigate(requireContext(), latitude, longitude, unitEntity.unitName)
        }
    }

    private fun setupDocumentCategories() {
        categoryAdapter = DocumentCategoryAdapter { templateId, templateName ->
            // 跳转到文书填写页
            val unitId = SelectedUnitManager.getSelectedUnitId()
            if (unitId == null) {
                ToastUtils.show("请先选择执法单位")
                return@DocumentCategoryAdapter
            }
            val bundle = Bundle().apply {
                putLong("templateId", templateId)
                putString("templateName", templateName)
                putLong("unitId", unitId)
            }
            TheRouter.build(Constant.documentFillRoute)
                .with(bundle)
                .navigation()
        }
        // 重要：先设置 layoutManager，再设置 adapter
        binding.rvDocumentCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDocumentCategories.adapter = categoryAdapter
        binding.rvDocumentCategories.setHasFixedSize(false)

        // 调试：检查RecyclerView状态
        android.util.Log.d("HomeFragment", "rv_document_categories visibility=${binding.rvDocumentCategories.visibility}")
        android.util.Log.d("HomeFragment", "rv_document_categories measuredHeight=${binding.rvDocumentCategories.measuredHeight}")
        android.util.Log.d("HomeFragment", "ll_document_categories visibility=${binding.llDocumentCategories.visibility}")

        // 延迟加载，等view布局完成后再加载数据
        binding.root.post {
            android.util.Log.d("HomeFragment", "post: rv_document_categories measuredHeight=${binding.rvDocumentCategories.measuredHeight}")
            loadDocumentCategories()
        }
    }

    private fun loadDocumentCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            android.util.Log.d("HomeFragment", "=== 开始观察 categories Flow ===")
            // 获取选中单位的行业分类ID
            val industryCategoryId = SelectedUnitManager.getSelectedCategoryId()
            android.util.Log.d("HomeFragment", "选中单位的行业分类ID: $industryCategoryId")

            // 从中间表获取该行业分类关联的模板ID列表
            val templateIdsForIndustry = if (industryCategoryId != null && industryCategoryId > 0) {
                documentRepository.getTemplateIdsByIndustryCategory(industryCategoryId).toSet()
            } else {
                emptySet()
            }
            android.util.Log.d("HomeFragment", "从中间表查询到的模板ID数量: ${templateIdsForIndustry.size}")

            documentRepository.getCategories().collect { categories ->
                android.util.Log.d("HomeFragment", "=== categories Flow 触发 ===")
                android.util.Log.d("HomeFragment", "分类数量: ${categories.size}")
                if (categories.isEmpty()) {
                    binding.llDocumentCategories.visibility = View.GONE
                    android.util.Log.d("HomeFragment", "分类为空，隐藏区域")
                    return@collect
                }

                binding.llDocumentCategories.visibility = View.VISIBLE

                categories.forEach { c ->
                    android.util.Log.d("HomeFragment", "  分类: id=${c.categoryId}, name=${c.categoryName}, displayType=${c.displayType}")
                }

                val categoryWithTemplatesList = mutableListOf<CategoryWithTemplates>()
                for (category in categories) {
                    // 使用 first() 同步获取 Flow 的第一个值
                    android.util.Log.d("HomeFragment", "查询分类[${category.categoryId}]的模板...")
                    var templates = documentRepository.getTemplatesByCategory(category.categoryId).first()
                    // 按行业分类过滤：如果选择了行业分类，只从中间表获取模板ID进行过滤
                    if (industryCategoryId != null && industryCategoryId > 0) {
                        // 只使用中间表关联进行过滤
                        templates = templates.filter { it.id in templateIdsForIndustry }
                        android.util.Log.d("HomeFragment", "按行业分类[$industryCategoryId]从中间表过滤后，分类[${category.categoryName}]的模板数量: ${templates.size}")
                    } else {
                        // 未选择行业分类时，只显示没有设置行业分类的模板（不在中间表中的）
                        templates = templates.filter { it.id !in templateIdsForIndustry }
                        android.util.Log.d("HomeFragment", "无行业分类过滤，分类[${category.categoryName}]的模板数量: ${templates.size}")
                    }
                    templates.takeIf { it.isNotEmpty() }?.forEach { t ->
                        android.util.Log.d("HomeFragment", "  模板[id=${t.id}, name=${t.templateName}, categoryId=${t.categoryId}]")
                    }
                    if (templates.isNotEmpty()) {
                        val items = templates.map { TemplateItem(it.id, it.templateName) }
                        categoryWithTemplatesList.add(CategoryWithTemplates(
                            category.categoryId,
                            category.categoryName,
                            category.displayType,
                            items
                        ))
                    }
                }
                android.util.Log.d("HomeFragment", "最终显示的分类数量: ${categoryWithTemplatesList.size}")

                // 确保容器可见（即使列表为空也要显示标题区域）
                binding.llDocumentCategories.visibility = View.VISIBLE

                // 设置数据
                categoryAdapter.submitList(categoryWithTemplatesList)
                android.util.Log.d("HomeFragment", "after submitList, itemCount=${categoryAdapter.itemCount}")
            }
        }
    }
}
