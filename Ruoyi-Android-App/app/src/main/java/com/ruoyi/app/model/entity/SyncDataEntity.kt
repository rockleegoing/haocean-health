package com.ruoyi.app.model.entity

import kotlinx.serialization.InternalSerializationApi

@InternalSerializationApi
/**
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
    val menus: List<MenuEntity> = emptyList(),
    val categories: List<CategoryDTO> = emptyList(),
    val units: List<UnitDTO> = emptyList(),
    val currentUser: CurrentUserInfo? = null
)

@kotlinx.serialization.Serializable
data class CategoryDTO(
    val categoryId: Long,
    val categoryName: String,
    val categoryCode: String?,
    val orderNum: Int?,
    val status: String?,
    val delFlag: String?,
    val createBy: String?,
    val createTime: String?,
    val updateBy: String?,
    val updateTime: String?,
    val remark: String?
)

@kotlinx.serialization.Serializable
data class UnitDTO(
    val unitId: Long,
    val unitName: String,
    val industryCategoryId: Long?,
    val industryCategoryName: String?,
    val region: String?,
    val supervisionType: String?,
    val creditCode: String?,
    val legalPerson: String?,
    val contactPhone: String?,
    val businessAddress: String?,
    val latitude: Double?,
    val longitude: Double?,
    val status: String?,
    val delFlag: String?,
    val createTime: String?,
    val updateTime: String?,
    val remark: String?
)

/**
 * 当前用户完整信息
 * 包含用户基本信息、角色、权限等
 */
@kotlinx.serialization.Serializable
data class CurrentUserInfo(
    val userId: Long,
    val userName: String,
    val nickName: String?,
    val email: String?,
    val phonenumber: String?,
    val sex: String?,
    val avatar: String?,
    val status: String?,
    val roles: List<String> = emptyList(),
    val permissions: List<String> = emptyList(),
    val pwdChrtype: Int = 0,
    val isDefaultModifyPwd: Boolean = false,
    val isPasswordExpired: Boolean = false
)
