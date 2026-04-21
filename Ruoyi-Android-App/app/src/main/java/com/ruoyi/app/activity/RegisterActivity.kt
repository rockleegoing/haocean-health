package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import com.bumptech.glide.Glide
import com.ruoyi.app.databinding.ActivityRegisterBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.UserViewModel
import com.ruoyi.code.Frame
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.base.activityViewModels
import com.ruoyi.code.utils.clickDelay
import com.ruoyi.code.witget.toast
import com.therouter.TheRouter

class RegisterActivity : BaseBindingActivity<ActivityRegisterBinding>() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, RegisterActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val viewModel: UserViewModel by activityViewModels()

    override fun initView() {
        binding.tvLogin.clickDelay {
            finish()
        }
        binding.btnRegister.clickDelay {
            viewModel.register(
                this,
                binding.etCode.text.toString().trim(),
                binding.etAccount.text.toString().trim(),
                binding.etPassword.text.toString().trim(),
                binding.etRPassword.text.toString().trim(),
            )
        }
    }

    override fun initData() {
        viewModel.getVerificationCode()
        viewModel.errorMsg.observe(this) {
            toast(it)
        }
        viewModel.captchaEnabled.observe(this) {
            if (it) {
                binding.llCaptchaImage.visibility = View.VISIBLE
            } else {
                binding.llCaptchaImage.visibility = View.GONE
            }
        }
        viewModel.imageCode.observe(this) {
            Glide.with(Frame.getContext())
                .load("data:image/gif;base64,${it}")
                .into(binding.logoCodeImage)
        }
        viewModel.loginSuceess.observe(this) {
            TheRouter.build(Constant.mainRoute)
                .navigation()
            finish()
        }
    }

}