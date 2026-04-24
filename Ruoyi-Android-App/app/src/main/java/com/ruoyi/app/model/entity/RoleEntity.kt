package com.ruoyi.app.model.entity

/**
 * 角色实体
 */
@kotlinx.serialization.Serializable
data class RoleEntity(
    val roleId: Long = 0,
    val roleName: String = "",
    val roleKey: String = "",
    val roleSort: Int = 0,
    val dataScope: String? = null,
    val menuCheckStrictly: Boolean = false,
    val deptCheckStrictly: Boolean = false,
    val status: String = "0",
    val delFlag: String = "0",
    val createBy: String? = null,
    val createTime: String? = null,
    val updateBy: String? = null,
    val updateTime: String? = null,
    val remark: String? = null
)
