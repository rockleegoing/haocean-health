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
import com.ruoyi.app.databinding.ActivityLegalBasisDetailBinding
import com.ruoyi.app.feature.law.db.entity.LegalBasisContentEntity
import com.ruoyi.app.feature.law.db.entity.LegalBasisEntity
import com.ruoyi.app.feature.law.repository.LawRepository
import com.ruoyi.app.R
import com.ruoyi.app.model.Constant
import com.therouter.router.Route
import kotlinx.coroutines.launch

@Route(path = Constant.legalBasisDetailRoute)
class LegalBasisDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLegalBasisDetailBinding
    private lateinit var repository: LawRepository
    private var basisId: Long = 0

    companion object {
        const val EXTRA_BASIS_ID = "basis_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLegalBasisDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = LawRepository(this)
        basisId = extractBasisIdFromIntent()
        android.util.Log.d("LegalBasisDetail", "onCreate: basisId=$basisId, extras keys=${intent.extras?.keySet()}")
        if (basisId > 0) {
            loadDetail()
        } else {
            android.util.Log.e("LegalBasisDetail", "onCreate: basisId is 0 - extras=${intent.extras?.keySet()}")
        }
        setupToolbar()
    }

    private fun extractBasisIdFromIntent(): Long {
        val extras = intent.extras
        android.util.Log.d("LegalBasisDetail", "extractBasisIdFromIntent: extras=$extras")
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
                android.util.Log.d("LegalBasisDetail", "extractBasisIdFromIntent: found therouter_bundle=$routerBundle")
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
            val legalBasis = repository.getLegalBasisById(basisId)
            legalBasis?.let { displayDetail(it) }

            // 从内容表加载内容
            repository.getLegalBasisContents(basisId).collect { contents ->
                displayContents(contents)
            }
        }
    }

    private fun displayDetail(legalBasis: LegalBasisEntity) {
        binding.tvTitle.text = legalBasis.title
    }

    private fun displayContents(contents: List<LegalBasisContentEntity>) {
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
