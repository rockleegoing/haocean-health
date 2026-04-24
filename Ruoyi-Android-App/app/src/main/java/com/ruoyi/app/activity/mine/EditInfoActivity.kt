package com.ruoyi.app.activity.mine

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.drake.net.Get
import com.drake.net.Put
import com.drake.net.utils.scopeNetLife
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.toast.ToastUtils
import com.ruoyi.app.R
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.api.OKHttpUtils
import com.ruoyi.app.databinding.ActivityEditInfoBinding
import com.ruoyi.app.model.entity.ResultEntity
import com.ruoyi.app.model.entity.UserInfoEntidy
import com.ruoyi.app.model.entity.UserResult
import com.ruoyi.app.model.request.EditInfoRequest
import com.ruoyi.app.utils.FlowBus
import com.ruoyi.code.Frame
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.clickDelay
import com.ruoyi.code.witget.toast

class EditInfoActivity : BaseBindingActivity<ActivityEditInfoBinding>() {

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, EditInfoActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    override fun initView() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
        binding.ivClearName.clickDelay {
            binding.etName.text.clear()
            binding.ivClearName.visibility = View.INVISIBLE
        }
        binding.etName.addTextChangedListener {
            val txt = it.toString()
            if (!TextUtils.isEmpty(txt)) {
                binding.ivClearName.visibility = View.VISIBLE
            } else {
                binding.ivClearName.visibility = View.INVISIBLE
            }
        }
        binding.etMobile.addTextChangedListener {
            val txt = it.toString()
            if (!TextUtils.isEmpty(txt)) {
                binding.ivClearMobile.visibility = View.VISIBLE
            } else {
                binding.ivClearMobile.visibility = View.INVISIBLE
            }
        }
        binding.ivClearMobile.clickDelay {
            binding.etMobile.text.clear()
            binding.ivClearMobile.visibility = View.INVISIBLE
        }

        binding.etEmail.addTextChangedListener {
            val txt = it.toString()
            if (!TextUtils.isEmpty(txt)) {
                binding.ivClearEmail.visibility = View.VISIBLE
            } else {
                binding.ivClearEmail.visibility = View.INVISIBLE
            }
        }
        binding.ivClearEmail.clickDelay {
            binding.etEmail.text.clear()
            binding.ivClearEmail.visibility = View.INVISIBLE
        }

        binding.radioFemale.clickDelay {
            binding.radioMale.isChecked = false
            binding.radioFemale.isChecked = true
        }
        binding.radioMale.clickDelay {
            binding.radioMale.isChecked = true
            binding.radioFemale.isChecked = false
        }

        binding.btnSubmit.clickDelay {
            val name = binding.etName.text.toString().trim()
            if (TextUtils.isEmpty(name)) {
                binding.etName.requestFocus()
                ToastUtils.show(Frame.getString(R.string.edit_p_nickname))
                return@clickDelay
            }
            val phone = binding.etMobile.text.toString().trim()
            if (TextUtils.isEmpty(phone)) {
                binding.etMobile.requestFocus()
                ToastUtils.show(Frame.getString(R.string.login_p_phone))
                return@clickDelay
            }
            val email = binding.etEmail.text.toString().trim()
            if (TextUtils.isEmpty(email)) {
                binding.etEmail.requestFocus()
                ToastUtils.show(Frame.getString(R.string.login_p_email))
                return@clickDelay
            }
            var sex = 0
            if (binding.radioMale.isChecked) {
                sex = 0
            }
            if (binding.radioFemale.isChecked) {
                sex = 1
            }
            val body = EditInfoRequest(name, email, phone, "" + sex)
            editProfile(body)
        }
    }

    override fun initData() {
        scopeNetLife {
            val body = Get<UserResult>(ConfigApi.getProfile).await()
            if (body.code == ConfigApi.SUCCESS) {
                binding.etName.setText(body.data.nickName)
                binding.etMobile.setText(body.data.phonenumber)
                binding.etEmail.setText(body.data.email)
                when (body.data.sex) {
                    "1" -> {
                        binding.radioMale.isChecked = false
                        binding.radioFemale.isChecked = true
                    }
                    "0" -> {
                        binding.radioMale.isChecked = true
                        binding.radioFemale.isChecked = false
                    }
                }
            } else {
                toast(body.msg)
            }
        }.catch {
            toast(it.message)
        }
    }

    private fun editProfile(request: EditInfoRequest) {
        scopeNetLife {
            val body = Put<ResultEntity<String>>(ConfigApi.updateProfile) {
                body = OKHttpUtils.getRequestBody(request)
            }.await()
            toast(body.msg)
            if (body.code == ConfigApi.SUCCESS) {
                val userInfoEntidy = UserInfoEntidy()
                userInfoEntidy.nickName = request.nickName
                FlowBus.withStick<UserInfoEntidy>(FlowBus.update_user_info).post(lifecycleScope, userInfoEntidy)
                finish()
            }
        }.catch {
            toast(it.message)
        }
    }

}
