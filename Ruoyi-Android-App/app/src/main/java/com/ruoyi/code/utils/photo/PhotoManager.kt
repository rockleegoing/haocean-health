package com.ruoyi.code.utils.photo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.ruoyi.app.utils.ImageUtils
import java.io.File


class PhotoManager : IPhotoStrategy() {

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            PhotoManager()
        }
    }

    private var activity: Activity? = null
    private var callback: OnPhotoCallback? = null

    private val PHONO_REQUEST_CODE = 9000 // 照相
    private val ALBUM_REQUEST_CODE = 9001 // 相册

    /**
     * 关联调用类
     *
     * @param context
     * @return
     */
    fun with(context: Activity): PhotoManager {
        activity = context
        return this
    }

    fun setCallback(callback: OnPhotoCallback?): PhotoManager {
        this.callback = callback
        return this
    }

    var file: File? = null

    override fun startPhotoCamera() {
        file = ImageUtils.createCameraFile(false)
        val intent = Intent()
        // 启动系统相机
        if (false) {
            // 录制视频
            intent.action = MediaStore.ACTION_VIDEO_CAPTURE
        } else {
            // 拍摄照片
            intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        }

        val imageUri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 通过 FileProvider 创建一个 Content 类型的 Uri 文件
            activity?.let {
                FileProvider.getUriForFile(
                    it, "com.ruoyi.app.provider", file!!
                )
            }
        } else {
            Uri.fromFile(file)
        }
        // 对目标应用临时授权该 Uri 所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        // 将拍取的照片保存到指定 Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        activity?.startActivityForResult(intent, PHONO_REQUEST_CODE)
    }

    override fun startPhotograph() {
        val intent = ImageUtils.openAlbum()
        activity?.startActivityForResult(intent, ALBUM_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (callback == null) {
            return
        }
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                PHONO_REQUEST_CODE -> {
                    callback?.onCompleted(file)
                }
                ALBUM_REQUEST_CODE -> {
                    val uri: Uri? = data?.data
                    callback?.onCompleted(
                        ImageUtils.uriToFile(
                            activity?.applicationContext!!,
                            uri!!
                        )
                    )
                }
            }
        }
        callback?.onCancel()
    }
}