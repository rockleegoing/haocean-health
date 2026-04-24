package com.ruoyi.app.feature.addunit

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.toast.ToastUtils
import com.ruoyi.app.R
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.IndustryCategoryEntity
import com.ruoyi.app.data.database.entity.UnitEntity
import com.ruoyi.app.databinding.ActivityAddUnitBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.clickDelay
import com.therouter.TheRouter
import com.therouter.router.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Route(path = Constant.addUnitRoute)
class AddUnitActivity : BaseBindingActivity<ActivityAddUnitBinding>() {

    private var selectedCategoryId: Long? = null
    private var selectedCategoryName: String? = null
    private var selectedBirthday: Long? = null
    private var categories: List<IndustryCategoryEntity> = emptyList()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, AddUnitActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun initView() {
        setupTitleBar()

        // 行业分类选择点击事件
        binding.tvIndustryCategory.clickDelay {
            showCategoryDialog()
        }

        // 性别单选
        binding.radioMale.clickDelay {
            binding.radioMale.isChecked = true
            binding.radioFemale.isChecked = false
        }
        binding.radioFemale.clickDelay {
            binding.radioMale.isChecked = false
            binding.radioFemale.isChecked = true
        }

        // 出生年月选择
        binding.tvBirthday.clickDelay {
            showDatePicker()
        }

        // 保存按钮点击事件
        binding.btnSave.clickDelay {
            if (validateForm()) {
                saveUnit()
            }
        }
    }

    override fun initData() {
        loadCategories()
    }

    private fun setupTitleBar() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
    }

    private fun loadCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            categories = AppDatabase.getInstance(applicationContext)
                .industryCategoryDao()
                .getAllCategories()
                .filter { it.status == "0" && it.delFlag == "0" }
        }
    }

    private fun showCategoryDialog() {
        if (categories.isEmpty()) {
            ToastUtils.show("请先同步行业分类数据")
            return
        }

        val items = categories.map { it.categoryName }.toTypedArray()
        var selectedIndex = -1

        AlertDialog.Builder(this)
            .setTitle("选择行业分类")
            .setSingleChoiceItems(items, selectedIndex) { dialog, which ->
                selectedIndex = which
            }
            .setPositiveButton("确定") { dialog, _ ->
                if (selectedIndex >= 0) {
                    val category = categories[selectedIndex]
                    selectedCategoryId = category.categoryId
                    selectedCategoryName = category.categoryName
                    binding.tvIndustryCategory.text = category.categoryName
                    binding.tvIndustryCategory.setTextColor(getColor(R.color.black))
                }
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        selectedBirthday?.let {
            calendar.timeInMillis = it
        }

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedBirthday = calendar.timeInMillis
                binding.tvBirthday.text = dateFormat.format(calendar.time)
                binding.tvBirthday.setTextColor(getColor(R.color.black))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun validateForm(): Boolean {
        // 行业分类必填
        if (selectedCategoryId == null) {
            ToastUtils.show("请选择行业分类")
            return false
        }

        // 单位名称必填
        val unitName = binding.etUnitName.text.toString().trim()
        if (TextUtils.isEmpty(unitName)) {
            binding.etUnitName.requestFocus()
            ToastUtils.show("请输入单位名称")
            return false
        }

        // 当事人必填
        val personName = binding.etPersonName.text.toString().trim()
        if (TextUtils.isEmpty(personName)) {
            binding.etPersonName.requestFocus()
            ToastUtils.show("请输入当事人姓名")
            return false
        }

        // 联系方式必填
        val contactPhone = binding.etContactPhone.text.toString().trim()
        if (TextUtils.isEmpty(contactPhone)) {
            binding.etContactPhone.requestFocus()
            ToastUtils.show("请输入联系方式")
            return false
        }

        return true
    }

    private fun saveUnit() {
        val unit = buildUnitEntity()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                AppDatabase.getInstance(applicationContext)
                    .unitDao()
                    .insertUnit(unit)

                withContext(Dispatchers.Main) {
                    ToastUtils.show("保存成功")
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    ToastUtils.show("保存失败：${e.message}")
                }
            }
        }
    }

    private fun buildUnitEntity(): UnitEntity {
        val gender = if (binding.radioMale.isChecked) "0" else "1"
        val businessArea = binding.etBusinessArea.text.toString().toDoubleOrNull()

        return UnitEntity(
            unitId = -System.currentTimeMillis(),  // 本地生成负数ID
            unitName = binding.etUnitName.text.toString().trim(),
            industryCategoryId = selectedCategoryId,
            industryCategoryName = selectedCategoryName,
            region = null,
            supervisionType = null,
            creditCode = binding.etCreditCode.text.toString().trim().ifEmpty { null },
            legalPerson = binding.etLegalPerson.text.toString().trim().ifEmpty { null },
            contactPhone = binding.etContactPhone.text.toString().trim(),
            businessAddress = binding.etBusinessAddress.text.toString().trim().ifEmpty { null },
            latitude = null,
            longitude = null,
            status = "0",
            delFlag = "0",
            createTime = System.currentTimeMillis(),
            updateTime = null,
            remark = null,
            personName = binding.etPersonName.text.toString().trim(),
            registrationAddress = binding.etRegistrationAddress.text.toString().trim().ifEmpty { null },
            businessArea = businessArea,
            licenseName = binding.etLicenseName.text.toString().trim().ifEmpty { null },
            licenseNo = binding.etLicenseNo.text.toString().trim().ifEmpty { null },
            gender = gender,
            nation = binding.etNation.text.toString().trim().ifEmpty { null },
            post = binding.etPost.text.toString().trim().ifEmpty { null },
            idCard = binding.etIdCard.text.toString().trim().ifEmpty { null },
            birthday = selectedBirthday,
            homeAddress = binding.etHomeAddress.text.toString().trim().ifEmpty { null },
            syncStatus = "PENDING"
        )
    }
}
