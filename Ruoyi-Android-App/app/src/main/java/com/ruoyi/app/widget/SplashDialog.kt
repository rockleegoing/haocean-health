package com.ruoyi.app.widget

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.ruoyi.app.R
import com.ruoyi.app.activity.common.WebActivity
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.databinding.DialogAgreementBinding
import com.ruoyi.code.Frame
import com.ruoyi.code.base.viewbinding.binding
import com.ruoyi.code.utils.ThreadUtils
import com.ruoyi.code.utils.clickDelay


class SplashDialog @JvmOverloads constructor(
    private val context: Context,
    private val clickListener : SplashDialogClickListener
) : Dialog(context) {

    private val binding: DialogAgreementBinding by binding()

    init {
        // 设置弹窗样式
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvPrivacyContent.movementMethod = LinkMovementMethod.getInstance();
        binding.tvPrivacyContent.setLinkTextColor(ContextCompat.getColor(context, R.color.purple_500));
        binding.tvPrivacyContent.isClickable = true
        binding.tvPrivacyContent.text =
            Html.fromHtml(Frame.getString(R.string.privacy_desc), Html.FROM_HTML_MODE_COMPACT, null
            ) { opening, tag, output, _ ->
                // 拦截 <a> 标签的点击事件
                if (tag.equals("a", ignoreCase = true) && !opening) {
                    val href = output.toString()
                    if (href.contains("privacy_policy")) {
                        // 跳转到隐私政策页面
                        WebActivity.startActivity(context, Frame.getString(R.string.login_privacy), ConfigApi.blogUrl + "/app/privacy.html")
                    } else if (href.contains("user_agreement")) {
                        // 跳转到用户协议页面
                        WebActivity.startActivity(context, Frame.getString(R.string.login_agreement), ConfigApi.blogUrl + "/app/agreement.html")
                    }
                }
            }
        binding.btnAgree.clickDelay {
            clickListener.onClick(1)
            dismiss()
        }
        binding.btnRefuse.clickDelay {
            clickListener.onClick(2)
            dismiss()
        }

    }

    override fun show() {
        // 检查Context是否有效（Activity是否存活）
        if (isContextValid()) {
            ThreadUtils.runMain {
                // 再次检查，确保在主线程执行时Context仍然有效
                if (isContextValid() && !isShowing) {
                    super.show()
                }
            }
        }
    }

    /**
     * 检查Context是否有效
     */
    private fun isContextValid(): Boolean {
        return when (context) {
            is android.app.Activity -> {
                !context.isFinishing && !context.isDestroyed
            }
            else -> {
                true  // 如果不是Activity，至少确保不为null
            }
        }
    }

    /**
     * 重写dismiss，确保在主线程执行
     */
    override fun dismiss() {
        if (ThreadUtils.isMainThread()) {
            super.dismiss()
        } else {
            ThreadUtils.runMain {
                if (isShowing) {
                    super.dismiss()
                }
            }
        }
    }

    public interface SplashDialogClickListener{
        fun onClick(type: Int)
    }
}