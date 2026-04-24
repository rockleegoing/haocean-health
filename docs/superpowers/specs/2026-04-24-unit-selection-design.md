# 选择单位模块设计方案

## 1. 需求概述

### 1.1 背景
执法人员在使用移动执法 APP 前，需要选择当前执法对象（被检查单位）。选择单位后，才能使用便捷执法中的各项功能。未选择单位时，执法功能全部禁用。

### 1.2 核心流程
```
登录 → 同步等待页(SyncWaitActivity) → 主页(MainActivity)
                                          ↓
                              检查是否已选单位
                              ↓              ↓
                           已选单位         未选单位
                              ↓              ↓
                          使用执法功能    → 弹出选择单位页
                                              ↓
                                         选择/跳过
                                            ↓
                                       返回主页
```

### 1.3 关键需求
| 需求 | 说明 |
|------|------|
| 离线优先 | 单位数据同步到 Room DB，本地查询 |
| 跳过机制 | 用户可跳过选择，直接进入主页 |
| 功能拦截 | 未选单位时，执法功能禁用 |
| 位置排序 | 有经纬度按距离排序，否则按创建时间倒序 |
| 分页加载 | 列表滚动分页显示 |
| 搜索筛选 | 名称搜索 + 行业分类 + 区域 + 监管类型 |

---

## 2. 后端设计

### 2.1 数据库表设计

**表名**: `sys_unit` (执法单位表)

```sql
CREATE TABLE `sys_unit` (
    `unit_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '单位ID',
    `unit_name` VARCHAR(100) NOT NULL COMMENT '单位名称',
    `industry_category` VARCHAR(50) DEFAULT NULL COMMENT '行业分类',
    `region` VARCHAR(50) DEFAULT NULL COMMENT '区域',
    `supervision_type` VARCHAR(50) DEFAULT NULL COMMENT '监管类型',
    `credit_code` VARCHAR(50) DEFAULT NULL COMMENT '统一社会信用代码',
    `legal_person` VARCHAR(50) DEFAULT NULL COMMENT '法定代表人',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `business_address` VARCHAR(255) DEFAULT NULL COMMENT '经营地址',
    `latitude` DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
    `longitude` DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态:0正常,1停用',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志:0存在,1删除',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`unit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执法单位表';
```

### 2.2 代码生成器使用

使用 RuoYi-Vue 内置代码生成器生成基础 CRUD：

1. **菜单路径**: `/tool/gen` → 点击"生成"
2. **表信息**:
   - 表名: `sys_unit`
   - 表前缀: `sys_`
   - 包名: `com.ruoyi.system`
   - 模块名: `system`
   - 业务名: `执法单位`
3. **生成内容**: Entity、Mapper、Service、ServiceImpl、Controller、Vue页面

### 2.3 后端 API 设计

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/app/unit/list` | 获取单位列表（匿名可访问） |
| GET | `/app/unit/{unitId}` | 获取单位详情（匿名可访问） |
| POST | `/app/unit` | 新增单位 |
| PUT | `/app/unit` | 修改单位 |
| DELETE | `/app/unit/{unitId}` | 删除单位 |

**说明**: `/app/unit/*` 接口需加入 `permitAll` 列表，允许匿名访问。

---

## 3. Android 端设计

### 3.1 页面结构

```
MainActivity
├── SelectUnitActivity (选择单位页)
│   ├── 搜索栏 + 筛选条件
│   ├── 单位列表 (RecyclerView + 分页)
│   └── 添加单位入口
└── AddUnitActivity (新增单位页) [可选后期]
```

### 3.2 路由配置

```kotlin
// Constant.kt
const val selectUnitRoute = "http://com.ruoyi/selectUnit"
const val addUnitRoute = "http://com.ruoyi/addUnit"
```

### 3.3 Room 数据库设计

**UnitEntity.kt**:
```kotlin
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
    val latitude: Double?,      // 纬度
    val longitude: Double?,     // 经度
    val status: String,
    val delFlag: String,
    val createTime: Long,
    val updateTime: Long?,
    val remark: String?
)
```

**UnitDao.kt**:
```kotlin
@Dao
interface UnitDao {
    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' ORDER BY createTime DESC")
    suspend fun getAllUnits(): List<UnitEntity>

    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' ORDER BY createTime DESC LIMIT :limit OFFSET :offset")
    suspend fun getUnitsPaged(limit: Int, offset: Int): List<UnitEntity>

    @Query("SELECT * FROM sys_unit WHERE delFlag = '0' AND unitName LIKE '%' || :keyword || '%'")
    suspend fun searchUnits(keyword: String): List<UnitEntity>

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
}
```

### 3.4 距离排序算法

```kotlin
/**
 * 计算两点之间的距离（单位：米）
 * 使用 Haversine 公式
 */
fun calculateDistance(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val R = 6371000.0 // 地球半径（米）
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}

/**
 * 排序：优先按距离，距离相同按创建时间倒序
 */
fun sortUnits(units: List<UnitEntity>, currentLat: Double?, currentLon: Double?): List<UnitEntity> {
    return if (currentLat != null && currentLon != null) {
        units.sortedWith(compareBy(
            // 优先：有经纬度的排前面
            { it.latitude == null || it.longitude == null },
            // 按距离排序
            { if (it.latitude != null && it.longitude != null)
                calculateDistance(currentLat, currentLon, it.latitude!!, it.longitude!!)
              else Double.MAX_VALUE }
        ))
    } else {
        units.sortedByDescending { it.createTime }
    }
}
```

### 3.5 当前选中单位管理

```kotlin
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

### 3.6 同步逻辑修改

**当前 `/app/sync` 响应**:
```json
{
    "code": 200,
    "users": [...],
    "depts": [...],
    "roles": [...],
    "menus": [...]
}
```

**新增 units 字段**:
```json
{
    "code": 200,
    "users": [...],
    "depts": [...],
    "roles": [...],
    "menus": [...],
    "units": [
        {
            "unitId": 1,
            "unitName": "测试单位",
            "industryCategory": "公共场所",
            "region": "北京",
            "supervisionType": "日常监督",
            "creditCode": "91110000XXXXXXXX",
            "legalPerson": "张三",
            "contactPhone": "13800138000",
            "businessAddress": "北京市朝阳区XXX路XXX号",
            "latitude": 39.904200,
            "longitude": 116.407400,
            "status": "0",
            "delFlag": "0",
            "createTime": "2026-04-24 10:00:00"
        }
    ]
}
```

### 3.7 跳转逻辑

**SyncWaitActivity.kt**:
```kotlin
when (result.status) {
    SyncStatus.SUCCESS -> {
        // 同步成功，跳转到主页
        TheRouter.build(Constant.mainRoute).navigation()
        finish()
    }
}
```

**MainActivity.kt**:
```kotlin
override fun initData() {
    // 检查是否已选单位
    if (!SelectedUnitManager.hasSelectedUnit()) {
        // 未选单位，弹出选择单位页
        TheRouter.build(Constant.selectUnitRoute).navigation()
    }

    // 后续初始化...
}
```

### 3.8 执法功能拦截

在需要拦截的 Fragment/Activity 中：
```kotlin
fun checkUnitSelected(): Boolean {
    if (!SelectedUnitManager.hasSelectedUnit()) {
        ToastUtils.show("请先选择执法单位")
        TheRouter.build(Constant.selectUnitRoute).navigation()
        return false
    }
    return true
}
```

---

## 4. 同步流程

### 4.1 完整同步流程

```
1. LoginActivity.preloadLoginData()
   ↓
2. 调用 /app/sync 接口
   ↓
3. 解析响应中的 users, depts, roles, menus, units
   ↓
4. 分别存储到 Room DB
   ↓
5. 同步完成，进入主页
   ↓
6. MainActivity 检查是否已选单位
```

### 4.2 同步管理器更新

**SyncManager.kt** 新增模块：
```kotlin
const val MODULE_UNIT = "执法单位"

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

private suspend fun syncUnits(context: Context?): Boolean {
    // 调用 /app/unit/list 获取单位数据
    // 存储到 Room DB
    return true
}
```

---

## 5. 实现步骤

### 阶段一：后端基础 (预计 2-3 小时)
1. 创建 `sys_unit` 数据表
2. 使用代码生成器生成 CRUD 代码
3. 新增 `/app/unit/*` 匿名可访问接口
4. 修改 `/app/sync` 接口，添加 units 字段
5. 更新 SecurityConfig，添加 `/app/unit/**` 到 permitAll

### 阶段二：Android 基础 (预计 2-3 小时)
1. 新增 `UnitEntity.kt` (Room)
2. 新增 `UnitDao.kt`
3. 更新 `AppDatabase.kt`
4. 新增 `UnitRepository.kt`
5. 新增 `SelectUnitActivity.kt` (选择单位页)
6. 修改 `LoginActivity.preloadLoginData()` 同步单位数据

### 阶段三：位置排序 (预计 1-2 小时)
1. 新增距离计算工具类
2. 实现带位置排序的列表查询
3. 获取设备当前位置

### 阶段四：功能完善 (预计 2-3 小时)
1. 实现搜索 + 筛选功能
2. 实现分页加载
3. 添加当前单位显示和切换功能
4. 执法功能拦截逻辑

---

## 6. 文件清单

### 后端 (RuoYi-Vue)
| 文件 | 说明 |
|------|------|
| `sql/sys_unit.sql` | 建表语句 |
| `SysUnit.java` | 实体类 |
| `SysUnitMapper.xml` | Mapper XML |
| `SysUnitMapper.java` | Mapper 接口 |
| `ISysUnitService.java` | 服务接口 |
| `SysUnitServiceImpl.java` | 服务实现 |
| `SysUnitController.java` | 控制器 |
| `SecurityConfig.java` | 添加 /app/unit/** |

### Android (Ruoyi-Android-App)
| 文件 | 说明 |
|------|------|
| `UnitEntity.kt` | 单位实体 |
| `UnitDao.kt` | 单位 DAO |
| `AppDatabase.kt` | 添加 UnitDao |
| `UnitRepository.kt` | 单位仓库 |
| `UnitApi.kt` | 单位 API |
| `SelectUnitActivity.kt` | 选择单位页 |
| `SelectUnitViewModel.kt` | 选择单位 VM |
| `activity_select_unit.xml` | 布局文件 |
| `SelectedUnitManager.kt` | 选中单位管理 |
| `DistanceUtils.kt` | 距离计算工具 |
| `Constant.kt` | 添加路由常量 |
| `SyncManager.kt` | 添加单位同步模块 |

---

## 7. 验证方式

### 7.1 后端验证
```bash
# 测试单位列表接口
curl http://localhost:8080/app/unit/list

# 测试同步接口包含单位
curl http://localhost:8080/app/sync
```

### 7.2 Android 验证
1. 重新编译安装 APK
2. 激活设备 → 登录
3. 观察同步等待页显示"执法单位"同步进度
4. 进入主页 → 弹出选择单位页
5. 列表按距离/时间排序
6. 选择单位 → 返回主页
7. 执法功能可用

---

## 8. 待确认事项

- [ ] `sys_unit` 表字段是否完整？
- [ ] 经纬度数据来源？（手动录入还是地图选点）
- [ ] 添加单位功能是否本期实现？
- [ ] 单位切换功能是否需要？
