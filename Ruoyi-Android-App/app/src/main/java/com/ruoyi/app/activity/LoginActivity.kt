package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ruoyi.app.R
import com.ruoyi.app.activity.common.WebActivity
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.api.repository.ActivationRepository
import com.ruoyi.app.api.repository.SyncRepository
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.UserMapper
import com.ruoyi.app.databinding.ActivityLoginBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.UserViewModel
import com.ruoyi.code.Frame
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.base.activityViewModels
import com.ruoyi.code.utils.clickDelay
import com.ruoyi.code.witget.toast
import com.tencent.mmkv.MMKV
import com.therouter.TheRouter
import com.therouter.router.Route
import com.xuexiang.xupdate.XUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Route(path = Constant.loginRoute)
class LoginActivity : BaseBindingActivity<ActivityLoginBinding>() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private val viewModel: UserViewModel by activityViewModels()

    override fun initView() {
        // 阶段一：进入登录页时检查激活状态
        checkActivationStatus()

        binding.btnLogin.clickDelay {
            viewModel.login(
                this@LoginActivity,
                binding.etAccount.text.toString().trim(),
                binding.etPassword.text.toString().trim(),
                binding.etCode.text.toString().trim()
            )
        }

        binding.logoCodeImage.clickDelay {
            viewModel.getVerificationCode()
        }

        binding.tvProtocol.clickDelay {
            WebActivity.startActivity(this@LoginActivity, Frame.getString(R.string.login_agreement), ConfigApi.blogUrl + "/app/agreement.html")
        }

        binding.tvPrivacy.clickDelay {
            WebActivity.startActivity(this@LoginActivity, Frame.getString(R.string.login_privacy), ConfigApi.blogUrl + "/app/privacy.html")
        }

        binding.tvRegister.clickDelay {
            RegisterActivity.startActivity(this@LoginActivity)
        }

        val token = MMKV.defaultMMKV().decodeString("token")
        if (!token.isNullOrEmpty()) {
            TheRouter.build(Constant.mainRoute)
                .navigation()
            finish()
            return
        } else {
            XUpdate.newBuild(this)
                .promptThemeColor(ContextCompat.getColor(this, R.color.red))
                .updateUrl(ConfigApi.uploadApp)
                .update()
        }

    }

    /**
     * 阶段一：检查设备激活状态
     * 未激活 → 跳转到激活页面
     * 覆盖安装 → 清空本地数据 + 重激活设备（已在 App.kt 处理）
     */
    private fun checkActivationStatus() {
        val isActivated = runBlocking {
            val activationRepository = ActivationRepository(this@LoginActivity)
            val status = activationRepository.getActivationStatus()
            status != null && status.isActivated
        }

        if (!isActivated) {
            // 未激活，跳转到激活页面
            ActivationActivity.startActivity(this@LoginActivity)
            finish()
            return
        }

        // 已激活，预加载登录验证数据
        preloadLoginData()
    }

    /**
     * 预加载登录验证数据
     * 后台异步执行，失败不阻塞用户操作
     */
    private fun preloadLoginData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 初始化 Room 数据库
                AppDatabase.getInstance(this@LoginActivity)
                // 同步所有数据（用户、部门、角色、菜单）到本地
                val syncRepository = SyncRepository(this@LoginActivity)
                val result = syncRepository.syncAllData()
                result.getOrNull()?.let { syncData ->
                    // 存储用户到本地
                    val users = syncData.users.map { apiUser ->
                        UserMapper.fromApi(apiUser)
                    }
                    AppDatabase.getInstance(getApplication()).userDao().insertUsers(users)
                    // 存储部门
                    val depts = syncData.depts.map { apiDept ->
                        com.ruoyi.app.data.database.entity.DeptEntity(
                            deptId = apiDept.deptId,
                            parentId = apiDept.parentId,
                            ancestors = apiDept.ancestors,
                            deptName = apiDept.deptName,
                            orderNum = apiDept.orderNum,
                            leader = apiDept.leader,
                            phone = apiDept.phone,
                            email = apiDept.email,
                            status = apiDept.status,
                            delFlag = apiDept.delFlag,
                            createBy = apiDept.createBy,
                            createTime = apiDept.createTime?.toLongOrNull() ?: System.currentTimeMillis(),
                            updateBy = apiDept.updateBy,
                            updateTime = apiDept.updateTime?.toLongOrNull(),
                            remark = apiDept.remark
                        )
                    }
                    AppDatabase.getInstance(getApplication()).deptDao().insertDepts(depts)
                    // 存储角色
                    val roles = syncData.roles.map { apiRole ->
                        com.ruoyi.app.data.database.entity.RoleEntity(
                            roleId = apiRole.roleId,
                            roleName = apiRole.roleName,
                            roleKey = apiRole.roleKey,
                            roleSort = apiRole.roleSort,
                            dataScope = apiRole.dataScope,
                            menuCheckStrictly = apiRole.menuCheckStrictly,
                            deptCheckStrictly = apiRole.deptCheckStrictly,
                            status = apiRole.status,
                            delFlag = apiRole.delFlag,
                            createBy = apiRole.createBy,
                            createTime = apiRole.createTime?.toLongOrNull() ?: System.currentTimeMillis(),
                            updateBy = apiRole.updateBy,
                            updateTime = apiRole.updateTime?.toLongOrNull(),
                            remark = apiRole.remark
                        )
                    }
                    AppDatabase.getInstance(getApplication()).roleDao().insertRoles(roles)
                    // 存储每个用户的权限信息到 SharedPreferences
                    val userPrefs = getSharedPreferences("user_permissions", Context.MODE_PRIVATE)
                    syncData.users.forEach { apiUser ->
                        userPrefs.edit().apply {
                            putString("user_${apiUser.userId}_permissions", apiUser.permissions.joinToString(","))
                            putString("user_${apiUser.userId}_roles", apiUser.roles.joinToString(","))
                            putInt("user_${apiUser.userId}_pwdChrtype", apiUser.pwdChrtype)
                            putBoolean("user_${apiUser.userId}_isDefaultModifyPwd", apiUser.isDefaultModifyPwd)
                            putBoolean("user_${apiUser.userId}_isPasswordExpired", apiUser.isPasswordExpired)
                            apply()
                        }
                    }
                }
            } catch (e: Exception) {
                // 预加载失败，不阻塞用户操作
                e.printStackTrace()
            }
        }
    }

    /**
     * 检查网络是否可用（离线优先）
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun initData() {
        viewModel.getVerificationCode()
        viewModel.errorMsg.observe(this) {
            toast(it)
        }
        viewModel.captchaEnabled.observe(this) {
            if (it) {
                binding.llCaptchaImage.visibility = View.VISIBLE
            } else {
                binding.llCaptchaImage.visibility = View.GONE
            }
        }
        viewModel.imageCode.observe(this) {
            Glide.with(Frame.getContext())
                .load("data:image/gif;base64,${it}")
                .into(binding.logoCodeImage)
        }
        // loginSuceess 跳转已移至 UserViewModel.login() 处理
        viewModel.isRegister.observe(this) {
            if (it) {
                binding.llRegister.visibility = View.VISIBLE
            } else {
                binding.llRegister.visibility = View.GONE
            }
        }
    }

}
