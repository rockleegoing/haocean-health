# 法律法规类型多对多绑定设计

> **日期：** 2026-04-30
> **状态：** 已确认

## 1. 概述

将 `law` 与 `law_type` 从一对多改为多对多关系，支持一部法律关联多个类型。

## 2. 数据库设计

### 2.1 新建中间表 `law_type_bind`

```sql
CREATE TABLE `law_type_bind` (
  `law_id` BIGINT NOT NULL COMMENT '法律ID',
  `type_id` BIGINT NOT NULL COMMENT '类型ID',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`law_id`, `type_id`)
) ENGINE=INNODB COMMENT='法律类型关联表';
```

### 2.2 删除字段

```sql
ALTER TABLE `law` DROP COLUMN `type_id`;
```

## 3. 后端设计

### 3.1 目录结构

```
ruoyi-system/src/main/java/com/ruoyi/system/
├── domain/LawTypeBind.java
├── mapper/LawTypeBindMapper.java
├── service/ILawTypeBindService.java
├── service/impl/LawTypeBindServiceImpl.java
```

### 3.2 LawTypeBind 实体

```java
public class LawTypeBind {
    private Long lawId;
    private Long typeId;
    private String createBy;
    private Date createTime;
    // getter/setter
}
```

### 3.3 LawTypeBindMapper.xml

关键SQL：
- `selectByLawId` — 查询某法律关联的所有类型
- `selectByTypeId` — 查询某类型关联的所有法律
- `insertBatch` — 批量插入
- `deleteByLawId` — 删除某法律的全部绑定
- `deleteByLawIdAndTypeId` — 删除单个绑定

### 3.4 接口设计

| 接口 | 方法 | 说明 |
|-----|------|-----|
| `/app/lawtype/bind/{lawId}` | GET | 查询某法律关联的类型列表 |
| `/app/lawtype/bind/{lawId}/{typeId}` | POST | 添加绑定 |
| `/app/lawtype/bind/{lawId}` | DELETE | 删除某法律的全部绑定 |
| `/app/lawtype/bind/{lawId}/{typeId}` | DELETE | 删除单个绑定 |

路径：`/app/lawtype/bind`

## 4. 前端设计

### 4.1 law/index.vue 表单修改

- 类型选择从单选改为多选（el-select multiple + collapse-tags）
- 保存法律时，先保存法律基本信息，再调用绑定接口更新关联关系

### 4.2 类型选择器

```vue
<el-select v-model="lawForm.typeIds" multiple placeholder="请选择类型">
  <el-option
    v-for="item in lawTypeOptions"
    :key="item.id"
    :label="item.name"
    :value="item.id"
  />
</el-select>
```

## 5. Android 端设计

### 5.1 新建文件

| 文件 | 职责 |
|-----|-----|
| `LawTypeBindEntity.kt` | 中间表实体 |
| `LawTypeBindDao.kt` | 中间表DAO |
| `LawTypeBindApi.kt` | 绑定关系API |
| `LawTypeBindRepository.kt` | 绑定关系Repository |

### 5.2 LawTypeBindEntity

```kotlin
@Entity(tableName = "law_type_bind", primaryKeys = ["lawId", "typeId"])
data class LawTypeBindEntity(
    val lawId: Long,
    val typeId: Long,
    val createTime: Long?
)
```

### 5.3 修改文件

| 文件 | 变更 |
|-----|------|
| `LawEntity.kt` | 移除 `typeId` 字段 |
| `AppDatabase.kt` | 注册 LawTypeBindEntity，version 17→18 |
| `SyncManager.kt` | 添加 `MODULE_LAW_TYPE_BIND` |

### 5.4 同步流程

```
SyncManager.syncAll()
    │
    ├── syncLawType() → LawTypeRepository.syncLawTypes()
    │
    ├── syncLaw() → LawRepository.syncLaws()
    │
    └── syncLawTypeBind() → LawTypeBindRepository.syncLawTypeBinds()
                              └── 清空本地中间表 → 插入服务端数据
```

## 6. API 响应格式

### 6.1 查询绑定列表

```json
GET /app/lawtype/bind/{lawId}

{
  "code": 200,
  "msg": "操作成功",
  "data": [
    { "lawId": 1, "typeId": 4 },
    { "lawId": 1, "typeId": 5 }
  ]
}
```

## 7. 文件清单

### 7.1 后端新增

| 文件 | 职责 |
|-----|-----|
| `LawTypeBind.java` | 关联实体 |
| `LawTypeBindMapper.java` | DAO接口 |
| `LawTypeBindMapper.xml` | MyBatis映射 |
| `ILawTypeBindService.java` | Service接口 |
| `LawTypeBindServiceImpl.java` | Service实现 |
| `AppLawTypeBindController.java` | 匿名访问接口 |

### 7.2 后端修改

| 文件 | 变更 |
|-----|------|
| `Law.java` | 移除 typeId 字段 |
| `LawMapper.xml` | 移除 typeId 相关 |
| `LawMapper.java` | 移除 typeId 相关 |

### 7.3 前端修改

| 文件 | 变更 |
|-----|------|
| `law/index.vue` | 类型多选 |

### 7.4 Android 新增

| 文件 | 职责 |
|-----|-----|
| `LawTypeBindEntity.kt` | 中间表实体 |
| `LawTypeBindDao.kt` | 中间表DAO |
| `LawTypeBindApi.kt` | 绑定关系API |
| `LawTypeBindRepository.kt` | 绑定关系Repository |

### 7.5 Android 修改

| 文件 | 变更 |
|-----|------|
| `LawEntity.kt` | 移除 typeId |
| `AppDatabase.kt` | 注册新实体，版本升级 |
| `SyncManager.kt` | 添加 MODULE_LAW_TYPE_BIND |
| `LawApi.kt` | LawDto 移除 typeId |
| `LawRepository.kt` | toEntity 移除 typeId |

## 8. 数据库迁移

执行顺序：
1. 创建 `law_type_bind` 表
2. 迁移现有 `law.type_id` 数据到中间表
3. 删除 `law.type_id` 列
4. AppDatabase version 16→17→18

---
