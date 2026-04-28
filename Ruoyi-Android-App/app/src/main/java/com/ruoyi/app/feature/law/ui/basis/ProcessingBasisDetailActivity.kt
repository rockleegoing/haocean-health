package com.ruoyi.app.feature.law.ui.basis

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ruoyi.app.databinding.ActivityProcessingBasisDetailBinding
import com.ruoyi.app.feature.law.db.entity.ProcessingBasisContentEntity
import com.ruoyi.app.feature.law.db.entity.ProcessingBasisEntity
import com.ruoyi.app.feature.law.repository.LawRepository
import com.ruoyi.app.R
import com.ruoyi.app.model.Constant
import com.therouter.router.Route
import kotlinx.coroutines.launch

@Route(path = Constant.processingBasisDetailRoute)
class ProcessingBasisDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProcessingBasisDetailBinding
    private lateinit var repository: LawRepository
    private var basisId: Long = 0

    companion object {
        const val EXTRA_BASIS_ID = "basis_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessingBasisDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = LawRepository(this)
        basisId = extractBasisIdFromIntent()
        android.util.Log.d("ProcessingBasisDetail", "onCreate: basisId=$basisId, extras=${intent.extras?.keySet()}")

        setupToolbar()

        if (basisId > 0) {
            loadDetail()
        } else {
            android.util.Log.e("ProcessingBasisDetail", "onCreate: basisId is 0 or not found in intent")
        }
    }

    private fun extractBasisIdFromIntent(): Long {
        val extras = intent.extras
        if (extras != null) {
            // 首先直接查找 basis_id
            for (key in extras.keySet()) {
                val value = extras.get(key)
                if (key == "basis_id" || key == "basisId") {
                    return parseLongValue(value)
                }
            }
            // 如果没找到，检查 therouter_bundle 嵌套
            val routerBundle = extras.getBundle("therouter_bundle")
            if (routerBundle != null) {
                for (key in routerBundle.keySet()) {
                    val value = routerBundle.get(key)
                    if (key == "basis_id" || key == "basisId") {
                        return parseLongValue(value)
                    }
                }
            }
        }
        return 0
    }

    private fun parseLongValue(value: Any?): Long {
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

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun copyToClipboard(text: String, label: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "$label 已复制", Toast.LENGTH_SHORT).show()
    }

    private fun loadDetail() {
        lifecycleScope.launch {
            val processingBasis = repository.getProcessingBasisById(basisId)
            processingBasis?.let { displayDetail(it) }

            // 从内容表加载内容
            repository.getProcessingBasisContents(basisId).collect { contents ->
                displayContents(contents)
            }
        }
    }

    private fun displayDetail(processingBasis: ProcessingBasisEntity) {
        binding.tvTitle.text = processingBasis.title ?: "无标题"
    }

    private fun displayContents(contents: List<ProcessingBasisContentEntity>) {
        binding.contentContainer.removeAllViews()
        for (content in contents) {
            val itemView = layoutInflater.inflate(R.layout.item_basis_content, binding.contentContainer, false)
            itemView.findViewById<TextView>(R.id.tv_label).text = content.label
            itemView.findViewById<TextView>(R.id.tv_content).text = content.content ?: "暂无"
            itemView.findViewById<ImageButton>(R.id.btn_copy).setOnClickListener {
                copyToClipboard(content.content ?: "", content.label)
            }
            binding.contentContainer.addView(itemView)
        }
    }
}
