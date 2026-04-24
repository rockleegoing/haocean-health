package com.ruoyi.app.utils

import kotlin.math.*

/**
 * 距离计算工具类
 * 使用 Haversine 公式计算两点之间的地球表面距离
 */
object DistanceUtils {

    private const val EARTH_RADIUS_METERS = 6371000.0

    /**
     * 计算两点之间的距离（单位：米）
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_METERS * c
    }

    /**
     * 格式化距离显示
     */
    fun formatDistance(distanceMeters: Double): String {
        return when {
            distanceMeters < 1000 -> "${distanceMeters.toInt()}m"
            else -> String.format("%.1fkm", distanceMeters / 1000)
        }
    }

    /**
     * 根据距离排序单位列表
     */
    fun sortUnitsByDistance(
        units: List<com.ruoyi.app.data.database.entity.UnitEntity>,
        currentLat: Double?,
        currentLon: Double?
    ): List<com.ruoyi.app.data.database.entity.UnitEntity> {
        return if (currentLat != null && currentLon != null) {
            units.sortedWith(compareBy(
                { it.latitude == null || it.longitude == null },
                { unit ->
                    if (unit.latitude != null && unit.longitude != null) {
                        calculateDistance(currentLat, currentLon, unit.latitude, unit.longitude)
                    } else {
                        Double.MAX_VALUE
                    }
                },
                { -it.createTime }
            ))
        } else {
            units.sortedByDescending { it.createTime }
        }
    }
}
