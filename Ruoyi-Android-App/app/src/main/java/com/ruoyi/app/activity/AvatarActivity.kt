package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.text.TextUtils
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.drake.net.Get
import com.drake.net.Post
import com.drake.net.utils.scopeNetLife
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.ruoyi.app.R
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.databinding.ActivityAvatarBinding
import com.ruoyi.app.model.entity.AvatarEntity
import com.ruoyi.app.model.entity.MineEntity
import com.ruoyi.app.model.entity.UserInfoEntidy
import com.ruoyi.app.utils.FlowBus
import com.ruoyi.app.utils.ImageUtils
import com.ruoyi.app.widget.ButtomSelectPop
import com.ruoyi.code.Frame
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.clickDelay
import com.ruoyi.code.utils.permission.PermissionInterceptor
import com.ruoyi.code.utils.photo.OnPhotoCallback
import com.ruoyi.code.utils.photo.PhotoManager
import com.ruoyi.code.witget.toast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class AvatarActivity : BaseBindingActivity<ActivityAvatarBinding>() {

    companion object {

        fun startActivity(context: Context) {
            val intent = Intent(context, AvatarActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private var leftTopX: Int = 0
    private var leftTopY: Int = 0
    private var borderXLength: Int = 0
    private var borderYLength: Int = 0

    //待出处理
    private lateinit var bitmap: Bitmap

    override fun initView() {
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })

        binding.btnSelectAvatar.clickDelay {
            XXPermissions.with(this)
                .permission(Permission.CAMERA)
                .interceptor(PermissionInterceptor())
                .request(OnPermissionCallback { _, allGranted ->
                    if (!allGranted) {
                        return@OnPermissionCallback
                    }
                    showDialog()
                })
        }

        binding.btnSubmit.clickDelay {
            if (bitmap == null) {
                ToastUtils.show(Frame.getString(R.string.avatar_select_pic))
                return@clickDelay
            }
            /*处理过的图片*/
            val handleBitmap = imageCrop(bitmap)

            /*缓存里面*/
            // /storage/emulated/0/Android/data/{包名}/cache/xxx.png
            val file = File(externalCacheDir, System.currentTimeMillis().toString() + ".png")

            val out: FileOutputStream
            try {
                out = FileOutputStream(file)
                handleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            /*然后上传到服务器*/
            upDataAvatar(file)
        }

        binding.choiceView.setonImageDetailsSizeChangged { x, y, lengthX, lengthY ->
            this.leftTopX = x
            this.leftTopY = y
            this.borderXLength = lengthX
            this.borderYLength = lengthY
        }
    }

    private fun imageCrop(bitmap: Bitmap): Bitmap {
        return Bitmap.createBitmap(
            bitmap,
            leftTopX,
            leftTopY,
            borderXLength,
            borderYLength,
            null,
            false
        )
    }


    override fun initData() {
        bitmap = BitmapFactory.decodeResource(resources, R.mipmap.profile, null)
        scopeNetLife {
            val body = Get<MineEntity>(ConfigApi.getInfo).await()
            if (body.code == ConfigApi.SUCESSS) {
                val avatar = body.user?.avatar
                if (!TextUtils.isEmpty(avatar)) {
                    Glide.with(Frame.getContext())
                        .load(ConfigApi.baseUrl + avatar)
                        .error(R.mipmap.profile)
                        .into(binding.image)
                }
            } else {
                toast(body.msg)
            }
        }
    }

    private fun upDataAvatar(file: File) {
        scopeNetLife {
            val image = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("avatarfile", file.name, image)
                .build()
            val body = Post<AvatarEntity>(ConfigApi.updateAvatar) {
                body = requestBody
            }.await()
            if (body.code == ConfigApi.SUCESSS) {
                val imgUrl = body.imgUrl
                val userInfoEntidy = UserInfoEntidy()
                userInfoEntidy.avatar = imgUrl
                FlowBus.withStick<UserInfoEntidy>(FlowBus.update_user_info)
                    .post(lifecycleScope, userInfoEntidy)
                toast(Frame.getString(R.string.avatar_success))
                finish()
            } else {
                toast(body.msg)
            }
        }.catch {
            toast(it.message)
        }
    }

    private fun showDialog() {
        ButtomSelectPop
            .Builder(this)
            .setView(binding.itemView)
            .setPopTopOnClick {
                XXPermissions.with(this)
                    .permission(Permission.READ_MEDIA_IMAGES)
                    .interceptor(PermissionInterceptor())
                    .request(OnPermissionCallback { _, allGranted ->
                        if (!allGranted) {
                            return@OnPermissionCallback
                        }
                    })
                if (it == 0) {
                    /*打开照相机*/
                    PhotoManager.instance
                        .with(this)
                        .setCallback(object : OnPhotoCallback {
                            override fun onCompleted(result: File?) {
                                bitmap = MediaStore.Images.Media.getBitmap(
                                    contentResolver,
                                    ImageUtils.fileToUri(this@AvatarActivity, result!!)
                                )
                                binding.image.setImageBitmap(bitmap)
                            }

                            override fun onError(errorMsg: Throwable?) {
                                toast(errorMsg?.message)
                            }

                            override fun onCancel() {

                            }
                        })
                        .startPhotoCamera()
                } else {
                    /*选择图片*/
                    PhotoManager.instance
                        .with(this)
                        .setCallback(object : OnPhotoCallback {
                            override fun onCompleted(result: File?) {
                                bitmap = MediaStore.Images.Media.getBitmap(
                                    contentResolver,
                                    ImageUtils.fileToUri(this@AvatarActivity, result!!)
                                )
                                binding.image.setImageBitmap(bitmap)
                            }

                            override fun onError(errorMsg: Throwable?) {
                                toast(errorMsg?.message)
                            }

                            override fun onCancel() {

                            }
                        })
                        .startPhotograph()
                }
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        PhotoManager.instance.onActivityResult(requestCode, resultCode, data)
    }
}
