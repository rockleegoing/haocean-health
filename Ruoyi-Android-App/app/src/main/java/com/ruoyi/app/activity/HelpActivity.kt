package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.R
import com.ruoyi.app.adapter.HelpAdapter
import com.ruoyi.app.databinding.ActivityHelpBinding
import com.ruoyi.app.model.entity.QuestionEntity
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.LocaleHelper
import java.util.Objects

class HelpActivity : BaseBindingActivity<ActivityHelpBinding>(), OnItemClickListener {

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, HelpActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private val language by lazy {
        LocaleHelper.getPersistedData(this, "zh")
    }

    private val adapter: HelpAdapter by lazy {
        HelpAdapter(if (language == "en") itemEnData else itemData).apply {
            setOnItemClickListener(this@HelpActivity)
        }
    }

    private val itemData: ArrayList<QuestionEntity>
        get() = arrayListOf(
            QuestionEntity(headerTitle = "若依问题", imageResource = R.mipmap.icon_help_github),
            QuestionEntity("若依开源吗？", "开源", beginList = true),
            QuestionEntity("若依可以商用吗？", "可以"),
            QuestionEntity("若依官网地址多少？", "http://ruoyi.vip"),
            QuestionEntity("若依文档地址多少？", "http://doc.ruoyi.vip", endList = true),
            QuestionEntity(headerTitle = "其他问题", imageResource = R.mipmap.icon_help_question),
            QuestionEntity("如何退出登录？", "请点击[我的] - [应用设置] - [退出登录]即可退出登录", beginList = true),
            QuestionEntity("如何修改用户头像？", "请点击[我的] - [选择头像] - [点击提交]即可更换用户头像"),
            QuestionEntity("如何修改登录密码？", "请点击[我的] - [应用设置] - [修改密码]即可修改登录密码", endList = true),
        )

    private val itemEnData: ArrayList<QuestionEntity>
        get() = arrayListOf(
            QuestionEntity(headerTitle = "If according to the problem", imageResource = R.mipmap.icon_help_github),
            QuestionEntity("If we rely on open source？", "open source", beginList = true),
            QuestionEntity("Can it be used commercially？", "can"),
            QuestionEntity("What is the official website address？", "http://ruoyi.vip"),
            QuestionEntity("What is the document address？", "http://doc.ruoyi.vip", endList = true),
            QuestionEntity(headerTitle = "Other issues", imageResource = R.mipmap.icon_help_question),
            QuestionEntity("How to log out and log in？", "Please click on [My] - [App Settings] - [Log Out] to log out", beginList = true),
            QuestionEntity("How to modify user profile picture？", "Please click on [My] - [Select Avatar] - [Submit] to change the user avatar"),
            QuestionEntity("How to change login password？", "Please click on [My] - [Application Settings] - [Change Password] to change your login password", endList = true),
        )

    override fun initView() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
    }

    override fun initData() {
        binding.recyclerview.adapter = adapter
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val item = adapter.data[position] as QuestionEntity
        if (!item.isHeader) {
            HelpDetailsActivity.startActivity(this, item)
        }
    }
}