package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.api.OKHttpUtils
import com.ruoyi.app.databinding.ActivityActivationBinding
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.clickDelay
import com.therouter.router.Route
import com.drake.net.Post
import com.drake.net.utils.scopeNetLife
import com.ruoyi.app.model.entity.ResultEntity
import com.tencent.mmkv.MMKV
import kotlinx.serialization.Serializable

@Route(path = "http://com.ruoyi/activation")
class ActivationActivity : BaseBindingActivity<ActivityActivationBinding>() {

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

            val params = mapOf(
                "codeValue" to activationCode,
                "deviceUuid" to deviceUuid,
                "deviceName" to deviceName,
                "deviceModel" to deviceModel,
                "deviceOs" to deviceOs,
                "appVersion" to appVersion
            )

            val result = Post<ResultEntity<ActivationCodeValidationResponse>>(ConfigApi.baseUrl + "/device/activationCode/validate") {
                body = OKHttpUtils.getRequestBody(params)
            }.await()

            if (result.code == 200 && result.data != null) {
                com.hjq.toast.ToastUtils.show("激活码验证成功")
                // 保存激活信息到本地数据库
                saveActivationInfo(result.data, activationCode)
                // 跳转登录页
                LoginActivity.startActivity(this@ActivationActivity)
                finish()
            } else {
                com.hjq.toast.ToastUtils.show(result.msg ?: "激活码验证失败")
            }
        }.catch {
            com.hjq.toast.ToastUtils.show(it.message)
        }
    }

    /**
     * 获取设备 UUID（唯一标识）
     */
    private fun getDeviceUuid(): String {
        // 优先从 MMKV 获取已保存的 UUID
        val savedUuid = MMKV.defaultMMKV().decodeString("device_uuid")
        if (!savedUuid.isNullOrEmpty()) {
            return savedUuid
        }
        // 生成新 UUID 并保存
        val uuid = java.util.UUID.randomUUID().toString()
        MMKV.defaultMMKV().encode("device_uuid", uuid)
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

    /**
     * 保存激活信息到本地数据库
     */
    private fun saveActivationInfo(response: ActivationCodeValidationResponse, activationCode: String) {
        // TODO: 后续实现 Room 数据库存储
        // 暂时保存激活码 ID 到 MMKV
        MMKV.defaultMMKV().encode("activation_code_id", response.activationCodeId)
        MMKV.defaultMMKV().encode("activation_code_value", activationCode)
        MMKV.defaultMMKV().encode("activation_expiry_time", response.expiryTime)
        MMKV.defaultMMKV().encode("is_activated", true)
    }

    @Serializable
    data class ActivationCodeValidationResponse(
        val activationCodeId: Long,      // 激活码 ID
        val deviceCount: Int = 1,        // 当前激活设备数
        val maxDeviceCount: Int = 1,     // 最大允许设备数
        val expiryTime: Long             // 过期时间戳
    )
}
