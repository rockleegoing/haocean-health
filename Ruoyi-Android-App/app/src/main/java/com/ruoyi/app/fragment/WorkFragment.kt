package com.ruoyi.app.fragment

import com.ruoyi.app.databinding.FragmentWorkBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingFragment
import com.therouter.router.Route

@Route(path = Constant.workFragment)
class WorkFragment : BaseBindingFragment<FragmentWorkBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = WorkFragment()
    }

    override fun initView() {
        binding.cardAddUnit.setOnClickListener {
            TheRouter.build(Constant.addUnitRoute).navigation()
        }
    }

    override fun initData() {
        // No data loading needed for this simple entry page
    }

}
