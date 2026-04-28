# 定性依据与处理依据表结构重构设计方案

## 概述

将定性依据和处理依据从单表结构拆分为：主表（存储基本信息）+ 内容表（存储可配置的内容行），支持内容行的动态管理。

## 一、数据库设计

### 1.1 定性依据主表 `sys_legal_basis`

| 字段 | 类型 | 说明 |
|------|------|------|
| basis_id | bigint(20) | 主键，自增 |
| title | varchar(255) | 标题 |
| regulation_id | bigint(20) | 关联法规ID |
| status | char(1) | 状态：0正常 1停用 |
| del_flag | char(1) | 删除标志：0正常 2删除 |
| create_by | varchar(64) | 创建者 |
| create_time | datetime | 创建时间 |
| update_by | varchar(64) | 更新者 |
| update_time | datetime | 更新时间 |
| remark | varchar(500) | 备注 |

### 1.2 定性依据内容表 `sys_legal_basis_content`

| 字段 | 类型 | 说明 |
|------|------|------|
| content_id | bigint(20) | 主键，自增 |
| basis_id | bigint(20) | 关联定性依据ID |
| label | varchar(100) | 标签（如：条款内容、法律责任、裁量标准） |
| content | text | 内容 |
| sort_order | int | 排序 |
| create_by | varchar(64) | 创建者 |
| create_time | datetime | 创建时间 |

**外键**：`basis_id` REFERENCES `sys_legal_basis`(basis_id) ON DELETE CASCADE

### 1.3 处理依据主表 `sys_processing_basis`

| 字段 | 类型 | 说明 |
|------|------|------|
| basis_id | bigint(20) | 主键，自增 |
| title | varchar(255) | 标题 |
| regulation_id | bigint(20) | 关联法规ID |
| status | char(1) | 状态：0正常 1停用 |
| del_flag | char(1) | 删除标志：0正常 2删除 |
| create_by | varchar(64) | 创建者 |
| create_time | datetime | 创建时间 |
| update_by | varchar(64) | 更新者 |
| update_time | datetime | 更新时间 |
| remark | varchar(500) | 备注 |

### 1.4 处理依据内容表 `sys_processing_basis_content`

| 字段 | 类型 | 说明 |
|------|------|------|
| content_id | bigint(20) | 主键，自增 |
| basis_id | bigint(20) | 关联处理依据ID |
| label | varchar(100) | 标签 |
| content | text | 内容 |
| sort_order | int | 排序 |
| create_by | varchar(64) | 创建者 |
| create_time | datetime | 创建时间 |

**外键**：`basis_id` REFERENCES `sys_processing_basis`(basis_id) ON DELETE CASCADE

## 二、后端设计

### 2.1 新增接口

#### 定性依据内容
- `POST /system/legalBasisContent` - 新增内容行
- `PUT /system/legalBasisContent` - 修改内容行
- `DELETE /system/legalBasisContent/{contentId}` - 删除内容行
- `GET /system/legalBasisContent/list/{basisId}` - 获取某定性依据的所有内容

#### 处理依据内容
- `POST /system/processingBasisContent` - 新增内容行
- `PUT /system/processingBasisContent` - 修改内容行
- `DELETE /system/processingBasisContent/{contentId}` - 删除内容行
- `GET /system/processingBasisContent/list/{basisId}` - 获取某处理依据的所有内容

### 2.2 修改接口

#### 定性依据
- `POST /system/legalBasis` - 新增定性依据（包含内容列表，事务处理）
- `PUT /system/legalBasis` - 修改定性依据（包含内容列表，事务处理）
- `GET /system/legalBasis/{basisId}` - 获取详情（主表+内容列表）

#### 处理依据
- `POST /system/processingBasis` - 新增处理依据（包含内容列表，事务处理）
- `PUT /system/processingBasis` - 修改处理依据（包含内容列表，事务处理）
- `GET /system/processingBasis/{basisId}` - 获取详情（主表+内容列表）

### 2.3 级联删除

数据库层设置 `ON DELETE CASCADE`，删除主表时自动删除关联内容表数据。

## 三、Android 设计

### 3.1 Room 实体

#### LegalBasisEntity（主表 - 精简版）
```kotlin
@Entity(tableName = "sys_legal_basis")
data class LegalBasisEntity(
    @PrimaryKey val basisId: Long,
    val title: String,
    val regulationId: Long?,
    val status: String,
    val delFlag: String,
    val createBy: String?,
    val createTime: Long?,
    val updateBy: String?,
    val updateTime: Long?,
    val remark: String?,
    val syncStatus: String = "SYNCED"
)
```

#### LegalBasisContentEntity（内容表）
```kotlin
@Entity(
    tableName = "sys_legal_basis_content",
    foreignKeys = [ForeignKey(
        entity = LegalBasisEntity::class,
        parentColumns = ["basis_id"],
        childColumns = ["basis_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class LegalBasisContentEntity(
    @PrimaryKey val contentId: Long,
    val basisId: Long,
    val label: String,
    val content: String,
    val sortOrder: Int,
    val createBy: String?,
    val createTime: Long?
)
```

#### ProcessingBasisEntity（主表 - 精简版）
```kotlin
@Entity(tableName = "sys_processing_basis")
data class ProcessingBasisEntity(
    @PrimaryKey val basisId: Long,
    val title: String,
    val regulationId: Long?,
    val status: String,
    val delFlag: String,
    val createBy: String?,
    val createTime: Long?,
    val updateBy: String?,
    val updateTime: Long?,
    val remark: String?,
    val syncStatus: String = "SYNCED"
)
```

#### ProcessingBasisContentEntity（内容表）
```kotlin
@Entity(
    tableName = "sys_processing_basis_content",
    foreignKeys = [ForeignKey(
        entity = ProcessingBasisEntity::class,
        parentColumns = ["basis_id"],
        childColumns = ["basis_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ProcessingBasisContentEntity(
    @PrimaryKey val contentId: Long,
    val basisId: Long,
    val label: String,
    val content: String,
    val sortOrder: Int,
    val createBy: String?,
    val createTime: Long?
)
```

### 3.2 DAO 接口

#### LegalBasisContentDao
```kotlin
@Dao
interface LegalBasisContentDao {
    @Query("SELECT * FROM sys_legal_basis_content WHERE basis_id = :basisId ORDER BY sort_order ASC")
    fun getContentsByBasisId(basisId: Long): Flow<List<LegalBasisContentEntity>>

    @Query("SELECT * FROM sys_legal_basis_content WHERE basis_id = :basisId ORDER BY sort_order ASC")
    suspend fun getContentsByBasisIdList(basisId: Long): List<LegalBasisContentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContents(contents: List<LegalBasisContentEntity>)

    @Query("DELETE FROM sys_legal_basis_content WHERE basis_id = :basisId")
    suspend fun deleteByBasisId(basisId: Long)

    @Query("DELETE FROM sys_legal_basis_content")
    suspend fun deleteAll()
}
```

#### ProcessingBasisContentDao
同上，替换表名和类名

### 3.3 Repository 层

LawRepository 新增方法：
```kotlin
fun getLegalBasisContents(basisId: Long): Flow<List<LegalBasisContentEntity>>
fun getProcessingBasisContents(basisId: Long): Flow<List<ProcessingBasisContentEntity>>
```

### 3.4 API 响应

#### 列表接口
返回主表数据（不含内容）

#### 详情接口
返回主表 + 内容列表
```json
{
    "code": 200,
    "data": {
        "basis": { ...主表数据 },
        "contents": [ ...内容列表 ]
    }
}
```

### 3.3 同步逻辑

1. 同步主表数据
2. 同步内容表数据（按 basisId 关联）

### 3.4 UI 设计

#### 列表页
不变，显示主表标题列表

#### 详情页（重构）
全部数据从内容表读取，按 sort_order 顺序显示。书页式布局：
```
[标题]

[编号]                    [复制]
[编号内容...]

[违法类型]                [复制]
[违法类型内容...]

[颁发机构]                [复制]
[颁发机构内容...]

[实施时间]                [复制]
[实施时间内容...]

[效级]                    [复制]
[效级内容...]

[条款内容]                [复制]
[条款内容内容...]

[法律责任]                [复制]
[法律责任内容...]

[裁量标准]                [复制]
[裁量标准内容...]

...（每个内容行都是 Label + 复制 + 内容）
```

**Android 端：只读显示，每行有复制按钮。数据全部从内容表读取，按 sort_order 排序。**

## 四、SQL 脚本

### 4.1 新建表脚本 V1.2.6__legal_basis_refactoring.sql

- 创建 `sys_legal_basis_content` 表
- 创建 `sys_processing_basis_content` 表
- 添加外键约束（级联删除）
- 修改旧表 `sys_legal_basis`，删除 `basis_no`、`violation_type`、`issuing_authority`、`effective_date`、`legal_level`、`clauses`、`legal_liability`、`discretion_standard` 字段
- 修改旧表 `sys_processing_basis`，删除 `basis_no`、`violation_type`、`issuing_authority`、`effective_date`、`legal_level`、`clauses`、`legal_liability`、`discretion_standard` 字段

### 4.2 初始数据脚本 V1.2.7__legal_basis_mock_data.sql

#### 定性依据 Mock 数据

每条定性依据 8 行内容，sort_order 1-8：

| sort_order | label |
|------------|-------|
| 1 | 编号 |
| 2 | 违法类型 |
| 3 | 颁发机构 |
| 4 | 实施时间 |
| 5 | 效级 |
| 6 | 条款内容 |
| 7 | 法律责任 |
| 8 | 裁量标准 |

- 插入定性依据主表数据（10条）
- 插入定性依据内容表数据（每条依据8行内容，共80条）

#### 处理依据 Mock 数据

每条处理依据 8 行内容，sort_order 1-8（标签同上）

- 插入处理依据主表数据（10条）
- 插入处理依据内容表数据（每条依据8行内容，共80条）

## 五、实现顺序

1. **后端**：
   - 创建新表结构
   - 修改实体类（精简主表）
   - 修改 Service 层
   - 修改 Controller 层
   - 新增内容表 CRUD 接口
2. **数据库**：执行 SQL 脚本
3. **Android**：
   - 修改 Room 实体（精简主表）
   - 新增内容表实体
   - 修改 API 解析（适配新接口）
   - 更新同步逻辑
   - **清空详情页代码，重构整个详情页 UI**（从内容表读取数据）
4. **Web 前端**：调整表单，支持内容行动态添加
5. **测试**：全流程测试

## 六、注意事项

1. 旧表 `sys_legal_basis` 和 `sys_processing_basis` 中的 `basis_no`、`violation_type`、`issuing_authority`、`effective_date`、`legal_level`、`clauses`、`legal_liability`、`discretion_standard` 字段**删除**
2. 新接口返回数据时使用新表结构
3. Android 详情页重构，**全部从内容表读取数据**
4. 主表只保留基础字段（title, regulation_id, status, del_flag 等）
5. Android 端同步时需同步主表+内容表数据（定性依据和处理依据都要同步内容表）
