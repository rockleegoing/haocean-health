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

## 若依代码生成器使用

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
