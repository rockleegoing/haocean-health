package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ruoyi.app.R
import com.ruoyi.app.adapter.FragmentPagerAdapter
import com.ruoyi.app.adapter.NavigationAdapter
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.databinding.ActivityMainBinding
import com.ruoyi.app.fragment.HomeFragment
import com.ruoyi.app.fragment.MineFragment
import com.ruoyi.app.fragment.WorkFragment
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.UserViewModel
import com.ruoyi.app.model.entity.ButtomItemEntity
import com.ruoyi.code.Frame
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.base.activityViewModels
import com.therouter.router.Route
import com.xuexiang.xupdate.XUpdate


@Route(path = Constant.mainRoute)
class MainActivity : BaseBindingActivity<ActivityMainBinding>(),
    NavigationAdapter.OnNavigationListener {

    companion object {

        private const val INTENT_KEY_IN_FRAGMENT_INDEX: String = "fragmentIndex"
        private const val INTENT_KEY_IN_FRAGMENT_CLASS: String = "fragmentClass"

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

        pagerAdapter = FragmentPagerAdapter<Fragment>(this).apply {
            addFragment(HomeFragment.newInstance())
            addFragment(WorkFragment.newInstance())
            addFragment(MineFragment.newInstance())
            binding.vpHomePager.adapter = this
        }

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
    }

    private fun switchFragment(fragmentIndex: Int) {
        if (fragmentIndex == -1) {
            return
        }
        when (fragmentIndex) {
            0, 1, 2 -> {
                binding.vpHomePager.currentItem = fragmentIndex
                navigationAdapter?.setSelectedPosition(fragmentIndex)
            }
        }
    }

    override fun onNavigationItemSelected(position: Int): Boolean {
        return when (position) {
            0, 1, 2 -> {
                binding.vpHomePager.currentItem = position
                true
            }

            else -> false
        }
    }

}