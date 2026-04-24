package com.ruoyi.app.model

import androidx.fragment.app.Fragment

data class MenuItem(
    val id: Int,
    val name: String,
    val iconRes: Int,
    val fragmentClass: Class<out Fragment>
)
