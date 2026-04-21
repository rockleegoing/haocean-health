package com.ruoyi.code.utils

import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.ruoyi.code.Frame.getContext
import com.ruoyi.code.Frame.getResources

object XOutdatedUtils {

    /**
     * getDrawable过时方法处理
     *
     * @param id
     * @return
     */
    fun getDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(getContext(), id)
    }

    /**
     * getDrawable过时方法处理
     *
     * @param id 资源id
     * @param theme 指定主题
     * @return
     */
    fun getDrawable(
        @DrawableRes id: Int,
        theme: Theme?
    ): Drawable? {
        return ResourcesCompat.getDrawable(getResources(), id, theme)
    }

    /**
     * getColor过时方法处理
     *
     * @param id
     * @return
     */
    fun getColor(@ColorRes id: Int): Int {
        return ContextCompat.getColor(getContext(), id)
    }

    /**
     * getColor过时方法处理
     *
     * @param id 资源id
     * @param theme 指定主题
     * @return
     */
    fun getColor(@ColorRes id: Int, theme: Theme?): Int {
        return ResourcesCompat.getColor(getResources(), id, theme)
    }

}