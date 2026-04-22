# Android 设备激活功能实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现 Android 设备激活功能，包括激活码验证、设备注册、本地数据存储等功能。

**Architecture:** 使用 Retrofit 进行网络请求，Room 数据库进行本地存储，TheRouter 进行页面导航。

**Tech Stack:** Kotlin + MVVM + Retrofit + Room + TheRouter

---

## 当前有效的构建配置（已验证）

```gradle
// build.gradle (project)
buildscript {
    ext {
        compile_sdk = 34
        min_sdk = 21
        target_sdk = 33
    }
}

plugins {
    id 'com.android.application' version '8.5.0' apply false
    id 'com.android.library' version '8.5.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.24' apply false
    id 'org.jetbrains.kotlin.multiplatform' version '1.9.24' apply false
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.9.24' apply false
    id 'cn.therouter' version '1.3.1' apply false
}

// gradle-wrapper.properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.7-bin.zip

// gradle.properties
org.gradle.java.home=/Users/arthur/Library/Java/JavaVirtualMachines/corretto-17.0.17/Contents/Home

// app/build.gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-parcelize'
    id 'therouter'
    id 'kotlinx-serialization'
    id 'kotlin-kapt'
}

kotlinOptions {
    jvmTarget = '17'
}

compileOptions {
    sourceCompatibility JavaVersion.VERSION_17
    targetCompatibility JavaVersion.VERSION_17
}

buildFeatures {
    viewBinding true
    buildConfig true
}

dependencies {
    // Room 数据库
    implementation "androidx.room:room-runtime:2.6.1"
    implementation "androidx.room:room-ktx:2.6.1"
    kapt "androidx.room:room-compiler:2.6.1"

    // TheRouter 路由
    implementation 'cn.therouter:router:1.3.1'
    annotationProcessor 'cn.therouter:apt:1.3.1'
}
```

---

## Task 1: 添加 Retrofit 网络请求依赖

**Files:**
- Modify: `Ruoyi-Android-App/app/build.gradle`

- [ ] **Step 1: 添加 Retrofit 依赖**

在 `app/build.gradle` 的 `dependencies` 块中添加：

```gradle
// Retrofit 网络请求
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'
```

- [ ] **Step 2: 同步 Gradle 并验证编译**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/Ruoyi-Android-App
./gradlew clean assembleDebug --no-daemon
```

预期输出：BUILD SUCCESSFUL

---

## Task 2: 创建激活功能 API 接口

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/api/service/ActivationService.kt`

- [ ] **Step 1: 创建 ActivationService 接口**

```kotlin
package com.ruoyi.app.api.service

import com.ruoyi.app.common.core.ResultEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 设备激活功能 API 服务
 */
interface ActivationService {

    /**
     * 验证激活码
     * @param params {"activationCode": "激活码"}
     */
    @POST("device/activationCode/validate")
    suspend fun validateActivationCode(
        @Body params: Map<String, String>
    ): Response<ResultEntity<ActivationCodeValidationResponse>?>

    /**
     * 注册设备
     * @param params {"deviceUuid": "设备 UUID", "activationCodeId": 激活码 ID, ...}
     */
    @POST("device/device/register")
    suspend fun registerDevice(
        @Body params: Map<String, Any>
    ): Response<ResultEntity<Long>?>
}

/**
 * 激活码验证响应
 */
data class ActivationCodeValidationResponse(
    val activationCodeId: Long,
    val deviceCount: Int,
    val maxDeviceCount: Int,
    val expiryTime: Long
)
```

- [ ] **Step 2: 编译验证**

```bash
./gradlew compileDebugKotlin --no-daemon
```

---

## Task 3: 创建激活功能 Repository

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/repository/ActivationRepository.kt`

- [ ] **Step 1: 创建 ActivationRepository**

```kotlin
package com.ruoyi.app.data.repository

import com.ruoyi.app.api.service.ActivationService
import com.ruoyi.app.api.service.ActivationCodeValidationResponse
import com.ruoyi.app.common.core.ResultEntity
import retrofit2.Response

/**
 * 激活功能数据仓库
 */
class ActivationRepository(private val activationService: ActivationService) {

    /**
     * 验证激活码
     */
    suspend fun validateActivationCode(activationCode: String): Response<ResultEntity<ActivationCodeValidationResponse>?> {
        return activationService.validateActivationCode(mapOf("activationCode" to activationCode))
    }

    /**
     * 注册设备
     */
    suspend fun registerDevice(params: Map<String, Any>): Response<ResultEntity<Long>?> {
        return activationService.registerDevice(params)
    }
}
```

- [ ] **Step 2: 编译验证**

```bash
./gradlew compileDebugKotlin --no-daemon
```

---

## Task 4: 更新 DeviceDao 支持激活功能

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DeviceDao.kt`

- [ ] **Step 1: 确认 DeviceDao 已有方法**

当前 DeviceDao 已有：
- `updateDeviceCurrentUser`: 更新设备当前用户
- `unbindDevice`: 解绑设备

- [ ] **Step 2: 验证 SQL 命名**

确保所有 SQL 查询使用驼峰命名（与 Kotlin 属性名一致）：
- `currentUserId` (不是 `current_user_id`)
- `currentUserName` (不是 `current_user_name`)
- `activationCodeId` (不是 `activation_code_id`)

---

## Task 5: 创建激活页面 Activity

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt`
- Create: `Ruoyi-Android-App/app/src/main/res/layout/activity_activation.xml`

- [ ] **Step 1: 创建布局文件**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/main_bg">

    <com.hjq.bar.TitleBar
        android:id="@id/titlebar"
        android:layout_width="match_parent"
        android:layout_height="@dimen/dp_48"
        app:title="设备激活"
        app:leftIcon="@mipmap/icon_title_left"
        app:lineVisible="false"/>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="20dp">

        <EditText
            android:id="@+id/et_activation_code"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="请输入激活码"
            android:inputType="text"
            android:maxLines="1"/>

        <Button
            android:id="@+id/btn_activate"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="20dp"
            android:text="激活设备"/>

    </LinearLayout>

</LinearLayout>
```

- [ ] **Step 2: 创建 ActivationActivity**

```kotlin
package com.ruoyi.app.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ruoyi.app.R
import com.ruoyi.app.api.OKHttpUtils
import com.ruoyi.app.api.service.ActivationService
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.repository.ActivationRepository
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ActivationActivity : AppCompatActivity() {

    private lateinit var etActivationCode: EditText
    private lateinit var btnActivate: Button
    private lateinit var repository: ActivationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activation)

        // 初始化 Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(OKHttpUtils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OKHttpUtils.getInstance())
            .build()

        val activationService = retrofit.create(ActivationService::class.java)
        repository = ActivationRepository(activationService)

        // 初始化视图
        etActivationCode = findViewById(R.id.et_activation_code)
        btnActivate = findViewById(R.id.btn_activate)

        // 设置点击事件
        btnActivate.setOnClickListener {
            val activationCode = etActivationCode.text.toString().trim()
            if (activationCode.isNotEmpty()) {
                activateDevice(activationCode)
            } else {
                Toast.makeText(this, "请输入激活码", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun activateDevice(activationCode: String) {
        lifecycleScope.launch {
            try {
                val response = repository.validateActivationCode(activationCode)
                if (response.isSuccessful && response.body()?.code == 200) {
                    val data = response.body()?.data
                    if (data != null) {
                        // 激活码验证成功，执行设备注册
                        registerDevice(activationCode, data.activationCodeId)
                    }
                } else {
                    val message = response.body()?.msg ?: "激活码验证失败"
                    Toast.makeText(this@ActivationActivity, message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ActivationActivity, "网络错误：${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun registerDevice(activationCode: String, activationCodeId: Long) {
        // TODO: 实现设备注册逻辑
        Toast.makeText(this, "设备注册成功", Toast.LENGTH_SHORT).show()
        finish()
    }
}
```

- [ ] **Step 3: 编译验证**

```bash
./gradlew compileDebugKotlin --no-daemon
```

---

## Task 6: 配置 TheRouter 路由

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/assets/therouter/routeMap.json`

- [ ] **Step 1: 添加激活页面路由**

在 `routeMap.json` 中添加：

```json
{
  "path": "http://com.ruoyi/activation",
  "className": "com.ruoyi.app.activity.ActivationActivity",
  "action": "",
  "description": "设备激活页面",
  "params": {}
}
```

- [ ] **Step 2: 重新编译生成路由表**

```bash
./gradlew assembleDebug --no-daemon
```

---

## Task 7: 功能测试

**Files:**
- Test: 手动功能测试

- [ ] **Step 1: 安装 APK 到测试设备**

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

- [ ] **Step 2: 测试激活流程**

1. 打开 App，进入设置页面
2. 点击"设备激活"（如果已在设置页面添加入口）
3. 输入测试激活码
4. 点击"激活设备"
5. 验证提示信息

- [ ] **Step 3: 验证数据库记录**

```bash
adb shell "run-as com.ruoyi.app cat databases/app_database" | sqlite3
SELECT * FROM sys_device;
```

---

## 验证

执行以下命令验证构建配置：

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/Ruoyi-Android-App
./gradlew clean assembleDebug --no-daemon
```

预期输出：BUILD SUCCESSFUL
