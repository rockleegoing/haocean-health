package com.ruoyi.app.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.bumptech.glide.Glide
import com.ruoyi.app.R
import com.ruoyi.app.databinding.DialogLikeMeBinding
import com.ruoyi.code.base.viewbinding.binding
import com.ruoyi.code.utils.ThreadUtils
import com.ruoyi.code.utils.clickDelay

class LikeMeDialog @JvmOverloads constructor(
    private val context: Context  // 保存Context引用用于检查
) : Dialog(context) {

    private val binding: DialogLikeMeBinding by binding()

    init {
        // 设置弹窗样式
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置关闭按钮点击事件
        binding.btnClose.clickDelay {
            dismiss()
        }

        // 使用当前Dialog的context而非全局Context，避免内存泄漏
        Glide.with(this.context)
            .load("https://ruoyi-go.qiqjia.com/zanshangma.webp")
            .error(R.mipmap.profile)
            .into(binding.ivImage)
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
}