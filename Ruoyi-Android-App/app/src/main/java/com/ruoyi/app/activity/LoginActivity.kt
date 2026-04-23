package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ruoyi.app.R
import com.ruoyi.app.activity.common.WebActivity
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.databinding.ActivityLoginBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.UserViewModel
import com.ruoyi.code.Frame
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.base.activityViewModels
import com.ruoyi.code.utils.clickDelay
import com.ruoyi.code.witget.toast
import com.tencent.mmkv.MMKV
import com.therouter.TheRouter
import com.therouter.router.Route
import com.xuexiang.xupdate.XUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Route(path = Constant.loginRoute)
class LoginActivity : BaseBindingActivity<ActivityLoginBinding>() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val viewModel: UserViewModel by activityViewModels()

    override fun initView() {
        binding.btnLogin.clickDelay {
            viewModel.login(
                this,
                binding.etAccount.text.toString().trim(),
                binding.etPassword.text.toString().trim(),
                binding.etCode.text.toString().trim()
            )
        }

        binding.logoCodeImage.clickDelay {
            viewModel.getVerificationCode()
        }

        binding.tvProtocol.clickDelay {
            WebActivity.startActivity(this, Frame.getString(R.string.login_agreement), ConfigApi.blogUrl + "/app/agreement.html")
        }

        binding.tvPrivacy.clickDelay {
            WebActivity.startActivity(this, Frame.getString(R.string.login_privacy), ConfigApi.blogUrl + "/app/privacy.html")
        }

        binding.tvRegister.clickDelay {
            RegisterActivity.startActivity(this@LoginActivity)
        }

        // 预加载 /getInfo 数据（后台异步，失败不阻塞）
        preloadLoginData()

        val token = MMKV.defaultMMKV().decodeString("token")
        if (!token.isNullOrEmpty()) {
            TheRouter.build(Constant.mainRoute)
                .navigation()
            finish()
            return
        } else {
            XUpdate.newBuild(this)
                .promptThemeColor(ContextCompat.getColor(this, R.color.red))
                .updateUrl(ConfigApi.uploadApp)
                .update()
        }

    }

    /**
     * 预加载登录验证数据
     * 后台异步执行，失败不阻塞用户操作
     */
    private fun preloadLoginData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 初始化 Room 数据库
                AppDatabase.getInstance(this@LoginActivity)
                // 预加载 /getInfo 数据（后台异步）
                viewModel.preloadLoginData()
            } catch (e: Exception) {
                // 预加载失败，不阻塞用户操作
            }
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
            TheRouter.build(Constant.mainRoute).navigation()
            finish()
        }
        viewModel.isRegister.observe(this) {
            if (it) {
                binding.llRegister.visibility = View.VISIBLE
            } else {
                binding.llRegister.visibility = View.GONE
            }
        }
    }

}
