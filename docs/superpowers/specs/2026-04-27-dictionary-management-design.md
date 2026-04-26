# 通用字典管理模块设计

> **Goal:** 为法律类型和监管类型创建独立的数据表和功能菜单，支持后台管理和 Android 移动端同步

## 1. 背景与需求

当前法律法规模块中：
- 法律类型和监管类型是硬编码数据（Android 端 `LegalType` 和 `SupervisionType` 对象）
- 需求：改为数据库管理，支持后台配置

**设计原则：**
- 法律类型和监管类型为独立表，无关联关系
- 作为通用字典，后续功能可复用
- 字典数据同步到 Android 移动端

## 2. 数据库设计

### 2.1 法律类型表 (sys_legal_type)

| 字段 | 类型 | 说明 |
|------|------|------|
| type_id | BIGINT | 主键 |
| type_code | VARCHAR(50) | 类型编码（唯一） |
| type_name | VARCHAR(100) | 类型名称 |
| sort_order | INT | 排序 |
| status | CHAR(1) | 状态：0正常 1停用 |
| create_by | VARCHAR(64) | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR(64) | 更新者 |
| update_time | DATETIME | 更新时间 |
| remark | VARCHAR(500) | 备注 |

### 2.2 监管类型表 (sys_supervision_type)

| 字段 | 类型 | 说明 |
|------|------|------|
| type_id | BIGINT | 主键 |
| type_code | VARCHAR(50) | 类型编码（唯一） |
| type_name | VARCHAR(100) | 类型名称 |
| sort_order | INT | 排序 |
| status | CHAR(1) | 状态：0正常 1停用 |
| create_by | VARCHAR(64) | 创建者 |
| create_time | DATETIME | 创建时间 |
| update_by | VARCHAR(64) | 更新者 |
| update_time | DATETIME | 更新时间 |
| remark | VARCHAR(500) | 备注 |

### 2.3 初始化数据

**法律类型初始数据：**
- 法律、法规、规章、规范性文件、批复文件、标准

**监管类型初始数据：**
- 食品生产、食品销售、餐饮服务、药品经营、医疗器械、化妆品、特种设备、工业产品、计量标准、认证认可、检验检测、广告监管、知识产权

## 3. 后端设计

### 3.1 模块结构

```
ruoyi-system/src/main/java/com/ruoyi/system/
├── domain/
│   └── dict/
│       ├── SysLegalType.java
│       └── SysSupervisionType.java
├── domain/vo/
│   └── LegalTypeVo.java (API 传输对象)
│   └── SupervisionTypeVo.java
├── mapper/
│   ├── SysLegalTypeMapper.java
│   └── SysSupervisionTypeMapper.java
├── mapper/xml/
│   ├── SysLegalTypeMapper.xml
│   └── SysSupervisionTypeMapper.xml
├── service/
│   ├── ISysLegalTypeService.java
│   ├── impl/SysLegalTypeServiceImpl.java
│   ├── ISysSupervisionTypeService.java
│   └── impl/SysSupervisionTypeServiceImpl.java
```

### 3.2 Controller 设计

**SysLegalTypeController.java**
- `GET /system/dict/legalType/list` - 列表查询
- `GET /system/dict/legalType/{typeId}` - 详情
- `POST /system/dict/legalType` - 新增
- `PUT /system/dict/legalType` - 修改
- `DELETE /system/dict/legalType/{typeIds}` - 删除
- `GET /system/dict/legalType/all` - 获取所有正常状态（用于下拉框）

**SysSupervisionTypeController.java**
- `GET /system/dict/supervisionType/list` - 列表查询
- `GET /system/dict/supervisionType/{typeId}` - 详情
- `POST /system/dict/supervisionType` - 新增
- `PUT /system/dict/supervisionType` - 修改
- `DELETE /system/dict/supervisionType/{typeIds}` - 删除
- `GET /system/dict/supervisionType/all` - 获取所有正常状态（用于下拉框）

## 4. 前端设计

### 4.1 菜单结构

```
系统管理 (menu_id=1)
└── 字典管理 (M目录, order=7)
    ├── 法律类型管理 (C菜单)
    └── 监管类型管理 (C菜单)
```

### 4.2 页面设计

**法律类型管理页面** (`/system/dict/legalType/index`)
- 表格列表（typeCode, typeName, sortOrder, status, createTime）
- 新增/编辑弹窗
- 删除确认
- 导出功能

**监管类型管理页面** (`/system/dict/supervisionType/index`)
- 表格列表（typeCode, typeName, sortOrder, status, createTime）
- 新增/编辑弹窗
- 删除确认
- 导出功能

### 4.3 法律法规页面改造

**修改 Regulation/index.vue**
- 法律类型下拉框：改为调用 API 获取字典数据
- 移除硬编码的法律类型选项

**修改 Article/index.vue**
- 法律类型下拉框：改为调用 API 获取字典数据

## 5. Android 端设计

### 5.1 数据库表

```kotlin
@Entity(tableName = "sys_legal_type")
data class LegalTypeEntity(
    @PrimaryKey val typeId: Long,
    val typeCode: String,
    val typeName: String,
    val sortOrder: Int,
    val status: String
)

@Entity(tableName = "sys_supervision_type")
data class SupervisionTypeEntity(
    @PrimaryKey val typeId: Long,
    val typeCode: String,
    val typeName: String,
    val sortOrder: Int,
    val status: String
)
```

### 5.2 同步策略

修改 `LawSyncManager`：
- 首次全量同步字典表
- 增量同步基于 updateTime

### 5.3 使用场景

**LawFragment.kt**
- 从本地数据库读取 LegalTypeEntity 和 SupervisionTypeEntity
- 显示分类网格

**RegulationListActivity.kt**
- 从本地数据库读取字典数据
- 法律类型、监管类型筛选下拉框

### 5.4 LawFragment 改造

```kotlin
// 获取法律类型列表
repository.getAllLegalTypes().collectLatest { types ->
    // 显示法律类型网格
}

// 获取监管类型列表
repository.getAllSupervisionTypes().collectLatest { types ->
    // 显示监管类型网格
}
```

## 6. API 设计

### 6.1 字典查询接口（匿名访问，供 Android 同步）

```
GET /system/dict/legalType/list
GET /system/dict/supervisionType/list
```

响应：
```json
{
  "code": 200,
  "msg": "操作成功",
  "rows": [
    { "typeId": 1, "typeCode": "law", "typeName": "法律", "sortOrder": 1 },
    { "typeCode": "regulation", "typeName": "法规", "sortOrder": 2 }
  ]
}
```

## 7. 实现顺序

1. **数据库**：创建表和初始化数据
2. **后端**：Domain、Mapper、Service、Controller
3. **前端菜单**：添加字典管理菜单
4. **前端页面**：法律类型管理、监管类型管理页面
5. **法律法规改造**：替换硬编码为 API 调用
6. **Android 数据库**：创建 Entity、Dao
7. **Android 同步**：修改 LawSyncManager
8. **Android 使用**：修改 LawFragment、RegulationListActivity

## 8. 文件清单

### 8.1 后端（新增）
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/dict/SysLegalType.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/dict/SysSupervisionType.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/vo/LegalTypeVo.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/vo/SupervisionTypeVo.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysLegalTypeMapper.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysSupervisionTypeMapper.java`
- `ruoyi-system/src/main/resources/mapper/system/SysLegalTypeMapper.xml`
- `ruoyi-system/src/main/resources/mapper/system/SysSupervisionTypeMapper.xml`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/ISysLegalTypeService.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/ISysSupervisionTypeService.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysLegalTypeServiceImpl.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysSupervisionTypeServiceImpl.java`
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLegalTypeController.java`
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysSupervisionTypeController.java`

### 8.2 前端（新增）
- `ruoyi-ui/src/views/system/dict/legalType/index.vue`
- `ruoyi-ui/src/views/system/dict/supervisionType/index.vue`
- `ruoyi-ui/src/api/system/dict.js`

### 8.3 前端（修改）
- `ruoyi-ui/src/views/system/regulation/index.vue` - 法律类型下拉改为API
- `ruoyi-ui/src/views/system/regulation/article/index.vue` - 法律类型下拉改为API

### 8.4 Android（新增）
- `app/src/main/java/com/ruoyi/app/feature/law/db/entity/LegalTypeEntity.kt`
- `app/src/main/java/com/ruoyi/app/feature/law/db/entity/SupervisionTypeEntity.kt`
- `app/src/main/java/com/ruoyi/app/feature/law/db/dao/LegalTypeDao.kt`
- `app/src/main/java/com/ruoyi/app/feature/law/db/dao/SupervisionTypeDao.kt`
- `app/src/main/java/com/ruoyi/app/feature/law/api/DictApi.kt`

### 8.5 Android（修改）
- `app/src/main/java/com/ruoyi/app/feature/law/model/Regulation.kt` - 移除硬编码的 LegalType、SupervisionType 对象
- `app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt` - 添加字典查询方法
- `app/src/main/java/com/ruoyi/app/sync/LawSyncManager.kt` - 添加字典同步
- `app/src/main/java/com/ruoyi/app/fragment/LawFragment.kt` - 从数据库读取字典

### 8.6 数据库
- `sql/V1.2.0__dict_tables.sql` - 字典表和初始化数据
- `sql/V1.2.1__dict_menu.sql` - 字典管理菜单
