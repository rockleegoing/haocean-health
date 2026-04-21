package com.ruoyi.app.model

import android.app.Application
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.scopeNetLife
import com.ruoyi.app.App
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.api.OKHttpUtils
import com.ruoyi.app.model.entity.ButtomItemEntity
import com.ruoyi.app.model.entity.MineEntity
import com.ruoyi.app.model.entity.WorkIndexEntity
import com.ruoyi.app.model.request.registerRequest
import com.ruoyi.code.base.scopeDialog
import com.tencent.mmkv.MMKV

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = (application as App).authRepository

    var errorMsg: MutableLiveData<String> = MutableLiveData()
    var loginSuceess: MutableLiveData<Boolean> = MutableLiveData()
    var isRegister: MutableLiveData<Boolean> = MutableLiveData()
    var captchaEnabled: MutableLiveData<Boolean> = MutableLiveData()
    private var uuid: MutableLiveData<String> = MutableLiveData()
    var imageCode: MutableLiveData<String> = MutableLiveData()
    var mineEntity: MutableLiveData<MineEntity> = MutableLiveData()
    var workData: MutableLiveData<WorkIndexEntity> = MutableLiveData()
    var homeButtomData: MutableLiveData<List<ButtomItemEntity>> = MutableLiveData()

    fun getVerificationCode() {
        scopeNetLife {
            isRegister.value = false
            val data = authRepository.getVerificationCode()
            if (data.code == ConfigApi.SUCESSS) {
                captchaEnabled.value = data.captchaEnabled
                if (captchaEnabled.value == true) {
                    uuid.value = data.uuid
                    imageCode.value = data.img
                }
            } else {
                errorMsg.value = data.msg
            }
        }.catch {
            errorMsg.value = it.message
        }

    }

    fun login(activity: FragmentActivity, username: String, password: String, code: String) {
        if (captchaEnabled.value == true) {
            if (TextUtils.isEmpty(code)) {
                errorMsg.value = "请输入验证码"
                return
            }
        }

        if (TextUtils.isEmpty(username)) {
            errorMsg.value = "请输入账号"
            return
        }

        if (TextUtils.isEmpty(password)) {
            errorMsg.value = "请输入密码"
            return
        }
        scopeDialog(activity) {
            val data = authRepository.login(code, username, password, uuid.value)
            if (data.isSuceess()) {
                MMKV.defaultMMKV().encode("token", data.token)
                loginSuceess.value = true
            } else {
                errorMsg.value = data.msg
            }
        }.catch {
            errorMsg.value = it.message
        }
    }

    fun getUserInfo(activity: FragmentActivity) {
        scopeDialog(activity) {
            val body = authRepository.getUserInfo()
            if (body.code == ConfigApi.SUCESSS) {
                mineEntity.value = body
            } else {
                errorMsg.value = body.msg
            }
        }
    }

    fun getWorkData(activity: FragmentActivity) {
        scopeDialog(activity) {
            val body = authRepository.getWorkData()
            if (body.code == ConfigApi.SUCESSS) {
                workData.value = body
            } else {
                errorMsg.value = body.msg
            }
        }.catch {
            errorMsg.value = it.message
        }
    }

    fun register(
        activity: FragmentActivity,
        code: String,
        username: String,
        password: String,
        rpassword: String
    ) {

        if (captchaEnabled.value == true) {
            if (TextUtils.isEmpty(code)) {
                errorMsg.value = "请输入验证码"
                return
            }
        }

        if (TextUtils.isEmpty(username)) {
            errorMsg.value = "请输入账号"
            return
        }

        if (TextUtils.isEmpty(password)) {
            errorMsg.value = "请输入密码"
            return
        }
        if (TextUtils.isEmpty(rpassword)) {
            errorMsg.value = "请输入重复密码"
            return
        }
        if (!rpassword.equals(password)) {
            errorMsg.value = "两次输入的密码不一致"
            return
        }
        scopeDialog(activity) {
            val requestBody = OKHttpUtils.getRequestBody(
                registerRequest(
                    code,
                    uuid.value ?: "",
                    password,
                    rpassword,
                    username
                )
            )
            val data = authRepository.register(requestBody)
            if (data.isSuceess()) {
                loginSuceess.value = true
            } else {
                errorMsg.value = data.msg
                getVerificationCode()
            }
        }.catch {
            errorMsg.value = it.message
            getVerificationCode()
        }
    }

    fun getHomeButtomData(activity: FragmentActivity) {
        scopeDialog(activity) {
            val body = authRepository.getHomeButtomData()
            if (body.code == ConfigApi.SUCESSS) {
                val data = body.data
                val list = ArrayList<ButtomItemEntity>();
                data.forEach {
                    val buttomItemEntity = ButtomItemEntity();
                    buttomItemEntity.selectIcon = it.selectIcon
                    buttomItemEntity.defaultIcon = it.defaultIcon
                    buttomItemEntity.selectColor = it.selectColor
                    buttomItemEntity.name = it.name
                    buttomItemEntity.defaultColor = it.defaultColor
                    list.add(buttomItemEntity)
                }

                homeButtomData.value = list
            } else {
                errorMsg.value = body.msg
            }
        }
    }

}
