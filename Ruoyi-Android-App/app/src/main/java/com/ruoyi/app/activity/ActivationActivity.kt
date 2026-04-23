package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.api.repository.ActivationRepository
import com.ruoyi.app.databinding.ActivityActivationBinding
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
            if (activationCode.isNotEmpty()) {
                validateActivationCode(activationCode)
            } else {
                com.hjq.toast.ToastUtils.show("请输入激活码")
            }
        }
    }

    override fun initData() {

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
                com.hjq.toast.ToastUtils.show("激活码验证成功")
                // 跳转登录页
                LoginActivity.startActivity(this@ActivationActivity)
                finish()
            }.onFailure { exception ->
                com.hjq.toast.ToastUtils.show(exception.message ?: "激活码验证失败")
            }
        }.catch {
            com.hjq.toast.ToastUtils.show(it.message)
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
