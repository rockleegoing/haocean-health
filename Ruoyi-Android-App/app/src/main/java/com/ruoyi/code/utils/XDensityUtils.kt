package com.ruoyi.code.utils

import com.ruoyi.code.Frame

object XDensityUtils {

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    fun dp2px(dpValue: Float): Int {
        val scale: Float = Frame.getDisplayMetrics().density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return dp值
     */
    fun px2dp(pxValue: Float): Int {
        val scale: Float = Frame.getDisplayMetrics().density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * sp转px
     *
     * @param spValue sp值
     * @return px值
     */
    fun sp2px(spValue: Float): Int {
        val fontScale: Float = Frame.getDisplayMetrics().scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * px转sp
     *
     * @param pxValue px值
     * @return sp值
     */
    fun px2sp(pxValue: Float): Int {
        val fontScale: Float = Frame.getDisplayMetrics().scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    fun getScreenHeight(): Int{
        return Frame.getDisplayMetrics().widthPixels
    }

    fun getScreenWidth(): Int {
        return Frame.getDisplayMetrics().heightPixels
    }
}