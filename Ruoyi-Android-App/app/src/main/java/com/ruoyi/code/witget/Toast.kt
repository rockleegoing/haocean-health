package com.ruoyi.code.witget

import androidx.annotation.StringRes
import com.drake.net.utils.runMain
import com.hjq.toast.ToastUtils
import com.ruoyi.code.Frame


/**
 * 短时间显示的吐司
 * @param msg 吐司内容
 */
fun toast(@StringRes msg: Int) {
    toast(Frame.getString(msg))
}

/**
 * 显示吐司
 * @param msg 吐司内容
 */
fun toast(msg: CharSequence?) {
    msg ?: return
    ToastUtils.cancel()
    runMain {
        ToastUtils.show(msg)
    }
}