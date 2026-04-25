package com.ruoyi.app.feature.document.ui

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.databinding.ActivitySignatureBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.feature.document.ui.SignatureView
import com.ruoyi.code.base.BaseBindingActivity
import com.therouter.router.Route
import java.io.File
import java.io.FileOutputStream

/**
 * 电子签名 Activity
 */
@Route(path = Constant.signatureRoute)
class SignatureActivity : BaseBindingActivity<ActivitySignatureBinding>() {

    private var variableName: String = ""

    override fun initView() {
        setupTitleBar()
        setupButtons()
    }

    override fun initData() {
        variableName = intent.getStringExtra(DocumentFillActivity.EXTRA_VARIABLE_NAME) ?: ""
    }

    private fun setupTitleBar() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                setResult(RESULT_CANCELED)
                finish()
            }
        })
    }

    private fun setupButtons() {
        binding.btnClear.setOnClickListener {
            binding.signatureView.clear()
        }

        binding.btnConfirm.setOnClickListener {
            saveSignature()
        }
    }

    private fun saveSignature() {
        val bitmap = binding.signatureView.getSignatureBitmap()
        if (bitmap == null) {
            com.hjq.toast.ToastUtils.show("请先签名")
            return
        }

        try {
            val signatureDir = File(cacheDir, "signatures")
            if (!signatureDir.exists()) {
                signatureDir.mkdirs()
            }

            val signatureFile = File(signatureDir, "signature_${System.currentTimeMillis()}.png")
            FileOutputStream(signatureFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            val resultIntent = Intent().apply {
                putExtra(EXTRA_SIGNATURE_PATH, signatureFile.absolutePath)
                putExtra(EXTRA_VARIABLE_NAME, variableName)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        } catch (e: Exception) {
            com.hjq.toast.ToastUtils.show("保存签名失败: ${e.message}")
        }
    }

    companion object {
        const val EXTRA_SIGNATURE_PATH = "signaturePath"
        const val EXTRA_VARIABLE_NAME = "variableName"
    }
}
