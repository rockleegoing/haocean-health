package com.ruoyi.app.activity.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.R
import com.ruoyi.app.databinding.ActivityChangeLanguageBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.code.Frame
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.ActivityManager
import com.ruoyi.code.utils.LocaleHelper
import com.ruoyi.code.utils.clickDelay
import com.therouter.TheRouter


class ChangeLanguageActivity : BaseBindingActivity<ActivityChangeLanguageBinding>() {

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, ChangeLanguageActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private var languageStr = "en"

    override fun initView() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }

            override fun onRightClick(titleBar: TitleBar?) {
                val context = LocaleHelper.setLocale(this@ChangeLanguageActivity, languageStr)
                Frame.initialize(context)
                recreate()
                ActivityManager.instance.clearActivity()
                TheRouter.build(Constant.mainRoute)
                    .navigation()
            }
        })
    }

    override fun initData() {
        val drawable = ContextCompat.getDrawable(this, R.mipmap.icon_select)
        binding.ivChina.setImageDrawable(null)
        binding.ivEnglish.setImageDrawable(null)
        val language = LocaleHelper.getPersistedData(this, "zh")

        if (TextUtils.equals(language, "zh")) {
            binding.ivChina.setImageDrawable(drawable)
        }
        if (TextUtils.equals(language, "en")) {
            binding.ivEnglish.setImageDrawable(drawable)
        }

        binding.llChina.clickDelay {
            binding.ivChina.setImageDrawable(drawable)
            binding.ivEnglish.setImageDrawable(null)
            languageStr = "zh"
        }

        binding.llEnglish.clickDelay {
            binding.ivChina.setImageDrawable(null)
            binding.ivEnglish.setImageDrawable(drawable)
            languageStr = "en"
        }
    }

}