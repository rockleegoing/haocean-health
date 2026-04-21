package com.ruoyi.code

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ruoyi.code.utils.XDensityUtils
import com.ruoyi.code.utils.XOutdatedUtils

@SuppressLint("StaticFieldLeak")
object Frame {

    private var context: Context? = null
    private var screenHeight = 0
    private var screenWidth = 0

    // #log
    const val tag = "Frame"

    fun initialize(context: Context) {
        Frame.context = context
        screenHeight = XDensityUtils.getScreenHeight()
        screenWidth = XDensityUtils.getScreenWidth()
    }

    fun getContext(): Context {
        synchronized(Frame::class.java) {
            if (context == null) throw NullPointerException(
                "Call Frame.init(context) within your Application onCreate() method."
            )
            return context!!
        }
    }

    fun getResources(): Resources {
        return getContext().resources
    }

    fun getString(@StringRes id: Int): String {
        return getResources().getString(id)
    }

    fun getDrawable(@DrawableRes id: Int): Drawable? {
        return XOutdatedUtils.getDrawable(id)
    }

    fun getColor(@ColorRes id: Int): Int {
        return XOutdatedUtils.getColor(id)
    }

    fun getDisplayMetrics(): DisplayMetrics {
        return getResources().displayMetrics
    }

}