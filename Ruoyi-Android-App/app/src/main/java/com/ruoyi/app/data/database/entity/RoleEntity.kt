package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 角色实体类
 */
@Entity(tableName = "sys_role")
data class RoleEntity(
    @PrimaryKey val roleId: Long,
    val roleName: String,
    val roleKey: String,
    val roleSort: Int,
    val dataScope: String?,
    val menuCheckStrictly: Boolean,
    val deptCheckStrictly: Boolean,
    val status: String,
    val delFlag: String,
    val createBy: String?,
    val createTime: Long,
    val updateBy: String?,
    val updateTime: Long?,
    val remark: String?
)
