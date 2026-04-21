package com.ruoyi.code.utils

import android.view.View
import com.ruoyi.code.utils.ViewClickDelay.SPACE_TIME
import com.ruoyi.code.utils.ViewClickDelay.hash
import com.ruoyi.code.utils.ViewClickDelay.lastClickTime

/**
 * https://blog.csdn.net/weixin_33730836/article/details/91362266
 * tv_test_click.clickDelay {
    Log.d("eeeeee", "2222222")
    }
 */
object ViewClickDelay {
    var hash: Int = 0
    var lastClickTime: Long = 0
    var SPACE_TIME: Long = 3000
}

infix fun View.clickDelay(clickAction: () -> Unit) {
    this.setOnClickListener {
        if (this.hashCode() != hash) {
            hash = this.hashCode()
            lastClickTime = System.currentTimeMillis()
            clickAction()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > SPACE_TIME) {
                lastClickTime = System.currentTimeMillis()
                clickAction()
            }
        }
    }
}