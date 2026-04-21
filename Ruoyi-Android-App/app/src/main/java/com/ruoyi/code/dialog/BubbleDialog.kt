package com.ruoyi.code.dialog


import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.RotateDrawable
import android.os.Bundle
import android.view.animation.LinearInterpolator
import androidx.annotation.StyleRes
import com.ruoyi.app.R
import com.ruoyi.app.databinding.DialogLoadingBinding
import com.ruoyi.code.base.viewbinding.binding
import com.ruoyi.code.utils.ThreadUtils.runMain

/**
 * iOS风格的加载对话框
 * @param title 加载对话框的标题
 * @how_to_user
 *   BubbleDialog(this.requireContext()).show()
 */
class BubbleDialog @JvmOverloads constructor(
    context: Context,
    var title: String? = context.getString(R.string.bubble_loading_title),
    @StyleRes themeResId: Int = R.style.BubbleDialog,
) : Dialog(context, themeResId) {

    private val binding: DialogLoadingBinding by binding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvMsg.text = title
        val rotateDrawable = binding.ivLoading.background as RotateDrawable
        ObjectAnimator.ofInt(rotateDrawable, "level", 0, 10000).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }

    override fun show() {
        runMain {
            super.show()
        }
    }

    /**
     * 更新标题文本
     */
    fun updateTitle(text: String) {
        if (isShowing) {
            runMain {
                binding.tvMsg.text = text
            }
        } else {
            title = text
        }
    }
}