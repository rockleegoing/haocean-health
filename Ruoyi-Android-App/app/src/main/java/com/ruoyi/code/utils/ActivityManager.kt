package com.ruoyi.code.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.*
import kotlin.system.exitProcess


/**
 * Activity栈任务管理器
 * 通常放在我们自定义Activity的基类中操作
 */
class ActivityManager private constructor() {

    private val activityStack: Stack<Activity> = Stack()

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ActivityManager()
        }
    }


    /**
     * 添加Activity
     */
    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }


    /**
     * 移出存在的Activity
     * 注意：配置变更时 onDestroy 也会触发，此时 Activity 会被系统自动重建
     * 不再主动调用 finish()，避免重复 finish 导致问题
     */
    fun removeActivity(activity: Activity) {
        activityStack.remove(activity)
        // 不再调用 finish()，Activity 由系统管理生命周期
    }


    /**
     * 获取最上面的Activity
     */
    fun getTopActivity(): Activity {
        return activityStack.lastElement()
    }


    /**
     * 清除Activity栈
     */
    fun clearActivity() {
        for (activity in activityStack)
            activity.finish()

        activityStack.clear()
    }

    /*
        退出应用程序
     */
    fun exitApp(context: Context) {
        //先清除Activity
        clearActivity()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        //再killProcesses
        activityManager.killBackgroundProcesses(context.packageName)
        exitProcess(0)
    }

}