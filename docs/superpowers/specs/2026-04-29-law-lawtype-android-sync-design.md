# 法律法规 Android 同步设计

> **日期：** 2026-04-29
> **状态：** 已确认

## 1. 概述

实现法律法规（Law）及法律类型（LawType）的 Android 端数据同步功能。Law 和 LawType 都是下行同步数据（后台为准，App 只读）。

## 2. 数据模型设计

### 2.1 LawTypeEntity（法律类型实体）

```kotlin
@Entity(
    tableName = "law_type",
    indices = [Index(value = ["parentId"])]
)
data class LawTypeEntity(
    @PrimaryKey val id: Long,           // 主键
    val parentId: Long,                 // 父类型ID（0为顶级）
    val ancestors: String,              // 祖先路径（格式：0,1,2）
    val name: String,                  // 类型名称
    val icon: String?,                 // 图标
    val sort: Int,                     // 排序
    val status: String                // 状态（0正常 1停用）
)
```

### 2.2 LawEntity 更新（添加 typeId）

```kotlin
@Entity(
    tableName = "law",
    indices = [
        Index(value = ["name"]),
        Index(value = ["typeId"])  // 新增
    ]
)
data class LawEntity(
    @PrimaryKey val id: Long,           // 主键
    val name: String,                   // 法律名称
    val releaseTime: Long?,           // 发布日期
    val typeId: Long?                  // 法律法规类型ID（新增）
)
```

## 3. 后端 API 设计

### 3.1 AppLawTypeController（新建）

**文件：** `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppLawTypeController.java`

| 接口 | 方法 | 说明 |
|-----|------|------|
| `/app/lawtype/treeList` | GET | 获取法律类型树形结构 |

### 3.2 AppLawController 更新

已有的 `/app/law/list` 接口已返回 `typeId` 字段（需确认后端 LawMapper.xml 已包含）。

## 4. Android 架构

```
┌─────────────────────────────────────────────────────┐
│                   SyncManager                        │
│  MODULE_LAW_TYPE = "法律法规类型"                   │
│  MODULE_LAW = "法律法规"                           │
└─────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────┐
│              LawTypeRepository                       │
│  - syncLawTypes(): 同步法律类型树                   │
└─────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────┐
│                 LawTypeApi                          │
│  - getLawTypeTree(): GET /app/lawtype/treeList    │
└─────────────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────┐
│              LawTypeDao (Room)                      │
│  - getAll(): List<LawTypeEntity>                   │
│  - insertAll(): 批量插入                           │
│  - deleteAll(): 清空                               │
└─────────────────────────────────────────────────────┘
```

## 5. 同步流程

```
SyncManager.syncAll()
    │
    ├── syncLawType() → LawTypeRepository.syncLawTypes()
    │                      └── LawTypeApi.getLawTypeTree()
    │                          └── LawTypeDao.insertAll()
    │
    └── syncLaw() → LawRepository.syncLaws()
                      └── LawApi.getLawList()
                          └── LawDao.insertAll()
```

## 6. 文件清单

### 6.1 后端（RuoYi-Vue）

| 文件 | 操作 | 职责 |
|-----|------|-----|
| `ruoyi-admin/.../AppLawTypeController.java` | 新建 | 法律类型 Android 接口 |
| `ruoyi-system/.../LawMapper.xml` | 确认 | selectLawList 已含 typeId |

### 6.2 Android（Ruoyi-Android-App）

| 文件 | 操作 | 职责 |
|-----|------|-----|
| `app/.../entity/LawTypeEntity.kt` | 新建 | 法律类型实体 |
| `app/.../dao/LawTypeDao.kt` | 新建 | 法律类型 DAO |
| `app/.../api/LawTypeApi.kt` | 新建 | 法律类型 API |
| `app/.../repository/LawTypeRepository.kt` | 新建 | 法律类型 Repository |
| `app/.../entity/LawEntity.kt` | 修改 | 添加 typeId 字段 |
| `app/.../dao/LawDao.kt` | 修改 | 添加 typeId 索引 |
| `app/.../api/LawApi.kt` | 修改 | LawDto 添加 typeId |
| `app/.../repository/LawRepository.kt` | 修改 | syncLaws 增强 |
| `app/.../database/AppDatabase.kt` | 修改 | 注册 LawTypeEntity |
| `app/.../sync/SyncManager.kt` | 修改 | 添加同步模块常量 |

## 7. API 响应格式

### 7.1 法律类型树

```json
GET /app/lawtype/treeList

{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "id": 1,
      "parentId": 0,
      "ancestors": "0",
      "name": "国家法律",
      "icon": "el-icon-document",
      "sort": 1,
      "status": "0",
      "children": [
        {
          "id": 4,
          "parentId": 1,
          "ancestors": "0,1",
          "name": "宪法",
          "icon": "el-icon-document",
          "sort": 1,
          "status": "0"
        }
      ]
    }
  ]
}
```

### 7.2 法律列表

```json
GET /app/law/list

{
  "code": 200,
  "msg": "操作成功",
  "rows": [
    {
      "id": 1,
      "name": "中华人民共和国宪法",
      "releaseTime": "2023-01-01",
      "typeId": 4
    }
  ]
}
```

## 8. 同步策略

根据 `rules/sync-conflict.md` 规范：

| 数据类型 | 同步方向 | 冲突策略 |
|---------|---------|---------|
| 法律法规 | 下行 | 后台为准 |
| 法律类型 | 下行 | 后台为准 |

均为只读数据，无需冲突处理。

## 9. 数据库迁移

AppDatabase version 从 16 升级到 17，添加 `law_type` 表。

---

## 10. 实现顺序

1. **后端**：新增 AppLawTypeController
2. **Android**：
   - LawTypeEntity + LawTypeDao
   - LawTypeApi + LawTypeRepository
   - LawEntity 添加 typeId
   - LawApi 更新 LawDto
   - LawRepository 增强
   - AppDatabase 注册
   - SyncManager 添加模块
