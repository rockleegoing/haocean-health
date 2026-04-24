# 添加单位功能设计文档

**版本**: v1.0.0
**日期**: 2026-04-25
**状态**: 待评审
**作者**: Claude

---

## 1. 概述

### 1.1 功能描述

在移动端日常办公模块中新增"添加单位"功能，允许执法人员在本地录入执法单位信息，支持离线存储，并通过统一增量同步机制将数据上报至后台管理系统。

### 1.2 需求背景

- 日常办公模块需要支持执法人员现场添加新发现的执法单位
- 数据优先本地存储，支持离线操作
- 通过 SyncManager 增量同步机制统一上报数据

### 1.3 涉及端

| 端 | 修改内容 |
|----|---------|
| 数据库 | 扩展 sys_unit 表，新增 11 个字段 |
| 后台 | 扩展 SysUnit 实体类，新增 AppUnitController 添加单位接口 |
| Android | 新增 AddUnitActivity 页面，扩展 UnitEntity |
| 前端 | 扩展单位管理页面，支持查看新增字段 |

---

## 2. 数据库设计

### 2.1 扩展字段

在 `sys_unit` 表中新增以下字段：

```sql
-- V1.0.11__sys_unit_extension.sql

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

### 2.2 完整字段列表

| 字段名 | 类型 | 说明 | 备注 |
|-------|------|------|------|
| unit_id | bigint | 单位ID | 主键 |
| unit_name | varchar(100) | 单位名称 | 必填 |
| person_name | varchar(50) | 当事人姓名 | 新增 |
| registration_address | varchar(255) | 注册地址 | 新增 |
| business_area | decimal(10,2) | 经营面积 | 新增，单位：平方米 |
| industry_category_id | bigint | 行业分类ID | 外键 |
| region | varchar(50) | 区域 | |
| supervision_type | varchar(50) | 监管类型 | |
| credit_code | varchar(50) | 统一社会信用代码 | |
| license_name | varchar(100) | 许可证名称 | 新增 |
| license_no | varchar(50) | 许可证号 | 新增 |
| legal_person | varchar(50) | 法定代表人 | |
| gender | char(1) | 性别(0男,1女) | 新增 |
| nation | varchar(20) | 民族 | 新增 |
| post | varchar(50) | 职务 | 新增 |
| id_card | varchar(18) | 身份证号码 | 新增 |
| birthday | datetime | 出生年月 | 新增 |
| contact_phone | varchar(20) | 联系电话 | |
| home_address | varchar(255) | 家庭住址 | 新增 |
| business_address | varchar(255) | 经营地址 | |
| latitude | decimal(10,6) | 纬度 | |
| longitude | decimal(10,6) | 经度 | |
| status | char(1) | 状态(0正常,1停用) | |
| del_flag | char(1) | 删除标志(0存在,1删除) | |
| create_by | varchar(64) | 创建者 | |
| create_time | datetime | 创建时间 | |
| update_by | varchar(64) | 更新者 | |
| update_time | datetime | 更新时间 | |
| remark | varchar(500) | 备注 | |

---

## 3. 后台设计

### 3.1 实体类扩展

**SysUnit.java** 新增字段：

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

### 3.2 新增 API 接口

**AppUnitController.java** 新增接口：

```java
/**
 * 新增单位（移动端调用，需携带 Token 认证）
 * 注：@Anonymous 表示允许不带 Session 的请求，Token 验证在 Filter 层处理
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

**权限说明**：移动端 API 使用 Token 认证，`@Anonymous` 仅表示允许不带 JSESSIONID 的请求，实际用户身份通过请求头中的 Token 验证。

### 3.3 Service 层

**ISysUnitService.java** 新增方法：

```java
/**
 * 新增执法单位
 */
int insertSysUnit(SysUnit sysUnit);
```

**SysUnitServiceImpl.java** 实现：

```java
@Override
public int insertSysUnit(SysUnit sysUnit) {
    return sysUnitMapper.insertSysUnit(sysUnit);
}
```

### 3.4 Mapper 层

**SysUnitMapper.java** 新增方法：

```java
/**
 * 新增执法单位
 */
int insertSysUnit(SysUnit sysUnit);
```

**SysUnitMapper.xml** 新增 SQL：

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

**注意**：字段顺序需与数据库表结构一致。

---

## 4. Android 端设计

### 4.1 页面结构

**WorkFragment 改造**：
- 底部 Tab "工作台" 在 UI 上改名为"日常办公"（修改 MainActivity 中的 tab 标题文字）
- WorkFragment 内容移除原有的 banner 和功能列表
- 改为仅显示"添加单位"卡片入口

```
┌─────────────────────────────────────┐
│  日常办公                             │
├─────────────────────────────────────┤
│                                      │
│  ┌───────────────────────────────┐  │
│  │  🏢                            │  │
│  │  添加单位                        │  │
│  │  快速录入执法单位信息              │  │
│  └───────────────────────────────┘  │
│                                      │
└─────────────────────────────────────┘
```

**MainActivity Tab 标题修改**：
修改 `R.string.work_nav_index` 资源值为"日常办公"，或直接在 MainActivity 的 list 中配置标题为"日常办公"。

**AddUnitActivity**：
- 单页面表单
- 行业分类选择（跳转到分类选择页面）
- 必填字段校验
- 本地保存，支持离线

### 4.2 表单字段

| 字段 | 必填 | 输入类型 | 说明 |
|------|------|---------|------|
| 行业分类 | 是 | 选择器 | 树形选择 |
| 单位名称 | 是 | 文本 | |
| 当事人 | 是 | 文本 | |
| 联系方式 | 是 | 手机号 | |
| 注册地址 | 是 | 文本 | |
| 经营地址 | 否 | 文本 | |
| 经营面积 | 否 | 数字 | 单位：m² |
| 社会信用代码 | 否 | 文本 | 18位 |
| 许可证名称 | 否 | 文本 | |
| 许可证号 | 否 | 文本 | |
| 法定代表人 | 否 | 文本 | |
| 性别 | 否 | 单选 | 男/女 |
| 民族 | 否 | 选择器 | |
| 职务 | 否 | 文本 | |
| 身份证号码 | 否 | 身份证 | 18位 |
| 出生年月 | 否 | 日期 | |
| 家庭住址 | 否 | 文本 | |

### 4.3 数据模型

**UnitEntity 扩展**：

```kotlin
data class UnitEntity(
    @PrimaryKey val unitId: Long,  // 本地生成，使用负数或时间戳
    val unitName: String,
    val personName: String?,           // 当事人
    val registrationAddress: String?,  // 注册地址
    val businessArea: Double?,        // 经营面积
    val industryCategoryId: Long?,
    val industryCategoryName: String?,
    val region: String?,
    val supervisionType: String?,
    val creditCode: String?,
    val licenseName: String?,          // 许可证名称
    val licenseNo: String?,            // 许可证号
    val legalPerson: String?,
    val gender: String?,               // 0男,1女
    val nation: String?,              // 民族
    val post: String?,                // 职务
    val idCard: String?,              // 身份证
    val birthday: Long?,              // 出生年月
    val contactPhone: String?,
    val homeAddress: String?,         // 家庭住址
    val businessAddress: String?,
    val latitude: Double?,
    val longitude: Double?,
    val status: String,
    val delFlag: String,
    val createTime: Long,
    val updateTime: Long?,
    val remark: String?,
    val syncStatus: String = "PENDING"  // 同步状态：PENDING=待同步, SYNCED=已同步
)
```

### 4.4 本地存储

**UnitDao 扩展方法**：

```kotlin
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insertUnit(unit: UnitEntity)

@Query("SELECT * FROM sys_unit WHERE syncStatus = 'PENDING'")
suspend fun getPendingUnits(): List<UnitEntity>

@Query("UPDATE sys_unit SET syncStatus = 'SYNCED' WHERE unitId = :unitId")
suspend fun markAsSynced(unitId: Long)
```

### 4.5 离线同步

**SyncManager 扩展**：

在 `syncUnit` 方法中：
1. 从本地查询 `syncStatus = 'PENDING'` 的单位
2. 调用后端 API 逐个上传
3. 上传成功后更新 `syncStatus = 'SYNCED'`

### 4.6 文件结构

```
app/src/main/java/com/ruoyi/app/
├── fragment/
│   └── WorkFragment.kt          # 改造：改为日常办公，添加单位入口
├── feature/addunit/
│   ├── AddUnitActivity.kt       # 新增：添加单位页面
│   ├── AddUnitViewModel.kt      # 新增：ViewModel
│   └── AddUnitRepository.kt     # 新增：Repository
└── data/database/
    └── entity/UnitEntity.kt     # 扩展：新增字段
```

### 4.7 路由配置

**Constant.kt** 新增路由常量：

```kotlin
// 日常办公模块路由
const val addUnitRoute = "http://com.ruoyi/addUnit"
```

**AddUnitActivity.kt** 路由注解：

```kotlin
@Route(path = Constant.addUnitRoute)
class AddUnitActivity : BaseBindingActivity<ActivityAddUnitBinding>() {
    // ...
}
```

---

## 5. 前端设计 (Vue)

### 5.1 单位管理页面扩展

**ruoyi-ui/src/views/system/unit/index.vue**：

- 在单位列表表格中新增展示字段（当事人、注册地址、经营面积、许可证等）
- 在单位详情/编辑弹窗中新增这些字段的展示和编辑

### 5.2 字段显示

在表格列中添加：

| 列名 | 字段 | 宽度 |
|------|------|------|
| 当事人 | person_name | 80 |
| 注册地址 | registration_address | 150 |
| 经营面积 | business_area | 80 |
| 许可证名称 | license_name | 120 |
| 许可证号 | license_no | 120 |
| 性别 | gender | 50 |
| 民族 | nation | 60 |
| 职务 | post | 80 |
| 身份证 | id_card | 150 |
| 出生日期 | birthday | 100 |
| 家庭住址 | home_address | 200 |

---

## 6. 增量同步机制

### 6.1 同步流程

```
┌─────────────────────────────────────────────────────────────┐
│                     增量同步流程                              │
├─────────────────────────────────────────────────────────────┤
│  1. SyncManager 检测需要同步的单位模块                          │
│  2. 查询本地 UnitDao 中 syncStatus = 'PENDING' 的记录         │
│  3. POST /app/unit 遍历待同步记录，上传单位数据                 │
│  4. 上传成功后更新本地记录的 syncStatus = 'SYNCED'             │
│  5. 如有失败，记录错误，等待下次重试                            │
└─────────────────────────────────────────────────────────────┘
```

### 6.2 离线支持

- 所有添加单位操作直接保存到本地 SQLite
- 网络恢复后自动触发同步
- 同步状态可在单位列表中查看

### 6.3 同步单位数据检查与扩展

**现有 SyncManager 单位同步分析**：

现有 `SyncManager.syncUnit()` 方法仅从服务器拉取单位数据到本地：
```kotlin
private suspend fun syncUnit(context: Context?): Boolean {
    if (context == null) return false
    return try {
        val repository = UnitRepository(context)
        repository.syncUnitsFromServer().isSuccess
    } catch (e: Exception) {
        false
    }
}
```

**问题**：
1. 仅支持单向同步（服务器 → 本地）
2. 没有上传本地新增单位到服务器的功能
3. 没有增量检查机制

**扩展设计**：

```kotlin
/**
 * 增量同步单位数据
 * 1. 从服务器拉取最新单位列表（完整覆盖）
 * 2. 上传本地新增的单位（syncStatus = 'PENDING'）
 */
private suspend fun syncUnit(context: Context?): Boolean {
    if (context == null) return false
    return try {
        val repository = UnitRepository(context)

        // 1. 拉取服务器数据并更新本地
        val pullSuccess = repository.syncUnitsFromServer().isSuccess

        // 2. 上传本地待同步单位
        val pendingUnits = repository.getPendingUnits()
        for (unit in pendingUnits) {
            val uploadSuccess = uploadUnitToServer(unit)
            if (uploadSuccess) {
                repository.markAsSynced(unit.unitId)
            }
        }

        pullSuccess
    } catch (e: Exception) {
        false
    }
}

/**
 * 上传单位到服务器（SyncManager 调用层）
 */
private suspend fun uploadUnitToServer(unit: UnitEntity): Boolean {
    // 调用 UnitRepository 的实际实现
    return repository.uploadUnitToServer(unit).isSuccess
}
```

**UnitRepository 扩展方法**（实际实现）：

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
            val result = Post<UnitResult>(ConfigApi.baseUrl + "/app/unit")
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

**UnitDTO 扩展**（与后端 SysUnit 对应）：

```kotlin
@kotlinx.serialization.Serializable
data class UnitDTO(
    // ... 现有字段
    val personName: String? = null,
    val registrationAddress: String? = null,
    val businessArea: Double? = null,
    val licenseName: String? = null,
    val licenseNo: String? = null,
    val gender: String? = null,
    val nation: String? = null,
    val post: String? = null,
    val idCard: String? = null,
    val birthday: Long? = null,  // 毫秒时间戳，与 UnitEntity 保持一致
    val homeAddress: String? = null,
    // ...
)
```

---

## 7. 验收标准

### 7.1 功能验收

- [ ] WorkFragment 正确显示"日常办公"标题和"添加单位"入口
- [ ] 点击"添加单位"能正常跳转到 AddUnitActivity
- [ ] 行业分类选择器能正确加载和选择
- [ ] 必填字段校验正常（单位名称、当事人、联系方式、注册地址）
- [ ] 表单数据能正确保存到本地数据库
- [ ] 离线状态下能正常添加单位
- [ ] 单位列表能看到新增的单位

### 7.2 同步验收

- [ ] 后端 API 能正确接收新增单位数据
- [ ] 增量同步能将本地新增单位上传到后台
- [ ] 同步状态正确更新
- [ ] 本地 syncStatus = 'PENDING' 的单位能正确上传
- [ ] 上传成功后 syncStatus 更新为 'SYNCED'
- [ ] 离线状态下添加的单位在联网后能正确同步
- [ ] 同步失败时能正确记录错误并等待重试

### 7.3 前端验收

- [ ] 单位管理页面能正确显示新增字段
- [ ] 新增单位能在后台管理系统看到

---

## 8. 开发任务分解

### 8.1 数据库
1. 创建 SQL 脚本 `V1.0.11__sys_unit_extension.sql`

### 8.2 后台
1. 扩展 SysUnit.java 实体类
2. 扩展 SysUnitMapper.java 和 SysUnitMapper.xml
3. 扩展 ISysUnitService.java 和 SysUnitServiceImpl.java
4. 在 AppUnitController.java 新增 POST 接口

### 8.3 Android
1. 扩展 UnitEntity.kt（新增字段 + syncStatus）
2. 扩展 UnitDao.kt（新增 insertUnit、getPendingUnits、markAsSynced 方法）
3. 扩展 Constant.kt（新增 addUnitRoute 常量）
4. 改造 WorkFragment.kt（改为日常办公，添加单位入口）
5. 新增 AddUnitActivity.kt（添加单位页面）
6. 新增 AddUnitViewModel.kt（ViewModel）
7. 新增 AddUnitRepository.kt（数据仓库）
8. 扩展 UnitRepository.kt（新增 getPendingUnits、markAsSynced、uploadUnitToServer 方法）
9. 创建 activity_add_unit.xml 布局
10. 在 AndroidManifest.xml 注册 AddUnitActivity
11. 扩展 SyncManager.kt（增量同步：拉取+上传）
12. 扩展 ConfigApi.kt（新增 appUnitAdd 接口地址）

### 8.4 前端
1. 扩展单位管理页面表格列
2. 扩展详情/编辑弹窗字段

---

## 9. 风险与注意事项

1. **离线冲突**：如果同一单位在多设备被修改，需要后续处理冲突策略
2. **字段验证**：身份证号、手机号等需要前端校验
3. **性能**：大量离线数据同步时需要分批处理
4. **ID 生成**：本地新增单位使用负数ID避免与服务器ID冲突
