package com.ruoyi.app.data.database.entity

/**
 * API 用户实体到数据库用户实体的映射器
 */
object UserMapper {

    /**
     * 将 API UserEntity 转换为 Room UserEntity
     */
    fun fromApi(apiUser: com.ruoyi.app.model.entity.UserEntity): UserEntity {
        return UserEntity(
            userId = apiUser.userId,
            deptId = null,
            userName = apiUser.userName,
            nickName = apiUser.nickName,
            userType = null,
            email = apiUser.email,
            phonenumber = apiUser.phonenumber,
            sex = apiUser.sex,
            avatar = apiUser.avatar,
            password = "", // 密码不应从 API 获取
            status = apiUser.status,
            delFlag = "0",
            loginIp = null,
            loginDate = null,
            createBy = null,
            createTime = System.currentTimeMillis(),
            updateBy = null,
            updateTime = null,
            remark = null
        )
    }
}
