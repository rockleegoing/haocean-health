package com.ruoyi.app.api.repository

import com.ruoyi.app.model.entity.ButtomItemEntityVo
import com.ruoyi.app.model.entity.CaptchaImageEntity
import com.ruoyi.app.model.entity.LoginEntity
import com.ruoyi.app.model.entity.MineEntity
import com.ruoyi.app.model.entity.ResultEntity
import com.ruoyi.app.model.entity.WorkIndexEntity
import okhttp3.RequestBody


/**
 * 相关接口
 */
interface AuthRepoInterface {
    suspend fun getVerificationCode(): CaptchaImageEntity
    suspend fun login(code: String, username: String, password: String, value: String?): LoginEntity
    suspend fun getUserInfo(): MineEntity
    suspend fun getWorkData(): WorkIndexEntity
    suspend fun register(requestBody: RequestBody): LoginEntity
    suspend fun getHomeButtomData(): ResultEntity<List<ButtomItemEntityVo>>
}
