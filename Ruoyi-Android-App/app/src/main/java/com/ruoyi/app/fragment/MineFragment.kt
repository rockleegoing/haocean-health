package com.ruoyi.app.fragment

import android.text.TextUtils
import com.bumptech.glide.Glide
import com.hjq.toast.ToastUtils
import com.ruoyi.app.R
import com.ruoyi.app.activity.AboutActivity
import com.ruoyi.app.activity.AvatarActivity
import com.ruoyi.app.activity.HelpActivity
import com.ruoyi.app.activity.SettingActivity
import com.ruoyi.app.activity.mine.EditInfoActivity
import com.ruoyi.app.activity.mine.InfoActivity
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.databinding.FragmentMineBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.UserViewModel
import com.ruoyi.app.model.entity.UserInfoEntidy
import com.ruoyi.app.utils.FlowBus
import com.ruoyi.app.widget.LikeMeDialog
import com.ruoyi.code.Frame
import com.ruoyi.code.base.BaseBindingFragment
import com.ruoyi.code.base.activityViewModels
import com.ruoyi.code.dialog.BaseDialogFragment
import com.ruoyi.code.dialog.BubbleDialog
import com.ruoyi.code.dialog.CProgressDialogUtils
import com.ruoyi.code.utils.clickDelay
import com.ruoyi.code.witget.toast
import com.therouter.TheRouter
import com.therouter.router.Route

@Route(path = Constant.mineFragment)
class MineFragment : BaseBindingFragment<FragmentMineBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = MineFragment()
    }

    private val viewModel: UserViewModel by activityViewModels()

    override fun initView() {
        binding.ivAvatar.clickDelay {
            AvatarActivity.startActivity(this.requireActivity())
        }
        binding.tvInfo.clickDelay {
            InfoActivity.startActivity(this.requireActivity())
        }
        binding.llEditInfo.clickDelay {
            EditInfoActivity.startActivity(this.requireActivity())
        }
        binding.llHelp.clickDelay {
            HelpActivity.startActivity(this.requireActivity())
        }
        binding.llAbout.clickDelay {
            AboutActivity.startActivity(this.requireActivity())
        }
        binding.llSetting.clickDelay {
            SettingActivity.startActivity(this.requireActivity())
        }
        binding.llAddUnit.clickDelay {
            TheRouter.build(Constant.addUnitRoute).navigation()
        }
        binding.llJiaoliuqun.clickDelay {
            ToastUtils.show(Frame.getString(R.string.mine_qq_line))
        }
        binding.llZaixiankefu.clickDelay {
            ToastUtils.show(Frame.getString(R.string.mine_construction))
        }
        binding.llFankuishequ.clickDelay {
            ToastUtils.show(Frame.getString(R.string.mine_construction))
        }
        binding.llDianzanwomen.clickDelay {
            val likeMeDialog = LikeMeDialog(requireActivity())
            if (!likeMeDialog.isShowing) {
                likeMeDialog.show()
            }
        }
    }

    override fun initData() {
        viewModel.getUserInfo(requireActivity())
        viewModel.errorMsg.observe(this) {
            toast(it)
        }
        viewModel.mineEntity.observe(this) {
            val avatar = it.user?.avatar
            if (!TextUtils.isEmpty(avatar)) {
                Glide.with(Frame.getContext())
                    .load(ConfigApi.baseUrl + it.user?.avatar)
                    .error(R.mipmap.profile)
                    .into(binding.ivAvatar)
            }
            val nickName = it.user?.nickName
            if (TextUtils.isEmpty(nickName)) {
                binding.nickName.text = Frame.getString(R.string.anonymous)
            } else {
                binding.nickName.text = it.user?.nickName
            }
        }

        FlowBus.withStick<UserInfoEntidy>(FlowBus.update_user_info).register(this) {
            val imgUrl = it.avatar
            if (!TextUtils.isEmpty(imgUrl)) {
                Glide.with(Frame.getContext())
                    .load(ConfigApi.baseUrl + imgUrl)
                    .error(R.mipmap.profile)
                    .into(binding.ivAvatar)
            }
            it.nickName?.let {
                binding.nickName.text = it
            }
        }

    }

}
