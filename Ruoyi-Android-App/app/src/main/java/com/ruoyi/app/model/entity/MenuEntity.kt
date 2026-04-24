package com.ruoyi.app.model.entity

/**
 * 菜单实体
 */
@kotlinx.serialization.Serializable
data class MenuEntity(
    val menuId: Long = 0,
    val menuName: String = "",
    val parentId: Long = 0,
    val orderNum: Int = 0,
    val path: String = "",
    val component: String? = null,
    val query: String? = null,
    val routeName: String? = null,
    val isFrame: Int = 0,
    val isCache: Int = 0,
    val menuType: String = "",
    val visible: String = "0",
    val status: String = "0",
    val perms: String? = null,
    val icon: String? = null,
    val createBy: String? = null,
    val createTime: String? = null,
    val updateBy: String? = null,
    val updateTime: String? = null,
    val remark: String? = null
)
