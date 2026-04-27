package com.ruoyi.app.feature.law.ui.basis

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ruoyi.app.databinding.ActivityProcessingBasisDetailBinding
import com.ruoyi.app.feature.law.db.entity.ProcessingBasisEntity
import com.ruoyi.app.feature.law.repository.LawRepository
import com.therouter.router.Route
import kotlinx.coroutines.launch

@Route(path = "/law/processingBasis/detail")
class ProcessingBasisDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProcessingBasisDetailBinding
    private lateinit var repository: LawRepository
    private var basisId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessingBasisDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = LawRepository(this)
        basisId = intent.getLongExtra("basis_id", 0)

        setupToolbar()
        setupCopyButtons()

        if (basisId > 0) {
            loadDetail()
        } else {
            finish()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupCopyButtons() {
        binding.btnCopyBasisNo.setOnClickListener {
            copyToClipboard(binding.tvBasisNo.text.toString())
        }
        binding.btnCopyViolationType.setOnClickListener {
            copyToClipboard(binding.tvViolationType.text.toString())
        }
        binding.btnCopyIssuingAuthority.setOnClickListener {
            copyToClipboard(binding.tvIssuingAuthority.text.toString())
        }
        binding.btnCopyEffectiveDate.setOnClickListener {
            copyToClipboard(binding.tvEffectiveDate.text.toString())
        }
        binding.btnCopyLegalLevel.setOnClickListener {
            copyToClipboard(binding.tvLegalLevel.text.toString())
        }
        binding.btnCopyClauses.setOnClickListener {
            copyToClipboard(binding.tvClauses.text.toString())
        }
        binding.btnCopyLegalLiability.setOnClickListener {
            copyToClipboard(binding.tvLegalLiability.text.toString())
        }
        binding.btnCopyDiscretionStandard.setOnClickListener {
            copyToClipboard(binding.tvDiscretionStandard.text.toString())
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("依据内容", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "已复制", Toast.LENGTH_SHORT).show()
    }

    private fun loadDetail() {
        lifecycleScope.launch {
            val processingBasis = repository.getProcessingBasisById(basisId)
            processingBasis?.let { displayDetail(it) }
        }
    }

    private fun displayDetail(processingBasis: ProcessingBasisEntity) {
        binding.tvTitle.text = processingBasis.title

        // 格式化编号：001 -> 1.
        val formattedBasisNo = formatBasisNo(processingBasis.basisNo)
        binding.tvSubtitle.text = if (formattedBasisNo.isNotEmpty()) "[$formattedBasisNo] ${processingBasis.violationType ?: ""} ${processingBasis.effectiveDate ?: ""}"

        binding.tvBasisNo.text = processingBasis.basisNo ?: "无"
        binding.tvViolationType.text = processingBasis.violationType ?: "无"
        binding.tvIssuingAuthority.text = processingBasis.issuingAuthority ?: "无"
        binding.tvEffectiveDate.text = processingBasis.effectiveDate ?: "无"
        binding.tvLegalLevel.text = processingBasis.legalLevel ?: "无"
        binding.tvClauses.text = processingBasis.clauses ?: "暂无条款内容"
        binding.tvLegalLiability.text = processingBasis.legalLiability ?: "暂无法律责任"
        binding.tvDiscretionStandard.text = processingBasis.discretionStandard ?: "暂无裁量标准"
    }

    private fun formatBasisNo(basisNo: String?): String {
        if (basisNo.isNullOrBlank()) return ""
        val num = basisNo.toLongOrNull() ?: return basisNo
        return "$num."
    }
}
