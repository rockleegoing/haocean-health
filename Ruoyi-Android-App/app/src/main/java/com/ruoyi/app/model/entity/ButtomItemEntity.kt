package com.ruoyi.app.model.entity

import android.graphics.drawable.Drawable

data class ButtomItemEntity(
    var defaultColor: String = "",
    var selectColor: String = "",
    var name: String = "",
    var defaultIcon: String = "",
    var selectIcon: String = "",
    val drawable: Drawable? = null
)