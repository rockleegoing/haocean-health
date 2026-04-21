package com.ruoyi.app.activity.work

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.drake.net.Get
import com.drake.net.utils.scopeNetLife
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.R
import com.ruoyi.app.adapter.OperationLogAdapter
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.databinding.ActivityLogsBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.entity.LogsEntity
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.clickDelay
import com.ruoyi.code.witget.toast
import com.therouter.router.Route


@Route(path = Constant.logsRoute)
class LogsActivity : BaseBindingActivity<ActivityLogsBinding>() {

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, LogsActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private val mAdapter by lazy {
        OperationLogAdapter()
    }

    // 起始页码
    var pageNum: Int = 1

    //每页加载数
    var pageSize: Int = 20

    override fun initView() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })

        binding.swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.red))

        binding.swipeLayout.setOnRefreshListener {
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                pageNum = 1
                getData()
            }, 1000)
        }

        binding.recyclerview.addOnScrollListener(rvListScrollListener);

        binding.fabTop.clickDelay {
            /**
             * 滑动到顶部
             */
            binding.recyclerview.smoothScrollToPosition(0)
        };

        getData()
    }

    override fun initData() {
        binding.recyclerview.adapter = mAdapter
        setLoadMoreAdapter(mAdapter)
    }

    //加载更多适配
    private fun <T> setLoadMoreAdapter(mAdapter: BaseQuickAdapter<T, BaseViewHolder>) {
        //设置加载更多
        mAdapter.loadMoreModule.setOnLoadMoreListener {
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                pageNum++
                getData()
            }, 1000)
        }

        //设置是否自动加载更多
        mAdapter.loadMoreModule.isAutoLoadMore = true
        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = true
    }

    private fun getData() {
        scopeNetLife {
            val body =
                Get<LogsEntity>(
                    ConfigApi.operlog + "?pageSize=" + pageSize
                            + "&isAsc=desc&orderByColumn=operTime&pageSize=" + pageNum
                ).await()
            if (body.code == 200) {
                val list = body.rows
                if (pageNum == 1) {
                    mAdapter.setList(list)
                    binding.swipeLayout.isRefreshing = false
                } else {
                    mAdapter.addData(list)
                    if (list.size < pageSize) {
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
    private val rvListScrollListener: OnScrollListener =
        object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == SCROLL_STATE_IDLE) {
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