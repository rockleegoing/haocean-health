@file:Suppress("unused")

package com.ruoyi.code.base.viewbinding

import androidx.viewbinding.ViewBinding
import com.google.android.material.navigation.NavigationView

/*
    作者：dylancaicoding
    链接：https://dylancaicoding.github.io/ViewBindingKTX/#/
    来源：github
 */
inline fun <reified VB : ViewBinding> NavigationView.updateHeaderView(index: Int = 0, block: VB.() -> Unit) =
  getHeaderView(index)?.getBinding<VB>()?.run(block)