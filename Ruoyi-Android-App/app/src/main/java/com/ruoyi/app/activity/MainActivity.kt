package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.drake.net.utils.scopeNetLife
import com.ruoyi.app.R
import com.ruoyi.app.adapter.FragmentPagerAdapter
import com.ruoyi.app.adapter.NavigationAdapter
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.api.repository.DeviceRepository
import com.ruoyi.app.databinding.ActivityMainBinding
import com.ruoyi.app.fragment.HomeFragment
import com.ruoyi.app.fragment.LawFragment
import com.ruoyi.app.fragment.MineFragment
import com.ruoyi.app.fragment.PhraseFragment
import com.ruoyi.app.fragment.SupervisionFragment
import com.ruoyi.app.fragment.WorkFragment
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.UserViewModel
import com.ruoyi.app.model.entity.ButtomItemEntity
import com.ruoyi.app.sync.SyncWorker
import com.ruoyi.code.Frame
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.base.activityViewModels
import com.ruoyi.code.utils.SpUtils
import com.therouter.router.Route
import com.xuexiang.xupdate.XUpdate


@Route(path = Constant.mainRoute)
class MainActivity : BaseBindingActivity<ActivityMainBinding>(),
    NavigationAdapter.OnNavigationListener {

    companion object {

        private const val INTENT_KEY_IN_FRAGMENT_INDEX: String = "fragmentIndex"
        private const val INTENT_KEY_IN_FRAGMENT_CLASS: String = "fragmentClass"
        /** 心跳间隔：5 分钟 */
        private const val HEARTBEAT_INTERVAL_MS = 5 * 60 * 1000L

        @JvmOverloads
        fun startActivity(
            context: Context,
            fragmentClass: Class<out Fragment>? = HomeFragment::class.java
        ) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(INTENT_KEY_IN_FRAGMENT_CLASS, fragmentClass)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private var navigationAdapter: NavigationAdapter? = null
    private var pagerAdapter: FragmentPagerAdapter<Fragment>? = null

    /** 设备 UUID（从本地存储获取，登录后由后端下发） */
    private var deviceUuid by SpUtils("device_uuid", "")

    /** 主线程 Handler，用于定时心跳 */
    private val heartbeatHandler = Handler(Looper.getMainLooper())

    /** 心跳定时任务：每 5 分钟上报一次在线状态 */
    private val heartbeatRunnable = object : Runnable {
        override fun run() {
            val uuid = deviceUuid
            if (uuid.isNotEmpty()) {
                scopeNetLife {
                    DeviceRepository(this@MainActivity).sendHeartbeat(uuid, "1")
                }
            }
            heartbeatHandler.postDelayed(this, HEARTBEAT_INTERVAL_MS)
        }
    }

    private val list = ArrayList<ButtomItemEntity>().apply {
        add(
            ButtomItemEntity(
                Constant.theme_default_color,
                Constant.theme_select_color,
                Frame.getString(R.string.index_nav_index),
                "",
                "",
                Frame.getDrawable(R.drawable.index_selector)
            )
        )
        add(
            ButtomItemEntity(
                Constant.theme_default_color,
                Constant.theme_select_color,
                Frame.getString(R.string.work_nav_index),
                "",
                "",
                Frame.getDrawable(R.drawable.work_selector)
            )
        )
        add(
            ButtomItemEntity(
                Constant.theme_default_color,
                Constant.theme_select_color,
                Frame.getString(R.string.law_nav_index),
                "",
                "",
                Frame.getDrawable(R.drawable.law_selector)
            )
        )
        add(
            ButtomItemEntity(
                Constant.theme_default_color,
                Constant.theme_select_color,
                Frame.getString(R.string.phrase_nav_index),
                "",
                "",
                Frame.getDrawable(R.drawable.phrase_selector)
            )
        )
        add(
            ButtomItemEntity(
                Constant.theme_default_color,
                Constant.theme_select_color,
                Frame.getString(R.string.supervision_nav_index),
                "",
                "",
                Frame.getDrawable(R.drawable.supervision_selector)
            )
        )
        add(
            ButtomItemEntity(
                Constant.theme_default_color,
                Constant.theme_select_color,
                Frame.getString(R.string.mine_nav_index),
                "",
                "",
                Frame.getDrawable(R.drawable.mine_selector)
            )
        )
    }

    private val viewModel: UserViewModel by activityViewModels()

    override fun initView() {

        navigationAdapter = NavigationAdapter(list).apply {
            setOnNavigationListener(this@MainActivity)
            binding.rvHomeNavigation.adapter = this
        }

        setupPagerAdapter()

        if (Constant.isPersonalization) {
            // 获取底部菜单
            viewModel.getHomeButtomData(this)
        }
        viewModel.homeButtomData.observe(this){
            list.clear()
            list.addAll(it)
            navigationAdapter?.notifyDataSetChanged()
        }

        onNewIntent(intent)
    }

    private fun setupPagerAdapter() {
        // 避免重复设置adapter，导致fragment重复创建
        if (binding.vpHomePager.adapter != null) {
            return
        }
        pagerAdapter = FragmentPagerAdapter<Fragment>(this).apply {
            addFragment(HomeFragment.newInstance())
            addFragment(WorkFragment.newInstance())
            addFragment(LawFragment.newInstance())
            addFragment(PhraseFragment.newInstance())
            addFragment(SupervisionFragment.newInstance())
            addFragment(MineFragment.newInstance())
            binding.vpHomePager.adapter = this
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val serializable = getIntent().getSerializableExtra(INTENT_KEY_IN_FRAGMENT_CLASS)
        pagerAdapter?.let {
            if (serializable != null) {
                switchFragment(
                    it.getFragmentIndex(serializable as Class<out Fragment>)
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.vpHomePager.let {
            // 保存当前 Fragment 索引位置
            outState.putInt(INTENT_KEY_IN_FRAGMENT_INDEX, it.currentItem)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // 恢复当前 Fragment 索引位置
        switchFragment(savedInstanceState.getInt(INTENT_KEY_IN_FRAGMENT_INDEX))
    }

    override fun initData() {
        XUpdate.newBuild(this)
            .updateUrl(ConfigApi.uploadApp)
            .update()

        // 启动心跳定时器
        heartbeatHandler.post(heartbeatRunnable)

        // 调度后台同步 WorkManager（30分钟周期）
        SyncWorker.schedule(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 停止心跳定时器
        heartbeatHandler.removeCallbacks(heartbeatRunnable)
        // App 退出时上报离线
        val uuid = deviceUuid
        if (uuid.isNotEmpty()) {
            scopeNetLife {
                DeviceRepository(this@MainActivity).sendHeartbeat(uuid, "0")
            }
        }
    }

    private fun switchFragment(fragmentIndex: Int) {
        if (fragmentIndex == -1) {
            return
        }
        when (fragmentIndex) {
            0, 1, 2, 3, 4, 5 -> {
                binding.vpHomePager.currentItem = fragmentIndex
                navigationAdapter?.setSelectedPosition(fragmentIndex)
            }
        }
    }

    override fun onNavigationItemSelected(position: Int): Boolean {
        return when (position) {
            0, 1, 2, 3, 4, 5 -> {
                binding.vpHomePager.currentItem = position
                true
            }

            else -> false
        }
    }

}