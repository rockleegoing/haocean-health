package com.ruoyi.app.fragment

import com.ruoyi.app.activity.SelectUnitActivity
import com.ruoyi.app.databinding.FragmentHomeBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.SelectedUnitManager
import com.ruoyi.code.base.BaseBindingFragment
import com.therouter.router.Route

@Route(path = Constant.homeFragment)
class HomeFragment : BaseBindingFragment<FragmentHomeBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun initView() {
        // 点击单位选择栏
        binding.llUnitSelector.setOnClickListener {
            SelectUnitActivity.startActivity(requireContext())
        }
    }

    override fun initData() {
        updateUnitSelector()
    }

    override fun onResume() {
        super.onResume()
        updateUnitSelector()
    }

    private fun updateUnitSelector() {
        val unitName = SelectedUnitManager.getSelectedUnitName()
        if (unitName != null) {
            binding.tvSelectedUnit.text = "当前单位：$unitName"
        } else {
            binding.tvSelectedUnit.text = "请选择执法单位"
        }
    }
}
