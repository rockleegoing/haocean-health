package com.ruoyi.code.dialog

import android.app.Activity
import android.content.DialogInterface
import com.ruoyi.app.R
import com.ruoyi.code.Frame

/**
 * CProgressDialogUtils.showProgressDialog(requireActivity(), "查询中...")
 * CProgressDialogUtils.cancelProgressDialog(requireActivity())
 * CProgressDialogUtils.cancelProgressDialog()
 */
object CProgressDialogUtils {

    private var sCircleProgressDialog: BubbleDialog? = null
    private var msg: String = Frame.getString(R.string.bubble_loading_title);

    fun showProgressDialog(activity: Activity) {
        showProgressDialog(activity, msg, false, null)
    }

    fun showProgressDialog(activity: Activity, msg: String) {
        showProgressDialog(activity, msg, false, null)
    }

    fun showProgressDialog(activity: Activity, listener: DialogInterface.OnCancelListener?) {
        showProgressDialog(activity, msg, true, listener)
    }

    fun showProgressDialog(
        activity: Activity,
        msg: String,
        listener: DialogInterface.OnCancelListener?
    ) {
        showProgressDialog(activity, msg, true, listener)
    }

    fun showProgressDialog(
        activity: Activity,
        msg: String,
        cancelable: Boolean,
        listener: DialogInterface.OnCancelListener?
    ) {
        if (activity.isFinishing) {
            return
        }
        if (sCircleProgressDialog == null) {
            sCircleProgressDialog = BubbleDialog(activity, msg)
            sCircleProgressDialog!!.setOwnerActivity(activity)
            sCircleProgressDialog!!.setOnCancelListener(listener)
            sCircleProgressDialog!!.setCancelable(cancelable)
        } else {
            if (activity == sCircleProgressDialog!!.ownerActivity) {
                sCircleProgressDialog!!.updateTitle(msg)
                sCircleProgressDialog!!.setCancelable(cancelable)
                sCircleProgressDialog!!.setOnCancelListener(listener)
            } else {
                //不相等,所以取消任何ProgressDialog
                cancelProgressDialog()
                sCircleProgressDialog = BubbleDialog(activity, msg)
                sCircleProgressDialog!!.setCancelable(cancelable)
                sCircleProgressDialog!!.setOwnerActivity(activity)
                sCircleProgressDialog!!.setOnCancelListener(listener)
            }
        }
        if (!sCircleProgressDialog!!.isShowing) {
            sCircleProgressDialog!!.show()
        }
    }

    fun cancelProgressDialog(activity: Activity) {
        if (sCircleProgressDialog != null && sCircleProgressDialog!!.isShowing) {
            if (sCircleProgressDialog!!.ownerActivity === activity) {
                sCircleProgressDialog!!.cancel()
                sCircleProgressDialog = null
            }
        }
    }

    fun cancelProgressDialog() {
        if (sCircleProgressDialog != null && sCircleProgressDialog!!.isShowing) {
            sCircleProgressDialog?.cancel()
            sCircleProgressDialog = null
        }
    }

}
