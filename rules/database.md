# 数据库管理规范

## SQL 脚本管理

### 目录结构

```
RuoYi-Vue/sql/
├── ry_20260417.sql    # 主数据库脚本（系统表 + 业务表）
└── quartz.sql          # 定时任务表脚本
```

### 命名规范

| 类型 | 命名格式 | 示例 |
|------|---------|------|
| 全量脚本 | `ry_YYYYMMDD.sql` | `ry_20260417.sql` |
| 增量脚本 | `V{版本号}__{描述}.sql` | `V1.1.0__add_user_table.sql` |
| 回滚脚本 | `{原脚本名}_rollback.sql` | `V1.1.0__add_user_table_rollback.sql` |
| 模块脚本 | `{模块名}.sql` | `quartz.sql`, `generator.sql` |

### 脚本内容规范

```sql
-- ============================================
-- 脚本：V1.1.0__add_user_email.sql
-- 版本：1.1.0
-- 日期：2026-04-21
-- 描述：添加用户邮箱字段
-- 作者：张三
-- ============================================

-- 前置检查：确保表存在
DROP PROCEDURE IF EXISTS add_column;
CREATE PROCEDURE add_column()
BEGIN
    -- 检查列是否已存在
    IF NOT EXISTS (
        SELECT * FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA='ry'
        AND TABLE_NAME='sys_user'
        AND COLUMN_NAME='email'
    ) THEN
        -- 添加列
        ALTER TABLE sys_user ADD COLUMN email VARCHAR(255) COMMENT '邮箱';
    END IF;
END;

CALL add_column();
DROP PROCEDURE add_column;

-- 数据初始化（如需要）
-- UPDATE sys_user SET email = username || '@example.com' WHERE email IS NULL;
```

---

## 数据库变更流程

### 流程图

```
开发环境 → 测试环境 → 生产环境
   ↓          ↓          ↓
 自测     集成测试      上线
```

### 详细步骤

#### 1. 开发环境变更

```bash
# 1. 创建增量脚本
touch sql/V1.2.0__add_feature_x.sql

# 2. 在开发数据库执行验证
mysql -u dev -p ry < sql/V1.2.0__add_feature_x.sql

# 3. 验证功能正常
```

#### 2. 测试环境部署

```bash
# 1. 将脚本提交到代码库
git add sql/V1.2.0__add_feature_x.sql
git commit -m "feat(db): 添加功能 X 相关表结构"
git push

# 2. 通知测试人员在测试环境执行
# 3. 测试人员验证后反馈
```

#### 3. 生产环境部署

```bash
# 1. 变更申请（提前 1-2 天）
# 2. DBA 审核脚本
# 3. 维护窗口执行
# 4. 验证执行结果
```

### 变更申请表

| 字段 | 说明 |
|------|------|
| 变更单号 | DB-2026-001 |
| 申请人 | 张三 |
| 变更时间 | 2026-04-21 02:00-04:00 |
| 变更内容 | 添加用户邮箱字段 |
| 影响范围 | sys_user 表，预计影响 10 万行数据 |
| 回滚方案 | 执行回滚脚本 V1.2.0_rollback.sql |
| 审核人 | DBA 李四 |

---

## 移动卫生执法系统代码生成器使用

### 1. 导入数据库表

访问：`http://localhost:8080/tool/gen`

操作步骤：
1. 点击「导入」按钮
2. 选择要生成代码的表
3. 点击「确定」

### 2. 生成配置

在代码生成器页面编辑表配置：
- **生成模板**: 选择单表/树表/主子表
- **包路径**: `com.ruoyi.{module}`
- **生成模块名**: 如 `system`, `user`
- **生成业务名**: 如 `user`, `dept`

### 3. 生成代码

```bash
# 1. 点击「生成」按钮
# 2. 下载生成的 zip 包
# 3. 解压到对应目录

# 生成的文件包括：
- domain/      # 实体类
- mapper/      # DAO 层
- service/     # 业务层
- controller/  # 控制器
- sql/         # SQL 脚本
```

### 4. 注意事项

- 生成前务必备注表字段注释
- 主键建议使用自增 ID
- 字段类型选择要合理（金额用 DECIMAL，时间用 DATETIME）

---

## 数据备份规范

### 备份策略

| 环境 | 备份频率 | 保留时间 | 备份方式 |
|------|---------|---------|---------|
| 生产环境 | 每日全量 | 30 天 | mysqldump + binlog |
| 测试环境 | 每周全量 | 7 天 | mysqldump |
| 开发环境 | 按需备份 | 3 天 | 手动备份 |

### 备份脚本示例

```bash
#!/bin/bash
# backup.sh - MySQL 备份脚本

# 配置
DB_HOST="localhost"
DB_USER="backup_user"
DB_PASS="backup_password"
DB_NAME="ry"
BACKUP_DIR="/data/backup/mysql"
DATE=$(date +%Y%m%d_%H%M%S)

# 创建备份目录
mkdir -p $BACKUP_DIR

# 执行备份
mysqldump -h$DB_HOST -u$DB_USER -p$DB_PASS \
  --single-transaction \
  --master-data=2 \
  --databases $DB_NAME | gzip > $BACKUP_DIR/ry_$DATE.sql.gz

# 删除 30 天前的备份
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete

# 记录日志
echo "$(date): Backup completed: ry_$DATE.sql.gz" >> /var/log/mysql_backup.log
```

### 恢复流程

```bash
# 1. 找到要恢复的备份文件
ls -la /data/backup/mysql/ry_20260421.sql.gz

# 2. 解压并恢复
gunzip -c /data/backup/mysql/ry_20260421.sql.gz | mysql -u root -p ry

# 3. 验证恢复结果
mysql -u root -p -e "SELECT COUNT(*) FROM ry.sys_user;"
```

---

## 常用 SQL 命令

### 表结构查询

```sql
-- 查看表结构
DESC sys_user;

-- 查看建表语句
SHOW CREATE TABLE sys_user;

-- 查看所有表
SHOW TABLES;

-- 查看索引
SHOW INDEX FROM sys_user;
```

### 数据操作

```sql
-- 批量插入（从临时表）
INSERT INTO sys_user (user_name, email)
SELECT user_name, email FROM tmp_user;

-- 批量更新
UPDATE sys_user SET status = '0' WHERE dept_id = 1;

-- 批量删除（软删除）
UPDATE sys_user SET del_flag = '1' WHERE create_time < '2025-01-01';
```

### 性能分析

```sql
-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query%';

-- 查看当前连接
SHOW PROCESSLIST;

-- 分析 SQL 执行计划
EXPLAIN SELECT * FROM sys_user WHERE user_id = 1;
```

---

## 数据库优化建议

### 索引设计

- 主键必须使用自增 ID
- 外键列必须添加索引
- 经常用于 WHERE、ORDER BY、JOIN 的列考虑添加索引
- 避免在列上使用函数，会导致索引失效

### 表设计

- 单表数据量建议不超过 500 万行
- 使用合适的字段类型（INT vs BIGINT）
- 添加 create_time、update_time 字段
- 使用逻辑删除（del_flag）代替物理删除

### 查询优化

```sql
-- ❌ 避免 N+1 查询
SELECT * FROM sys_user;  -- 然后循环查询部门
SELECT * FROM sys_dept WHERE dept_id = ?;

-- ✅ 使用 JOIN
SELECT u.*, d.dept_name
FROM sys_user u
LEFT JOIN sys_dept d ON u.dept_id = d.dept_id;
```

---

## 权限管理

### 用户权限分配

```sql
-- 创建只读用户（用于报表查询）
CREATE USER 'readonly'@'%' IDENTIFIED BY 'password';
GRANT SELECT ON ry.* TO 'readonly'@'%';

-- 创建应用用户（用于后端服务）
CREATE USER 'app_user'@'%' IDENTIFIED BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE ON ry.* TO 'app_user'@'%';

-- 创建管理员用户（用于 DBA 操作）
CREATE USER 'dba'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON ry.* TO 'dba'@'%';
```

### 安全建议

- 生产环境禁止使用 root 用户连接
- 不同应用使用不同的数据库用户
- 定期审计用户权限
- 禁用匿名访问

---

## SQLite 本地数据库规范 (Android)

### 表设计规范

```sql
-- 表命名：t_模块前缀_业务名
CREATE TABLE t_law_record (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    unit_id         INTEGER NOT NULL DEFAULT 0,     -- 外键：单位 ID
    record_type     TEXT NOT NULL,                   -- 记录类型
    file_path       TEXT,                            -- 文件路径
    sync_status     TEXT DEFAULT 'PENDING',          -- 同步状态
    version         INTEGER DEFAULT 1,               -- 版本号（用于冲突检测）
    create_by       TEXT DEFAULT '',
    create_time     INTEGER DEFAULT (strftime('%s', 'now') * 1000),
    update_by       TEXT DEFAULT '',
    update_time     INTEGER DEFAULT (strftime('%s', 'now') * 1000),
    remark          TEXT DEFAULT '',
    del_flag        TEXT DEFAULT '0'
);

-- 索引设计
CREATE INDEX idx_law_unit ON t_law_record(unit_id);
CREATE INDEX idx_law_sync ON t_law_record(sync_status);
CREATE INDEX idx_law_time ON t_law_record(create_time);
```

### Room 实体类规范

```kotlin
@Entity(tableName = "t_law_record")
data class LawRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "unit_id")
    val unitId: Long,

    @ColumnInfo(name = "record_type")
    val recordType: String,

    @ColumnInfo(name = "file_path")
    val filePath: String?,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "PENDING",

    @ColumnInfo(name = "version")
    val version: Int = 1,

    @ColumnInfo(name = "create_time")
    val createTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "del_flag")
    val delFlag: String = "0"
)
```

### 数据迁移规范

```kotlin
// Room 数据库版本迁移
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 添加新列
        database.execSQL("ALTER TABLE t_law_record ADD COLUMN sync_status TEXT DEFAULT 'PENDING'")
        database.execSQL("ALTER TABLE t_law_record ADD COLUMN version INTEGER DEFAULT 1")

        // 创建新表
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS t_sync_queue (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                table_name TEXT NOT NULL,
                record_id INTEGER NOT NULL,
                sync_type TEXT NOT NULL,
                sync_status TEXT DEFAULT 'PENDING',
                retry_count INTEGER DEFAULT 0,
                request_data TEXT
            )
        """)

        // 创建索引
        database.execSQL("CREATE INDEX IF NOT EXISTS idx_sync_status ON t_sync_queue(sync_status)")
    }
}

// 数据库构建
val db = Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    "ruoyi_db"
)
    .addMigrations(MIGRATION_1_2)
    .build()
```

### 同步队列表设计

```sql
-- 同步队列：存储待同步的数据变更
CREATE TABLE t_sync_queue (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    table_name      TEXT NOT NULL,        -- 表名
    record_id       INTEGER NOT NULL,     -- 记录 ID
    sync_type       TEXT NOT NULL,        -- CREATE/UPDATE/DELETE/DOWNLOAD
    sync_status     TEXT DEFAULT 'PENDING',
    priority        INTEGER DEFAULT 0,    -- 优先级 (0 普通 1 紧急)
    retry_count     INTEGER DEFAULT 0,
    max_retry       INTEGER DEFAULT 3,
    next_retry_time INTEGER,
    request_data    TEXT,                 -- JSON 格式请求数据
    error_message   TEXT,
    create_time     INTEGER DEFAULT (strftime('%s', 'now') * 1000)
);

-- 索引
CREATE INDEX idx_sync_status ON t_sync_queue(sync_status);
CREATE INDEX idx_sync_retry ON t_sync_queue(next_retry_time);
```

### 本地缓存表设计

```sql
-- 用户信息缓存
CREATE TABLE t_user_cache (
    user_id         INTEGER PRIMARY KEY,
    username        TEXT NOT NULL,
    encrypted_pwd   TEXT,                 -- 加密密码（用于离线登录）
    avatar          TEXT,
    last_login_time INTEGER,
    sync_time       INTEGER DEFAULT (strftime('%s', 'now') * 1000)
);

-- 执法单位缓存
CREATE TABLE t_unit_cache (
    unit_id         INTEGER PRIMARY KEY,
    unit_name       TEXT NOT NULL,
    unit_code       TEXT,
    industry_type   TEXT,
    address         TEXT,
    legal_person    TEXT,
    sync_time       INTEGER DEFAULT (strftime('%s', 'now') * 1000),
    version         INTEGER DEFAULT 1
);

-- 法律法规缓存（只读，后台下发）
-- 支持 300+ 法律法规存储
CREATE TABLE t_law_book (
    book_id         INTEGER PRIMARY KEY,
    book_name       TEXT NOT NULL,
    law_code        TEXT,                 -- 发文文号
    law_type        TEXT,                 -- 法律类型：法律/法规/规章/规范性文件
    industry_code   TEXT,                 -- 适用行业分类编码
    publish_date    TEXT,                 -- 发布日期
    implement_date TEXT,                  -- 实施日期
    content         TEXT,                 -- 完整内容
    chapter_list    TEXT,                 -- 章节列表（JSON）
    sync_time       INTEGER DEFAULT (strftime('%s', 'now') * 1000),
    version         INTEGER DEFAULT 1
);

-- 法律法规章节表
CREATE TABLE t_law_chapter (
    chapter_id      INTEGER PRIMARY KEY,
    book_id         INTEGER NOT NULL,
    chapter_title   TEXT NOT NULL,
    chapter_order   INTEGER DEFAULT 0,
    chapter_content TEXT,
    parent_chapter_id INTEGER DEFAULT 0,   -- 父章节 ID（支持多级章节）
    FOREIGN KEY (book_id) REFERENCES t_law_book(book_id)
);

-- 法律法规定性依据表
CREATE TABLE t_law_basis (
    basis_id        INTEGER PRIMARY KEY,
    law_code        TEXT NOT NULL,
    clause_number   TEXT,                 -- 条款号（如：第 X 条）
    clause_content  TEXT NOT NULL,
    violation_keywords TEXT,              -- 违法关键词（JSON 数组）
    penalty_range   TEXT,                 -- 处罚幅度
    industry_code   TEXT,                 -- 适用行业
    UNIQUE(law_code, clause_number)
);

-- 地方性法规表
CREATE TABLE t_local_regulation (
    regulation_id   INTEGER PRIMARY KEY,
    regulation_name TEXT NOT NULL,
    region_code     TEXT NOT NULL,        -- 行政区划代码
    region_name     TEXT,                 -- 地区名称
    regulation_type TEXT,                 -- 地方法规/地方政府规章
    content         TEXT,
    publish_date    TEXT,
    implement_date  TEXT,
    is_active       TEXT DEFAULT '1',
    sync_time       INTEGER DEFAULT (strftime('%s', 'now') * 1000)
);

-- 索引设计
CREATE INDEX idx_law_book_type ON t_law_book(law_type);
CREATE INDEX idx_law_book_industry ON t_law_book(industry_code);
CREATE INDEX idx_law_chapter_book ON t_law_chapter(book_id);
CREATE INDEX idx_law_basis_code ON t_law_basis(law_code);
CREATE INDEX idx_law_basis_keywords ON t_law_basis(violation_keywords);
CREATE INDEX idx_local_regulation_region ON t_local_regulation(region_code);

-- 全文搜索索引（用于快速检索）
CREATE INDEX idx_law_book_name ON t_law_book(book_name);
CREATE INDEX idx_law_chapter_title ON t_law_chapter(chapter_title);
CREATE INDEX idx_local_regulation_name ON t_local_regulation(regulation_name);
```

### 法律法规分类体系

**300+ 法律法规分类结构**：

```
法律法规库（300+ 部）
├── 法律（50+ 部）
│   ├── 中华人民共和国基本医疗卫生与健康促进法
│   ├── 中华人民共和国传染病防治法
│   ├── 中华人民共和国执业医师法
│   ├── 中华人民共和国中医药法
│   ├── 中华人民共和国疫苗管理法
│   ├── 中华人民共和国药品管理法
│   └── ...
├── 行政法规（80+ 部）
│   ├── 医疗机构管理条例
│   ├── 公共场所卫生管理条例
│   ├── 生活饮用水卫生监督管理办法
│   ├── 传染病防治法实施办法
│   ├── 疫苗流通和预防接种管理条例
│   └── ...
├── 部门规章（100+ 部）
│   ├── 医疗机构管理条例实施细则
│   ├── 医师执业注册管理办法
│   ├── 中医诊所备案管理暂行办法
│   ├── 医疗质量管理办法
│   └── ...
├── 地方性法规（50+ 部）
│   ├── XX 省公共场所卫生管理办法
│   ├── XX 省生活饮用水卫生监督管理条例
│   └── ...
└── 规范性文件（20+ 部）
    ├── 国家卫生健康委员会公告
    ├── 卫生标准管理制度
    └── ...
```

**法律类型枚举**：
```kotlin
enum class LawType(val code: String, val label: String) {
    LAW("law", "法律"),
    REGULATION("regulation", "行政法规"),
    RULE("rule", "部门规章"),
    LOCAL_REGULATION("local_regulation", "地方性法规"),
    NORMATIVE_DOCUMENT("normative", "规范性文件"),
    STANDARD("standard", "卫生标准"),
    APPROVAL("approval", "批复文件")
}
```

**行业分类关联**：
```kotlin
enum class IndustryCode(val code: String, val label: String) {
    PUBLIC_PLACE("public_place", "公共场所"),
    DRINKING_WATER("drinking_water", "生活饮用水"),
    MEDICAL_INSTITUTION("medical", "医疗机构"),
    SCHOOL_HEALTH("school", "学校卫生"),
    OCCUPATIONAL_HEALTH("occupational", "职业卫生"),
    DISINFECT_PRODUCT("disinfect", "消毒产品"),
    CANTEN("canteen", "餐饮具集中消毒")
}
```
```

### 数据加密存储

```kotlin
// 使用 AndroidX Security 加密敏感数据
class EncryptedTypeConverter {
    private val masterKey by lazy {
        MasterKey.Builder(Application.context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs by lazy {
        EncryptedSharedPreferences.create(
            Application.context,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @TypeConverter
    fun encrypt(value: String): String {
        val editor = encryptedPrefs.edit()
        editor.putString("encrypted_value", value)
        editor.apply()
        return encryptedPrefs.getString("encrypted_value", "") ?: ""
    }

    @TypeConverter
    fun decrypt(encryptedValue: String): String {
        return encryptedPrefs.getString("encrypted_value", "") ?: ""
    }
}
```

### SQLite 性能优化

```sql
-- 1. 使用 WITHOUT ROWID 优化只查询主键的表
CREATE TABLE t_dict_type (
    dict_id       INTEGER PRIMARY KEY,
    dict_name     TEXT NOT NULL,
    dict_type     TEXT NOT NULL UNIQUE
) WITHOUT ROWID;

-- 2. 使用覆盖索引避免回表
CREATE INDEX idx_sync_covering ON t_sync_queue(sync_status, sync_type, record_id);

-- 3. 批量插入使用事务
BEGIN TRANSACTION;
-- 批量 INSERT 语句
COMMIT;

-- 4. 使用 ANALYZE 更新统计信息
ANALYZE;
```

### 数据库升级检查

```kotlin
// 检查数据库版本
val db = dbHelper.readableDatabase
val version = db.version

if (version < LATEST_VERSION) {
    // 提示用户需要升级
    showUpgradeDialog()
}

// 强制版本检查
fun checkSchemaVersion(db: SQLiteDatabase): Boolean {
    val cursor = db.rawQuery(
        "SELECT sql FROM sqlite_master WHERE type='table' AND name='t_law_record'",
        null
    )
    return cursor.use { it.moveToFirst() }
}
```
