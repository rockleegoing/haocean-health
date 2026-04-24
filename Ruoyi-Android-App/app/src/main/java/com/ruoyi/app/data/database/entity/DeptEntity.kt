package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 部门实体类
 */
@Entity(tableName = "sys_dept")
data class DeptEntity(
    @PrimaryKey val deptId: Long,
    val parentId: Long,
    val ancestors: String,
    val deptName: String,
    val orderNum: Int,
    val leader: String?,
    val phone: String?,
    val email: String?,
    val status: String,
    val delFlag: String,
    val createBy: String?,
    val createTime: Long,
    val updateBy: String?,
    val updateTime: Long?,
    val remark: String?
)
