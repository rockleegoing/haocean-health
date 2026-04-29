package com.ruoyi.app.model

import androidx.fragment.app.Fragment

data class MenuItem(
    val id: Int,                        // 菜单项ID
    val name: String,                  // 菜单名称
    val iconRes: Int,                  // 图标资源ID
    val fragmentClass: Class<out Fragment>  // 对应的Fragment类
)
