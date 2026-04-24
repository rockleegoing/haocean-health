package com.ruoyi.app.api.repository

import android.content.Context
import com.drake.net.Get
import com.drake.net.Post
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.api.OKHttpUtils
import com.ruoyi.app.model.entity.ButtomItemEntity
import com.ruoyi.app.model.entity.ButtomItemEntityVo
import com.ruoyi.app.model.entity.CaptchaImageEntity
import com.ruoyi.app.model.entity.LoginEntity
import com.ruoyi.app.model.entity.MineEntity
import com.ruoyi.app.model.entity.ResultEntity
import com.ruoyi.app.model.entity.SyncDataEntity
import com.ruoyi.app.model.entity.WorkIndexEntity
import com.ruoyi.app.model.request.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody

class AuthRepository(
    private var context: Context
) : AuthRepoInterface {

    companion object {
        private const val TAG = "AuthRepository"
    }

    override suspend fun getVerificationCode() = withContext(Dispatchers.IO) {
        Get<CaptchaImageEntity>(ConfigApi.getCaptchaImage).await()
    }

    override suspend fun login(
        code: String,
        username: String,
        password: String,
        uuid: String?
    ): LoginEntity = withContext(Dispatchers.IO) {
        Post<LoginEntity>(ConfigApi.login) {
            body = OKHttpUtils.getRequestBody(
                LoginRequest(
                    code,
                    password,
                    username,
                    uuid ?: ""
                )
            )
        }.await()
    }

    override suspend fun getUserInfo(): MineEntity = withContext(Dispatchers.IO) {
        Get<MineEntity>(ConfigApi.getInfo).await()
    }

    /**
     * 获取当前用户完整数据（用于离线登录后获取 permissions 等）
     * 调用 /app/sync?userId=xxx 获取当前用户的完整信息
     */
    override suspend fun syncCurrentUser(userId: Long): Result<SyncDataEntity> = withContext(Dispatchers.IO) {
        try {
            val url = "${ConfigApi.baseUrl}${ConfigApi.appSync}?userId=$userId"
            val data = Get<SyncDataEntity>(url).await()
            if (data.code == ConfigApi.SUCCESS) {
                Result.success(data)
            } else {
                Result.failure(Exception(data.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWorkData(): WorkIndexEntity = withContext(Dispatchers.IO) {
        Get<WorkIndexEntity>(ConfigApi.appWork).await()
    }

    override suspend fun register(requestBody: RequestBody): LoginEntity =
        withContext(Dispatchers.IO) {
            Post<LoginEntity>(ConfigApi.register) {
                body = requestBody
            }.await()
        }

    override suspend fun getHomeButtomData(): ResultEntity<List<ButtomItemEntityVo>> = withContext(Dispatchers.IO) {
        Get<ResultEntity<List<ButtomItemEntityVo>>>(ConfigApi.appHomeButtom).await()
    }
}

