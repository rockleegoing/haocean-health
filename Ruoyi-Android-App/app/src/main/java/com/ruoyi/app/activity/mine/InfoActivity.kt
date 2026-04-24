package com.ruoyi.app.activity.mine

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.databinding.ActivityInfoBinding
import com.ruoyi.app.model.entity.UserResult
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.witget.toast

class InfoActivity : BaseBindingActivity<ActivityInfoBinding>() {

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, InfoActivity::class.java)
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
    }

    override fun initData() {
        scopeNetLife {
            val body = Get<UserResult>(ConfigApi.getProfile).await()
            if (body.code == ConfigApi.SUCCESS) {
                binding.tvInfoName.text = body.data.nickName
                binding.tvInfoMobile.text = body.data.phonenumber
                binding.tvInfoEmail.text = body.data.email
                binding.tvInfoCreate.text = body.data.createTime
                binding.tvInfoPost.text = body.postGroup
                binding.tvInfoRole.text = body.roleGroup
            } else {
                toast(body.msg)
            }
        }.catch {
            toast(it.message)
        }
    }

}
