package com.ruoyi.app

import android.app.Application
import android.content.Context
import android.content.Intent
import com.hjq.toast.ToastUtils
import com.ruoyi.app.activity.LoginActivity
import com.ruoyi.app.api.OKHttpUtils
import com.ruoyi.app.api.ServiceAuthLocator
import com.ruoyi.app.api.repository.AuthRepoInterface
import com.ruoyi.app.api.service.OKHttpUpdateHttpService
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.model.entity.CustomUpdateParser
import com.ruoyi.code.Frame
import com.ruoyi.code.utils.LocaleHelper.getPersistedData
import com.ruoyi.code.utils.LocaleHelper.setLocale
import com.tencent.mmkv.MMKV
import com.therouter.TheRouter
import com.xuexiang.xupdate.XUpdate
import com.xuexiang.xupdate.entity.UpdateError
import com.xuexiang.xupdate.utils.UpdateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class App : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /*接口相关*/
    val authRepository: AuthRepoInterface
        get() = ServiceAuthLocator.provideAuthRepository(this)

    override fun attachBaseContext(base: Context?) {
        TheRouter.isDebug = BuildConfig.DEBUG
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        application = this

        // 确保 MMKV 已初始化（MMKV.initialize 可以安全调用多次）
        try {
            MMKV.initialize(this)
        } catch (e: Exception) {
            // MMKV 可能已经初始化，忽略异常
        }

        // 检测覆盖安装
        checkAppUpgrade()

        val language = getPersistedData(this, "zh")
        val context = setLocale(this, language!!)
        Frame.initialize(context)
        TheRouter.init(this)
        initUploadApp()
    }

    /**
     * 检测覆盖安装
     * 如果是覆盖安装，清空本地数据并重置激活状态
     */
    private fun checkAppUpgrade() {
        val prefs = getSharedPreferences("app_info", Context.MODE_PRIVATE)
        val lastVersion = prefs.getInt("last_version", 0)
        val currentVersion = try {
            packageManager.getPackageInfo(packageName, 0).versionCode
        } catch (e: Exception) {
            0
        }

        if (currentVersion > lastVersion && lastVersion > 0) {
            // 覆盖安装：清空数据 + 重置激活状态
            applicationScope.launch(Dispatchers.IO) {
                AppDatabase.getInstance(this@App).clearAllTables()
            }
            // 重置激活状态（清除 activation 相关 key）
            MMKV.defaultMMKV().removeValueForKey("activation_status")
            MMKV.defaultMMKV().removeValueForKey("device_uuid")
        }

        // 更新版本号记录
        prefs.edit().putInt("last_version", currentVersion).apply()
    }


    private fun initUploadApp() {
        val versionCode = UpdateUtils.getVersionCode(this)
        XUpdate.get()
            .debug(BuildConfig.DEBUG)
            .isWifiOnly(false) //默认设置只在wifi下检查版本更新
            .isGet(true) //默认设置使用get请求检查版本
            .isAutoMode(false) //默认设置非自动模式，可根据具体使用配置
            .param("versionCode", versionCode) //设置默认公共请求参数
            .param("appKey", packageName)
            .setOnUpdateFailureListener { error ->
                //设置版本更新出错的监听
                if (error.code != UpdateError.ERROR.CHECK_NO_NEW_VERSION) {
                    //对不同错误进行处理
                    ToastUtils.show(error.toString())
                }
            }
            .setIUpdateParser(CustomUpdateParser(versionCode))
            .supportSilentInstall(true) //设置是否支持静默安装，默认是true
            .setIUpdateHttpService(OKHttpUpdateHttpService()) //这个必须设置！实现网络请求功能。
            .init(this) //这个必须初始化

        XUpdate.get().setILogger { priority, tag, message, t ->
            //实现日志记录功能
            println(message)
        }
    }

    companion object {
        lateinit var application: Application

        fun init() {
            ToastUtils.init(application)
            MMKV.initialize(application)
            OKHttpUtils.initialize(application)
        }
    }

}