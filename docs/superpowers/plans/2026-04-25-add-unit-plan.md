# 添加单位功能实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在日常办公模块中实现添加单位功能，支持本地离线存储和增量同步

**Architecture:** 遵循现有项目架构，数据库层扩展字段 → 后台层新增API → Android端新增页面和离线同步 → 前端Vue扩展字段展示

**Tech Stack:** Kotlin + Room + Retrofit + Coroutines (Android), Java + MyBatis (后台), Vue + Element UI (前端)

---

## 文件结构

```
RuoYi-Vue/
├── sql/
│   └── V1.0.11__sys_unit_extension.sql          # 新增
├── ruoyi-system/src/main/java/com/ruoyi/system/
│   ├── domain/SysUnit.java                       # 修改：新增字段
│   ├── mapper/SysUnitMapper.java                 # 修改：新增方法
│   └── service/ISysUnitService.java              # 修改：新增方法
│       └── impl/SysUnitServiceImpl.java         # 修改：新增方法
├── ruoyi-system/src/main/resources/mapper/
│   └── system/SysUnitMapper.xml                  # 修改：新增SQL
└── ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/
    └── AppUnitController.java                    # 修改：新增POST接口

Ruoyi-Android-App/app/src/main/
├── java/com/ruoyi/app/
│   ├── data/database/
│   │   └── entity/UnitEntity.kt                # 修改：新增字段+syncStatus
│   ├── data/database/dao/UnitDao.kt            # 修改：新增方法
│   ├── api/repository/UnitRepository.kt          # 修改：新增方法
│   ├── api/ConfigApi.kt                       # 修改：新增常量
│   ├── model/Constant.kt                       # 修改：新增路由常量
│   ├── feature/addunit/                        # 新增目录
│   │   ├── AddUnitActivity.kt
│   │   ├── AddUnitViewModel.kt
│   │   └── AddUnitRepository.kt
│   ├── fragment/WorkFragment.kt                # 修改：改造为日常办公
│   ├── sync/SyncManager.kt                     # 修改：扩展同步逻辑
│   └── AndroidManifest.xml                      # 修改：注册Activity
└── res/layout/
    └── activity_add_unit.xml                    # 新增

Ruoyi-Vue/ruoyi-ui/src/
└── views/system/unit/
    └── index.vue                               # 修改：扩展字段
```

---

## 实施任务

### Phase 1: 数据库

#### Task 1: 创建数据库扩展脚本

**Files:**
- Create: `RuoYi-Vue/sql/V1.0.11__sys_unit_extension.sql`

- [ ] **Step 1: 创建 SQL 脚本**

```sql
-- V1.0.11__sys_unit_extension.sql
-- 日期: 2026-04-25
-- 描述: 扩展 sys_unit 表，新增当事人相关字段

ALTER TABLE `sys_unit`
    ADD COLUMN `person_name` varchar(50) DEFAULT NULL COMMENT '当事人姓名' AFTER `unit_name`,
    ADD COLUMN `registration_address` varchar(255) DEFAULT NULL COMMENT '注册地址' AFTER `person_name`,
    ADD COLUMN `business_area` decimal(10,2) DEFAULT NULL COMMENT '经营面积（平方米）' AFTER `registration_address`,
    ADD COLUMN `license_name` varchar(100) DEFAULT NULL COMMENT '许可证名称' AFTER `business_area`,
    ADD COLUMN `license_no` varchar(50) DEFAULT NULL COMMENT '许可证号' AFTER `license_name`,
    ADD COLUMN `gender` char(1) DEFAULT NULL COMMENT '性别(0男,1女)' AFTER `license_no`,
    ADD COLUMN `nation` varchar(20) DEFAULT NULL COMMENT '民族' AFTER `gender`,
    ADD COLUMN `post` varchar(50) DEFAULT NULL COMMENT '职务' AFTER `nation`,
    ADD COLUMN `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号码' AFTER `post`,
    ADD COLUMN `birthday` datetime DEFAULT NULL COMMENT '出生年月' AFTER `id_card`,
    ADD COLUMN `home_address` varchar(255) DEFAULT NULL COMMENT '家庭住址' AFTER `birthday`;

-- 注：legal_person（法定代表人）是现有字段，无需新增
```

- [ ] **Step 2: 提交数据库脚本**

```bash
git add RuoYi-Vue/sql/V1.0.11__sys_unit_extension.sql
git commit -m "feat(database): 扩展sys_unit表新增当事人相关字段"
```

---

### Phase 2: 后台

#### Task 2: 扩展 SysUnit 实体类

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysUnit.java`

- [ ] **Step 1: 添加新字段**

在 SysUnit.java 中添加以下字段及 getter/setter：

```java
/** 当事人姓名 */
@Excel(name = "当事人姓名")
private String personName;

/** 注册地址 */
@Excel(name = "注册地址")
private String registrationAddress;

/** 经营面积 */
@Excel(name = "经营面积")
private BigDecimal businessArea;

/** 许可证名称 */
@Excel(name = "许可证名称")
private String licenseName;

/** 许可证号 */
@Excel(name = "许可证号")
private String licenseNo;

/** 性别 */
@Excel(name = "性别")
private String gender;

/** 民族 */
@Excel(name = "民族")
private String nation;

/** 职务 */
@Excel(name = "职务")
private String post;

/** 身份证号码 */
@Excel(name = "身份证号码")
private String idCard;

/** 出生年月 */
@Excel(name = "出生年月")
private Date birthday;

/** 家庭住址 */
@Excel(name = "家庭住址")
private String homeAddress;
```

- [ ] **Step 2: 添加 getter/setter 方法**

为每个新字段添加标准的 getter/setter 方法，格式如下：

```java
public String getPersonName() {
    return personName;
}

public void setPersonName(String personName) {
    this.personName = personName;
}
// ... 其他字段类似
```

- [ ] **Step 3: 提交实体类修改**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysUnit.java
git commit -m "feat(backend): 扩展SysUnit实体类新增字段"
```

---

#### Task 3: 扩展 SysUnitMapper

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysUnitMapper.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysUnitMapper.xml`

- [ ] **Step 1: 在 Mapper 接口添加方法**

```java
/**
 * 新增执法单位
 */
int insertSysUnit(SysUnit sysUnit);
```

- [ ] **Step 2: 在 Mapper XML 添加 SQL**

```xml
<insert id="insertSysUnit" parameterType="com.ruoyi.system.domain.SysUnit">
    INSERT INTO sys_unit (
        unit_name, person_name, registration_address, business_area,
        industry_category_id, region, supervision_type, credit_code,
        license_name, license_no, legal_person, gender, nation, post,
        id_card, birthday, contact_phone, home_address, business_address,
        latitude, longitude, status, del_flag, create_by, create_time, remark
    ) VALUES (
        #{unitName}, #{personName}, #{registrationAddress}, #{businessArea},
        #{industryCategoryId}, #{region}, #{supervisionType}, #{creditCode},
        #{licenseName}, #{licenseNo}, #{legalPerson}, #{gender}, #{nation}, #{post},
        #{idCard}, #{birthday}, #{contactPhone}, #{homeAddress}, #{businessAddress},
        #{latitude}, #{longitude}, #{status}, #{delFlag}, #{createBy}, #{createTime}, #{remark}
    )
</insert>
```

- [ ] **Step 3: 提交 Mapper 修改**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysUnitMapper.java
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysUnitMapper.xml
git commit -m "feat(backend): 扩展SysUnitMapper新增insertSysUnit方法"
```

---

#### Task 4: 扩展 Service 层

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysUnitService.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysUnitServiceImpl.java`

- [ ] **Step 1: 在接口添加方法声明**

```java
/**
 * 新增执法单位
 */
int insertSysUnit(SysUnit sysUnit);
```

- [ ] **Step 2: 在实现类添加方法实现**

```java
@Override
public int insertSysUnit(SysUnit sysUnit) {
    return sysUnitMapper.insertSysUnit(sysUnit);
}
```

- [ ] **Step 3: 提交 Service 修改**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysUnitService.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysUnitServiceImpl.java
git commit -m "feat(backend): 扩展SysUnitService新增insertSysUnit方法"
```

---

#### Task 5: 扩展 AppUnitController

**Files:**
- Modify: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppUnitController.java`

- [ ] **Step 1: 添加新增单位接口**

```java
/**
 * 新增单位（移动端调用，需携带 Token 认证）
 */
@Anonymous
@PostMapping
public AjaxResult add(@RequestBody SysUnit unit) {
    // 设置默认值
    unit.setStatus("0");
    unit.setDelFlag("0");
    unit.setCreateBy(getUsername());
    unit.setCreateTime(new Date());

    int rows = unitService.insertSysUnit(unit);
    if (rows > 0) {
        // 返回新增的单位ID，便于移动端更新本地记录
        return AjaxResult.success("添加成功", unit.getUnitId());
    }
    return AjaxResult.error("添加失败");
}
```

- [ ] **Step 2: 提交 Controller 修改**

```bash
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppUnitController.java
git commit -m "feat(backend): AppUnitController新增POST接口用于添加单位"
```

---

### Phase 3: Android

#### Task 6: 扩展 UnitEntity

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/UnitEntity.kt`

- [ ] **Step 1: 添加新字段**

```kotlin
data class UnitEntity(
    @PrimaryKey val unitId: Long,
    val unitName: String,
    val personName: String?,           // 当事人
    val registrationAddress: String?, // 注册地址
    val businessArea: Double?,         // 经营面积
    // ... 现有字段保持不变
    val licenseName: String?,          // 许可证名称
    val licenseNo: String?,            // 许可证号
    val gender: String?,               // 0男,1女
    val nation: String?,               // 民族
    val post: String?,                 // 职务
    val idCard: String?,              // 身份证
    val birthday: Long?,              // 出生年月
    val homeAddress: String?,          // 家庭住址
    // ... 其他现有字段
    val syncStatus: String = "PENDING"  // 同步状态：PENDING=待同步, SYNCED=已同步
)
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/UnitEntity.kt
git commit -m "feat(android): 扩展UnitEntity新增字段和syncStatus"
```

---

#### Task 7: 扩展 UnitDao

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/UnitDao.kt`

- [ ] **Step 1: 添加新方法**

```kotlin
@Query("SELECT * FROM sys_unit WHERE syncStatus = 'PENDING'")
suspend fun getPendingUnits(): List<UnitEntity>

@Query("UPDATE sys_unit SET syncStatus = 'SYNCED' WHERE unitId = :unitId")
suspend fun markAsSynced(unitId: Long)
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/UnitDao.kt
git commit -m "feat(android): UnitDao新增getPendingUnits和markAsSynced方法"
```

---

#### Task 8: 扩展 ConfigApi

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/api/ConfigApi.kt`

- [ ] **Step 1: 添加常量**

```kotlin
const val appUnitAdd = "/app/unit"  // 新增单位接口
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/api/ConfigApi.kt
git commit -m "feat(android): ConfigApi新增appUnitAdd常量"
```

---

#### Task 9: 扩展 Constant

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/model/Constant.kt`

- [ ] **Step 1: 添加路由常量**

```kotlin
// 日常办公模块路由
const val addUnitRoute = "http://com.ruoyi/addUnit"
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/model/Constant.kt
git commit -m "feat(android): Constant新增addUnitRoute路由常量"
```

---

#### Task 10: 改造 WorkFragment

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/fragment/WorkFragment.kt`
- Modify: `Ruoyi-Android-App/app/src/main/res/layout/fragment_work.xml`

- [ ] **Step 1: 改造 WorkFragment**

保留类名 WorkFragment，修改内容为"日常办公"标题 + "添加单位"卡片入口

```kotlin
// fragment_work.xml 布局改造
LinearLayout (vertical)
  - TextView: "日常办公" 标题
  - CardView: "添加单位" 入口卡片
    - ImageView: 图标
    - TextView: "添加单位"
    - TextView: "快速录入执法单位信息"
```

WorkFragment.kt 中：
- 移除原有的 banner 和 adapter 设置
- 设置卡片点击事件，跳转到 AddUnitActivity

```kotlin
binding.cardAddUnit.setOnClickListener {
    TheRouter.build(Constant.addUnitRoute).navigation()
}
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/fragment/WorkFragment.kt
git add Ruoyi-Android-App/app/src/main/res/layout/fragment_work.xml
git commit -m "feat(android): WorkFragment改造为日常办公添加单位入口"
```

---

#### Task 11: 创建 AddUnitActivity

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/addunit/AddUnitActivity.kt`
- Create: `Ruoyi-Android-App/app/src/main/res/layout/activity_add_unit.xml`

- [ ] **Step 1: 创建布局文件 activity_add_unit.xml**

```
ScrollView
  LinearLayout (vertical, padding 16dp)
    - 行业分类选择 Row
    - 单位名称输入 Row
    - 当事人输入 Row
    - 联系方式输入 Row
    - 注册地址输入 Row
    - 经营地址输入 Row
    - 经营面积输入 Row
    - 社会信用代码输入 Row
    - 许可证名称输入 Row
    - 许可证号输入 Row
    - 法定代表人输入 Row
    - 性别选择 Row (单选: 男/女)
    - 民族选择 Row
    - 职务输入 Row
    - 身份证号码输入 Row
    - 出生年月选择 Row
    - 家庭住址输入 Row
    - 保存按钮
```

- [ ] **Step 2: 创建 AddUnitActivity.kt**

```kotlin
@Route(path = Constant.addUnitRoute)
class AddUnitActivity : BaseBindingActivity<ActivityAddUnitBinding>() {

    private val viewModel: AddUnitViewModel by viewModels()

    override fun initView() {
        // 行业分类选择点击事件
        binding.tvIndustryCategory.setOnClickListener {
            // 跳转到行业分类选择
        }

        // 保存按钮点击事件
        binding.btnSave.setOnClickListener {
            if (validateForm()) {
                viewModel.saveUnit(buildUnitEntity())
            }
        }
    }

    override fun initData() {
        viewModel.saveResult.observe(this) { success ->
            if (success) {
                ToastUtils.show("保存成功")
                finish()
            }
        }
    }

    private fun validateForm(): Boolean {
        // 校验必填字段
        return true
    }

    private fun buildUnitEntity(): UnitEntity {
        return UnitEntity(
            unitId = -System.currentTimeMillis(),  // 本地生成负数ID
            unitName = binding.etUnitName.text.toString(),
            personName = binding.etPersonName.text.toString(),
            // ... 其他字段
            syncStatus = "PENDING"
        )
    }
}
```

- [ ] **Step 3: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/addunit/AddUnitActivity.kt
git add Ruoyi-Android-App/app/src/main/res/layout/activity_add_unit.xml
git commit -m "feat(android): 新增AddUnitActivity添加单位页面"
```

---

#### Task 12: 创建 AddUnitViewModel

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/addunit/AddUnitViewModel.kt`

- [ ] **Step 1: 创建 ViewModel**

```kotlin
class AddUnitViewModel : ViewModel() {

    private val repository = AddUnitRepository()

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    fun saveUnit(unit: UnitEntity) {
        viewModelScope.launch {
            val success = repository.saveUnit(unit)
            _saveResult.value = success
        }
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/addunit/AddUnitViewModel.kt
git commit -m "feat(android): 新增AddUnitViewModel"
```

---

#### Task 13: 创建 AddUnitRepository

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/addunit/AddUnitRepository.kt`

- [ ] **Step 1: 创建 Repository**

```kotlin
class AddUnitRepository {

    suspend fun saveUnit(unit: UnitEntity): Boolean {
        return try {
            // 保存到本地数据库
            AppDatabase.getInstance().unitDao().insertUnit(unit)
            true
        } catch (e: Exception) {
            false
        }
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/addunit/AddUnitRepository.kt
git commit -m "feat(android): 新增AddUnitRepository"
```

---

#### Task 14: 扩展 UnitRepository

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/api/repository/UnitRepository.kt`

- [ ] **Step 1: 添加新方法**

```kotlin
/**
 * 获取本地待同步的单位列表
 */
suspend fun getPendingUnits(): List<UnitEntity> {
    return withContext(Dispatchers.IO) {
        unitDao.getPendingUnits()
    }
}

/**
 * 标记单位为已同步
 */
suspend fun markAsSynced(unitId: Long) {
    withContext(Dispatchers.IO) {
        unitDao.markAsSynced(unitId)
    }
}

/**
 * 上传单个单位到服务器
 */
suspend fun uploadUnitToServer(unit: UnitEntity): Result<UnitDTO> {
    return withContext(Dispatchers.IO) {
        try {
            val result = Post<UnitResult>(ConfigApi.baseUrl + ConfigApi.appUnitAdd)
                .body(unit.toDTO())
                .await()
            if (result.code == ConfigApi.SUCCESS) {
                Result.success(result.data)
            } else {
                Result.failure(Exception(result.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/api/repository/UnitRepository.kt
git commit -m "feat(android): UnitRepository新增待同步单位处理方法"
```

---

#### Task 15: 扩展 SyncManager

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/SyncManager.kt`

- [ ] **Step 1: 修改 syncUnit 方法**

将原有的 `syncUnit` 方法改为双向同步：

```kotlin
private suspend fun syncUnit(context: Context?): Boolean {
    if (context == null) return false
    return try {
        val repository = UnitRepository(context)

        // 1. 先上传本地待同步单位（避免被服务器数据覆盖）
        val pendingUnits = repository.getPendingUnits()
        for (unit in pendingUnits) {
            val uploadSuccess = uploadUnitToServer(unit)
            if (uploadSuccess) {
                repository.markAsSynced(unit.unitId)
            }
        }

        // 2. 再拉取服务器数据更新本地
        val pullSuccess = repository.syncUnitsFromServer().isSuccess

        pullSuccess
    } catch (e: Exception) {
        false
    }
}

private suspend fun uploadUnitToServer(unit: UnitEntity): Boolean {
    return repository.uploadUnitToServer(unit).isSuccess
}
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/SyncManager.kt
git commit -m "feat(android): SyncManager扩展为单位双向同步"
```

---

#### Task 16: 注册 AddUnitActivity

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/AndroidManifest.xml`

- [ ] **Step 1: 添加 Activity 注册**

```xml
<activity
    android:name=".feature.addunit.AddUnitActivity"
    android:exported="false"
    android:theme="@style/Theme.RuoYi" />
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/AndroidManifest.xml
git commit -m "feat(android): AndroidManifest注册AddUnitActivity"
```

---

### Phase 4: 前端

#### Task 17: 扩展单位管理页面

**Files:**
- Modify: `Ruoyi-Vue/ruoyi-ui/src/views/system/unit/index.vue`

- [ ] **Step 1: 在表格列中添加新字段**

在 el-table-column 中添加：

```vue
<el-table-column label="当事人" prop="personName" width="80" />
<el-table-column label="注册地址" prop="registrationAddress" width="150" />
<el-table-column label="经营面积" prop="businessArea" width="80" />
<el-table-column label="许可证名称" prop="licenseName" width="120" />
<el-table-column label="许可证号" prop="licenseNo" width="120" />
<el-table-column label="性别" prop="gender" width="50" />
<el-table-column label="民族" prop="nation" width="60" />
<el-table-column label="职务" prop="post" width="80" />
<el-table-column label="身份证" prop="idCard" width="150" />
<el-table-column label="出生日期" prop="birthday" width="100" />
<el-table-column label="家庭住址" prop="homeAddress" width="200" />
```

- [ ] **Step 2: 在表单中添加新字段**

在弹窗表单中添加对应的表单项

- [ ] **Step 3: 提交**

```bash
git add Ruoyi-Vue/ruoyi-ui/src/views/system/unit/index.vue
git commit -m "feat(frontend): 单位管理页面扩展新增字段"
```

---

## 执行总结

| Phase | Tasks | 说明 |
|-------|-------|------|
| 数据库 | 1 | SQL 脚本 |
| 后台 | 4 | 实体类、Mapper、Service、Controller |
| Android | 11 | Entity、Dao、Repository、Activity、ViewModel、布局、路由、同步等 |
| 前端 | 1 | Vue 页面字段扩展 |
| **Total** | **17** | |

---

## 执行选项

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
