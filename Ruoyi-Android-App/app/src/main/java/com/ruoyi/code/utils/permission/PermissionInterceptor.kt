package com.ruoyi.code.utils.permission

import android.app.Activity
import android.app.AlertDialog
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.annotation.Nullable
import com.hjq.permissions.*
import com.ruoyi.code.witget.toast


class PermissionInterceptor : IPermissionInterceptor {


    val HANDLER: Handler = Handler(Looper.getMainLooper())

    /** 权限申请标记  */
    private var mRequestFlag = false

    /** 权限申请说明 Popup  */
    private var mPermissionPopup: PopupWindow? = null

    override fun launchPermissionRequest(
        activity: Activity,
        allPermissions: List<String?>,
        @Nullable callback: OnPermissionCallback?
    ) {
        mRequestFlag = true
        val deniedPermissions = XXPermissions.getDenied(activity, allPermissions)
        val message = activity.getString(
            com.ruoyi.app.R.string.common_permission_message,
            PermissionNameConvert.getPermissionString(activity, deniedPermissions)
        )
        val decorView = activity.window.decorView as ViewGroup
        val activityOrientation = activity.resources.configuration.orientation
        var showPopupWindow = activityOrientation == Configuration.ORIENTATION_PORTRAIT
        for (permission in allPermissions) {
            if (!XXPermissions.isSpecial(permission!!)) {
                continue
            }
            if (XXPermissions.isGranted(activity, permission)) {
                continue
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
                TextUtils.equals(Permission.MANAGE_EXTERNAL_STORAGE, permission)
            ) {
                continue
            }
            // 如果申请的权限带有特殊权限，并且还没有授予的话
            // 就不用 PopupWindow 对话框来显示，而是用 Dialog 来显示
            showPopupWindow = false
            break
        }
        if (showPopupWindow) {
            PermissionFragment.launch(activity, ArrayList(allPermissions), this, callback)
            // 延迟 300 毫秒是为了避免出现 PopupWindow 显示然后立马消失的情况
            // 因为框架没有办法在还没有申请权限的情况下，去判断权限是否永久拒绝了，必须要在发起权限申请之后
            // 所以只能通过延迟显示 PopupWindow 来做这件事，如果 300 毫秒内权限申请没有结束，证明本次申请的权限没有永久拒绝
            HANDLER.postDelayed({
                if (!mRequestFlag) {
                    return@postDelayed
                }
                if (activity.isFinishing ||
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed
                ) {
                    return@postDelayed
                }
                showPopupWindow(activity, decorView, message)
            }, 300)
        } else {
            // 注意：这里的 Dialog 只是示例，没有用 DialogFragment 来处理 Dialog 生命周期
            AlertDialog.Builder(activity)
                .setTitle(com.ruoyi.app.R.string.common_permission_description)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(com.ruoyi.app.R.string.common_permission_granted) { dialog, _ ->
                    dialog.dismiss()
                    PermissionFragment.launch(
                        activity, ArrayList(allPermissions),
                        this@PermissionInterceptor, callback
                    )
                }
                .setNegativeButton(com.ruoyi.app.R.string.common_permission_denied) { dialog, _ ->
                    dialog.dismiss()
                    if (callback == null) {
                        return@setNegativeButton
                    }
                    callback.onDenied(deniedPermissions, false)
                }
                .show()
        }
    }

    override fun grantedPermissionRequest(
        activity: Activity, allPermissions: List<String?>,
        grantedPermissions: List<String?>, allGranted: Boolean,
        @Nullable callback: OnPermissionCallback?
    ) {
        if (callback == null) {
            return
        }
        callback.onGranted(grantedPermissions, allGranted)
    }

    override fun deniedPermissionRequest(
        activity: Activity, allPermissions: List<String>,
        deniedPermissions: List<String?>, doNotAskAgain: Boolean,
        @Nullable callback: OnPermissionCallback?
    ) {
        callback?.onDenied(deniedPermissions, doNotAskAgain)
        if (doNotAskAgain) {
            if (deniedPermissions.size == 1 && Permission.ACCESS_MEDIA_LOCATION == deniedPermissions[0]
            ) {
                toast(com.ruoyi.app.R.string.common_permission_media_location_hint_fail)
                return
            }
            showPermissionSettingDialog(activity, allPermissions, deniedPermissions, callback)
            return
        }
        if (deniedPermissions.size == 1) {
            val deniedPermission = deniedPermissions[0]
            var backgroundPermissionOptionLabel: String? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                backgroundPermissionOptionLabel =
                    activity.packageManager.backgroundPermissionOptionLabel.toString()
            }
            if (TextUtils.isEmpty(backgroundPermissionOptionLabel)) {
                backgroundPermissionOptionLabel =
                    activity.getString(com.ruoyi.app.R.string.common_permission_background_default_option_label)
            }
            if (Permission.ACCESS_BACKGROUND_LOCATION == deniedPermission) {
                toast(
                    activity.getString(
                        com.ruoyi.app.R.string.common_permission_background_location_fail_hint,
                        backgroundPermissionOptionLabel
                    )
                )
                return
            }
            if (Permission.BODY_SENSORS_BACKGROUND == deniedPermission) {
                toast(
                    activity.getString(
                        com.ruoyi.app.R.string.common_permission_background_sensors_fail_hint,
                        backgroundPermissionOptionLabel
                    )
                )
                return
            }
        }
        val message: String
        val permissionNames: List<String> =
            PermissionNameConvert.permissionsToNames(activity, deniedPermissions)
        message = if (permissionNames.isNotEmpty()) {
            activity.getString(
                com.ruoyi.app.R.string.common_permission_fail_assign_hint,
                PermissionNameConvert.listToString(activity, permissionNames)
            )
        } else {
            activity.getString(com.ruoyi.app.R.string.common_permission_fail_hint)
        }
        toast(message)
    }

    override fun finishPermissionRequest(
        activity: Activity, allPermissions: List<String?>,
        skipRequest: Boolean, @Nullable callback: OnPermissionCallback?
    ) {
        mRequestFlag = false
        dismissPopupWindow()
    }

    private fun showPopupWindow(activity: Activity, decorView: ViewGroup, message: String) {
        if (mPermissionPopup == null) {
            val contentView: View = LayoutInflater.from(activity)
                .inflate(com.ruoyi.app.R.layout.permission_description_popup, decorView, false)
            mPermissionPopup = PopupWindow(activity)
            mPermissionPopup!!.contentView = contentView
            mPermissionPopup!!.width = WindowManager.LayoutParams.MATCH_PARENT
            mPermissionPopup!!.height = WindowManager.LayoutParams.WRAP_CONTENT
            mPermissionPopup!!.animationStyle = android.R.style.Animation_Dialog
            mPermissionPopup!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mPermissionPopup!!.isTouchable = true
            mPermissionPopup!!.isOutsideTouchable = true
        }
        val messageView =
            mPermissionPopup!!.contentView.findViewById<TextView>(com.ruoyi.app.R.id.tv_permission_description_message)
        messageView.text = message
        // 注意：这里的 PopupWindow 只是示例，没有监听 Activity onDestroy 来处理 PopupWindow 生命周期
        mPermissionPopup!!.showAtLocation(decorView, Gravity.TOP, 0, 0)
    }

    private fun dismissPopupWindow() {
        if (mPermissionPopup == null) {
            return
        }
        if (!mPermissionPopup!!.isShowing) {
            return
        }
        mPermissionPopup!!.dismiss()
    }

    private fun showPermissionSettingDialog(
        activity: Activity?, allPermissions: List<String>,
        deniedPermissions: List<String?>, callback: OnPermissionCallback?
    ) {
        if (activity == null || activity.isFinishing || activity.isDestroyed
        ) {
            return
        }
        val message: String
        val permissionNames: List<String> =
            PermissionNameConvert.permissionsToNames(activity, deniedPermissions)
        message = if (permissionNames.isNotEmpty()) {
            activity.getString(
                com.ruoyi.app.R.string.common_permission_manual_assign_fail_hint,
                PermissionNameConvert.listToString(activity, permissionNames)
            )
        } else {
            activity.getString(com.ruoyi.app.R.string.common_permission_manual_fail_hint)
        }

        // 这里的 Dialog 只是示例，没有用 DialogFragment 来处理 Dialog 生命周期
        AlertDialog.Builder(activity)
            .setTitle(com.ruoyi.app.R.string.common_permission_alert)
            .setMessage(message)
            .setPositiveButton(com.ruoyi.app.R.string.common_permission_goto_setting_page) { dialog, _ ->
                dialog.dismiss()
                XXPermissions.startPermissionActivity(activity,
                    deniedPermissions, object : OnPermissionPageCallback {
                        override fun onGranted() {
                            if (callback == null) {
                                return
                            }
                            callback.onGranted(allPermissions, true)
                        }

                        override fun onDenied() {
                            showPermissionSettingDialog(
                                activity, allPermissions,
                                XXPermissions.getDenied(activity, allPermissions), callback
                            )
                        }
                    })
            }
            .show()
    }
}