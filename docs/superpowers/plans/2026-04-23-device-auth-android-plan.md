# Android 端存储完善实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将激活信息从 MMKV 迁移到 Room 数据库，实现完整的本地持久化和网络同步机制。

**Architecture:** 新增 ActivationRepository 封装数据访问逻辑，修改 ActivationActivity 使用 Room 存储，保持与现有 AuthRepository 相似的代码风格。

**Tech Stack:** Kotlin, Room, Coroutines, MMKV (现有)

---

### Task 1: 创建 ActivationRepository 接口

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/api/repository/ActivationRepoInterface.kt`

- [ ] **Step 1: 创建接口文件**

```kotlin
package com.ruoyi.app.api.repository

import com.ruoyi.app.data.database.entity.ActivationCodeEntity

/**
 * 激活码仓库接口
 */
interface ActivationRepoInterface {
    /**
     * 验证激活码并保存到 Room
     * @param codeValue 激活码值
     * @param deviceUuid 设备 UUID
     * @param deviceName 设备名称
     * @param deviceModel 设备型号
     * @param deviceOs 操作系统
     * @param appVersion App 版本
     * @return 验证结果
     */
    suspend fun validateAndSave(
        codeValue: String,
        deviceUuid: String,
        deviceName: String,
        deviceModel: String,
        deviceOs: String,
        appVersion: String
    ): Result<ActivationResponse>

    /**
     * 获取本地激活状态
     */
    suspend fun getActivationStatus(): ActivationStatus?

    /**
     * 同步离线数据到后端
     */
    suspend fun syncOfflineData(): List<ActivationCodeEntity>
}

/**
 * 激活响应数据
 */
data class ActivationResponse(
    val success: Boolean,
    val message: String,
    val activationCodeId: Long,
    val deviceCount: Int,
    val maxDeviceCount: Int,
    val expiryTime: Long
)

/**
 * 激活状态
 */
data class ActivationStatus(
    val isActivated: Boolean,
    val activationCodeId: Long?,
    val activationCodeValue: String?,
    val expiryTime: Long?
)
```

- [ ] **Step 2: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/Ruoyi-Android-App
git add app/src/main/java/com/ruoyi/app/api/repository/ActivationRepoInterface.kt
git commit -m "feat(android): 创建激活码仓库接口"
```

---

### Task 2: 创建 ActivationRepository 实现类

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/api/repository/ActivationRepository.kt`

- [ ] **Step 1: 创建实现类文件**

```kotlin
package com.ruoyi.app.api.repository

import android.content.Context
import com.drake.net.Post
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.api.OKHttpUtils
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.ActivationCodeEntity
import com.ruoyi.app.model.entity.ResultEntity
import com.ruoyi.app.data.database.entity.DeviceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.ruoyi.app.data.database.dao.ActivationCodeDao
import com.ruoyi.app.data.database.dao.DeviceDao

/**
 * 激活码仓库实现类
 */
class ActivationRepository(
    private val context: Context
) : ActivationRepoInterface {

    companion object {
        private const val TAG = "ActivationRepository"
    }

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    private val activationCodeDao: ActivationCodeDao by lazy {
        database.activationCodeDao()
    }

    private val deviceDao: DeviceDao by lazy {
        database.deviceDao()
    }

    override suspend fun validateAndSave(
        codeValue: String,
        deviceUuid: String,
        deviceName: String,
        deviceModel: String,
        deviceOs: String,
        appVersion: String
    ): Result<ActivationResponse> = withContext(Dispatchers.IO) {
        try {
            // 1. 调用后端 API 验证激活码
            val params = mapOf(
                "codeValue" to codeValue,
                "deviceUuid" to deviceUuid,
                "deviceName" to deviceName,
                "deviceModel" to deviceModel,
                "deviceOs" to deviceOs,
                "appVersion" to appVersion
            )

            val response = Post<ResultEntity<ActivationApiResponse>>(
                ConfigApi.baseUrl + "/device/activationCode/validate"
            ) {
                body = OKHttpUtils.getRequestBody(params)
            }.await()

            if (response.code == 200 && response.data != null) {
                // 2. 验证成功，保存到 Room
                val apiData = response.data
                val codeEntity = ActivationCodeEntity(
                    codeId = apiData.activationCodeId,
                    codeValue = codeValue,
                    status = "1", // 已使用
                    expireTime = apiData.expiryTime,
                    bindDeviceId = deviceUuid,
                    bindUserId = null,
                    remark = "",
                    createBy = "system",
                    createTime = System.currentTimeMillis(),
                    updateBy = null,
                    updateTime = null
                )
                activationCodeDao.insertActivationCode(codeEntity)

                // 3. 保存或更新设备信息
                val deviceEntity = DeviceEntity(
                    deviceId = apiData.activationCodeId, // 临时使用激活码 ID
                    deviceUuid = deviceUuid,
                    deviceName = deviceName,
                    deviceModel = deviceModel,
                    deviceOs = deviceOs,
                    appVersion = appVersion,
                    currentUserId = null,
                    currentUserName = null,
                    activationCodeId = apiData.activationCodeId,
                    lastSyncTime = System.currentTimeMillis(),
                    lastLoginTime = System.currentTimeMillis(),
                    lastLoginIp = null,
                    status = "0", // 离线
                    remark = "",
                    createBy = "system",
                    createTime = System.currentTimeMillis(),
                    updateBy = null,
                    updateTime = null
                )
                deviceDao.insertDevice(deviceEntity)

                Result.success(
                    ActivationResponse(
                        success = true,
                        message = response.msg ?: "激活成功",
                        activationCodeId = apiData.activationCodeId,
                        deviceCount = apiData.deviceCount,
                        maxDeviceCount = apiData.maxDeviceCount,
                        expiryTime = apiData.expiryTime
                    )
                )
            } else {
                Result.failure(Exception(response.msg ?: "激活码验证失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getActivationStatus(): ActivationStatus? = withContext(Dispatchers.IO) {
        // 获取最新的已使用激活码
        val codes = activationCodeDao.getAllActivationCodes()
        val usedCode = codes.firstOrNull { it.status == "1" }

        if (usedCode != null) {
            ActivationStatus(
                isActivated = true,
                activationCodeId = usedCode.codeId,
                activationCodeValue = usedCode.codeValue,
                expiryTime = usedCode.expireTime
            )
        } else {
            null
        }
    }

    override suspend fun syncOfflineData(): List<ActivationCodeEntity> = withContext(Dispatchers.IO) {
        // 获取所有需要同步的激活码
        val codes = activationCodeDao.getAllActivationCodes()
        // 后续实现：筛选未同步到后端的记录并上传
        return@withContext codes
    }

    /**
     * 后端 API 响应数据结构
     */
    data class ActivationApiResponse(
        val activationCodeId: Long,
        val deviceCount: Int,
        val maxDeviceCount: Int,
        val expiryTime: Long
    )
}
```

- [ ] **Step 2: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/Ruoyi-Android-App
git add app/src/main/java/com/ruoyi/app/api/repository/ActivationRepository.kt
git commit -m "feat(android): 创建激活码仓库实现类"
```

---

### Task 3: 修改 ActivationActivity 使用 Room 存储

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt
```

- [ ] **Step 2: 添加 ActivationRepository 成员变量**

在类中添加：

```kotlin
private val activationRepository: ActivationRepository by lazy {
    ActivationRepository(applicationContext)
}
```

- [ ] **Step 3: 修改 validateActivationCode 方法**

将原有的 `saveActivationInfo` 调用替换为：

```kotlin
private fun validateActivationCode(activationCode: String) {
    scopeNetLife {
        // 获取设备信息
        val deviceUuid = getDeviceUuid()
        val deviceName = Build.MODEL
        val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
        val deviceOs = "Android ${Build.VERSION.RELEASE}"
        val appVersion = getAppVersionName()

        // 使用 ActivationRepository 验证并保存
        val result = activationRepository.validateAndSave(
            codeValue = activationCode,
            deviceUuid = deviceUuid,
            deviceName = deviceName,
            deviceModel = deviceModel,
            deviceOs = deviceOs,
            appVersion = appVersion
        )

        result.onSuccess { response ->
            com.hjq.toast.ToastUtils.show("激活码验证成功")
            // 跳转登录页
            LoginActivity.startActivity(this@ActivationActivity)
            finish()
        }.onFailure { exception ->
            com.hjq.toast.ToastUtils.show(exception.message ?: "激活码验证失败")
        }
    }.catch {
        com.hjq.toast.ToastUtils.show(it.message)
    }
}
```

- [ ] **Step 4: 删除或注释掉原有的 saveActivationInfo 方法**

```kotlin
// 原方法已废弃，改用 ActivationRepository
// private fun saveActivationInfo(response: ActivationCodeValidationResponse, activationCode: String) {
//     MMKV.defaultMMKV().encode("activation_code_id", response.activationCodeId)
//     MMKV.defaultMMKV().encode("activation_code_value", activationCode)
//     MMKV.defaultMMKV().encode("activation_expiry_time", response.expiryTime)
//     MMKV.defaultMMKV().encode("is_activated", true)
// }
```

- [ ] **Step 5: 删除原有的 ActivationCodeValidationResponse 类**

（已在 ActivationRepository 中定义 ActivationApiResponse）

- [ ] **Step 6: 验证文件内容**

```bash
cat Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt
```

- [ ] **Step 7: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/Ruoyi-Android-App
git add app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt
git commit -m "feat(android): ActivationActivity 使用 ActivationRepository 存储激活信息"
```

---

### Task 4: 添加离线模式支持（可选）

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/api/repository/ActivationRepository.kt`

- [ ] **Step 1: 添加离线验证方法**

在 `ActivationRepository` 中添加：

```kotlin
/**
 * 离线模式下预加载激活码
 */
suspend fun preloadActivationCode(code: ActivationCodeEntity) {
    activationCodeDao.insertActivationCode(code)
}

/**
 * 检查激活码是否在本地有效
 */
suspend fun isActivationCodeValidLocally(codeValue: String): Boolean {
    val code = activationCodeDao.getActivationCodeByValue(codeValue)
    return code != null && code.status == "0" &&
           (code.expireTime == null || code.expireTime > System.currentTimeMillis())
}
```

- [ ] **Step 2: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/Ruoyi-Android-App
git add app/src/main/java/com/ruoyi/app/api/repository/ActivationRepository.kt
git commit -m "feat(android): 添加离线模式支持方法"
```

---

### Task 5: 编译测试 Android 代码

**Files:**
- Test: Gradle 编译

- [ ] **Step 1: 编译 Android 项目**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/Ruoyi-Android-App
./gradlew clean assembleDebug
```

预期输出：`BUILD SUCCESSFUL`

- [ ] **Step 2: 检查是否有编译错误**

如有错误，修复后重新编译。

---

## 验证

执行完所有任务后，应验证：
- [ ] ActivationRepository 成功创建
- [ ] ActivationActivity 使用 Room 存储激活信息
- [ ] Android 项目编译成功
- [ ] 激活码成功写入 Room 数据库

---

## 后续任务

Android 端存储完善后，继续执行：
- `2026-04-23-device-auth-frontend-plan.md` - 前端体验优化
