package com.ruoyi.app.activity.work

import androidx.fragment.app.Fragment
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.R
import com.ruoyi.app.adapter.FragmentPagerAdapter
import com.ruoyi.app.databinding.ActivityMessageBinding
import com.ruoyi.app.fragment.NoticeFragment
import com.ruoyi.app.model.Constant
import com.ruoyi.code.Frame
import com.ruoyi.code.base.BaseBindingActivity
import com.therouter.router.Route

/**
 *  通知&公告
 */
@Route(path = Constant.messagesRoute)
class MessageActivity : BaseBindingActivity<ActivityMessageBinding>() {

    private var pagerAdapter: FragmentPagerAdapter<Fragment>? = null

    override fun initView() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
        pagerAdapter = FragmentPagerAdapter<Fragment>(this).apply {
            //1、通知 2、公告
            addFragment(NoticeFragment.newInstance("1"), Frame.getString(R.string.notice))
            addFragment(NoticeFragment.newInstance("2"), Frame.getString(R.string.announcement))
            binding.viewPager.adapter = this
        }
        binding.tablayout.setupWithViewPager(binding.viewPager)
    }

    override fun initData() {

    }
}