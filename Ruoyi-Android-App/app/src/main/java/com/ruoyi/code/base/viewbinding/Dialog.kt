@file:Suppress("unused")

package com.ruoyi.code.base.viewbinding

import android.app.Dialog
import androidx.viewbinding.ViewBinding
import kotlin.LazyThreadSafetyMode.NONE

/*
    作者：dylancaicoding
    链接：https://dylancaicoding.github.io/ViewBindingKTX/#/
    来源：github
 */
inline fun <reified VB : ViewBinding> Dialog.binding() = lazy(NONE) {
    inflateBinding<VB>(layoutInflater).also { setContentView(it.root) }
}
