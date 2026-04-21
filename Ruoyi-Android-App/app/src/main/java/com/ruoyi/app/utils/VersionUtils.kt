//package com.ruoyi.app.utils
//
//import android.content.Context
//import android.content.pm.PackageManager
//
//
//object VersionUtils {
//
//    fun getVersionName(context: Context): String {
//        try {
//            // 获取 PackageManager 实例
//            val packageManager = context.packageManager
//            // 获取当前应用的包名
//            val packageName = context.packageName
//            // 获取 PackageInfo 对象，第二个参数 0 表示不获取额外信息
//            val packageInfo = packageManager.getPackageInfo(packageName, 0)
//            // 返回 versionName
//            return packageInfo.versionName
//        } catch (e: PackageManager.NameNotFoundException) {
//            e.printStackTrace()
//            return "1.0"
//        }
//    }
//}