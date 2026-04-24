# 选择单位模块设计方案（含行业分类）

## 1. 需求概述

### 1.1 背景
执法人员在使用移动执法 APP 前，需要选择当前执法对象（被检查单位）。选择单位后，才能使用便捷执法中的各项功能。未选择单位时，执法功能全部禁用。

**行业分类是所有执法操作的核心基础**，后续的监管事项、文书模板、规范用语等功能都与行业分类关联。

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
| 离线优先 | 单位、行业分类数据同步到 Room DB，本地查询 |
| 跳过机制 | 用户可跳过选择，直接进入主页 |
| 功能拦截 | 未选单位时，执法功能禁用 |
| 位置排序 | 有经纬度按距离排序，否则按创建时间倒序 |
| 分页加载 | 列表滚动分页显示 |
| 搜索筛选 | 名称搜索 + 行业分类 + 区域 + 监管类型 |
| 行业分类优先 | 行业分类单独维护，与单位关联 |

---

## 2. 后端设计

### 2.1 数据库表设计

#### 2.1.1 行业分类表 `sys_industry_category`

```sql
CREATE TABLE `sys_industry_category` (
    `category_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父分类ID',
    `ancestors` VARCHAR(100) DEFAULT '' COMMENT '祖级列表',
    `category_code` VARCHAR(50) DEFAULT NULL COMMENT '分类编码',
    `level` INT(1) DEFAULT 1 COMMENT '层级(1=一级,2=二级,3=三级)',
    `order_num` INT(3) DEFAULT 0 COMMENT '显示顺序',
    `status` CHAR(1) DEFAULT '0' COMMENT '状态(0=正常,1=停用)',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0=存在,1=删除)',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行业分类表';
```

#### 2.1.2 执法单位表 `sys_unit`（修改版）

```sql
CREATE TABLE `sys_unit` (
    `unit_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '单位ID',
    `unit_name` VARCHAR(100) NOT NULL COMMENT '单位名称',
    `industry_category_id` BIGINT(20) DEFAULT NULL COMMENT '行业分类ID',
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
    PRIMARY KEY (`unit_id`),
    KEY `idx_industry_category_id` (`industry_category_id`),
    CONSTRAINT `fk_unit_industry_category` FOREIGN KEY (`industry_category_id`)
        REFERENCES `sys_industry_category` (`category_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执法单位表';
```

**关键变更**：`industry_category` (VARCHAR) → `industry_category_id` (BIGINT FK)

### 2.2 代码生成器使用

#### 2.2.1 行业分类 CRUD
使用 RuoYi 代码生成器：

1. **菜单路径**: `/tool/gen` → 点击"生成"
2. **表信息**:
   - 表名: `sys_industry_category`
   - 表前缀: `sys_`
   - 包名: `com.ruoyi.system`
   - 模块名: `system`
   - 业务名: `行业分类`

#### 2.2.2 执法单位 CRUD
1. **表名**: `sys_unit`
2. **业务名**: `执法单位`

### 2.3 后端 API 设计

#### 2.3.1 行业分类 API (`/app/category/*`)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/app/category/list` | 获取行业分类列表（树形） |
| GET | `/app/category/{categoryId}` | 获取分类详情 |

#### 2.3.2 执法单位 API (`/app/unit/*`)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/app/unit/list` | 获取单位列表 |
| GET | `/app/unit/{unitId}` | 获取单位详情 |

**说明**: 所有 `/app/*` 接口需加入 `permitAll` 列表，允许匿名访问。

### 2.4 /app/sync 响应更新

```json
{
    "code": 200,
    "users": [...],
    "depts": [...],
    "roles": [...],
    "menus": [...],
    "categories": [
        {
            "categoryId": 1,
            "categoryName": "公共场所",
            "parentId": 0,
            "ancestors": "0",
            "categoryCode": "GCDG",
            "level": 1,
            "orderNum": 1,
            "status": "0",
            "children": [
                {
                    "categoryId": 2,
                    "categoryName": "住宿场所",
                    "parentId": 1,
                    "ancestors": "0,1",
                    "categoryCode": "GCDG-ZSDG",
                    "level": 2,
                    "orderNum": 1,
                    "status": "0",
                    "children": []
                }
            ]
        }
    ],
    "units": [
        {
            "unitId": 1,
            "unitName": "北京饭店",
            "industryCategoryId": 2,
            "industryCategoryName": "住宿场所",
            "region": "北京",
            "supervisionType": "日常监督",
            "creditCode": "91110000100001234",
            "legalPerson": "李明",
            "contactPhone": "010-12345678",
            "businessAddress": "北京市东城区东长安街33号",
            "latitude": 39.904200,
            "longitude": 116.407400,
            "status": "0",
            "delFlag": "0",
            "createTime": "2026-04-24 10:00:00"
        }
    ]
}
```

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

#### 3.3.1 IndustryCategoryEntity (行业分类)

```kotlin
@Entity(tableName = "sys_industry_category")
data class IndustryCategoryEntity(
    @PrimaryKey val categoryId: Long,
    val categoryName: String,
    val parentId: Long,          // 父分类ID，0表示顶级
    val ancestors: String,        // 祖级列表 "0,1,2"
    val categoryCode: String?,   // 分类编码
    val level: Int,              // 层级 1/2/3
    val orderNum: Int,           // 排序
    val status: String,
    val delFlag: String,
    val createBy: String?,
    val createTime: Long,
    val updateBy: String?,
    val updateTime: Long?,
    val remark: String?,
    // 非数据库字段，用于内存中的树形结构
    @Ignore var children: List<IndustryCategoryEntity> = emptyList()
)
```

#### 3.3.2 UnitEntity (执法单位)

```kotlin
@Entity(tableName = "sys_unit")
data class UnitEntity(
    @PrimaryKey val unitId: Long,
    val unitName: String,
    val industryCategoryId: Long?,   // 行业分类ID（外键）
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

#### 3.3.3 IndustryCategoryDao

```kotlin
@Dao
interface IndustryCategoryDao {

    @Query("SELECT * FROM sys_industry_category WHERE delFlag = '0' AND status = '0' ORDER BY orderNum")
    suspend fun getAllCategories(): List<IndustryCategoryEntity>

    @Query("SELECT * FROM sys_industry_category WHERE delFlag = '0' AND status = '0' AND parentId = :parentId ORDER BY orderNum")
    suspend fun getCategoriesByParent(parentId: Long): List<IndustryCategoryEntity>

    @Query("SELECT * FROM sys_industry_category WHERE categoryId = :categoryId")
    suspend fun getCategoryById(categoryId: Long): IndustryCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<IndustryCategoryEntity>)

    @Query("DELETE FROM sys_industry_category")
    suspend fun deleteAllCategories()

    /**
     * 构建分类树
     * 将扁平列表转换为树形结构
     */
    suspend fun buildCategoryTree(): List<IndustryCategoryEntity> {
        val allCategories = getAllCategories()
        return buildTree(allCategories, 0)
    }

    private fun buildTree(categories: List<IndustryCategoryEntity>, parentId: Long): List<IndustryCategoryEntity> {
        return categories.filter { it.parentId == parentId }
            .map { category ->
                category.copy(
                    children = buildTree(categories, category.categoryId)
                )
            }
    }
}
```

#### 3.3.4 UnitDao

```kotlin
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

    @Query("SELECT * FROM sys_unit WHERE unitId = :unitId")
    suspend fun getUnitById(unitId: Long): UnitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnits(units: List<UnitEntity>)

    @Query("DELETE FROM sys_unit")
    suspend fun deleteAllUnits()
}
```

### 3.4 SelectedUnitManager 扩展

```kotlin
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

    fun getSelectedCategoryId(): Long? {
        val id = MMKV.defaultMMKV().decodeLong(KEY_SELECTED_CATEGORY_ID, -1)
        return if (id == -1L) null else id
    }

    fun getSelectedCategoryName(): String? {
        return MMKV.defaultMMKV().decodeString(KEY_SELECTED_CATEGORY_NAME)
    }

    // ... 其他方法
}
```

### 3.5 同步流程

```
1. LoginActivity.preloadLoginData()
   ↓
2. 调用 /app/sync 接口
   ↓
3. 解析响应中的 categories, units
   ↓
4. 先存储行业分类到 Room (categories)
   ↓
5. 再存储单位到 Room (units)
   ↓
6. 同步完成，进入主页
```

---

## 4. 实现步骤

### 阶段一：行业分类后端 (预计 1-2 小时)
1. 创建 `sys_industry_category` 数据表
2. 使用代码生成器生成 CRUD 代码
3. 新增 `/app/category/*` 匿名可访问接口
4. 修改 `/app/sync` 接口，添加 categories 字段

### 阶段二：单位后端 (预计 1-2 小时)
1. 创建 `sys_unit` 数据表（修改版，含外键）
2. 使用代码生成器生成 CRUD 代码
3. 新增 `/app/unit/*` 匿名可访问接口
4. 更新 SecurityConfig

### 阶段三：Android 基础 (预计 2-3 小时)
1. 新增/更新 `IndustryCategoryEntity.kt`
2. 新增/更新 `IndustryCategoryDao.kt`
3. 更新 `UnitEntity.kt` (改为使用 industryCategoryId)
4. 更新 `AppDatabase.kt`
5. 新增 `SelectUnitActivity.kt`
6. 修改 `LoginActivity.preloadLoginData()` 同步分类和单位

### 阶段四：位置排序 (预计 1-2 小时)
1. 新增距离计算工具类
2. 实现带位置排序的列表查询

### 阶段五：筛选功能 (预计 1-2 小时)
1. 实现行业分类筛选
2. 实现区域筛选
3. 实现监管类型筛选

---

## 5. 文件清单

### 后端 (RuoYi-Vue)
| 文件 | 说明 |
|------|------|
| `sql/sys_industry_category.sql` | 行业分类建表 |
| `sql/sys_unit.sql` | 单位建表（修改版） |
| `SysIndustryCategory.java` | 分类实体 |
| `SysIndustryCategoryMapper.xml` | Mapper |
| `ISysIndustryCategoryService.java` | 服务接口 |
| `SysIndustryCategoryServiceImpl.java` | 服务实现 |
| `SysIndustryCategoryController.java` | 控制器 |
| `AppCategoryController.java` | Android分类接口 |
| `SysUnit.java` | 单位实体 |
| `SysUnitMapper.xml` | Mapper |
| `ISysUnitService.java` | 服务接口 |
| `SysUnitServiceImpl.java` | 服务实现 |
| `SysUnitController.java` | 控制器 |
| `AppUnitController.java` | Android单位接口 |
| `SysLoginController.java` | 修改 /app/sync |
| `SecurityConfig.java` | 添加 /app/** |

### Android (Ruoyi-Android-App)
| 文件 | 说明 |
|------|------|
| `IndustryCategoryEntity.kt` | 行业分类实体 |
| `IndustryCategoryDao.kt` | 分类 DAO |
| `UnitEntity.kt` | 单位实体（修改版） |
| `UnitDao.kt` | 单位 DAO |
| `AppDatabase.kt` | 添加 DAO |
| `CategoryRepository.kt` | 分类仓库 |
| `UnitRepository.kt` | 单位仓库 |
| `SelectUnitActivity.kt` | 选择单位页 |
| `SelectUnitViewModel.kt` | VM |
| `activity_select_unit.xml` | 布局 |
| `SelectedUnitManager.kt` | 选中管理 |
| `DistanceUtils.kt` | 距离工具 |
| `Constant.kt` | 添加路由 |
| `SyncManager.kt` | 添加同步 |
| `LoginActivity.kt` | 修改同步 |

---

## 6. 验证方式

### 6.1 后端验证
```bash
# 测试行业分类接口
curl http://localhost:8080/app/category/list

# 测试单位列表接口
curl http://localhost:8080/app/unit/list

# 测试同步接口
curl http://localhost:8080/app/sync
```

### 6.2 Android 验证
1. 重新编译安装 APK
2. 激活 → 登录
3. 观察同步等待页显示行业分类和单位同步
4. 进入主页 → 弹出选择单位页
5. 选择单位时显示对应行业分类
6. 后续执法功能可正常使用

---

## 7. 待确认事项

- [ ] 行业分类层级设计为几级？（建议3级）
- [ ] 是否需要支持分类的新增/编辑功能？
- [ ] 行业分类是否有预置数据需要初始化？
