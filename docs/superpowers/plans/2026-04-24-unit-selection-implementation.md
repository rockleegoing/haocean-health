# 选择单位模块实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现执法单位选择功能，包括后端 API 和 Android 端选择页面

**Architecture:** 离线优先设计，单位数据通过 /app/sync 同步到 Room DB，选择单位页面直接从本地查询，支持按位置距离排序

**Tech Stack:** Spring Boot (RuoYi-Vue), Kotlin, Room, TheRouter, MMKV

---

## 文件结构

### 后端 (RuoYi-Vue)
```
ruoyi-framework/src/main/java/com/ruoyi/framework/config/SecurityConfig.java  ← 添加 /app/unit/**
ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLoginController.java  ← 修改 /app/sync
ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppUnitController.java  ← 新增匿名单位接口
```

### Android (Ruoyi-Android-App)
```
app/src/main/java/com/ruoyi/app/data/database/entity/UnitEntity.kt  ← 新增
app/src/main/java/com/ruoyi/app/data/database/dao/UnitDao.kt  ← 新增
app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt  ← 修改
app/src/main/java/com/ruoyi/app/api/repository/UnitRepository.kt  ← 新增
app/src/main/java/com/ruoyi/app/api/UnitApi.kt  ← 新增
app/src/main/java/com/ruoyi/app/activity/SelectUnitActivity.kt  ← 新增
app/src/main/java/com/ruoyi/app/activity/SelectUnitViewModel.kt  ← 新增
app/src/main/java/com/ruoyi/app/model/SelectedUnitManager.kt  ← 新增
app/src/main/java/com/ruoyi/app/utils/DistanceUtils.kt  ← 新增
app/src/main/java/com/ruoyi/app/model/Constant.kt  ← 修改
app/src/main/java/com/ruoyi/app/sync/SyncManager.kt  ← 修改
app/src/main/java/com/ruoyi/app/activity/LoginActivity.kt  ← 修改
app/src/main/java/com/ruoyi/app/activity/MainActivity.kt  ← 修改
app/src/main/res/layout/activity_select_unit.xml  ← 新增
app/src/main/res/values/strings.xml  ← 修改
```

---

## 阶段一：后端修改

### Task 1: SecurityConfig 添加 /app/unit/** 到 permitAll

**Files:**
- Modify: `ruoyi-framework/src/main/java/com/ruoyi/framework/config/SecurityConfig.java:103`

- [ ] **Step 1: 添加 /app/unit/** 到 permitAll 列表**

找到 SecurityConfig.java 第 103 行附近的 permitAll 配置：

```java
// 找到这行
.requestMatchers("/login", "/register", "/captchaImage", "/health", "/prod-api/health", "/app/**").permitAll()

// 修改为
.requestMatchers("/login", "/register", "/captchaImage", "/health", "/prod-api/health", "/app/**", "/app/unit/**").permitAll()
```

- [ ] **Step 2: 验证修改**

Run: `grep -n "app/unit" ruoyi-framework/src/main/java/com/ruoyi/framework/config/SecurityConfig.java`
Expected: 显示包含 `/app/unit` 的行

---

### Task 2: 创建 AppUnitController (匿名可访问的单位接口)

**Files:**
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppUnitController.java`

- [ ] **Step 1: 创建 AppUnitController**

```java
package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysUnit;
import com.ruoyi.system.service.ISysUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Android 端执法单位接口（匿名可访问）
 */
@RestController
@RequestMapping("/app/unit")
public class AppUnitController extends BaseController {

    @Autowired
    private ISysUnitService unitService;

    /**
     * 获取单位列表
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list() {
        List<SysUnit> list = unitService.selectUnitList(new SysUnit());
        return AjaxResult.success(list);
    }

    /**
     * 获取单位详情
     */
    @Anonymous
    @GetMapping("/{unitId}")
    public AjaxResult getInfo(@PathVariable Long unitId) {
        SysUnit unit = unitService.selectUnitById(unitId);
        return AjaxResult.success(unit);
    }
}
```

- [ ] **Step 2: 验证文件创建**

Run: `ls -la ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppUnitController.java`
Expected: 文件存在

---

### Task 3: 修改 /app/sync 接口，添加 units 数据

**Files:**
- Modify: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLoginController.java`

- [ ] **Step 1: 查看当前 /app/sync 方法**

找到 `appSync` 方法的位置（约第 128-145 行）

- [ ] **Step 2: 添加 units 到响应**

```java
@com.ruoyi.common.annotation.Anonymous
@GetMapping("/app/sync")
public AjaxResult appSync()
{
    AjaxResult ajax = AjaxResult.success();

    // 1. 获取所有用户（含明文密码），使用 mapper 直接查询绕过数据权限
    List<SysUser> users = userMapper.selectUserListWithoutDataScope();
    ajax.put("users", users);

    // 2. 获取所有部门
    List<SysDept> depts = deptMapper.selectDeptListWithoutDataScope();
    ajax.put("depts", depts);

    // 3. 获取所有角色
    List<SysRole> roles = roleMapper.selectRoleListWithoutDataScope();
    ajax.put("roles", roles);

    // 4. 获取所有菜单
    List<SysMenu> menus = menuService.selectMenuAll();
    ajax.put("menus", menus);

    // 5. 获取所有执法单位（新增）
    List<SysUnit> units = unitService.selectUnitList(new SysUnit());
    ajax.put("units", units);

    return ajax;
}
```

- [ ] **Step 3: 添加必要的导入**

```java
import com.ruoyi.system.domain.SysUnit;
import com.ruoyi.system.service.ISysUnitService;
```

- [ ] **Step 4: 注入 ISysUnitService**

```java
@Autowired
private ISysUnitService unitService;
```

- [ ] **Step 5: 验证后端启动**

Run: `cd RuoYi-Vue && mvn spring-boot:run -pl ruoyi-admin`
Expected: 启动成功，无报错

- [ ] **Step 6: 测试接口**

Run: `curl http://localhost:8080/app/sync | head -c 500`
Expected: JSON 响应包含 units 字段

---

## 阶段二：Android 基础

### Task 4: 创建 UnitEntity (Room Entity)

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/data/database/entity/UnitEntity.kt`

- [ ] **Step 1: 创建 UnitEntity**

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 执法单位实体类
 */
@Entity(tableName = "sys_unit")
data class UnitEntity(
    @PrimaryKey val unitId: Long,
    val unitName: String,
    val industryCategory: String?,
    val region: String?,
    val supervisionType: String?,
    val creditCode: String?,
    val legalPerson: String?,
    val contactPhone: String?,
    val businessAddress: String?,
    val latitude: Double?,
    val longitude: Double?,
    val status: String,
    val delFlag: String,
    val createTime: Long,
    val updateTime: Long?,
    val remark: String?
)
```

- [ ] **Step 2: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "UnitEntity|BUILD"`
Expected: 无 UnitEntity 相关错误，BUILD SUCCESSFUL

---

### Task 5: 创建 UnitDao

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/data/database/dao/UnitDao.kt`

- [ ] **Step 1: 创建 UnitDao**

```kotlin
package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.UnitEntity

/**
 * 执法单位数据访问对象
 */
@Dao
interface UnitDao {

    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' ORDER BY createTime DESC")
    suspend fun getAllUnits(): List<UnitEntity>

    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' ORDER BY createTime DESC LIMIT :limit OFFSET :offset")
    suspend fun getUnitsPaged(limit: Int, offset: Int): List<UnitEntity>

    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' AND unitName LIKE '%' || :keyword || '%' ORDER BY createTime DESC")
    suspend fun searchUnits(keyword: String): List<UnitEntity>

    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' AND industryCategory = :category ORDER BY createTime DESC")
    suspend fun getUnitsByCategory(category: String): List<UnitEntity>

    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' AND region = :region ORDER BY createTime DESC")
    suspend fun getUnitsByRegion(region: String): List<UnitEntity>

    @Query("SELECT * FROM sys_unit WHERE unitId = :unitId")
    suspend fun getUnitById(unitId: Long): UnitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnits(units: List<UnitEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnit(unit: UnitEntity)

    @Delete
    suspend fun deleteUnit(unit: UnitEntity)

    @Query("DELETE FROM sys_unit")
    suspend fun deleteAllUnits()

    @Query("SELECT COUNT(*) FROM sys_unit")
    suspend fun getUnitCount(): Int
}
```

- [ ] **Step 2: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "UnitDao|BUILD"`
Expected: 无 UnitDao 相关错误，BUILD SUCCESSFUL

---

### Task 6: 更新 AppDatabase 添加 UnitDao

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt`

- [ ] **Step 1: 查看当前 AppDatabase 结构**

找到 entities 列表和 abstract methods

- [ ] **Step 2: 添加 UnitEntity 到 entities 列表**

```kotlin
@Database(
    entities = [
        UserEntity::class,
        RoleEntity::class,
        DeptEntity::class,
        IndustryCategoryEntity::class,
        ActivationCodeEntity::class,
        DeviceEntity::class,
        SyncQueueEntity::class,
        DataVersionEntity::class,
        UnitEntity::class  // 新增
    ],
    version = 2,  // 需要增加版本号
    exportSchema = false
)
```

- [ ] **Step 3: 添加 abstract 方法**

```kotlin
abstract fun unitDao(): UnitDao
```

- [ ] **Step 4: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "AppDatabase|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 7: 创建 SelectedUnitManager

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/model/SelectedUnitManager.kt`

- [ ] **Step 1: 创建 SelectedUnitManager**

```kotlin
package com.ruoyi.app.model

import com.tencent.mmkv.MMKV

/**
 * 当前选中单位管理器
 * 使用 MMKV 持久化存储
 */
object SelectedUnitManager {

    private const val KEY_SELECTED_UNIT_ID = "selected_unit_id"
    private const val KEY_SELECTED_UNIT_NAME = "selected_unit_name"

    fun saveSelectedUnit(unitId: Long, unitName: String) {
        MMKV.defaultMMKV().encode(KEY_SELECTED_UNIT_ID, unitId)
        MMKV.defaultMMKV().encode(KEY_SELECTED_UNIT_NAME, unitName)
    }

    fun getSelectedUnitId(): Long? {
        val id = MMKV.defaultMMKV().decodeLong(KEY_SELECTED_UNIT_ID, -1)
        return if (id == -1L) null else id
    }

    fun getSelectedUnitName(): String? {
        return MMKV.defaultMMKV().decodeString(KEY_SELECTED_UNIT_NAME)
    }

    fun clearSelectedUnit() {
        MMKV.defaultMMKV().removeValueForKey(KEY_SELECTED_UNIT_ID)
        MMKV.defaultMMKV().removeValueForKey(KEY_SELECTED_UNIT_NAME)
    }

    fun hasSelectedUnit(): Boolean {
        return getSelectedUnitId() != null
    }
}
```

- [ ] **Step 2: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "SelectedUnitManager|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 8: 创建 DistanceUtils

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/utils/DistanceUtils.kt`

- [ ] **Step 1: 创建 DistanceUtils**

```kotlin
package com.ruoyi.app.utils

import kotlin.math.*

/**
 * 距离计算工具类
 * 使用 Haversine 公式计算两点之间的地球表面距离
 */
object DistanceUtils {

    private const val EARTH_RADIUS_METERS = 6371000.0

    /**
     * 计算两点之间的距离（单位：米）
     *
     * @param lat1 第一个点的纬度
     * @param lon1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lon2 第二个点的经度
     * @return 距离（米）
     */
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return EARTH_RADIUS_METERS * c
    }

    /**
     * 格式化距离显示
     *
     * @param distanceMeters 距离（米）
     * @return 格式化后的字符串，如 "1.5km"、"500m"
     */
    fun formatDistance(distanceMeters: Double): String {
        return when {
            distanceMeters < 1000 -> "${distanceMeters.toInt()}m"
            else -> String.format("%.1fkm", distanceMeters / 1000)
        }
    }
}
```

- [ ] **Step 2: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "DistanceUtils|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 9: 创建 UnitRepository

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/api/repository/UnitRepository.kt`

- [ ] **Step 1: 创建 UnitRepository**

```kotlin
package com.ruoyi.app.api.repository

import android.content.Context
import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.UnitEntity
import com.ruoyi.app.model.entity.UnitResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 执法单位仓库
 */
class UnitRepository(private val context: Context) {

    private val unitDao = AppDatabase.getInstance(context).unitDao()

    /**
     * 从后端获取单位列表并存储到本地
     */
    suspend fun syncUnitsFromServer(): Result<List<UnitEntity>> = withContext(Dispatchers.IO) {
        try {
            val result = Get<UnitResult>(ConfigApi.baseUrl + ConfigApi.unitList).await()
            if (result.code == ConfigApi.SUCESSS) {
                val units = result.data.map { it.toEntity() }
                unitDao.insertUnits(units)
                Result.success(units)
            } else {
                Result.failure(Exception(result.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 从本地获取所有单位
     */
    suspend fun getAllUnitsFromLocal(): List<UnitEntity> = withContext(Dispatchers.IO) {
        unitDao.getAllUnits()
    }

    /**
     * 从本地搜索单位
     */
    suspend fun searchUnits(keyword: String): List<UnitEntity> = withContext(Dispatchers.IO) {
        unitDao.searchUnits(keyword)
    }

    /**
     * 根据分类获取单位
     */
    suspend fun getUnitsByCategory(category: String): List<UnitEntity> = withContext(Dispatchers.IO) {
        unitDao.getUnitsByCategory(category)
    }

    /**
     * 根据区域获取单位
     */
    suspend fun getUnitsByRegion(region: String): List<UnitEntity> = withContext(Dispatchers.IO) {
        unitDao.getUnitsByRegion(region)
    }

    /**
     * 获取单位详情
     */
    suspend fun getUnitById(unitId: Long): UnitEntity? = withContext(Dispatchers.IO) {
        unitDao.getUnitById(unitId)
    }

    /**
     * UnitResult 转换为 UnitEntity
     */
    private fun com.ruoyi.app.model.entity.UnitEntity.toEntity(): UnitEntity {
        return UnitEntity(
            unitId = this.unitId,
            unitName = this.unitName,
            industryCategory = this.industryCategory,
            region = this.region,
            supervisionType = this.supervisionType,
            creditCode = this.creditCode,
            legalPerson = this.legalPerson,
            contactPhone = this.contactPhone,
            businessAddress = this.businessAddress,
            latitude = this.latitude,
            longitude = this.longitude,
            status = this.status,
            delFlag = this.delFlag,
            createTime = this.createTime,
            updateTime = this.updateTime,
            remark = this.remark
        )
    }
}
```

- [ ] **Step 2: 创建 UnitResult 实体（如果不存在）**

检查 `app/src/main/java/com/ruoyi/app/model/entity/` 下是否有 UnitResult.kt，如果没有需要创建

- [ ] **Step 3: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "UnitRepository|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 10: 更新 ConfigApi 添加单位 API

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/api/ConfigApi.kt`

- [ ] **Step 1: 添加单位相关 API**

```kotlin
// 单位相关
const val unitList = "/app/unit/list"  // 单位列表
const val unitDetail = "/app/unit"      // 单位详情
```

- [ ] **Step 2: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "ConfigApi|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 11: 创建 SelectUnitActivity 布局文件

**Files:**
- Create: `app/src/main/res/layout/activity_select_unit.xml`

- [ ] **Step 1: 创建布局文件**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/white">

    <!-- 标题栏 -->
    <com.hjq.bar.TitleBar
        android:id="@+id/titlebar"
        android:layout_width="match_parent"
        android:layout_height="@dimen/dp_48"
        app:title="选择执法单位"
        app:leftIcon="@mipmap/icon_title_left"
        app:lineVisible="false"/>

    <!-- 搜索栏 -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="12dp">

        <EditText
            android:id="@+id/et_search"
            android:layout_width="0dp"
            android:layout_height="40dp"
            android:layout_weight="1"
            android:background="@drawable/bg_search"
            android:hint="搜索单位名称"
            android:paddingStart="12dp"
            android:paddingEnd="12dp"
            android:singleLine="true"
            android:textSize="14sp"/>

    </LinearLayout>

    <!-- 筛选条件 -->
    <HorizontalScrollView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:scrollbars="none">

        <LinearLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:paddingStart="12dp"
            android:paddingEnd="12dp">

            <com.google.android.material.chip.Chip
                android:id="@+id/chip_industry"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="行业分类"
                style="@style/Widget.Material3.Chip.Filter"/>

            <com.google.android.material.chip.Chip
                android:id="@+id/chip_region"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="区域"
                android:layout_marginStart="8dp"
                style="@style/Widget.Material3.Chip.Filter"/>

            <com.google.android.material.chip.Chip
                android:id="@+id/chip_supervision"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="监管类型"
                android:layout_marginStart="8dp"
                style="@style/Widget.Material3.Chip.Filter"/>

        </LinearLayout>

    </HorizontalScrollView>

    <!-- 单位列表 -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_units"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:padding="12dp"
        android:clipToPadding="false"/>

    <!-- 跳过按钮 -->
    <TextView
        android:id="@+id/tv_skip"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:padding="16dp"
        android:text="暂不选择，进入主页"
        android:textColor="@color/blue"
        android:textSize="14sp"/>

</LinearLayout>
```

- [ ] **Step 2: 创建搜索框背景 drawable**

创建 `app/src/main/res/drawable/bg_search.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#F5F5F5"/>
    <corners android:radius="20dp"/>
</shape>
```

---

### Task 12: 创建 SelectUnitActivity

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/activity/SelectUnitActivity.kt`

- [ ] **Step 1: 创建 SelectUnitActivity**

```kotlin
package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruoyi.app.adapter.UnitListAdapter
import com.ruoyi.app.databinding.ActivitySelectUnitBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.SelectedUnitManager
import com.ruoyi.app.utils.DistanceUtils
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.clickDelay
import com.therouter.TheRouter
import com.therouter.router.Route
import android.location.Location

@Route(path = Constant.selectUnitRoute)
class SelectUnitActivity : BaseBindingActivity<ActivitySelectUnitBinding>() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, SelectUnitActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private lateinit var unitAdapter: UnitListAdapter
    private val viewModel = SelectUnitViewModel()

    // 当前设备位置
    private var currentLat: Double? = null
    private var currentLon: Double? = null

    override fun initView() {
        // 返回按钮
        binding.titlebar.setOnTitleBarListener(object : com.hjq.bar.OnTitleBarListener {
            override fun onLeftClick(titleBar: com.hjq.bar.TitleBar?) {
                finish()
            }
        })

        // 设置 RecyclerView
        unitAdapter = UnitListAdapter { unit ->
            // 选择单位
            SelectedUnitManager.saveSelectedUnit(unit.unitId, unit.unitName)
            // 跳转主页
            TheRouter.build(Constant.mainRoute).navigation()
            finish()
        }
        binding.rvUnits.apply {
            layoutManager = LinearLayoutManager(this@SelectUnitActivity)
            adapter = unitAdapter
        }

        // 搜索
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchUnits(s?.toString() ?: "")
            }
        })

        // 跳过按钮
        binding.tvSkip.clickDelay {
            TheRouter.build(Constant.mainRoute).navigation()
            finish()
        }
    }

    override fun initData() {
        // 获取当前位置（如果可用）
        viewModel.getCurrentLocation { lat, lon ->
            currentLat = lat
            currentLon = lon
            loadUnits()
        }

        // 观察单位列表
        viewModel.units.observe(this) { units ->
            val sortedUnits = DistanceUtils.sortUnitsByDistance(units, currentLat, currentLon)
            unitAdapter.submitList(sortedUnits)
        }

        // 观察加载状态
        viewModel.isLoading.observe(this) { isLoading ->
            // 显示/隐藏加载状态
        }
    }

    private fun loadUnits() {
        viewModel.loadUnits()
    }
}
```

- [ ] **Step 2: 创建 SelectUnitViewModel**

```kotlin
package com.ruoyi.app.activity

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.scopeNetLife
import com.ruoyi.app.api.repository.UnitRepository
import com.ruoyi.app.data.database.entity.UnitEntity

class SelectUnitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UnitRepository(application)

    val units = MutableLiveData<List<UnitEntity>>()
    val isLoading = MutableLiveData<Boolean>()

    fun loadUnits() {
        scopeNetLife {
            isLoading.value = true
            val localUnits = repository.getAllUnitsFromLocal()
            units.value = localUnits
            isLoading.value = false
        }
    }

    fun searchUnits(keyword: String) {
        scopeNetLife {
            val result = if (keyword.isEmpty()) {
                repository.getAllUnitsFromLocal()
            } else {
                repository.searchUnits(keyword)
            }
            units.value = result
        }
    }

    fun getCurrentLocation(callback: (Double?, Double?) -> Unit) {
        // TODO: 获取设备当前位置
        // 简化实现：返回 null，启用按创建时间排序
        callback(null, null)
    }
}
```

- [ ] **Step 3: 创建 UnitListAdapter**

创建 `app/src/main/java/com/ruoyi/app/adapter/UnitListAdapter.kt`:

```kotlin
package com.ruoyi.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.data.database.entity.UnitEntity
import com.ruoyi.app.databinding.ItemUnitBinding

class UnitListAdapter(
    private val onItemClick: (UnitEntity) -> Unit
) : ListAdapter<UnitEntity, UnitListAdapter.UnitViewHolder>(UnitDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        val binding = ItemUnitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UnitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UnitViewHolder(
        private val binding: ItemUnitBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(unit: UnitEntity) {
            binding.tvUnitName.text = unit.unitName
            binding.tvAddress.text = unit.businessAddress ?: "暂无地址"
            binding.tvIndustry.text = unit.industryCategory ?: ""

            // 距离显示（如果有经纬度）
            if (unit.latitude != null && unit.longitude != null) {
                binding.tvDistance.visibility = android.view.View.VISIBLE
                // 距离需要在排序时计算，这里暂时显示
                binding.tvDistance.text = ""
            } else {
                binding.tvDistance.visibility = android.view.View.GONE
            }
        }
    }

    class UnitDiffCallback : DiffUtil.ItemCallback<UnitEntity>() {
        override fun areItemsTheSame(oldItem: UnitEntity, newItem: UnitEntity): Boolean {
            return oldItem.unitId == newItem.unitId
        }

        override fun areContentsTheSame(oldItem: UnitEntity, newItem: UnitEntity): Boolean {
            return oldItem == newItem
        }
    }
}
```

- [ ] **Step 4: 创建 ItemUnitBinding 布局**

创建 `app/src/main/res/layout/item_unit.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="12dp"
    android:background="?attr/selectableItemBackground">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <TextView
            android:id="@+id/tv_unit_name"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:textColor="@color/black"
            android:textSize="16sp"
            android:textStyle="bold"/>

        <TextView
            android:id="@+id/tv_distance"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textColor="@color/gray"
            android:textSize="12sp"
            android:visibility="gone"/>

    </LinearLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="4dp"
        android:orientation="horizontal">

        <TextView
            android:id="@+id/tv_industry"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@drawable/bg_tag"
            android:paddingStart="6dp"
            android:paddingEnd="6dp"
            android:paddingTop="2dp"
            android:paddingBottom="2dp"
            android:textColor="@color/blue"
            android:textSize="10sp"/>

        <TextView
            android:id="@+id/tv_address"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_marginStart="8dp"
            android:textColor="@color/gray"
            android:textSize="12sp"
            android:maxLines="1"
            android:ellipsize="end"/>

    </LinearLayout>

</LinearLayout>
```

- [ ] **Step 5: 创建标签背景 drawable**

创建 `app/src/main/res/drawable/bg_tag.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#E6F0FF"/>
    <corners android:radius="4dp"/>
</shape>
```

- [ ] **Step 6: 更新 DistanceUtils 添加排序方法**

在 `DistanceUtils.kt` 中添加:

```kotlin
/**
 * 根据距离排序单位列表
 * 有经纬度的按距离排序，没有的排后面
 * 距离相同按创建时间倒序
 */
fun sortUnitsByDistance(
    units: List<UnitEntity>,
    currentLat: Double?,
    currentLon: Double?
): List<UnitEntity> {
    return if (currentLat != null && currentLon != null) {
        units.sortedWith(compareBy(
            // 优先：有经纬度的排前面
            { it.latitude == null || it.longitude == null },
            // 按距离排序
            { unit ->
                if (unit.latitude != null && unit.longitude != null) {
                    calculateDistance(currentLat, currentLon, unit.latitude, unit.longitude)
                } else {
                    Double.MAX_VALUE
                }
            },
            // 创建时间倒序
            { -it.createTime }
        ))
    } else {
        units.sortedByDescending { it.createTime }
    }
}
```

- [ ] **Step 7: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "SelectUnit|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 13: 更新 Constant.kt 添加路由常量

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/model/Constant.kt`

- [ ] **Step 1: 添加路由常量**

```kotlin
const val selectUnitRoute = "http://com.ruoyi/selectUnit"
```

- [ ] **Step 2: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "Constant|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 14: 更新 LoginActivity.preloadLoginData() 同步单位数据

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/activity/LoginActivity.kt`

- [ ] **Step 1: 在 preloadLoginData() 中添加单位数据存储**

在同步成功后添加:

```kotlin
// 存储单位到本地
val units = syncData.units.map { apiUnit ->
    com.ruoyi.app.data.database.entity.UnitEntity(
        unitId = apiUnit.unitId,
        unitName = apiUnit.unitName,
        industryCategory = apiUnit.industryCategory,
        region = apiUnit.region,
        supervisionType = apiUnit.supervisionType,
        creditCode = apiUnit.creditCode,
        legalPerson = apiUnit.legalPerson,
        contactPhone = apiUnit.contactPhone,
        businessAddress = apiUnit.businessAddress,
        latitude = apiUnit.latitude,
        longitude = apiUnit.longitude,
        status = apiUnit.status,
        delFlag = apiUnit.delFlag,
        createTime = System.currentTimeMillis(), // 或解析 createTime
        updateTime = null,
        remark = apiUnit.remark
    )
}
AppDatabase.getInstance(getApplication()).unitDao().insertUnits(units)
```

- [ ] **Step 2: 创建 API 层的 UnitEntity（如果没有）**

检查 `app/src/main/java/com/ruoyi/app/model/entity/UnitEntity.kt` 是否存在，如果不存在需要创建

- [ ] **Step 3: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "LoginActivity|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 15: 更新 MainActivity 检查是否已选单位

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/activity/MainActivity.kt`

- [ ] **Step 1: 在 initData() 中添加单位检查**

在 `initData()` 方法开头添加:

```kotlin
override fun initData() {
    // 检查是否已选单位，未选则弹出选择单位页
    if (!SelectedUnitManager.hasSelectedUnit()) {
        SelectUnitActivity.startActivity(this)
    }

    // 原有初始化代码...
    XUpdate.newBuild(this)
        .updateUrl(ConfigApi.uploadApp)
        .update()

    // 启动心跳定时器
    heartbeatHandler.post(heartbeatRunnable)

    // 调度后台同步 WorkManager（30分钟周期）
    SyncWorker.schedule(this)
}
```

- [ ] **Step 2: 添加 SelectUnitActivity 导入**

```kotlin
import com.ruoyi.app.activity.SelectUnitActivity
import com.ruoyi.app.model.SelectedUnitManager
```

- [ ] **Step 3: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "MainActivity|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 16: 更新 SyncManager 添加单位同步模块

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/sync/SyncManager.kt`

- [ ] **Step 1: 添加单位同步常量和方法**

添加常量:

```kotlin
const val MODULE_UNIT = "执法单位"
```

添加到 FULL_SYNC_MODULES:

```kotlin
val FULL_SYNC_MODULES = listOf(
    MODULE_USER_PERMISSION,
    MODULE_UNIT,  // 新增
    MODULE_INDUSTRY_CATEGORY,
    MODULE_LAW,
    MODULE_PHRASE,
    MODULE_SUPERVISION,
    MODULE_DOCUMENT_TEMPLATE,
    MODULE_MEDIA_FILE
)
```

添加同步方法:

```kotlin
private suspend fun syncUnit(context: Context?): Boolean {
    return try {
        val repository = UnitRepository(context!!)
        val result = repository.syncUnitsFromServer()
        result.isSuccess
    } catch (e: Exception) {
        false
    }
}
```

在 syncModule 的 when 中添加:

```kotlin
MODULE_UNIT -> syncUnit(context)
```

- [ ] **Step 2: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "SyncManager|BUILD"`
Expected: BUILD SUCCESSFUL

---

## 阶段三：验证

### Task 17: 整体验证

- [ ] **Step 1: 编译 APK**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: 安装 APK**

Run: `adb install -r app/build/outputs/apk/debug/app-debug.apk`
Expected: Success

- [ ] **Step 3: 测试流程**

1. 激活设备 → 使用 TEST0001
2. 登录（admin / admin123）
3. 观察同步等待页是否显示"执法单位"同步
4. 进入主页 → 弹出选择单位页
5. 列表是否显示测试单位
6. 选择单位 → 进入主页
7. 底部 Tab 显示"便捷执法"

---

## 文件清单汇总

| 任务 | 创建/修改 | 文件路径 |
|------|----------|---------|
| Task 1 | 修改 | `ruoyi-framework/.../SecurityConfig.java` |
| Task 2 | 创建 | `ruoyi-admin/.../AppUnitController.java` |
| Task 3 | 修改 | `ruoyi-admin/.../SysLoginController.java` |
| Task 4 | 创建 | `app/.../data/database/entity/UnitEntity.kt` |
| Task 5 | 创建 | `app/.../data/database/dao/UnitDao.kt` |
| Task 6 | 修改 | `app/.../data/database/AppDatabase.kt` |
| Task 7 | 创建 | `app/.../model/SelectedUnitManager.kt` |
| Task 8 | 创建 | `app/.../utils/DistanceUtils.kt` |
| Task 9 | 创建 | `app/.../api/repository/UnitRepository.kt` |
| Task 10 | 修改 | `app/.../api/ConfigApi.kt` |
| Task 11 | 创建 | `app/src/main/res/layout/activity_select_unit.xml` |
| Task 12 | 创建 | `app/.../activity/SelectUnitActivity.kt` |
| Task 12 | 创建 | `app/.../activity/SelectUnitViewModel.kt` |
| Task 12 | 创建 | `app/.../adapter/UnitListAdapter.kt` |
| Task 12 | 创建 | `app/src/main/res/layout/item_unit.xml` |
| Task 12 | 创建 | `app/src/main/res/drawable/bg_search.xml` |
| Task 12 | 创建 | `app/src/main/res/drawable/bg_tag.xml` |
| Task 13 | 修改 | `app/.../model/Constant.kt` |
| Task 14 | 修改 | `app/.../activity/LoginActivity.kt` |
| Task 15 | 修改 | `app/.../activity/MainActivity.kt` |
| Task 16 | 修改 | `app/.../sync/SyncManager.kt` |

---

## 注意事项

1. **后端任务由用户执行**：用户使用 RuoYi 代码生成器生成基础 CRUD 代码后，我只需要修改 SecurityConfig、创建 AppUnitController、修改 /app/sync

2. **Room 数据库版本**：如果 AppDatabase 版本从 1 升级，需要处理数据库迁移

3. **测试数据**：确保后端 sys_unit 表有测试数据

4. **API 兼容性**：确保后端 /app/sync 返回的 units 字段与 Android 端 UnitEntity 字段匹配
