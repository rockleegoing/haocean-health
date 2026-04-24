package com.ruoyi.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * 导航工具类
 * 通过浏览器打开高德Web地图
 */
object NavigationUtils {

    fun navigate(context: Context, latitude: Double, longitude: Double, unitName: String) {
        openWebMap(context, latitude, longitude, unitName)
    }

    private fun openWebMap(context: Context, lat: Double, lon: Double, name: String) {
        try {
            // 使用高德地图网页版搜索定位
            val webUrl = "https://www.amap.com/search?query=$name&center=$lon,$lat"
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
