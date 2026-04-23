package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
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
            // 使用 Post 请求验证激活码
            // TODO: 等待后端 API 实现后完善
            val params = mapOf("activationCode" to activationCode)
            val result = Post<ResultEntity<ActivationCodeValidationResponse>>(ConfigApi.baseUrl + "/device/activationCode/validate") {
                body = OKHttpUtils.getRequestBody(params)
            }.await()

            if (result.code == 200 && result.data != null) {
                com.hjq.toast.ToastUtils.show("激活码验证成功")
                // TODO: 执行设备注册逻辑
                finish()
            } else {
                com.hjq.toast.ToastUtils.show(result.msg ?: "激活码验证失败")
            }
        }.catch {
            com.hjq.toast.ToastUtils.show(it.message)
        }
    }

    @Serializable
    data class ActivationCodeValidationResponse(
        val activationCodeId: Long,
        val deviceCount: Int,
        val maxDeviceCount: Int,
        val expiryTime: Long
    )
}
