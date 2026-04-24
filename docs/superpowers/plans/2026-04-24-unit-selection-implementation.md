# 选择单位模块实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现执法单位选择功能，包括后端 API 和 Android 端选择页面

**Architecture:** 离线优先设计，行业分类和单位数据通过 /app/sync 同步到 Room DB，选择单位页面直接从本地查询，支持按位置距离排序

**Tech Stack:** Spring Boot (RuoYi-Vue), Kotlin, Room, TheRouter, MMKV

---

## 评审确认的设计决策

| 决策项 | 选择 |
|--------|------|
| 行业分类 Entity 结构 | 扁平结构（orderNum, delFlag，无 parentCode/level） |
| /app/sync units 数据 | 后端 SQL JOIN 返回 industryCategoryName |
| 单位选择入口 | 首页顶部显示，点击弹出选择页 |
| 跳过机制 | 允许跳过，执法功能点击时提示"请先选择执法单位" |

---

## 文件结构

### 后端 (RuoYi-Vue)
```
ruoyi-framework/src/main/java/com/ruoyi/framework/config/SecurityConfig.java  ← /app/** 已配置，无需修改
ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLoginController.java  ← 添加 categories, units 到 /app/sync
ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppCategoryController.java  ← 新增
ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppUnitController.java  ← 新增
ruoyi-system/src/main/java/com/ruoyi/system/service/ISysUnitService.java  ← 添加 selectUnitListWithCategory
ruoyi-system/src/main/java/com/ruoyi/system/impl/SysUnitServiceImpl.java  ← 实现 selectUnitListWithCategory
ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysUnitMapper.java  ← 添加 selectUnitListWithCategory
ruoyi-system/src/main/resources/mapper/system/SysUnitMapper.xml  ← 添加 JOIN 查询
```

### Android (Ruoyi-Android-App)
```
app/src/main/java/com/ruoyi/app/data/database/entity/IndustryCategoryEntity.kt  ← 修改：扁平结构
app/src/main/java/com/ruoyi/app/data/database/entity/UnitEntity.kt  ← 新增
app/src/main/java/com/ruoyi/app/data/database/dao/IndustryCategoryDao.kt  ← 修改：delFlag 过滤
app/src/main/java/com/ruoyi/app/data/database/dao/UnitDao.kt  ← 新增
app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt  ← 修改：添加 UnitDao，version 2→3
app/src/main/java/com/ruoyi/app/api/repository/CategoryRepository.kt  ← 新增
app/src/main/java/com/ruoyi/app/api/repository/UnitRepository.kt  ← 新增
app/src/main/java/com/ruoyi/app/model/SelectedUnitManager.kt  ← 修改：添加 categoryId, categoryName
app/src/main/java/com/ruoyi/app/utils/DistanceUtils.kt  ← 新增
app/src/main/java/com/ruoyi/app/model/Constant.kt  ← 修改：添加 selectUnitRoute
app/src/main/java/com/ruoyi/app/fragment/HomeFragment.kt  ← 修改：添加顶部单位选择栏
app/src/main/java/com/ruoyi/app/activity/SelectUnitActivity.kt  ← 新增
app/src/main/java/com/ruoyi/app/activity/SelectUnitViewModel.kt  ← 新增
app/src/main/java/com/ruoyi/app/adapter/UnitListAdapter.kt  ← 新增
app/src/main/java/com/ruoyi/app/sync/SyncManager.kt  ← 修改：实现 categories, units 同步
app/src/main/java/com/ruoyi/app/activity/LoginActivity.kt  ← 修改：解析 categories, units
app/src/main/res/layout/fragment_home.xml  ← 修改：添加顶部单位选择栏
app/src/main/res/layout/activity_select_unit.xml  ← 新增
app/src/main/res/layout/item_unit.xml  ← 新增
app/src/main/res/drawable/bg_search.xml  ← 新增
app/src/main/res/drawable/bg_tag.xml  ← 新增
app/src/main/res/values/strings.xml  ← 修改：添加相关字符串
```

---

## 阶段一：后端修改

### Task 1: 创建 AppCategoryController (匿名可访问的行业分类接口)

**Files:**
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppCategoryController.java`

- [ ] **Step 1: 创建 AppCategoryController**

```java
package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysIndustryCategory;
import com.ruoyi.system.service.ISysIndustryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Android 端行业分类接口（匿名可访问）
 */
@RestController
@RequestMapping("/app/category")
public class AppCategoryController extends BaseController {

    @Autowired
    private ISysIndustryCategoryService categoryService;

    /**
     * 获取行业分类列表
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list() {
        List<SysIndustryCategory> list = categoryService.selectIndustryCategoryList(new SysIndustryCategory());
        return AjaxResult.success(list);
    }

    /**
     * 获取行业分类详情
     */
    @Anonymous
    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId) {
        SysIndustryCategory category = categoryService.selectIndustryCategoryById(categoryId);
        return AjaxResult.success(category);
    }
}
```

- [ ] **Step 2: 验证文件创建**

Run: `ls -la ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppCategoryController.java`
Expected: 文件存在

---

### Task 2: 修改 SysUnitService 添加 JOIN 查询方法

**Files:**
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/ISysUnitService.java`
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysUnitServiceImpl.java`
- Modify: `ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysUnitMapper.java`
- Modify: `ruoyi-system/src/main/resources/mapper/system/SysUnitMapper.xml`

- [ ] **Step 1: 在 ISysUnitService 添加接口方法**

```java
/**
 * 查询执法单位列表（带行业分类名称）
 */
List<SysUnit> selectUnitListWithCategory(SysUnit sysUnit);
```

- [ ] **Step 2: 在 SysUnitMapper 添加方法声明**

```java
List<SysUnit> selectUnitListWithCategory(SysUnit sysUnit);
```

- [ ] **Step 3: 在 SysUnitMapper.xml 添加 JOIN 查询**

```xml
<select id="selectUnitListWithCategory" resultMap="SysUnitResult">
    SELECT u.unit_id, u.unit_name, u.industry_category_id,
           c.category_name AS industry_category_name,
           u.region, u.supervision_type, u.credit_code,
           u.legal_person, u.contact_phone, u.business_address,
           u.latitude, u.longitude, u.status, u.del_flag,
           u.create_by, u.create_time, u.update_by, u.update_time, u.remark
    FROM sys_unit u
    LEFT JOIN sys_industry_category c ON u.industry_category_id = c.category_id
    WHERE u.del_flag = '0'
    <if test="unitName != null and unitName != ''">
        AND u.unit_name LIKE CONCAT('%', #{unitName}, '%')
    </if>
    <if test="industryCategoryId != null">
        AND u.industry_category_id = #{industryCategoryId}
    </if>
    <if test="region != null and region != ''">
        AND u.region = #{region}
    </if>
    <if test="supervisionType != null and supervisionType != ''">
        AND u.supervision_type = #{supervisionType}
    </if>
    ORDER BY u.create_time DESC
</select>
```

- [ ] **Step 4: 在 SysUnitServiceImpl 实现方法**

```java
@Override
public List<SysUnit> selectUnitListWithCategory(SysUnit sysUnit) {
    return sysUnitMapper.selectUnitListWithCategory(sysUnit);
}
```

- [ ] **Step 5: 在 SysUnit 实体添加 industryCategoryName 字段**

```java
/**
 * 行业分类名称（不映射数据库列，通过 JOIN 查询获取）
 */
@Excel(name = "行业分类名称")
private String industryCategoryName;

// getter/setter
public String getIndustryCategoryName() {
    return industryCategoryName;
}

public void setIndustryCategoryName(String industryCategoryName) {
    this.industryCategoryName = industryCategoryName;
}
```

---

### Task 3: 创建 AppUnitController (匿名可访问的单位接口)

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
     * 获取单位列表（带行业分类名称）
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list() {
        List<SysUnit> list = unitService.selectUnitListWithCategory(new SysUnit());
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

---

### Task 4: 修改 /app/sync 接口，添加 categories 和 units

**Files:**
- Modify: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLoginController.java`

- [ ] **Step 1: 查看当前 /app/sync 方法**

找到 `appSync` 方法的位置

- [ ] **Step 2: 添加 categories 和 units 到响应**

```java
@com.ruoyi.common.annotation.Anonymous
@GetMapping("/app/sync")
public AjaxResult appSync()
{
    AjaxResult ajax = AjaxResult.success();

    // 1. 获取所有用户（含明文密码）
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

    // 5. 获取所有行业分类（新增）
    List<SysIndustryCategory> categories = categoryService.selectIndustryCategoryList(new SysIndustryCategory());
    ajax.put("categories", categories);

    // 6. 获取所有执法单位，带行业分类名称（新增）
    List<SysUnit> units = unitService.selectUnitListWithCategory(new SysUnit());
    ajax.put("units", units);

    return ajax;
}
```

- [ ] **Step 3: 添加必要的导入**

```java
import com.ruoyi.system.domain.SysIndustryCategory;
import com.ruoyi.system.domain.SysUnit;
import com.ruoyi.system.service.ISysIndustryCategoryService;
import com.ruoyi.system.service.ISysUnitService;
```

- [ ] **Step 4: 注入服务**

```java
@Autowired
private ISysIndustryCategoryService categoryService;

@Autowired
private ISysUnitService unitService;
```

---

## 阶段二：Android 基础

### Task 5: 修改 IndustryCategoryEntity (扁平结构)

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/data/database/entity/IndustryCategoryEntity.kt`

- [ ] **Step 1: 更新 IndustryCategoryEntity**

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 行业分类实体类
 * 一级扁平结构
 */
@Entity(tableName = "sys_industry_category")
data class IndustryCategoryEntity(
    @PrimaryKey val categoryId: Long,
    val categoryName: String,
    val categoryCode: String?,   // 分类编码
    val orderNum: Int,         // 显示顺序
    val status: String,        // 状态(0=正常,1=停用)
    val delFlag: String,       // 删除标志(0=存在,1=删除)
    val createBy: String?,
    val createTime: Long,
    val updateBy: String?,
    val updateTime: Long?,
    val remark: String?
)
```

- [ ] **Step 2: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "IndustryCategoryEntity|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 6: 修改 IndustryCategoryDao (添加 delFlag 过滤)

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/data/database/dao/IndustryCategoryDao.kt`

- [ ] **Step 1: 更新查询方法**

```kotlin
@Dao
interface IndustryCategoryDao {

    @Query("SELECT * FROM sys_industry_category WHERE delFlag = '0' AND status = '0' ORDER BY orderNum")
    suspend fun getAllCategories(): List<IndustryCategoryEntity>

    @Query("SELECT * FROM sys_industry_category WHERE categoryId = :categoryId")
    suspend fun getCategoryById(categoryId: Long): IndustryCategoryEntity?

    @Query("SELECT * FROM sys_industry_category WHERE categoryId IN (:ids)")
    suspend fun getCategoriesByIds(ids: List<Long>): List<IndustryCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<IndustryCategoryEntity>)

    @Query("DELETE FROM sys_industry_category")
    suspend fun deleteAllCategories()
}
```

- [ ] **Step 2: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "IndustryCategoryDao|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 7: 创建 UnitEntity (Room Entity)

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
    val industryCategoryId: Long?,   // 行业分类ID（外键）
    val industryCategoryName: String?, // 行业分类名称（来自JOIN查询）
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
Expected: BUILD SUCCESSFUL

---

### Task 8: 创建 UnitDao

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

    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' AND industryCategoryId = :categoryId ORDER BY createTime DESC")
    suspend fun getUnitsByCategory(categoryId: Long): List<UnitEntity>

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
Expected: BUILD SUCCESSFUL

---

### Task 9: 更新 AppDatabase (添加 UnitDao，version 2→3)

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt`

- [ ] **Step 1: 添加 UnitEntity 到 entities 列表**

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
    version = 3,  // 需要增加版本号
    exportSchema = false
)
```

- [ ] **Step 2: 添加 abstract 方法**

```kotlin
abstract fun unitDao(): UnitDao
```

- [ ] **Step 3: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "AppDatabase|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 10: 修改 SelectedUnitManager (添加 categoryId, categoryName)

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/model/SelectedUnitManager.kt`

- [ ] **Step 1: 更新 SelectedUnitManager**

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
    private const val KEY_SELECTED_CATEGORY_ID = "selected_category_id"
    private const val KEY_SELECTED_CATEGORY_NAME = "selected_category_name"

    fun saveSelectedUnit(unitId: Long, unitName: String, categoryId: Long?, categoryName: String?) {
        MMKV.defaultMMKV().encode(KEY_SELECTED_UNIT_ID, unitId)
        MMKV.defaultMMKV().encode(KEY_SELECTED_UNIT_NAME, unitName)
        MMKV.defaultMMKV().encode(KEY_SELECTED_CATEGORY_ID, categoryId ?: -1)
        MMKV.defaultMMKV().encode(KEY_SELECTED_CATEGORY_NAME, categoryName ?: "")
    }

    fun getSelectedUnitId(): Long? {
        val id = MMKV.defaultMMKV().decodeLong(KEY_SELECTED_UNIT_ID, -1)
        return if (id == -1L) null else id
    }

    fun getSelectedUnitName(): String? {
        return MMKV.defaultMMKV().decodeString(KEY_SELECTED_UNIT_NAME)
    }

    fun getSelectedCategoryId(): Long? {
        val id = MMKV.defaultMMKV().decodeLong(KEY_SELECTED_CATEGORY_ID, -1)
        return if (id == -1L) null else id
    }

    fun getSelectedCategoryName(): String? {
        return MMKV.defaultMMKV().decodeString(KEY_SELECTED_CATEGORY_NAME)
    }

    fun clearSelectedUnit() {
        MMKV.defaultMMKV().removeValueForKey(KEY_SELECTED_UNIT_ID)
        MMKV.defaultMMKV().removeValueForKey(KEY_SELECTED_UNIT_NAME)
        MMKV.defaultMMKV().removeValueForKey(KEY_SELECTED_CATEGORY_ID)
        MMKV.defaultMMKV().removeValueForKey(KEY_SELECTED_CATEGORY_NAME)
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

### Task 11: 创建 CategoryRepository

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/api/repository/CategoryRepository.kt`

- [ ] **Step 1: 创建 CategoryRepository**

```kotlin
package com.ruoyi.app.api.repository

import android.content.Context
import com.drake.net.Get
import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.IndustryCategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 行业分类仓库
 */
class CategoryRepository(private val context: Context) {

    private val categoryDao = AppDatabase.getInstance(context).industryCategoryDao()

    /**
     * 从后端获取行业分类列表并存储到本地
     */
    suspend fun syncCategoriesFromServer(): Result<List<IndustryCategoryEntity>> = withContext(Dispatchers.IO) {
        try {
            val result = Get<CategoryResult>(ConfigApi.baseUrl + ConfigApi.categoryList).await()
            if (result.code == ConfigApi.SUCCESS) {
                val categories = result.data.map { it.toEntity() }
                categoryDao.insertCategories(categories)
                Result.success(categories)
            } else {
                Result.failure(Exception(result.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 从本地获取所有行业分类
     */
    suspend fun getAllCategoriesFromLocal(): List<IndustryCategoryEntity> = withContext(Dispatchers.IO) {
        categoryDao.getAllCategories()
    }

    /**
     * 根据ID获取行业分类
     */
    suspend fun getCategoryById(categoryId: Long): IndustryCategoryEntity? = withContext(Dispatchers.IO) {
        categoryDao.getCategoryById(categoryId)
    }
}

/**
 * API 响应实体
 */
@kotlinx.serialization.Serializable
data class CategoryResult(
    val code: Int,
    val msg: String,
    val data: List<CategoryDTO> = emptyList()
)

@kotlinx.serialization.Serializable
data class CategoryDTO(
    val categoryId: Long,
    val categoryName: String,
    val categoryCode: String?,
    val orderNum: Int,
    val status: String,
    val delFlag: String,
    val createTime: String?,
    val updateTime: String?
) {
    fun toEntity(): IndustryCategoryEntity {
        return IndustryCategoryEntity(
            categoryId = categoryId,
            categoryName = categoryName,
            categoryCode = categoryCode ?: "",
            orderNum = orderNum,
            status = status,
            delFlag = delFlag,
            createBy = null,
            createTime = createTime?.toLongOrNull() ?: System.currentTimeMillis(),
            updateBy = null,
            updateTime = updateTime?.toLongOrNull(),
            remark = null
        )
    }
}
```

---

### Task 12: 创建 UnitRepository

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
            if (result.code == ConfigApi.SUCCESS) {
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
    suspend fun getUnitsByCategory(categoryId: Long): List<UnitEntity> = withContext(Dispatchers.IO) {
        unitDao.getUnitsByCategory(categoryId)
    }

    /**
     * 获取单位详情
     */
    suspend fun getUnitById(unitId: Long): UnitEntity? = withContext(Dispatchers.IO) {
        unitDao.getUnitById(unitId)
    }
}

/**
 * API 响应实体
 */
@kotlinx.serialization.Serializable
data class UnitResult(
    val code: Int,
    val msg: String,
    val data: List<UnitDTO> = emptyList()
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
    val status: String,
    val delFlag: String,
    val createTime: String?,
    val updateTime: String?,
    val remark: String?
) {
    fun toEntity(): UnitEntity {
        return UnitEntity(
            unitId = unitId,
            unitName = unitName,
            industryCategoryId = industryCategoryId,
            industryCategoryName = industryCategoryName,
            region = region,
            supervisionType = supervisionType,
            creditCode = creditCode,
            legalPerson = legalPerson,
            contactPhone = contactPhone,
            businessAddress = businessAddress,
            latitude = latitude,
            longitude = longitude,
            status = status,
            delFlag = delFlag,
            createTime = createTime?.toLongOrNull() ?: System.currentTimeMillis(),
            updateTime = updateTime?.toLongOrNull(),
            remark = remark
        )
    }
}
```

---

### Task 13: 创建 DistanceUtils

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
     */
    fun formatDistance(distanceMeters: Double): String {
        return when {
            distanceMeters < 1000 -> "${distanceMeters.toInt()}m"
            else -> String.format("%.1fkm", distanceMeters / 1000)
        }
    }

    /**
     * 根据距离排序单位列表
     */
    fun sortUnitsByDistance(
        units: List<UnitEntity>,
        currentLat: Double?,
        currentLon: Double?
    ): List<UnitEntity> {
        return if (currentLat != null && currentLon != null) {
            units.sortedWith(compareBy(
                { it.latitude == null || it.longitude == null },
                { unit ->
                    if (unit.latitude != null && unit.longitude != null) {
                        calculateDistance(currentLat, currentLon, unit.latitude, unit.longitude)
                    } else {
                        Double.MAX_VALUE
                    }
                },
                { -it.createTime }
            ))
        } else {
            units.sortedByDescending { it.createTime }
        }
    }
}
```

---

### Task 14: 更新 ConfigApi

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/api/ConfigApi.kt`

- [ ] **Step 1: 添加路由常量**

```kotlin
// 单位相关
const val selectUnitRoute = "http://com.ruoyi/selectUnit"
const val categoryList = "/app/category/list"
const val unitList = "/app/unit/list"
```

- [ ] **Step 2: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "ConfigApi|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 15: 更新 SyncManager (实现 categories 和 units 同步)

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/sync/SyncManager.kt`

- [ ] **Step 1: 添加常量**

```kotlin
const val MODULE_UNIT = "执法单位"
```

- [ ] **Step 2: 添加到 FULL_SYNC_MODULES**

```kotlin
val FULL_SYNC_MODULES = listOf(
    MODULE_USER_PERMISSION,
    MODULE_INDUSTRY_CATEGORY,
    MODULE_UNIT,  // 新增
    MODULE_LAW,
    MODULE_PHRASE,
    MODULE_SUPERVISION,
    MODULE_DOCUMENT_TEMPLATE,
    MODULE_MEDIA_FILE
)
```

- [ ] **Step 3: 更新 syncModule**

```kotlin
private suspend fun syncModule(context: Context?, module: String): Boolean {
    return try {
        when (module) {
            MODULE_USER_PERMISSION -> syncUserPermissions()
            MODULE_INDUSTRY_CATEGORY -> syncIndustryCategory(context)
            MODULE_UNIT -> syncUnit(context)  // 新增
            MODULE_LAW -> syncLaw(context)
            MODULE_PHRASE -> syncPhrase(context)
            MODULE_SUPERVISION -> syncSupervision(context)
            MODULE_DOCUMENT_TEMPLATE -> syncDocumentTemplate(context)
            MODULE_MEDIA_FILE -> syncMediaFile(context)
            else -> true
        }
    } catch (e: Exception) {
        false
    }
}
```

- [ ] **Step 4: 实现 syncUnit 方法**

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

- [ ] **Step 5: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "SyncManager|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 16: 更新 LoginActivity.preloadLoginData() (解析 categories 和 units)

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/activity/LoginActivity.kt`

- [ ] **Step 1: 在同步成功后添加 categories 和 units 存储**

在 `preloadLoginData()` 方法中，syncData 处理部分添加：

```kotlin
// 存储行业分类到本地
syncData.categories?.let { categories ->
    val categoryEntities = categories.map { apiCategory ->
        com.ruoyi.app.data.database.entity.IndustryCategoryEntity(
            categoryId = apiCategory.categoryId,
            categoryName = apiCategory.categoryName,
            categoryCode = apiCategory.categoryCode ?: "",
            orderNum = apiCategory.orderNum,
            status = apiCategory.status,
            delFlag = apiCategory.delFlag,
            createBy = apiCategory.createBy,
            createTime = apiCategory.createTime?.toLongOrNull() ?: System.currentTimeMillis(),
            updateBy = apiCategory.updateBy,
            updateTime = apiCategory.updateTime?.toLongOrNull(),
            remark = apiCategory.remark
        )
    }
    AppDatabase.getInstance(getApplication()).industryCategoryDao().insertCategories(categoryEntities)
}

// 存储单位到本地
syncData.units?.let { units ->
    val unitEntities = units.map { apiUnit ->
        com.ruoyi.app.data.database.entity.UnitEntity(
            unitId = apiUnit.unitId,
            unitName = apiUnit.unitName,
            industryCategoryId = apiUnit.industryCategoryId,
            industryCategoryName = apiUnit.industryCategoryName,
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
            createTime = apiUnit.createTime?.toLongOrNull() ?: System.currentTimeMillis(),
            updateTime = apiUnit.updateTime?.toLongOrNull(),
            remark = apiUnit.remark
        )
    }
    AppDatabase.getInstance(getApplication()).unitDao().insertUnits(unitEntities)
}
```

- [ ] **Step 2: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "LoginActivity|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 17: 创建 SelectUnitActivity 及相关组件

**Files:**
- Create: `app/src/main/res/layout/activity_select_unit.xml`
- Create: `app/src/main/res/layout/item_unit.xml`
- Create: `app/src/main/res/drawable/bg_search.xml`
- Create: `app/src/main/res/drawable/bg_tag.xml`
- Create: `app/src/main/java/com/ruoyi/app/activity/SelectUnitActivity.kt`
- Create: `app/src/main/java/com/ruoyi/app/activity/SelectUnitViewModel.kt`
- Create: `app/src/main/java/com/ruoyi/app/adapter/UnitListAdapter.kt`

- [ ] **Step 1: 创建 activity_select_unit.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/white">

    <com.hjq.bar.TitleBar
        android:id="@+id/titlebar"
        android:layout_width="match_parent"
        android:layout_height="@dimen/dp_48"
        app:title="选择执法单位"
        app:leftIcon="@mipmap/icon_title_left"
        app:lineVisible="false"/>

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

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_units"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:padding="12dp"
        android:clipToPadding="false"/>

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

- [ ] **Step 2: 创建 item_unit.xml**

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

- [ ] **Step 3: 创建 bg_search.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#F5F5F5"/>
    <corners android:radius="20dp"/>
</shape>
```

- [ ] **Step 4: 创建 bg_tag.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#E6F0FF"/>
    <corners android:radius="4dp"/>
</shape>
```

- [ ] **Step 5: 创建 SelectUnitActivity**

```kotlin
package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

    private var currentLat: Double? = null
    private var currentLon: Double? = null

    override fun initView() {
        binding.titlebar.setOnTitleBarListener(object : com.hjq.bar.OnTitleBarListener {
            override fun onLeftClick(titleBar: com.hjq.bar.TitleBar?) {
                finish()
            }
        })

        unitAdapter = UnitListAdapter { unit ->
            SelectedUnitManager.saveSelectedUnit(
                unit.unitId,
                unit.unitName,
                unit.industryCategoryId,
                unit.industryCategoryName
            )
            TheRouter.build(Constant.mainRoute).navigation()
            finish()
        }
        binding.rvUnits.apply {
            layoutManager = LinearLayoutManager(this@SelectUnitActivity)
            adapter = unitAdapter
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchUnits(s?.toString() ?: "")
            }
        })

        binding.tvSkip.clickDelay {
            TheRouter.build(Constant.mainRoute).navigation()
            finish()
        }
    }

    override fun initData() {
        viewModel.getCurrentLocation { lat, lon ->
            currentLat = lat
            currentLon = lon
            loadUnits()
        }

        viewModel.units.observe(this) { units ->
            val sortedUnits = DistanceUtils.sortUnitsByDistance(units, currentLat, currentLon)
            unitAdapter.submitList(sortedUnits)
        }
    }

    private fun loadUnits() {
        viewModel.loadUnits()
    }
}
```

- [ ] **Step 6: 创建 SelectUnitViewModel**

```kotlin
package com.ruoyi.app.activity

import android.app.Application
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
        callback(null, null)
    }
}
```

- [ ] **Step 7: 创建 UnitListAdapter**

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
            binding.tvIndustry.text = unit.industryCategoryName ?: ""
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

---

### Task 18: 修改 HomeFragment (添加顶部单位选择栏)

**Files:**
- Modify: `app/src/main/res/layout/fragment_home.xml`
- Modify: `app/src/main/java/com/ruoyi/app/fragment/HomeFragment.kt`

- [ ] **Step 1: 在 fragment_home.xml 顶部添加单位选择栏**

在根布局开头添加：

```xml
<!-- 顶部单位选择栏 -->
<LinearLayout
    android:id="@+id/ll_unit_selector"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="12dp"
    android:background="?attr/selectableItemBackground"
    android:gravity="center_vertical">

    <ImageView
        android:layout_width="20dp"
        android:layout_height="20dp"
        android:src="@mipmap/icon_building"
        android:tint="@color/blue"/>

    <TextView
        android:id="@+id/tv_selected_unit"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:layout_marginStart="8dp"
        android:text="请选择执法单位"
        android:textColor="@color/black"
        android:textSize="14sp"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="▼"
        android:textColor="@color/gray"
        android:textSize="12sp"/>

</LinearLayout>

<View
    android:layout_width="match_parent"
    android:layout_height="1dp"
    android:background="#EEEEEE"/>
```

- [ ] **Step 2: 修改 HomeFragment**

```kotlin
package com.ruoyi.app.fragment

import com.ruoyi.app.activity.SelectUnitActivity
import com.ruoyi.app.databinding.FragmentHomeBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.SelectedUnitManager
import com.ruoyi.code.base.BaseBindingFragment
import com.therouter.router.Route

@Route(path = Constant.homeFragment)
class HomeFragment : BaseBindingFragment<FragmentHomeBinding>() {

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }

    override fun initView() {
        // 点击单位选择栏
        binding.llUnitSelector.setOnClickListener {
            SelectUnitActivity.startActivity(requireContext())
        }
    }

    override fun initData() {
        updateUnitSelector()
    }

    override fun onResume() {
        super.onResume()
        updateUnitSelector()
    }

    private fun updateUnitSelector() {
        val unitName = SelectedUnitManager.getSelectedUnitName()
        if (unitName != null) {
            binding.tvSelectedUnit.text = "当前单位：$unitName"
        } else {
            binding.tvSelectedUnit.text = "请选择执法单位"
        }
    }
}
```

- [ ] **Step 3: 验证编译**

Run: `./gradlew assembleDebug 2>&1 | grep -E "HomeFragment|BUILD"`
Expected: BUILD SUCCESSFUL

---

### Task 19: 更新 strings.xml

**Files:**
- Modify: `app/src/main/res/values/strings.xml`

- [ ] **Step 1: 添加相关字符串**

```xml
<string name="select_unit">选择执法单位</string>
<string name="current_unit">当前单位：%s</string>
<string name="please_select_unit">请选择执法单位</string>
<string name="skip_select">暂不选择，进入主页</string>
<string name="unit_name">单位名称</string>
<string name="industry_category">行业分类</string>
<string name="region">区域</string>
<string name="supervision_type">监管类型</string>
<string name="search_unit_hint">搜索单位名称</string>
<string name="no_unit_selected">请先选择执法单位</string>
```

---

## 阶段三：验证

### Task 20: 整体验证

- [ ] **Step 1: 编译 APK**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: 安装 APK**

Run: `adb install -r app/build/outputs/apk/debug/app-debug.apk`
Expected: Success

- [ ] **Step 3: 测试流程**

1. 激活设备 → 使用 TEST0001
2. 登录（admin / admin123）
3. 观察同步等待页显示"行业分类"和"执法单位"同步
4. 进入主页 → 首页顶部显示"请选择执法单位"
5. 点击顶部栏 → 弹出选择单位页
6. 选择单位 → 返回主页，顶部显示"当前单位：XXX"
7. 点击"暂不选择" → 返回主页，顶部仍显示"请选择执法单位"

---

## 文件清单汇总

| Task | 创建/修改 | 文件路径 |
|------|----------|---------|
| Task 1 | 创建 | `ruoyi-admin/.../AppCategoryController.java` |
| Task 2 | 修改 | `ruoyi-system/.../ISysUnitService.java` |
| Task 2 | 修改 | `ruoyi-system/.../SysUnitServiceImpl.java` |
| Task 2 | 修改 | `ruoyi-system/.../SysUnitMapper.java` |
| Task 2 | 修改 | `ruoyi-system/.../SysUnitMapper.xml` |
| Task 2 | 修改 | `ruoyi-system/.../SysUnit.java` |
| Task 3 | 创建 | `ruoyi-admin/.../AppUnitController.java` |
| Task 4 | 修改 | `ruoyi-admin/.../SysLoginController.java` |
| Task 5 | 修改 | `app/.../entity/IndustryCategoryEntity.kt` |
| Task 6 | 修改 | `app/.../dao/IndustryCategoryDao.kt` |
| Task 7 | 创建 | `app/.../entity/UnitEntity.kt` |
| Task 8 | 创建 | `app/.../dao/UnitDao.kt` |
| Task 9 | 修改 | `app/.../AppDatabase.kt` |
| Task 10 | 修改 | `app/.../SelectedUnitManager.kt` |
| Task 11 | 创建 | `app/.../CategoryRepository.kt` |
| Task 12 | 创建 | `app/.../UnitRepository.kt` |
| Task 13 | 创建 | `app/.../DistanceUtils.kt` |
| Task 14 | 修改 | `app/.../Constant.kt` |
| Task 15 | 修改 | `app/.../SyncManager.kt` |
| Task 16 | 修改 | `app/.../LoginActivity.kt` |
| Task 17 | 创建 | `app/.../SelectUnitActivity.kt` |
| Task 17 | 创建 | `app/.../SelectUnitViewModel.kt` |
| Task 17 | 创建 | `app/.../UnitListAdapter.kt` |
| Task 17 | 创建 | `app/res/layout/activity_select_unit.xml` |
| Task 17 | 创建 | `app/res/layout/item_unit.xml` |
| Task 17 | 创建 | `app/res/drawable/bg_search.xml` |
| Task 17 | 创建 | `app/res/drawable/bg_tag.xml` |
| Task 18 | 修改 | `app/.../HomeFragment.kt` |
| Task 18 | 修改 | `app/res/layout/fragment_home.xml` |
| Task 19 | 修改 | `app/res/values/strings.xml` |
| 补充 A | 创建 | `RuoYi-Vue/sql/sys_industry_category.sql` |
| 补充 B | 创建 | `RuoYi-Vue/sql/sys_menu.sql` |
| 补充 C | 修改 | `app/.../model/entity/SyncDataEntity.kt` |
| Task 21 | 修改 | `app/.../SelectUnitActivity.kt` |
| Task 21 | 修改 | `app/.../SelectUnitViewModel.kt` |
| Task 22 | 修改 | `app/.../SelectUnitViewModel.kt` |
| Task 22 | 修改 | `app/.../UnitListAdapter.kt` |

---

## 注意事项

1. **后端任务由用户执行**：用户使用 RuoYi 代码生成器生成基础 CRUD 代码后，我只需要修改 SecurityConfig（无需，/app/** 已配置）、创建 AppCategoryController、AppUnitController、修改 /app/sync

2. **Room 数据库版本**：从 version 2 升级到 3，使用 `.fallbackToDestructiveMigration()`

3. **测试数据**：确保后端 sys_unit 表有测试数据，且 sys_industry_category 表有预置数据

4. **API 兼容性**：确保 /app/sync 返回的 categories 和 units 字段与 Android 端 Entity 字段匹配

5. **功能拦截**：执法功能点击时需要检查 SelectedUnitManager.hasSelectedUnit()，未选则 Toast 提示

6. **预置数据**：后端执行 `sys_industry_category.sql` 插入 8 个行业分类预置数据

7. **菜单配置**：后端执行 `sys_menu.sql` 插入行业分类和单位管理菜单

---

## 缺失内容补充

### 补充 A: 预置数据 SQL

**Files:**
- Create: `RuoYi-Vue/sql/sys_industry_category.sql`

```sql
-- 行业分类预置数据
INSERT INTO `sys_industry_category` (`category_name`, `category_code`, `order_num`, `status`, `remark`) VALUES
('公共场所', 'GCDG', 1, '0', '公共场所卫生监督'),
('医疗机构', 'YLJG', 2, '0', '医疗机构卫生监督'),
('学校卫生', 'XXWS', 3, '0', '学校卫生监督'),
('饮用水卫生', 'YSXWS', 4, '0', '饮用水及涉水产品卫生监督'),
('传染病防治', 'CRBFZ', 5, '0', '传染病防治卫生监督'),
('放射卫生', 'FSWS', 6, '0', '放射卫生监督'),
('消毒产品', 'XCCP', 7, '0', '消毒产品卫生监督'),
('职业卫生', 'ZYWS', 8, '0', '职业卫生监督');
```

### 补充 B: 菜单 SQL

**Files:**
- Create: `RuoYi-Vue/sql/sys_menu.sql`

```sql
-- 系统管理下新增行业分类菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`) VALUES
('行业分类管理', 1, 1, 'industryCategory', 'system/industryCategory/index', 'C', '0', '0', 'system:industryCategory:list', 'tree', 'admin');

-- 日常办公菜单组
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`) VALUES
('日常办公', 0, 10, 'dailyOffice', NULL, 'M', '0', '0', '', 'log', 'admin');

-- 单位管理菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`) VALUES
('单位管理', (SELECT menu_id FROM sys_menu WHERE path='dailyOffice'), 1, 'unit', 'system/unit/index', 'C', '0', '0', 'system:unit:list', 'building', 'admin');
```

### 补充 C: SyncDataEntity 更新

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/model/entity/SyncDataEntity.kt`

需要在 SyncDataEntity 中添加 categories 和 units 字段：

```kotlin
@kotlinx.serialization.Serializable
data class SyncDataEntity(
    val code: Int,
    val msg: String,
    val users: List<UserEntity> = emptyList(),
    val depts: List<DeptEntity> = emptyList(),
    val roles: List<RoleEntity> = emptyList(),
    val menus: List<MenuEntity> = emptyList(),
    val categories: List<CategoryDTO> = emptyList(),  // 新增
    val units: List<UnitDTO> = emptyList()              // 新增
)
```

### 补充 D: 执法功能拦截（伪代码）

在各个执法功能的点击事件中添加检查：

```kotlin
// 例如：现场执法按钮点击
binding.btnLawEnforcement.setOnClickListener {
    if (!SelectedUnitManager.hasSelectedUnit()) {
        toast("请先选择执法单位")
        SelectUnitActivity.startActivity(requireContext())
        return@setOnClickListener
    }
    // 正常跳转
}
```

---

## 筛选功能实现（阶段五）

### Task 21: 实现行业分类/区域/监管类型筛选

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/activity/SelectUnitActivity.kt`
- Modify: `app/src/main/java/com/ruoyi/app/activity/SelectUnitViewModel.kt`

- [ ] **Step 1: 在 SelectUnitViewModel 添加筛选方法**

```kotlin
fun filterByCategory(categoryId: Long?) {
    scopeNetLife {
        val result = if (categoryId == null) {
            repository.getAllUnitsFromLocal()
        } else {
            repository.getUnitsByCategory(categoryId)
        }
        units.value = result
    }
}

fun filterByRegion(region: String?) {
    scopeNetLife {
        val result = if (region.isNullOrEmpty()) {
            repository.getAllUnitsFromLocal()
        } else {
            repository.getUnitsByRegion(region)
        }
        units.value = result
    }
}
```

- [ ] **Step 2: 在 SelectUnitActivity 实现 Chip 点击**

```kotlin
// 行业分类筛选
binding.chipIndustry.setOnClickListener {
    // 显示行业分类选择对话框
    showCategoryFilterDialog()
}

// 区域筛选
binding.chipRegion.setOnClickListener {
    // 显示区域选择对话框
    showRegionFilterDialog()
}

// 监管类型筛选
binding.chipSupervision.setOnClickListener {
    // 显示监管类型选择对话框
    showSupervisionTypeFilterDialog()
}
```

---

## 分页加载实现

### Task 22: 实现单位列表分页加载

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/activity/SelectUnitViewModel.kt`
- Modify: `app/src/main/java/com/ruoyi/app/adapter/UnitListAdapter.kt`

- [ ] **Step 1: 在 SelectUnitViewModel 添加分页变量**

```kotlin
class SelectUnitViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UnitRepository(application)

    val units = MutableLiveData<List<UnitEntity>>()
    val isLoading = MutableLiveData<Boolean>()

    private var currentPage = 0
    private val pageSize = 20
    private var hasMoreData = true
    private var currentKeyword = ""
    private var currentCategoryId: Long? = null
    private var currentRegion: String? = null
    // ...
}
```

- [ ] **Step 2: 实现加载更多**

```kotlin
fun loadMore() {
    if (isLoading.value == true || !hasMoreData) return
    scopeNetLife {
        isLoading.value = true
        currentPage++
        val offset = currentPage * pageSize
        val moreUnits = unitDao.getUnitsPaged(pageSize, offset)
        if (moreUnits.isEmpty()) {
            hasMoreData = false
        } else {
            val currentList = units.value.orEmpty()
            units.value = currentList + moreUnits
        }
        isLoading.value = false
    }
}

fun resetAndLoad() {
    currentPage = 0
    hasMoreData = true
    loadUnits()
}
```

- [ ] **Step 3: 在 UnitListAdapter 添加加载更多监听**

```kotlin
// 在 UnitListAdapter 中添加
fun addLoadMoreListener(recyclerView: RecyclerView, loadMore: () -> Unit) {
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            if (totalItemCount <= lastVisibleItem + 5) {
                loadMore()
            }
        }
    })
}
```
