package com.ruoyi.app.model.entity

@kotlinx.serialization.Serializable
data class ButtomItemEntityVo(
    val defaultColor: String = "",
    val selectColor: String = "",
    val name: String = "",
    val defaultIcon: String = "",
    val selectIcon: String = ""
)