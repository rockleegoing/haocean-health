package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * 部门实体
 */
@kotlinx.serialization.Serializable
data class DeptEntity(
    val deptId: Long = 0,
    val parentId: Long = 0,
    val ancestors: String = "",
    val deptName: String = "",
    val orderNum: Int = 0,
    val leader: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val status: String = "0",
    val delFlag: String = "0",
    val createBy: String? = null,
    val createTime: String? = null,
    val updateBy: String? = null,
    val updateTime: String? = null,
    val remark: String? = null
)
