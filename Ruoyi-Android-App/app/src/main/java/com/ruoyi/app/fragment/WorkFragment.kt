package com.ruoyi.app.fragment

import com.ruoyi.app.adapter.BannerHolderCreator
import com.ruoyi.app.adapter.WorkManageAdapter
import com.ruoyi.app.databinding.FragmentWorkBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.UserViewModel
import com.ruoyi.app.model.entity.BasicEntity
import com.ruoyi.code.base.BaseBindingFragment
import com.ruoyi.code.base.activityViewModels
import com.ruoyi.code.witget.toast
import com.therouter.router.Route

@Route(path = Constant.workFragment)
class WorkFragment : BaseBindingFragment<FragmentWorkBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = WorkFragment()
    }

    private val adapter: WorkManageAdapter by lazy {
        WorkManageAdapter()
    }

    private val viewModel: UserViewModel by activityViewModels()

    override fun initView() {
        binding.recyclerview.adapter = adapter
    }

    override fun initData() {
        viewModel.getWorkData(requireActivity())
        viewModel.errorMsg.observe(this) {
            toast(it)
        }
        val holderCreator = BannerHolderCreator()
        viewModel.workData.observe(this) {
            val data = it.data
            val banner = data.banner
            binding.xbanner.setBannerData(banner, holderCreator)

            val basic = data.function.basic
            val plug = data.function.plug
            val mList = ArrayList<BasicEntity>()
            mList.add(basic)
            mList.add(plug)
            adapter.setNewInstance(mList)
        }
    }

}