package com.ruoyi.app.fragment

import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.ruoyi.app.R
import com.ruoyi.app.adapter.NoticeAdapter
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.databinding.FragmentNoticeBinding
import com.ruoyi.app.model.entity.NoticeEntity
import com.ruoyi.code.base.BaseBindingFragment
import com.ruoyi.code.utils.clickDelay
import com.ruoyi.code.witget.toast

class NoticeFragment : BaseBindingFragment<FragmentNoticeBinding>() {

    companion object {
        @JvmStatic
        fun newInstance(noticeType: String) = NoticeFragment().apply {
            arguments = Bundle().apply {
                putString("noticeType", noticeType)
            }
        }
    }

    // 起始页码
    var pageNum: Int = 1

    // 消息类型 1通知 2公告
    var noticeType: String? = "1"

    private val mAdapter by lazy {
        NoticeAdapter()
    }

    override fun initView() {
        arguments?.let {
            noticeType = it.getString("noticeType")
        }
        binding.swipeLayout.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.red
            )
        )

        binding.swipeLayout.setOnRefreshListener {
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                pageNum = 1
                getNoticeList(pageNum, noticeType)
            }, 1000)
        }

        binding.recyclerview.addOnScrollListener(rvListScrollListener);

        binding.fabTop.clickDelay {
            /**
             * 滑动到顶部
             */
            binding.recyclerview.smoothScrollToPosition(0)
        };
        binding.recyclerview.adapter = mAdapter
        setLoadMoreAdapter(mAdapter)
    }

    override fun initData() {
        getNoticeList(pageNum, noticeType)
    }
    //加载更多适配
    private fun <T> setLoadMoreAdapter(mAdapter: BaseQuickAdapter<T, BaseViewHolder>) {
        //设置加载更多
        mAdapter.loadMoreModule.setOnLoadMoreListener {
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                pageNum++
                getNoticeList(pageNum, noticeType)
            }, 1000)
        }

        //设置是否自动加载更多
        mAdapter.loadMoreModule.isAutoLoadMore = true
        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = true
    }

    private fun getNoticeList(pageNum: Int, noticeType: String?) {
        scopeNetLife {
            val body =
                Get<NoticeEntity>(
                    ConfigApi.noticeList + "?pageSize=20&pageSize=" + pageNum + "&noticeType=" + noticeType
                ).await()
            if (body.code == 200) {
                val list = body.rows
                if (pageNum == 1) {
                    mAdapter.setList(list)
                    binding.swipeLayout.isRefreshing = false
                } else {
                    mAdapter.addData(list)
                    if (list.size < 20) {
                        //如果s少于20,显示没有更多数据布局
                        mAdapter.loadMoreModule.loadMoreEnd(false)
                    } else {
                        mAdapter.loadMoreModule.loadMoreComplete()
                    }
                }
            } else {
                toast(body.msg)
                mAdapter.loadMoreModule.loadMoreEnd(false)
            }
        }.catch {
            toast(it.message)
        }
    }

    /**
     * 列表监听是否显示 滑动到顶部按钮
     */
    private val rvListScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 判断第一条item是否可见，如果不可见则显示回顶部按钮
                    if (recyclerView.layoutManager!!.findViewByPosition(0) != null) {
                        if (binding.fabTop.getVisibility() === View.VISIBLE) {
                            binding.fabTop.setVisibility(View.INVISIBLE) // 设置滑动顶部按钮不可见
                        }
                    } else {
                        if (binding.fabTop.getVisibility() === View.INVISIBLE) {
                            binding.fabTop.setVisibility(View.VISIBLE) // 设置滑动顶部按钮可见
                        }
                    }
                }
            }
        }
}