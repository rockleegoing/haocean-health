package com.ruoyi.app.feature.document.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.R
import com.ruoyi.app.databinding.ActivityDocumentFillBinding
import com.ruoyi.app.feature.document.api.DocumentApi
import com.ruoyi.app.feature.document.model.DocumentVariable
import com.ruoyi.app.feature.document.model.VariableType
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingActivity
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 文书变量填写表单 Activity
 */
@Route(path = Constant.documentFillRoute)
class DocumentFillActivity : BaseBindingActivity<ActivityDocumentFillBinding>() {

    private var templateId: Long = 0
    private var recordId: Long = 0
    private var templateName: String = ""
    private val variables = mutableListOf<DocumentVariable>()
    private val variableValues = mutableMapOf<String, String>()
    private val editTexts = mutableMapOf<String, EditText>()
    private val signatureVars = mutableListOf<DocumentVariable>()

    private val signatureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val signaturePath = result.data?.getStringExtra(SignatureActivity.EXTRA_SIGNATURE_PATH)
            val varName = result.data?.getStringExtra(SignatureActivity.EXTRA_VARIABLE_NAME)
            if (signaturePath != null && varName != null) {
                variableValues[varName] = signaturePath
                editTexts[varName]?.setText("已签名")
            }
        }
    }

    override fun initView() {
        setupTitleBar()
    }

    override fun initData() {
        templateId = intent.getLongExtra(DocumentListActivity.EXTRA_TEMPLATE_ID, 0)
        recordId = intent.getLongExtra(DocumentListActivity.EXTRA_RECORD_ID, 0)
        templateName = intent.getStringExtra(DocumentListActivity.EXTRA_TEMPLATE_NAME) ?: "文书填写"

        if (templateId == 0L) {
            Toast.makeText(this, "模板不存在", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.titlebar.title = templateName
        loadTemplateDetail()
    }

    private fun setupTitleBar() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
    }

    private fun loadTemplateDetail() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val (template, variableList) = DocumentApi.getTemplateDetail(templateId)
                binding.progressBar.visibility = View.GONE

                if (template != null) {
                    variables.clear()
                    variables.addAll(variableList)
                    variableValues.clear()
                    editTexts.clear()
                    signatureVars.clear()

                    buildFormUI()
                } else {
                    Toast.makeText(this@DocumentFillActivity, "加载模板失败", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@DocumentFillActivity, "加载失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buildFormUI() {
        binding.formContainer.removeAllViews()

        for (variable in variables.sortedBy { it.sortOrder }) {
            when (variable.variableType) {
                VariableType.TEXT -> addTextInput(variable)
                VariableType.DATE -> addDateInput(variable)
                VariableType.SELECT -> addSelectInput(variable)
                VariableType.SIGNATURE -> addSignatureInput(variable)
                else -> addTextInput(variable)
            }
        }

        // 添加提交按钮
        addSubmitButton()
    }

    private fun addTextInput(variable: DocumentVariable) {
        val container = createVariableContainer(variable)

        val editText = EditText(this).apply {
            tag = variable.variableName
            hint = variable.variableLabel ?: variable.variableName
            variable.defaultValue?.let { setText(it) }
            variable.maxLength?.let {
                filters = arrayOf(android.text.InputFilter.LengthFilter(it))
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            }
        }
        editTexts[variable.variableName] = editText
        container.addView(editText)
        binding.formContainer.addView(container)
    }

    private fun addDateInput(variable: DocumentVariable) {
        val container = createVariableContainer(variable)

        val editText = EditText(this).apply {
            tag = variable.variableName
            hint = variable.variableLabel ?: variable.variableName
            isFocusable = false
            isClickable = true
            variable.defaultValue?.let { setText(it) }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            }
            setOnClickListener {
                showDatePicker(this, variable)
            }
        }
        editTexts[variable.variableName] = editText
        container.addView(editText)
        binding.formContainer.addView(container)
    }

    private fun addSelectInput(variable: DocumentVariable) {
        val container = createVariableContainer(variable)

        val spinner = Spinner(this).apply {
            tag = variable.variableName
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            }
        }

        val options = parseOptions(variable.options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        spinner.adapter = adapter

        container.addView(spinner)
        binding.formContainer.addView(container)
    }

    private fun addSignatureInput(variable: DocumentVariable) {
        signatureVars.add(variable)
        val container = createVariableContainer(variable)

        val editText = EditText(this).apply {
            tag = variable.variableName
            hint = "点击签名"
            isFocusable = false
            isClickable = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            }
            setOnClickListener {
                navigateToSignature(variable)
            }
        }
        editTexts[variable.variableName] = editText
        container.addView(editText)
        binding.formContainer.addView(container)
    }

    private fun createVariableContainer(variable: DocumentVariable): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
            addView(TextView(context).apply {
                text = if (variable.required == "1") "${variable.variableLabel ?: variable.variableName} *" else (variable.variableLabel ?: variable.variableName)
                setTextColor(context.getColor(R.color.text_primary))
                textSize = 14f
            })
        }
    }

    private fun addSubmitButton() {
        val button = com.google.android.material.button.MaterialButton(this).apply {
            text = "生成文书"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 32
            }
            setOnClickListener {
                if (validateForm()) {
                    submitForm()
                }
            }
        }
        binding.formContainer.addView(button)
    }

    private fun showDatePicker(editText: EditText, variable: DocumentVariable) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val date = String.format(Locale.CHINA, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                editText.setText(date)
                variableValues[variable.variableName] = date
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun navigateToSignature(variable: DocumentVariable) {
        val bundle = Bundle().apply {
            putString(SignatureActivity.EXTRA_VARIABLE_NAME, variable.variableName)
        }
        signatureLauncher.launch(
            TheRouter.build(Constant.signatureRoute).with(bundle).navigation(this@DocumentFillActivity)
        )
    }

    private fun parseOptions(optionsJson: String?): List<String> {
        if (optionsJson.isNullOrEmpty()) return emptyList()
        return try {
            val jsonArray = org.json.JSONArray(optionsJson)
            val options = mutableListOf<String>()
            for (i in 0 until jsonArray.length()) {
                options.add(jsonArray.getString(i))
            }
            options
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun validateForm(): Boolean {
        for (variable in variables) {
            if (variable.required == "1") {
                val value = when (variable.variableType) {
                    VariableType.SELECT -> {
                        val spinner = binding.formContainer.findViewWithTag<Spinner>(variable.variableName)
                        spinner?.selectedItem?.toString()
                    }
                    else -> editTexts[variable.variableName]?.text.toString()
                }

                if (value.isNullOrBlank()) {
                    Toast.makeText(this, "${variable.variableLabel ?: variable.variableName} 为必填项", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
        }
        return true
    }

    private fun submitForm() {
        // 收集所有变量值
        for (variable in variables) {
            val value = when (variable.variableType) {
                VariableType.SELECT -> {
                    val spinner = binding.formContainer.findViewWithTag<Spinner>(variable.variableName)
                    spinner?.selectedItem?.toString() ?: ""
                }
                else -> editTexts[variable.variableName]?.text.toString() ?: ""
            }
            variableValues[variable.variableName] = value
        }

        // TODO: 调用 API 提交文书
        Toast.makeText(this, "文书生成成功", Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }

    companion object {
        const val EXTRA_VARIABLE_NAME = "variableName"
    }
}
