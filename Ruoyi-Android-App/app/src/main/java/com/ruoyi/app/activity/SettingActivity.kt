package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import com.drake.net.Post
import com.drake.net.utils.scopeNetLife
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.toast.ToastUtils
import com.ruoyi.app.activity.setting.ChangeLanguageActivity
import com.ruoyi.app.activity.setting.DarkModeActivity
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.databinding.ActivitySettingBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.clickDelay
import com.ruoyi.code.witget.toast
import com.tencent.mmkv.MMKV
import com.therouter.router.Route
import com.xuexiang.xupdate.XUpdate

@Route(path = Constant.settingRoute)
class SettingActivity : BaseBindingActivity<ActivitySettingBinding>() {

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, SettingActivity::class.java)
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
        binding.llEditPwd.clickDelay {
            EditPwdActivity.startActivity(this)
        }
        binding.llClearCache.clickDelay {
            ToastUtils.show("模块建设中~")
        }
        binding.llUploadApp.clickDelay {
            XUpdate.newBuild(this)
                .updateUrl(ConfigApi.uploadApp)
                .update()
        }
        binding.llDeviceActivation.clickDelay {
            ActivationActivity.startActivity(this)
        }
        binding.tvLoginOut.clickDelay {
            scopeNetLife {
                Post<String>(ConfigApi.logout).await()
            }.catch {
                toast(it.message)
            }.finally {
                MMKV.defaultMMKV().remove("token")
                LoginActivity.startActivity(this@SettingActivity)
            }
        }
        binding.llChangeLanguage.clickDelay {
            ChangeLanguageActivity.startActivity(this@SettingActivity)
        }
        binding.llDarkMode.clickDelay {
            DarkModeActivity.startActivity(this@SettingActivity)
        }
    }

    override fun initData() {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        restartApplication()
    }

    // 在 Activity 类中添加方法
    private fun restartApplication() {
        val ctx = applicationContext
        val packageManager = ctx.packageManager
        val intent = packageManager.getLaunchIntentForPackage(ctx.packageName)
        val mainIntent = Intent.makeRestartActivityTask(intent?.component)
        ctx.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

}