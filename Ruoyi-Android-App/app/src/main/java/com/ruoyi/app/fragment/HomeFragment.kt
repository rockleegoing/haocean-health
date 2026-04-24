package com.ruoyi.app.fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.ruoyi.app.R
import com.ruoyi.app.adapter.HomeMenuAdapter
import com.ruoyi.app.adapter.HomeMenuPagerAdapter
import com.ruoyi.app.databinding.FragmentHomeBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.MenuItem
import com.ruoyi.code.base.BaseBindingFragment
import com.therouter.router.Route

@Route(path = Constant.homeFragment)
class HomeFragment : BaseBindingFragment<FragmentHomeBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    private lateinit var menuAdapter: HomeMenuAdapter
    private lateinit var pagerAdapter: HomeMenuPagerAdapter

    private val menuItems: List<MenuItem> by lazy {
        listOf(
            MenuItem(1, getString(R.string.menu_law_enforcement), R.drawable.ic_menu_law_enforcement, LawEnforcementFragment::class.java),
            MenuItem(2, getString(R.string.menu_law), R.drawable.ic_menu_law, LawFragment::class.java),
            MenuItem(3, getString(R.string.menu_supervision), R.drawable.ic_menu_supervision, SupervisionFragment::class.java),
            MenuItem(4, getString(R.string.menu_scene_notes), R.drawable.ic_menu_scene_notes, SceneNotesFragment::class.java),
            MenuItem(5, getString(R.string.menu_phrase), R.drawable.ic_menu_phrase, PhraseFragment::class.java),
            MenuItem(6, getString(R.string.menu_office), R.drawable.ic_menu_office, OfficeFragment::class.java)
        )
    }

    private val fragments: List<androidx.fragment.app.Fragment> by lazy {
        listOf(
            LawEnforcementFragment.newInstance(),
            LawFragment.newInstance(),
            SupervisionFragment.newInstance(),
            SceneNotesFragment.newInstance(),
            PhraseFragment.newInstance(),
            OfficeFragment.newInstance()
        )
    }

    override fun initView() {
        setupMenuRecyclerView()
        setupViewPager()
    }

    private fun setupMenuRecyclerView() {
        menuAdapter = HomeMenuAdapter(menuItems) { position ->
            binding.vpHomeContent.currentItem = position
        }
        binding.rvHomeMenu.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = menuAdapter
        }
    }

    private fun setupViewPager() {
        pagerAdapter = HomeMenuPagerAdapter(requireActivity(), fragments)
        binding.vpHomeContent.adapter = pagerAdapter
        binding.vpHomeContent.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                menuAdapter.setSelectedPosition(position)
                binding.rvHomeMenu.scrollToPosition(position)
            }
        })
    }

    override fun initData() {
    }
}
