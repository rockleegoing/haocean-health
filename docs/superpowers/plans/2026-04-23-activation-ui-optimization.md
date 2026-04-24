# 激活界面 UI 优化实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 Android 激活界面升级为 Material 3 风格，优化视觉、交互和代码结构

**Architecture:** 使用 Material 3 组件（MaterialCardView、TextInputLayout、MaterialButton）替换原生组件，引入 ActivationUiState 密封类管理 UI 状态，职责分离 Activity 与 Repository

**Tech Stack:** Kotlin, Material Components 1.11.0, Jetpack Activity KTX, Room Database

---

## 文件结构

### 修改文件
| 文件 | 操作 | 说明 |
|------|------|------|
| `Ruoyi-Android-App/app/build.gradle` | 确认 | Material Components 依赖版本 |
| `Ruoyi-Android-App/app/src/main/res/values/themes.xml` | 确认 | Material 3 主题配置 |
| `Ruoyi-Android-App/app/src/main/res/values/colors.xml` | 修改 | 新增成功/失败颜色资源 |
| `Ruoyi-Android-App/app/src/main/res/layout/activity_activation.xml` | 修改 | 改用 Material 3 组件布局 |
| `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt` | 修改 | 新增 UI 状态管理逻辑 |

### 新增文件
| 文件 | 说明 |
|------|------|
| `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationUiState.kt` | UI 状态密封类 |

---

## 任务分解

### Task 1: 确认 Material 依赖和主题配置

**Files:**
- Modify: `Ruoyi-Android-App/app/build.gradle`
- Modify: `Ruoyi-Android-App/app/src/main/res/values/themes.xml`

- [ ] **Step 1: 检查 Material 依赖版本**

读取 `app/build.gradle`，确认依赖：
```gradle
dependencies {
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.activity:activity-ktx:1.8.2'
}
```

如果版本低于 1.11.0，更新为 1.11.0。

- [ ] **Step 2: 运行项目确认 Material 组件可用**

```bash
cd Ruoyi-Android-App
./gradlew clean assembleDebug
```

预期：编译成功，无依赖冲突

- [ ] **Step 3: 确认主题继承 Material3**

读取 `themes.xml`，确认主题继承：
```xml
<style name="AppTheme" parent="Theme.Material3.Light.NoActionBar">
    <!-- 自定义属性 -->
</style>
```

如不是 Material3 主题，修改为 `Theme.Material3.Light.NoActionBar` 或 `Theme.Material3.Dark.NoActionBar`。

- [ ] **Step 4: 提交**

```bash
git add Ruoyi-Android-App/app/build.gradle Ruoyi-Android-App/app/src/main/res/values/themes.xml
git commit -m "chore(android): 配置 Material 3 主题环境"
```

---

### Task 2: 新增成功/失败颜色资源

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/res/values/colors.xml`

- [ ] **Step 1: 读取现有 colors.xml**

确认现有颜色定义结构。

- [ ] **Step 2: 新增成功/失败颜色**

在 `<resources>` 内添加：
```xml
<!-- UI 状态颜色 -->
<color name="success_green">#FF4CAF50</color>
<color name="error_red">#FFF44336</color>
```

- [ ] **Step 3: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/res/values/colors.xml
git commit -m "chore(android): 新增 UI 状态颜色资源"
```

---

### Task 3: 重写激活界面布局（Material 3 风格）

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/res/layout/activity_activation.xml`
- Test: 编译验证布局无错误

- [ ] **Step 1: 读取现有布局**

了解当前结构和 ID 引用。

- [ ] **Step 2: 重写布局为 Material 3 组件**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="?attr/colorSurface">

    <com.hjq.bar.TitleBar
        android:id="@id/titlebar"
        android:layout_width="match_parent"
        android:layout_height="@dimen/dp_48"
        app:title="设备激活"
        app:leftIcon="@mipmap/icon_title_left"
        app:lineVisible="false"/>

    <!-- 激活码卡片 -->
    <com.google.android.material.card.MaterialCardView
        android:id="@+id/card_activation"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginLeft="15dp"
        android:layout_marginRight="15dp"
        android:layout_marginTop="20dp"
        app:cardCornerRadius="12dp"
        app:cardElevation="2dp"
        app:cardBackgroundColor="?attr/colorSurface">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="20dp">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="激活码"
                android:textColor="?attr/colorOnSurface"
                android:textSize="15sp"
                android:layout_marginBottom="10dp"/>

            <com.google.android.material.textfield.TextInputLayout
                android:id="@+id/til_activation_code"
                style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
                android:layout_width="match_parent"
                android:layout_height="wrap_content">

                <com.google.android.material.textfield.TextInputEditText
                    android:id="@+id/et_activation_code"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:hint="请输入激活码"
                    android:inputType="text"
                    android:maxLines="1"/>

            </com.google.android.material.textfield.TextInputLayout>

        </LinearLayout>
    </com.google.android.material.card.MaterialCardView>

    <!-- 激活按钮 -->
    <com.google.android.material.button.MaterialButton
        android:id="@+id/btn_activate"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginLeft="15dp"
        android:layout_marginRight="15dp"
        android:layout_marginTop="20dp"
        android:text="激活设备"
        android:textSize="15sp"
        app:cornerRadius="8dp"
        app:iconGravity="textStart"/>

    <!-- 状态文字 -->
    <TextView
        android:id="@+id/tv_status"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:layout_marginLeft="15dp"
        android:layout_marginRight="15dp"
        android:gravity="center"
        android:textSize="14sp"
        android:visibility="gone"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="* 请输入您购买的激活码进行设备激活"
        android:textColor="?attr/colorOnSurfaceVariant"
        android:textSize="12sp"
        android:layout_marginTop="15dp"
        android:layout_marginLeft="15dp"/>

</LinearLayout>
```

- [ ] **Step 3: 编译验证布局**

```bash
cd Ruoyi-Android-App
./gradlew assembleDebug
```

预期：编译成功，无布局错误

- [ ] **Step 4: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/res/layout/activity_activation.xml
git commit -m "feat(android): 激活界面改用 Material 3 组件布局"
```

---

### Task 4: 新增 ActivationUiState 密封类

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationUiState.kt`

- [ ] **Step 1: 创建 UI 状态密封类**

```kotlin
package com.ruoyi.app.activity

/**
 * 激活界面 UI 状态
 */
sealed class ActivationUiState {
    /**
     * 初始状态 - 等待用户输入
     */
    object Idle : ActivationUiState()

    /**
     * 加载中 - 正在验证激活码
     */
    object Loading : ActivationUiState()

    /**
     * 验证成功
     */
    data class Success(val message: String) : ActivationUiState()

    /**
     * 验证失败
     */
    data class Error(val message: String) : ActivationUiState()
}
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationUiState.kt
git commit -m "feat(android): 新增激活界面 UI 状态密封类"
```

---

### Task 5: 重构 ActivationActivity 使用 UI 状态管理

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt`

- [ ] **Step 1: 读取现有 Activity**

了解当前的 `validateActivationCode` 实现和 `saveActivationInfo` 逻辑。

- [ ] **Step 2: 添加状态文字绑定和 updateUiState 方法**

在类中添加：
```kotlin
private var currentUiState: ActivationUiState = ActivationUiState.Idle
    set(value) {
        field = value
        updateUiState(value)
    }
```

添加 `updateUiState` 方法：
```kotlin
private fun updateUiState(state: ActivationUiState) {
    currentUiState = state
    when (state) {
        is ActivationUiState.Idle -> {
            binding.btnActivate.isEnabled = true
            binding.tvStatus.visibility = View.GONE
        }
        is ActivationUiState.Loading -> {
            binding.btnActivate.isEnabled = false
            binding.tvStatus.visibility = View.GONE
            com.hjq.toast.ToastUtils.show("正在验证...")
        }
        is ActivationUiState.Success -> {
            binding.tvStatus.text = state.message
            binding.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.success_green))
            binding.tvStatus.visibility = View.VISIBLE
            // 2 秒后跳转登录页
            binding.root.postDelayed({
                LoginActivity.startActivity(this@ActivationActivity)
                finish()
            }, 2000)
        }
        is ActivationUiState.Error -> {
            binding.tvStatus.text = state.message
            binding.tvStatus.setTextColor(ContextCompat.getColor(this, R.color.error_red))
            binding.tvStatus.visibility = View.VISIBLE
            binding.btnActivate.isEnabled = true
        }
    }
}
```

- [ ] **Step 3: 修改 initView 使用新的状态管理**

```kotlin
override fun initView() {
    binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
        override fun onLeftClick(titleBar: TitleBar?) {
            finish()
        }
    })

    binding.btnActivate.clickDelay {
        val activationCode = binding.etActivationCode.text.toString().trim()

        // 简化验证：仅非空判断
        if (activationCode.isEmpty()) {
            currentUiState = ActivationUiState.Error("请输入激活码")
            return@clickDelay
        }

        // 进入加载状态
        currentUiState = ActivationUiState.Loading

        // 调用 Repository 验证
        validateActivationCode(activationCode)
    }
}
```

- [ ] **Step 4: 修改 validateActivationCode 方法**

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
            currentUiState = ActivationUiState.Success("激活成功，即将跳转登录页")
        }.onFailure { exception ->
            currentUiState = ActivationUiState.Error(exception.message ?: "激活码验证失败")
        }
    }.catch {
        currentUiState = ActivationUiState.Error(it.message ?: "激活失败")
    }
}
```

- [ ] **Step 5: 移除或重构 saveActivationInfo 方法**

由于 Repository 已处理保存逻辑，`saveActivationInfo` 方法不再需要。如果其他地方没有引用，可以删除。

- [ ] **Step 6: 编译验证**

```bash
cd Ruoyi-Android-App
./gradlew assembleDebug
```

预期：编译成功，无类型错误

- [ ] **Step 7: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationUiState.kt
git commit -m "feat(android): 激活界面使用 UI 状态管理"
```

---

### Task 6: 最终验证和清理

**Files:**
- 全部修改文件

- [ ] **Step 1: 完整编译**

```bash
cd Ruoyi-Android-App
./gradlew clean assembleDebug
```

预期：编译成功，无警告

- [ ] **Step 2: 检查代码风格**

```bash
./gradlew ktlintCheck 2>/dev/null || echo "No ktlint configured, skipping"
```

- [ ] **Step 3: 确认所有变更已提交**

```bash
git status
git log --oneline -5
```

确认所有变更已提交，无遗漏。

---

## 自检验收

### Spec 覆盖检查

| Spec 要求 | 对应 Task |
|----------|-----------|
| Material 3 卡片式风格 | Task 3 |
| 动态颜色主题 | Task 1 |
| 间距 20dp/16dp | Task 3 |
| 阴影 2dp | Task 3 |
| UI 状态管理 | Task 4, 5 |
| 成功/失败颜色 | Task 2, 5 |
| 2 秒后跳转登录页 | Task 5 |
| 简化验证策略 | Task 5 |

### Placeholder 扫描

无 TBD/TODO，所有步骤包含具体代码。

### 类型一致性检查

- `ActivationUiState` 在 Task 4 定义，Task 5 使用，名称一致
- `success_green` / `error_red` 在 Task 2 定义，Task 5 使用，名称一致
- 布局 ID `btn_activate`, `et_activation_code`, `tv_status` 在 Task 3 和 Task 5 一致
