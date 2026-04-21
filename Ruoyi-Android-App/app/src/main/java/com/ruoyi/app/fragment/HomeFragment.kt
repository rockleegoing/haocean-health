package com.ruoyi.app.fragment

import com.ruoyi.app.databinding.FragmentHomeBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingFragment
import com.therouter.router.Route

@Route(path = Constant.homeFragment)
class HomeFragment : BaseBindingFragment<FragmentHomeBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun initView() {

    }

    override fun initData() {
    }

}