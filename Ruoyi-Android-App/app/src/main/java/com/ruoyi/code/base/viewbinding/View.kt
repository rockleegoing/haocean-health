package com.ruoyi.code.base.viewbinding

import android.view.View
import androidx.viewbinding.ViewBinding
import com.ruoyi.app.R

inline fun <reified VB : ViewBinding> View.getBinding() = getBinding(VB::class.java)

/*
    作者：dylancaicoding
    链接：https://dylancaicoding.github.io/ViewBindingKTX/#/
    来源：github
 */
@Suppress("UNCHECKED_CAST")
fun <VB : ViewBinding> View.getBinding(clazz: Class<VB>) =
    getTag(R.id.tag_view_binding) as? VB ?: (clazz.getMethod("bind", View::class.java)
        .invoke(null, this) as VB)
        .also { setTag(R.id.tag_view_binding, it) }
