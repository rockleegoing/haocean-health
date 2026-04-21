@file:Suppress("unused")

package com.ruoyi.code.base.viewbinding

import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import kotlin.LazyThreadSafetyMode.*

/*
    作者：dylancaicoding
    链接：https://dylancaicoding.github.io/ViewBindingKTX/#/
    来源：github
 */
inline fun <reified VB : ViewBinding> ComponentActivity.binding(setContentView: Boolean = true) =
    lazy(NONE) {
        inflateBinding<VB>(layoutInflater).also { binding ->
            if (setContentView) setContentView(binding.root)
            if (binding is ViewDataBinding) binding.lifecycleOwner = this
        }
    }
