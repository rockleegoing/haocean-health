@file:Suppress("unused")

package com.ruoyi.code.base.viewbinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlin.LazyThreadSafetyMode.*

/*
    作者：dylancaicoding
    链接：https://dylancaicoding.github.io/ViewBindingKTX/#/
    来源：github
 */
inline fun <reified VB : ViewBinding> ViewGroup.inflate() =
  inflateBinding<VB>(LayoutInflater.from(context), this, true)

inline fun <reified VB : ViewBinding> ViewGroup.binding(attachToParent: Boolean = false) = lazy(NONE) {
  inflateBinding<VB>(LayoutInflater.from(context), if (attachToParent) this else null, attachToParent)
}
