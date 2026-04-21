package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.drake.net.Put
import com.drake.net.utils.scopeNetLife
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.toast.ToastUtils
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.api.OKHttpUtils
import com.ruoyi.app.databinding.ActivityEditPwdBinding
import com.ruoyi.app.model.entity.ResultEntity
import com.ruoyi.app.model.request.PwdRequest
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.clickDelay
import com.ruoyi.code.witget.toast

class EditPwdActivity : BaseBindingActivity<ActivityEditPwdBinding>() {

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, EditPwdActivity::class.java)
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

        binding.etOldPassword.addTextChangedListener {
            val txt = it.toString()
            if (!TextUtils.isEmpty(txt)){
                binding.ivClearOld.visibility = View.VISIBLE
            }else{
                binding.ivClearOld.visibility = View.INVISIBLE
            }
        }
        binding.ivClearOld.clickDelay {
            binding.etOldPassword.text.clear()
            binding.ivClearOld.visibility = View.INVISIBLE
        }

        binding.etRPassword.addTextChangedListener {
            val txt = it.toString()
            if (!TextUtils.isEmpty(txt)){
                binding.ivClearAgen.visibility = View.VISIBLE
            }else{
                binding.ivClearAgen.visibility = View.INVISIBLE
            }
        }

        binding.ivClearAgen.clickDelay {
            binding.etRPassword.text.clear()
            binding.ivClearAgen.visibility = View.INVISIBLE
        }

        binding.etOldPassword.addTextChangedListener {
            val txt = it.toString()
            if (!TextUtils.isEmpty(txt)){
                binding.ivClearOld.visibility = View.VISIBLE
            }else{
                binding.ivClearOld.visibility = View.INVISIBLE
            }
        }
        binding.ivClearOld.clickDelay {
            binding.etOldPassword.text.clear()
            binding.ivClearOld.visibility = View.INVISIBLE
        }

        binding.btnSubmit.clickDelay {
            val oldPwd = binding.etOldPassword.text.toString().trim()
            if (TextUtils.isEmpty(oldPwd)) {
                binding.etOldPassword.requestFocus()
                ToastUtils.show("旧密码不能为空")
                return@clickDelay
            }
            val newPwd = binding.etNewPassword.text.toString().trim()
            if (TextUtils.isEmpty(oldPwd)) {
                binding.etNewPassword.requestFocus()
                ToastUtils.show("新密码不能为空")
                return@clickDelay
            }
            val newRPwd = binding.etRPassword.text.toString().trim()
            if (TextUtils.isEmpty(oldPwd)) {
                binding.etRPassword.requestFocus()
                ToastUtils.show("确认密码不能为空")
                return@clickDelay
            }
            if (newRPwd != newPwd) {
                ToastUtils.show("两次密码不一致")
                return@clickDelay
            }

            upDataPwd(PwdRequest(newPwd, oldPwd))
        }
    }

    override fun initData() {

    }

    private fun upDataPwd(pwdRequest: PwdRequest) {
        scopeNetLife {
            val body = Put<ResultEntity<String>>(ConfigApi.updatePwd) {
                body = OKHttpUtils.getRequestBody(pwdRequest)
            }.await()
            toast(body.msg)
            if (body.isSuceess()) {
                finish()
            }
        }.catch {
            toast(it.message)
        }
    }

}