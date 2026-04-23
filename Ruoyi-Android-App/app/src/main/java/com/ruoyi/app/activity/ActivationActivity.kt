package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.R
import com.ruoyi.app.api.repository.ActivationRepository
import com.ruoyi.app.databinding.ActivityActivationBinding
import com.ruoyi.app.model.ActivationUiState
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.clickDelay
import com.therouter.router.Route
import com.drake.net.utils.scopeNetLife

@Route(path = "http://com.ruoyi/activation")
class ActivationActivity : BaseBindingActivity<ActivityActivationBinding>() {

    private val activationRepository: ActivationRepository by lazy {
        ActivationRepository(applicationContext)
    }

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("ruoyi_app", Context.MODE_PRIVATE)
    }

    private var currentUiState: ActivationUiState = ActivationUiState.Idle
        set(value) {
            field = value
            updateUiState(value)
        }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, ActivationActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun initView() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })

        binding.btnActivate.clickDelay {
            val activationCode = binding.etActivationCode.text.toString().trim()

            // 简化验证：仅非空判断
            if (activationCode.isEmpty()) {
                currentUiState = ActivationUiState.Error("请输入激活码")
                return@clickDelay
            }

            // 进入加载状态
            currentUiState = ActivationUiState.Loading

            // 调用 Repository 验证
            validateActivationCode(activationCode)
        }
    }

    override fun initData() {

    }

    private fun updateUiState(state: ActivationUiState) {
        when (state) {
            is ActivationUiState.Idle -> {
                binding.btnActivate.isEnabled = true
                binding.tvStatus.visibility = View.GONE
            }
            is ActivationUiState.Loading -> {
                binding.btnActivate.isEnabled = false
                binding.tvStatus.visibility = View.GONE
                com.hjq.toast.ToastUtils.show("正在验证...")
            }
            is ActivationUiState.Success -> {
                binding.tvStatus.text = state.message
                binding.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.success_green))
                binding.tvStatus.visibility = View.VISIBLE
                // 2 秒后跳转登录页
                binding.root.postDelayed({
                    LoginActivity.startActivity(this@ActivationActivity)
                    finish()
                }, 2000)
            }
            is ActivationUiState.Error -> {
                binding.tvStatus.text = state.message
                binding.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.error_red))
                binding.tvStatus.visibility = View.VISIBLE
                binding.btnActivate.isEnabled = true
            }
        }
    }

    private fun validateActivationCode(activationCode: String) {
        scopeNetLife {
            // 获取设备信息
            val deviceUuid = getDeviceUuid()
            val deviceName = Build.MODEL
            val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
            val deviceOs = "Android ${Build.VERSION.RELEASE}"
            val appVersion = getAppVersionName()

            // 使用 ActivationRepository 验证并保存
            val result = activationRepository.validateAndSave(
                codeValue = activationCode,
                deviceUuid = deviceUuid,
                deviceName = deviceName,
                deviceModel = deviceModel,
                deviceOs = deviceOs,
                appVersion = appVersion
            )

            result.onSuccess { response ->
                currentUiState = ActivationUiState.Success("激活成功，即将跳转登录页")
            }.onFailure { exception ->
                currentUiState = ActivationUiState.Error(exception.message ?: "激活码验证失败")
            }
        }.catch {
            currentUiState = ActivationUiState.Error(it.message ?: "激活失败")
        }
    }

    /**
     * 获取设备 UUID（唯一标识）
     */
    private fun getDeviceUuid(): String {
        // 优先从 SharedPreferences 获取已保存的 UUID
        val savedUuid = sharedPreferences.getString("device_uuid", null)
        if (!savedUuid.isNullOrEmpty()) {
            return savedUuid
        }
        // 生成新 UUID 并保存
        val uuid = java.util.UUID.randomUUID().toString()
        sharedPreferences.edit().putString("device_uuid", uuid).apply()
        return uuid
    }

    /**
     * 获取 App 版本号
     */
    private fun getAppVersionName(): String {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }
}
