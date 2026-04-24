package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi /**
 * Android端数据同步响应实体
 * 用于 /app/sync 接口
 */
@kotlinx.serialization.Serializable
data class SyncDataEntity(
    val code: Int,
    val msg: String,
    val users: List<UserEntity> = emptyList(),
    val depts: List<DeptEntity> = emptyList(),
    val roles: List<RoleEntity> = emptyList(),
    val menus: List<MenuEntity> = emptyList()
)
