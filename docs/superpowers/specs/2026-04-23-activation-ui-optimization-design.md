# 激活界面 UI 优化设计文档

**文档版本**: v1.0
**创建日期**: 2026-04-23
**状态**: 已批准

---

## 一、优化目标

对 Android 端激活界面 (`ActivationActivity`) 进行全面优化，包括：
1. UI 视觉 - 采用 Material 3 风格，提升视觉品质
2. 交互体验 - 优化加载、反馈、导航流程
3. 代码结构 - 清晰的 UI 状态管理，职责分离

---

## 二、设计决策

### 2.1 视觉风格

| 项目 | 决策 | 说明 |
|------|------|------|
| **整体风格** | 现代卡片式 | 圆角 12dp + 白色卡片背景 + 轻盈阴影 |
| **配色方案** | Material 3 动态色 | Android 12+ 使用 `dynamicLightColorScheme` / `dynamicDarkColorScheme` |
| **间距密度** | 舒适宽松 | 卡片内边距 20dp，元素间距 16dp |
| **阴影层级** | 轻盈柔和 | 卡片 elevation 2dp，按钮 1dp 默认/2dp 按下 |

### 2.2 交互体验

| 项目 | 决策 | 说明 |
|------|------|------|
| **加载状态** | 简化处理 | 禁用按钮 + Toast 提示"正在验证" |
| **结果反馈** | 状态文字 + 颜色 | 输入框下方显示结果文字（绿色成功/红色失败），停留 2 秒后跳转 |
| **输入格式** | 基础款 | 无特殊格式化，placeholder "请输入激活码" |
| **验证策略** | 简化处理 | 前端仅非空判断，其他交由后端验证 |
| **成功后导航** | 跳转登录页 | 关闭激活页，打开 `LoginActivity` |

### 2.3 代码结构

| 项目 | 决策 | 说明 |
|------|------|------|
| **UI 状态管理** | `ActivationUiState` 密封类 | 管理 `Idle` / `Loading` / `Success` / `Error` 四种状态 |
| **职责分离** | Activity 仅处理 UI | 验证逻辑、数据存储交由 `ActivationRepository` |
| **颜色资源** | Material 3 `colorScheme` | 不再硬编码颜色值，使用 `MaterialTheme.colorScheme` |

---

## 三、实现细节

### 3.1 布局变更

**文件**: `app/src/main/res/layout/activity_activation.xml`

**主要改动**:
```xml
<!-- 新增卡片容器 -->
<com.google.android.material.card.MaterialCardView
    android:id="@+id/card_activation"
    app:cardCornerRadius="12dp"
    app:cardElevation="2dp"
    app:cardBackgroundColor="?attr/colorSurface">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="20dp">

        <TextView
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

<!-- 状态文字 -->
<TextView
    android:id="@+id/tv_status"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginTop="16dp"
    android:layout_marginLeft="15dp"
    android:layout_marginRight="15dp"
    android:gravity="center"
    android:visibility="gone"
    tools:visibility="visible"/>

<!-- 按钮改用 MaterialButton -->
<com.google.android.material.button.MaterialButton
    android:id="@+id/btn_activate"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginLeft="15dp"
    android:layout_marginRight="15dp"
    android:layout_marginTop="20dp"
    android:text="激活设备"
    android:textSize="15sp"
    app:cornerRadius="8dp"/>
```

### 3.2 Activity 代码变更

**文件**: `app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt`

**新增 UI 状态密封类**:
```kotlin
sealed class ActivationUiState {
    object Idle : ActivationUiState()
    object Loading : ActivationUiState()
    data class Success(val message: String) : ActivationUiState()
    data class Error(val message: String) : ActivationUiState()
}
```

**状态更新逻辑**:
```kotlin
private fun updateUiState(state: ActivationUiState) {
    when (state) {
        is ActivationUiState.Idle -> {
            binding.btnActivate.isEnabled = true
            binding.tvStatus.visibility = View.GONE
        }
        is ActivationUiState.Loading -> {
            binding.btnActivate.isEnabled = false
            binding.tvStatus.visibility = View.GONE
            ToastUtils.show("正在验证...")
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

### 3.3 颜色资源新增

**文件**: `app/src/main/res/values/colors.xml`

**新增颜色**:
```xml
<color name="success_green">#FF4CAF50</color>
<color name="error_red">#FFF44336</color>
```

### 3.4 主题配置

**文件**: `app/src/main/java/com/ruoyi/app/AppTheme.kt` (或现有主题文件)

**启用动态颜色**:
```kotlin
val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val context = LocalContext.current
    if (darkTheme) dynamicDarkColorScheme(context)
    else dynamicLightColorScheme(context)
} else {
    if (darkTheme) DarkColorScheme else LightColorScheme
}
```

---

## 四、依赖变更

**文件**: `app/build.gradle`

**新增/确认依赖**:
```gradle
dependencies {
    // Material Components (确认版本)
    implementation 'com.google.android.material:material:1.11.0'

    // Activity KTX (用于 ViewBinding)
    implementation 'androidx.activity:activity-ktx:1.8.2'
}
```

---

## 五、验收标准

### UI 视觉
- [ ] 卡片圆角 12dp，白色背景，2dp 阴影
- [ ] 输入框使用 `TextInputLayout` + `TextInputEditText`
- [ ] 按钮使用 `MaterialButton`，圆角 8dp
- [ ] Android 12+ 设备使用动态颜色主题

### 交互体验
- [ ] 点击按钮后禁用，显示"正在验证"Toast
- [ ] 成功时输入框下方显示绿色成功文字
- [ ] 失败时输入框下方显示红色错误文字
- [ ] 成功后 2 秒自动跳转登录页

### 代码质量
- [ ] 使用 `ActivationUiState` 管理状态
- [ ] 无硬编码颜色值，全部使用 `colorScheme` 或资源引用
- [ ] Activity 职责清晰，业务逻辑在 Repository 层

---

## 六、风险评估

| 风险 | 等级 | 应对措施 |
|------|------|----------|
| Material 组件与现有样式冲突 | 低 | 提前测试，必要时自定义 Style |
| 动态颜色在部分机型表现不一致 | 低 | 提供 fallback 颜色方案 |
| 2 秒延迟跳转用户觉得太慢 | 低 | 可根据反馈调整为 1.5 秒或立即跳转 |

---

## 七、后续扩展

1. **激活码扫描** - 支持扫描二维码录入激活码
2. **离线激活** - 支持离线模式生成激活凭证
3. **批量激活** - 企业版支持批量导入激活码

---

## 八、文件清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `app/src/main/res/layout/activity_activation.xml` | 修改 | 改用 Material 3 组件 |
| `app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt` | 修改 | 新增 UI 状态管理 |
| `app/src/main/res/values/colors.xml` | 修改 | 新增成功/失败颜色 |
| `app/build.gradle` | 确认 | 确保 Material 依赖 |
