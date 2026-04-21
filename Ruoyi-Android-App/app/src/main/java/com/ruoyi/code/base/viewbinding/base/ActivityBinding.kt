package com.ruoyi.code.base.viewbinding.base

import android.app.Activity
import android.view.View
import androidx.viewbinding.ViewBinding

/*
    作者：dylancaicoding
    链接：https://dylancaicoding.github.io/ViewBindingKTX/#/
    来源：github
 */
interface ActivityBinding<VB : ViewBinding> {
    val binding: VB
    fun Activity.setContentViewWithBinding(): View
}

class ActivityBindingDelegate<VB : ViewBinding> : ActivityBinding<VB> {
    private lateinit var _binding: VB

    override val binding: VB get() = _binding

    override fun Activity.setContentViewWithBinding(): View {
        _binding = ViewBindingUtil.inflateWithGeneric(this, layoutInflater)
        setContentView(_binding.root)
        return _binding.root
    }
}