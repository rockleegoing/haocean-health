package com.ruoyi.code.utils


import android.os.Handler
import android.os.Looper


internal object ThreadUtils {
    fun runMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            Handler(Looper.getMainLooper()).post { block() }
        }
    }

    fun isMainThread(): Boolean {
        return Looper.getMainLooper().thread.id == Thread.currentThread().id
    }
}