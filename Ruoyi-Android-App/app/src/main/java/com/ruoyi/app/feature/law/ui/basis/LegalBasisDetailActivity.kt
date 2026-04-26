package com.ruoyi.app.feature.law.ui.basis

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ruoyi.app.databinding.ActivityLegalBasisDetailBinding
import com.ruoyi.app.feature.law.db.entity.LegalBasisEntity
import com.ruoyi.app.feature.law.repository.LawRepository
import kotlinx.coroutines.launch

class LegalBasisDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLegalBasisDetailBinding
    private lateinit var repository: LawRepository
    private var basisId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLegalBasisDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = LawRepository(this)
        basisId = intent.getLongExtra("basis_id", 0)
        if (basisId > 0) {
            loadDetail()
        }
        setupCopyButton()
    }

    private fun setupCopyButton() {
        binding.btnCopyClauses.setOnClickListener {
            val clauses = binding.tvClauses.text.toString()
            copyToClipboard(clauses)
            Toast.makeText(this, "条款内容已复制", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("条款内容", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun loadDetail() {
        lifecycleScope.launch {
            val legalBasis = repository.getLegalBasisById(basisId)
            legalBasis?.let { displayDetail(it) }
        }
    }

    private fun displayDetail(legalBasis: LegalBasisEntity) {
        binding.tvTitle.text = legalBasis.title
        binding.tvBasisNo.text = "编号: ${legalBasis.basisNo ?: "无"}"
        binding.tvViolationType.text = "违法类型: ${legalBasis.violationType ?: "无"}"
        binding.tvIssuingAuthority.text = "颁发机构: ${legalBasis.issuingAuthority ?: "未知"}"
        binding.tvEffectiveDate.text = "实施时间: ${legalBasis.effectiveDate ?: "未知"}"
        binding.tvLegalLevel.text = "效级: ${legalBasis.legalLevel ?: "无"}"
        binding.tvClauses.text = legalBasis.clauses ?: "暂无条款内容"
        binding.tvLegalLiability.text = legalBasis.legalLiability ?: "暂无法律责任"
        binding.tvDiscretionStandard.text = legalBasis.discretionStandard ?: "暂无裁量标准"
    }
}
