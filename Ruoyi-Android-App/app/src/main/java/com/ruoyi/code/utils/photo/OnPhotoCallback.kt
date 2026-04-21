package com.ruoyi.code.utils.photo

import java.io.File

interface OnPhotoCallback {

    /**
     * 完成的时候会回调此方法，结果存在于result中
     *
     * @param result 扫描结果
     */
    fun onCompleted(result: File?)

    /**
     * 当过程出错的时候会回调
     *
     * @param errorMsg 错误信息
     */
    fun onError(errorMsg: Throwable?)

    /**
     * 当被取消的时候回调
     */
    fun onCancel()
}