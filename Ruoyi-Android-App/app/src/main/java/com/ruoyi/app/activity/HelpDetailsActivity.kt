package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.databinding.ActivityHelpDetailsBinding
import com.ruoyi.app.model.entity.QuestionEntity
import com.ruoyi.code.base.BaseBindingActivity

class HelpDetailsActivity : BaseBindingActivity<ActivityHelpDetailsBinding>() {

    companion object {

        fun startActivity(context: Context, item: QuestionEntity) {
            val intent = Intent(context, HelpDetailsActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.putExtra("data", item)
            context.startActivity(intent)
        }

    }

    override fun initView() {
        val item = intent.getParcelableExtra<QuestionEntity>("data")

        binding.titlebar.title = item?.question
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })

        binding.question.text = item?.question
        binding.questionContent.text = item?.questionContent
    }

    override fun initData() {

    }
}