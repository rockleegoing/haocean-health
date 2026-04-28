# 定性依据与处理依据表结构重构设计方案

## 概述

将定性依据和处理依据从单表结构拆分为：主表（存储基本信息）+ 内容表（存储可配置的内容行），支持内容行的动态管理。

## 一、数据库设计

### 1.1 定性依据主表 `sys_legal_basis`

| 字段 | 类型 | 说明 |
|------|------|------|
| basis_id | bigint(20) | 主键，自增 |
| basis_no | varchar(20) | 编号 |
| title | varchar(255) | 标题 |
| violation_type | varchar(100) | 违法类型 |
| issuing_authority | varchar(100) | 颁发机构 |
| effective_date | varchar(20) | 实施时间 |
| legal_level | varchar(20) | 效级 |
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

结构同 `sys_legal_basis`

### 1.4 处理依据内容表 `sys_processing_basis_content`

结构同 `sys_legal_basis_content`

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

#### LegalBasisEntity（主表）
```kotlin
@Entity(tableName = "sys_legal_basis")
data class LegalBasisEntity(
    @PrimaryKey val basisId: Long,
    val basisNo: String?,
    val title: String,
    val violationType: String?,
    val issuingAuthority: String?,
    val effectiveDate: String?,
    val legalLevel: String?,
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

### 3.2 API 响应

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

#### 详情页
书页式布局：
```
[标题]
[编号]                    [复制]
[编号内容...]

[违法类型]                [复制]
[违法类型内容...]

[条款内容]                [复制]
[条款内容...]

[法律责任]                [复制]
[法律责任...]

[裁量标准]                [复制]
[裁量标准...]

...（每个内容行都是 Label + 复制 + 内容）
```

**Android 端：只读显示，每行有复制按钮**

## 四、SQL 脚本

### 4.1 新建表脚本 V1.2.6__legal_basis_refactoring.sql

- 创建 `sys_legal_basis_content` 表
- 创建 `sys_processing_basis_content` 表
- 添加外键约束（级联删除）

### 4.2 初始数据脚本 V1.2.7__legal_basis_mock_data.sql

- 插入定性依据主表数据（10条）
- 插入定性依据内容表数据（每条依据3-5行内容）
- 插入处理依据主表数据（10条）
- 插入处理依据内容表数据（每条依据3-5行内容）

## 五、实现顺序

1. **后端**：创建新表、修改实体、修改 Service、修改 Controller
2. **数据库**：执行 SQL 脚本
3. **Android**：修改 Room 实体、修改 API 解析、同步逻辑更新
4. **Web 前端**：调整表单，支持内容行动态添加
5. **测试**：全流程测试

## 六、注意事项

1. 旧表 `sys_legal_basis` 和 `sys_processing_basis` 中的 `clauses`、`legal_liability`、`discretion_standard` 字段保留暂不删除（兼容旧接口）
2. 新接口返回数据时使用新表结构
3. 迁移期间旧数据仍可访问，新功能使用新表结构
