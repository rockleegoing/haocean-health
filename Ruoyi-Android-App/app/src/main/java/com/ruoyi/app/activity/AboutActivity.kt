package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.databinding.ActivityAboutBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.clickDelay
import com.xuexiang.xupdate.utils.UpdateUtils

class AboutActivity : BaseBindingActivity<ActivityAboutBinding>() {

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, AboutActivity::class.java)
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

        binding.llSite.clickDelay {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(binding.tvSite.text.toString())
            startActivity(intent)
        }
        binding.tvVersionName.text = UpdateUtils.getVersionName(this@AboutActivity)
        binding.tvEmail.text = Constant.site_email
        binding.tvPhone.text = Constant.site_phone
        binding.tvSite.text = Constant.site_url
        binding.tvCopyRight.text = Constant.copy_right

    }

    override fun initData() {

    }
}