package com.ruoyi.app.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.scopeNetLife
import com.ruoyi.app.App
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.api.OKHttpUtils
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.model.entity.ButtomItemEntity
import com.ruoyi.app.model.entity.MineEntity
import com.ruoyi.app.model.entity.UserEntity
import com.ruoyi.app.model.entity.WorkIndexEntity
import com.ruoyi.app.model.request.registerRequest
import com.ruoyi.code.base.scopeDialog
import com.tencent.mmkv.MMKV
import com.therouter.TheRouter

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

    companion object {
        // 离线登录用户名缓存 Key
        private const val KEY_OFFLINE_USERNAME = "offline_username"
        // 用户信息 SharedPreferences
        private const val PREF_USER_INFO = "user_info"
        private const val KEY_PERMISSIONS = "permissions"
        private const val KEY_ROLES = "roles"
        private const val KEY_PWD_CHRTYPE = "pwdChrtype"
        private const val KEY_IS_DEFAULT_MODIFY_PWD = "isDefaultModifyPwd"
        private const val KEY_IS_PASSWORD_EXPIRED = "isPasswordExpired"
    }

    private val userInfoPrefs: SharedPreferences by lazy {
        getApplication<Application>().getSharedPreferences(PREF_USER_INFO, Context.MODE_PRIVATE)
    }

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

    /**
     * 登录验证
     * 直接使用预加载缓存的明文密码进行验证
     */
    fun login(activity: FragmentActivity, username: String, password: String, code: String) {
        if (TextUtils.isEmpty(username)) {
            errorMsg.value = "请输入账号"
            return
        }

        if (TextUtils.isEmpty(password)) {
            errorMsg.value = "请输入密码"
            return
        }

        // 直接使用离线登录验证
        scopeNetLife {
            offlineLogin(username, password)
        }.catch {
            errorMsg.value = "登录失败：${it.message}"
        }
    }

    /**
     * 离线登录
     * 使用 Room DB 中预加载的明文密码进行验证
     */
    private suspend fun offlineLogin(username: String, password: String) {
        // 从 Room DB 查询用户
        val user = AppDatabase.getInstance(getApplication()).userDao().getUserByUserName(username)

        if (user == null) {
            errorMsg.postValue("用户不存在，请联系管理员")
            return
        }

        // 直接比对明文密码
        if (password == user.plainPassword) {
            // 离线登录成功，使用 userId 作为临时 token
            MMKV.defaultMMKV().encode("token", user.userId.toString())
            MMKV.defaultMMKV().encode(KEY_OFFLINE_USERNAME, username)

            // 同步当前用户完整数据（包含 permissions）
            syncCurrentUserData(user.userId)

            TheRouter.build(Constant.syncWaitRoute).navigation()
        } else {
            errorMsg.postValue("密码错误")
        }
    }

    /**
     * 同步当前用户完整数据并存储到本地
     * 权限数据已在预加载时存储，此处直接读取
     */
    private suspend fun syncCurrentUserData(userId: Long) {
        try {
            // 从预加载的 SharedPreferences 中读取权限数据
            val userPerms = getApplication<Application>().getSharedPreferences("user_permissions", Context.MODE_PRIVATE)
            val permissions = userPerms.getString("user_${userId}_permissions", "") ?: ""
            val roles = userPerms.getString("user_${userId}_roles", "") ?: ""
            val pwdChrtype = userPerms.getInt("user_${userId}_pwdChrtype", 0)
            val isDefaultModifyPwd = userPerms.getBoolean("user_${userId}_isDefaultModifyPwd", false)
            val isPasswordExpired = userPerms.getBoolean("user_${userId}_isPasswordExpired", false)

            // 存储到 userInfoPrefs 供 getUserInfo 使用
            userInfoPrefs.edit().apply {
                putString(KEY_PERMISSIONS, permissions)
                putString(KEY_ROLES, roles)
                putInt(KEY_PWD_CHRTYPE, pwdChrtype)
                putBoolean(KEY_IS_DEFAULT_MODIFY_PWD, isDefaultModifyPwd)
                putBoolean(KEY_IS_PASSWORD_EXPIRED, isPasswordExpired)
                apply()
            }
        } catch (e: Exception) {
            // 同步失败不阻塞流程
            e.printStackTrace()
        }
    }

    fun getUserInfo(activity: FragmentActivity) {
        scopeDialog(activity) {
            // 从本地 Room DB 和 SharedPreferences 读取用户信息
            val token = MMKV.defaultMMKV().decodeString("token")
            val userId = token?.toLongOrNull()

            if (userId != null) {
                val user = AppDatabase.getInstance(getApplication()).userDao().getUserById(userId)
                if (user != null) {
                    val permissions = userInfoPrefs.getString(KEY_PERMISSIONS, "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
                    val roles = userInfoPrefs.getString(KEY_ROLES, "")?.split(",")?.filter { it.isNotEmpty() } ?: emptyList()
                    val pwdChrtype = userInfoPrefs.getInt(KEY_PWD_CHRTYPE, 0)
                    val isDefaultModifyPwd = userInfoPrefs.getBoolean(KEY_IS_DEFAULT_MODIFY_PWD, false)
                    val isPasswordExpired = userInfoPrefs.getBoolean(KEY_IS_PASSWORD_EXPIRED, false)

                    // 转换为 UserEntity 供 MineEntity 使用
                    val userEntity = UserEntity(
                        userId = user.userId,
                        userName = user.userName,
                        nickName = user.nickName,
                        email = user.email ?: "",
                        phonenumber = user.phonenumber ?: "",
                        avatar = user.avatar ?: "",
                        sex = user.sex ?: "0",
                        status = user.status,
                        password = "",
                        plainPassword = ""
                    )

                    val mineEntityData = MineEntity(
                        code = 200,
                        msg = "操作成功",
                        user = userEntity,
                        roles = roles,
                        permissions = permissions,
                        pwdChrtype = pwdChrtype,
                        isDefaultModifyPwd = isDefaultModifyPwd,
                        isPasswordExpired = isPasswordExpired
                    )
                    mineEntity.value = mineEntityData
                    return@scopeDialog
                }
            }
            // 如果本地没有数据，尝试从网络获取
            try {
                val body = authRepository.getUserInfo()
                if (body.code == ConfigApi.SUCESSS) {
                    mineEntity.value = body
                } else {
                    errorMsg.value = body.msg
                }
            } catch (e: Exception) {
                errorMsg.value = "获取用户信息失败"
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
