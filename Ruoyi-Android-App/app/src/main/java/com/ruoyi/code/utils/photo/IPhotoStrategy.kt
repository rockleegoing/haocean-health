package com.ruoyi.code.utils.photo

import android.content.Intent

abstract class IPhotoStrategy {

    /**
     * 发起照相
     *
     */
    abstract fun startPhotograph()

    /**
     * 发起相册
     *
     */
    abstract fun startPhotoCamera()

    /**
     * 结果回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}