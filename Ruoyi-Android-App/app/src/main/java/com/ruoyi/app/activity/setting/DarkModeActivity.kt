package com.ruoyi.app.activity.setting

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.toast.ToastUtils
import com.ruoyi.app.R
import com.ruoyi.app.databinding.ActivityDarkModeBinding
import com.ruoyi.code.Frame
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.clickDelay

class DarkModeActivity : BaseBindingActivity<ActivityDarkModeBinding>() {

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, DarkModeActivity::class.java)
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

            override fun onRightClick(titleBar: TitleBar?) {
                ToastUtils.show(Frame.getString(R.string.mine_construction))
            }
        })
    }

    override fun initData() {
        val drawable = ContextCompat.getDrawable(this, R.mipmap.icon_select)

        binding.llLight.clickDelay {
            binding.ivLight.setImageDrawable(drawable)
            binding.ivDark.setImageDrawable(null)
        }

        binding.llDark.clickDelay {
            binding.ivDark.setImageDrawable(drawable)
            binding.ivLight.setImageDrawable(null)
        }
    }

}