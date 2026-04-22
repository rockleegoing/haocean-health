# 设备与认证模块设计文档

**创建日期**: 2026-04-22
**状态**: 待审核
**模块**: 设备与认证（模块 1）

---

## 一、背景与目标

### 1.1 问题描述

当前系统需要支持执法人员在移动设备上离线执法，但现有架构依赖在线验证，存在以下问题：

1. **登录依赖网络**：无网络环境下无法登录
2. **数据同步机制缺失**：离线期间的数据变更无法自动同步
3. **设备管理缺失**：无法追踪设备激活状态和使用情况
4. **激活码管理缺失**：缺少设备激活的控制机制

### 1.2 设计目标

1. **离线优先**：登录验证完全离线，无需网络
2. **自动同步**：后台静默检查并同步数据变更
3. **设备管控**：通过激活码机制控制设备访问权限
4. **数据一致性**：本地 SQLite 与后台 MySQL 结构一致，便于排查问题

### 1.3 成功标准

- [ ] 无网络环境下可正常登录
- [ ] 登录成功后自动同步数据，显示明确进度条
- [ ] 激活码机制正常工作，支持批量生成
- [ ] 后台可查看设备激活状态和当前登录人

---

## 二、架构设计

### 2.1 整体流程

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           App 启动流程                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐              │
│  │ 1. 检查激活  │ →  │ 2. 未激活？  │ →  │ 3. 激活页面  │              │
│  │    状态      │    │ 是→跳转激活  │    │ 输入激活码   │              │
│  └──────────────┘    └──────────────┘    └──────────────┘              │
│                                        │                                 │
│                                        │ 激活成功                        │
│                                        ▼                                 │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐              │
│  │ 6. 完成登录  │ ←  │ 5. 数据同步  │ ←  │ 4. 登录页面  │              │
│  │    进入首页  │    │ 显示进度条   │    │ 本地验证     │              │
│  └──────────────┘    └──────────────┘    └──────────────┘              │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.2 数据同步架构

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         数据同步策略                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  同步阶段一：登录页面打开时                                              │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ • 检查网络连接                                                   │   │
│  │ • 有网 → 调用 API 同步用户登录数据到 SQLite                        │   │
│  │ • 无网 → 使用本地已有数据                                        │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  同步阶段二：登录成功后（阻塞式，显示进度条）                            │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ P0 必须数据（同步完成才进入首页）                                 │   │
│  │ • 用户权限信息（sys_user_role）                                  │   │
│  │ • 所属单位信息（sys_dept）                                       │   │
│  │ • 行业分类数据（industry_category）                              │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  同步阶段三：后台静默同步（非阻塞）                                      │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ P1 重要数据（后台继续同步）                                       │   │
│  │ • 法律法规库（law_* 表）                                         │   │
│  │ • 规范用语库（phrase_* 表）                                      │   │
│  │ • 监管事项库（supervision_* 表）                                 │   │
│  │ • 文书模板（document_template）                                  │   │
│  │                                                                  │   │
│  │ 后台服务：每 30 分钟检查变更，自动拉取                               │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.3 本地数据库表结构（SQLite）

```sql
-- 用户表（与后台 sys_user 一致）
CREATE TABLE sys_user (
    user_id INTEGER PRIMARY KEY,
    dept_id INTEGER,
    user_name TEXT NOT NULL,
    nick_name TEXT,
    password TEXT NOT NULL,  -- bcrypt 哈希
    salt TEXT,
    email TEXT,
    phonenumber TEXT,
    sex TEXT,
    avatar TEXT,
    status TEXT DEFAULT '0',
    del_flag TEXT DEFAULT '0',
    login_ip TEXT,
    login_date TEXT,
    create_by TEXT,
    create_time TEXT,
    update_by TEXT,
    update_time TEXT,
    remark TEXT
);

-- 角色表（与后台 sys_role 一致）
CREATE TABLE sys_role (
    role_id INTEGER PRIMARY KEY,
    role_name TEXT NOT NULL,
    role_key TEXT NOT NULL,
    role_sort INTEGER,
    data_scope TEXT DEFAULT '1',
    status TEXT DEFAULT '0',
    del_flag TEXT DEFAULT '0',
    create_by TEXT,
    create_time TEXT,
    update_by TEXT,
    update_time TEXT,
    remark TEXT
);

-- 用户角色关联表
CREATE TABLE sys_user_role (
    user_id INTEGER NOT NULL,
    role_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

-- 单位表
CREATE TABLE sys_dept (
    dept_id INTEGER PRIMARY KEY,
    parent_id INTEGER DEFAULT 0,
    ancestors TEXT,
    dept_name TEXT NOT NULL,
    order_num INTEGER,
    leader TEXT,
    phone TEXT,
    email TEXT,
    status TEXT DEFAULT '0',
    del_flag TEXT DEFAULT '0',
    create_by TEXT,
    create_time TEXT,
    update_by TEXT,
    update_time TEXT
);

-- 行业分类表
CREATE TABLE industry_category (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    parent_id INTEGER DEFAULT 0,
    category_name TEXT NOT NULL,
    category_code TEXT NOT NULL,
    order_num INTEGER DEFAULT 0,
    status TEXT DEFAULT '0',
    create_time TEXT,
    update_time TEXT
);

-- 激活码表（本地缓存）
CREATE TABLE sys_activation_code (
    code_id INTEGER PRIMARY KEY AUTOINCREMENT,
    code_value TEXT NOT NULL UNIQUE,
    status TEXT DEFAULT '0',  -- 0 未使用 1 已使用 2 已过期 3 已禁用
    expire_time TEXT,
    bind_device_id TEXT,
    bind_user_id INTEGER,
    create_time TEXT,
    update_time TEXT
);

-- 设备表
CREATE TABLE sys_device (
    device_id INTEGER PRIMARY KEY AUTOINCREMENT,
    device_uuid TEXT NOT NULL UNIQUE,
    device_name TEXT,
    device_model TEXT,
    current_user_id INTEGER,  -- 实时显示当前登录人
    activation_code_id INTEGER,
    last_sync_time TEXT,
    status TEXT DEFAULT '0',  -- 0 离线 1 在线
    create_time TEXT,
    update_time TEXT
);

-- 同步队列（记录待同步的本地变更）
CREATE TABLE sync_queue (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    table_name TEXT NOT NULL,
    record_id INTEGER NOT NULL,
    operation TEXT NOT NULL,  -- INSERT/UPDATE/DELETE
    data_json TEXT,
    sync_status TEXT DEFAULT '0',  -- 0 待同步 1 已同步 2 失败
    create_time TEXT,
    sync_time TEXT
);

-- 数据版本表（用于增量同步）
CREATE TABLE data_version (
    table_name TEXT PRIMARY KEY,
    last_sync_time TEXT,
    server_version INTEGER DEFAULT 0,
    local_version INTEGER DEFAULT 0
);
```

---

## 三、后端设计（RuoYi-Vue）

### 3.1 数据库表设计（MySQL）

```sql
-- 激活码表
CREATE TABLE sys_activation_code (
    code_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    code_value VARCHAR(20) NOT NULL COMMENT '激活码值',
    status CHAR(1) DEFAULT '0' COMMENT '状态（0 未使用 1 已使用 2 已过期 3 已禁用）',
    expire_time DATETIME COMMENT '有效期',
    bind_device_id VARCHAR(64) COMMENT '绑定设备 ID',
    bind_user_id BIGINT COMMENT '绑定用户 ID',
    remark VARCHAR(500) COMMENT '备注',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code_value (code_value),
    KEY idx_status (status),
    KEY idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='激活码表';

-- 设备表
CREATE TABLE sys_device (
    device_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    device_uuid VARCHAR(64) NOT NULL COMMENT '设备唯一标识',
    device_name VARCHAR(100) COMMENT '设备名称',
    device_model VARCHAR(100) COMMENT '设备型号',
    device_os VARCHAR(50) COMMENT '操作系统',
    app_version VARCHAR(20) COMMENT 'App 版本',
    current_user_id BIGINT COMMENT '当前登录用户 ID',
    current_user_name VARCHAR(64) COMMENT '当前登录用户名（冗余字段，便于查询）',
    activation_code_id BIGINT COMMENT '激活码 ID',
    last_sync_time DATETIME COMMENT '最后同步时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(64) COMMENT '最后登录 IP',
    status CHAR(1) DEFAULT '0' COMMENT '状态（0 离线 1 在线）',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_device_uuid (device_uuid),
    KEY idx_current_user (current_user_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备表';
```

### 3.2 模块结构

```
RuoYi-Vue/
├── ruoyi-admin/
│   └── src/main/java/com/ruoyi/
│       └── web/controller/device/
│           ├── SysActivationCodeController.java  # 激活码管理接口
│           └── SysDeviceController.java          # 设备管理接口
├── ruoyi-system/
│   └── src/main/java/com/ruoyi/system/
│       ├── domain/
│       │   ├── SysActivationCode.java
│       │   └── SysDevice.java
│       ├── mapper/
│       │   ├── SysActivationCodeMapper.java
│       │   └── SysDeviceMapper.java
│       └── service/
│           ├── ISysActivationCodeService.java
│           ├── ISysDeviceService.java
│           └── impl/
│               ├── SysActivationCodeServiceImpl.java
│               └── SysDeviceServiceImpl.java
└── ruoyi-ui/
    └── src/views/device/
        ├── activation-code/
        │   ├── index.vue        # 激活码列表
        │   ├── CreateDialog.vue # 新建/批量生成对话框
        │   └── DetailDialog.vue # 详情/复制对话框
        └── device/
            └── index.vue        # 设备列表
```

### 3.3 API 接口设计

#### 3.3.1 激活码管理接口

```java
// POST /device/activationCode/list
// 分页查询激活码列表
Request: { pageNum, pageNum, codeValue, status, expireTimeFrom, expireTimeTo }
Response: { total, rows: [{ codeId, codeValue, status, expireTime, bindDeviceId, bindUserId, currentUser, createTime }] }

// POST /device/activationCode
// 新增激活码（支持批量）
Request: { count, expireDays, remark }  // count 1-50
Response: { codes: [{ codeValue, expireTime }] }

// PUT /device/activationCode
// 修改激活码
Request: { codeId, status, expireTime, remark }

// DELETE /device/activationCode/{codeIds}
// 删除激活码
Request: /device/activationCode/1,2,3

// GET /device/activationCode/{codeId}
// 获取激活码详情
Response: { codeId, codeValue, status, expireTime, bindDeviceId, bindUserId, currentUser, createTime, updateTime, remark }

// PUT /device/activationCode/batchGenerate
// 批量生成激活码
Request: { count, expireDays, remark }
Response: { codes: [{ codeValue, expireTime }] }

// POST /device/activationCode/validate
// 验证激活码（App 调用）
Request: { codeValue, deviceUuid, deviceName, deviceModel }
Response: { valid, message, userInfo }
```

#### 3.3.2 设备管理接口

```java
// POST /device/device/list
// 分页查询设备列表
Request: { pageNum, pageNum, deviceUuid, status, currentUserId }
Response: { total, rows: [{ deviceId, deviceUuid, deviceName, deviceModel, currentUserId, currentUser, lastSyncTime, status, createTime }] }

// GET /device/device/{deviceId}
// 获取设备详情
Response: { deviceId, deviceUuid, deviceName, deviceModel, deviceOs, appVersion, currentUserId, currentUser, activationCodeId, lastSyncTime, lastLoginTime, lastLoginIp, status, createTime }

// PUT /device/device/unbind
// 设备解绑
Request: { deviceId }

// POST /device/device/clearData
// 远程清除设备数据
Request: { deviceId, reason }
```

#### 3.3.3 数据同步接口

```java
// POST /common/sync/userData
// 同步用户数据（登录页面打开时调用）
Request: { userId }
Response: {
    users: [...],
    roles: [...],
    userRoles: [...]
}

// POST /common/sync/full
// 全量同步数据（首次登录）
Request: { userId, lastUpdateTime }
Response: {
    depts: [...],
    industryCategories: [...],
    laws: [...],
    phrases: [...],
    supervisions: [...],
    templates: [...]
}

// POST /common/sync/incremental
// 增量同步数据（后续登录）
Request: { userId, tableVersions: [{ tableName, lastSyncTime, version }] }
Response: {
    changes: [{ tableName, data: [...], hasMore: false }],
    serverVersions: [{ tableName, version }]
}

// POST /common/sync/upload
// 上传本地变更（离线期间产生的数据）
Request: { userId, syncQueue: [{ tableName, recordId, operation, dataJson }] }
Response: { success, failed: [...] }
```

### 3.4 激活码生成规则

```java
/**
 * 生成激活码
 * 格式：8 位大写字母 + 数字组合，如 ABCD1234
 */
public String generateActivationCode() {
    String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";  // 排除易混淆字符（I/O,0/1）
    StringBuilder code = new StringBuilder(8);
    SecureRandom random = new SecureRandom();
    for (int i = 0; i < 8; i++) {
        code.append(chars.charAt(random.nextInt(chars.length())));
    }
    return code.toString();
}
```

---

## 四、前端设计（Android）

### 4.1 模块结构

```
Ruoyi-Android-App/
├── app/src/main/java/com/ruoyi/app/
│   ├── activity/
│   │   ├── LoginActivity.kt          # 登录页面（改造）
│   │   └── ActivationActivity.kt     # 激活页面（新增）
│   ├── fragment/
│   │   └── SyncProgressFragment.kt   # 同步进度条对话框（新增）
│   ├── data/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt        # Room 数据库配置
│   │   │   ├── dao/
│   │   │   │   ├── UserDao.kt
│   │   │   │   ├── RoleDao.kt
│   │   │   │   ├── DeptDao.kt
│   │   │   │   ├── IndustryCategoryDao.kt
│   │   │   │   ├── ActivationCodeDao.kt
│   │   │   │   ├── DeviceDao.kt
│   │   │   │   └── SyncQueueDao.kt
│   │   │   └── entity/
│   │   │       ├── UserEntity.kt
│   │   │       ├── RoleEntity.kt
│   │   │       ├── DeptEntity.kt
│   │   │       ├── IndustryCategoryEntity.kt
│   │   │       ├── ActivationCodeEntity.kt
│   │   │       ├── DeviceEntity.kt
│   │   │       └── SyncQueueEntity.kt
│   │   ├── repository/
│   │   │   ├── UserRepository.kt
│   │   │   ├── SyncRepository.kt
│   │   │   └── ActivationRepository.kt
│   │   └── sync/
│   │       ├── SyncService.kt        # 后台同步服务
│   │       ├── SyncManager.kt        # 同步管理器
│   │       └── SyncProgress.kt       # 同步进度模型
│   ├── api/
│   │   └── service/
│   │       ├── ActivationService.kt  # 激活码 API
│   │       └── SyncService.kt        # 同步 API
│   └── utils/
│       ├── DeviceUtils.kt            # 设备信息工具
│       └── PasswordUtils.kt          # 密码 bcrypt 工具
```

### 4.2 关键流程实现

#### 4.2.1 登录流程（LoginActivity.kt）

```kotlin
class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 检查激活状态
        if (!ActivationManager.isActivated(this)) {
            ActivationActivity.start(this)
            finish()
            return
        }

        // 2. 同步用户登录数据（有网时）
        if (NetworkUtils.isAvailable(this)) {
            lifecycleScope.launch {
                val userData = syncUserData()
                userData?.let {
                    database.userDao().insertAll(it.users)
                    database.roleDao().insertAll(it.roles)
                }
            }
        }
    }

    private fun login(username: String, password: String) {
        lifecycleScope.launch {
            // 3. 本地 bcrypt 验证
            val user = database.userDao().findByUsername(username)
            if (user != null && PasswordUtils.matches(password, user.password)) {
                // 4. 登录成功，显示同步进度条
                SyncProgressFragment.show(syncAllData(user.userId))
            } else {
                showToast("用户名或密码错误")
            }
        }
    }
}
```

#### 4.2.2 同步进度条（SyncProgressFragment.kt）

```kotlin
class SyncProgressFragment : DialogFragment() {

    // 同步阶段
    enum class SyncStage(val name: String, val weight: Int) {
        P0_USER("用户数据", 10),
        P0_DEPT("单位信息", 15),
        P0_INDUSTRY("行业分类", 15),
        P1_LAW("法律法规库", 20),
        P1_PHRASE("规范用语库", 15),
        P1_SUPERVISION("监管事项库", 15),
        P1_TEMPLATE("文书模板", 10)
    }

    // 进度更新
    private fun updateProgress(stage: SyncStage, progress: Int) {
        binding.stageName.text = stage.name
        binding.progressBar.progress = progress
        binding.progressText.text = "$progress%"
    }

    // 支持跳过
    private fun showSkipButton() {
        binding.btnSkip.setOnClickListener {
            dismiss()
            MainActivity.start(requireContext())
        }
    }
}
```

#### 4.2.3 后台同步服务（SyncService.kt）

```kotlin
class SyncService : Service() {

    // 每 30 分钟检查一次
    private val syncInterval = 30 * 60 * 1000L

    private val syncJob = object : JobIntentService() {
        override fun onHandleWork(intent: Intent) {
            checkAndSync()
        }
    }

    private fun checkAndSync() {
        // 1. 获取本地数据版本
        val versions = database.dataVersionDao().getAll()

        // 2. 请求服务器检查变更
        val changes = api.checkChanges(versions)

        // 3. 有变更则同步
        if (changes.isNotEmpty()) {
            SyncManager.syncIncremental(changes)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 启动定时任务
        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                delay(syncInterval)
                checkAndSync()
            }
        }
        return START_STICKY
    }
}
```

### 4.3 数据库 Room 配置

```kotlin
@Database(
    entities = [
        UserEntity::class,
        RoleEntity::class,
        DeptEntity::class,
        IndustryCategoryEntity::class,
        ActivationCodeEntity::class,
        DeviceEntity::class,
        SyncQueueEntity::class,
        DataVersionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun roleDao(): RoleDao
    abstract fun deptDao(): DeptDao
    abstract fun industryCategoryDao(): IndustryCategoryDao
    abstract fun activationCodeDao(): ActivationCodeDao
    abstract fun deviceDao(): DeviceDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun dataVersionDao(): DataVersionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ruoyi.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
```

---

## 五、前端设计（Vue - 激活码管理页面）

### 5.1 页面布局

```vue
<!-- activation-code/index.vue -->
<template>
  <div class="app-container">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="激活码">
          <el-input v-model="queryParams.codeValue" placeholder="请输入激活码" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="未使用" value="0" />
            <el-option label="已使用" value="1" />
            <el-option label="已过期" value="2" />
            <el-option label="已禁用" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button type="primary" @click="handleAdd">生成激活码</el-button>
        </el-form-item>
      </el-form>

      <!-- 列表 -->
      <el-table :data="codeList">
        <el-table-column label="激活码" prop="codeValue" width="120">
          <template #default="{ row }">
            <el-tag size="small" copyable>{{ row.codeValue }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="绑定用户" prop="currentUser" width="150" />
        <el-table-column label="有效期" width="200">
          <template #default="{ row }">
            <countdown :time="row.expireTime" />
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" />
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link @click="handleCopy(row)">复制</el-button>
            <el-button link @click="handleDetail(row)">详情</el-button>
            <el-button link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-model:total="total" @pagination="getList" />
    </el-card>

    <!-- 生成激活码对话框 -->
    <CreateDialog v-model="visible" @success="getList" />
  </div>
</template>
```

### 5.2 生成激活码对话框

```vue
<!-- CreateDialog.vue -->
<template>
  <el-dialog v-model="visible" title="生成激活码" width="500px">
    <el-form :model="form" label-width="80px">
      <el-form-item label="数量" required>
        <el-input-number v-model="form.count" :min="1" :max="50" />
        <span class="form-tip">单次最多生成 50 个</span>
      </el-form-item>
      <el-form-item label="有效期" required>
        <el-input-number v-model="form.expireDays" :min="1" :max="365" />
        <span class="form-tip">单位：天（默认 30 天）</span>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" placeholder="选填" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">生成</el-button>
    </template>
  </el-dialog>

  <!-- 生成结果对话框 -->
  <el-dialog v-model="resultVisible" title="生成的激活码" width="500px">
    <el-table :data="generatedCodes" size="small">
      <el-table-column label="激活码" prop="codeValue" />
      <el-table-column label="有效期" prop="expireTime" />
      <el-table-column label="操作" width="80">
        <template #default="{ row }">
          <el-button size="small" @click="copyCode(row.codeValue)">复制</el-button>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <el-button @click="copyAll">全部复制</el-button>
      <el-button type="primary" @click="resultVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>
```

---

## 六、测试策略

### 6.1 单元测试

| 测试项 | 测试内容 |
|--------|---------|
| 激活码生成 | 生成格式正确、唯一性、批量生成 |
| 激活码验证 | 未使用/已使用/已过期/已禁用状态验证 |
| bcrypt 密码验证 | 密码匹配、不匹配、大小写敏感 |
| 数据版本对比 | 增量同步逻辑、版本号递增 |

### 6.2 集成测试

| 测试项 | 测试内容 |
|--------|---------|
| 登录流程 | 激活→登录→同步完整流程 |
| 离线登录 | 无网络环境下登录验证 |
| 同步中断恢复 | 网络中断后重试、跳过 |
| 后台静默同步 | 定时检查、自动同步 |

### 6.3 端到端测试

```bash
# 1. 激活码生成测试
curl -X POST http://localhost:8080/device/activationCode \
  -H "Content-Type: application/json" \
  -d '{"count": 5, "expireDays": 30}'

# 2. 激活码验证测试
curl -X POST http://localhost:8080/device/activationCode/validate \
  -H "Content-Type: application/json" \
  -d '{"codeValue": "ABCD1234", "deviceUuid": "test-uuid-123"}'

# 3. 数据同步测试
curl -X POST http://localhost:8080/common/sync/userData \
  -H "Content-Type: application/json" \
  -d '{"userId": 1}'
```

---

## 七、关键文件路径

### 后端（RuoYi-Vue）
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/device/SysActivationCodeController.java`
- `ruoyi-admin/src/main/java/com/ruoyi/web/controller/device/SysDeviceController.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/SysActivationCode.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDevice.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysActivationCodeMapper.java`
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDeviceMapper.java`
- `ruoyi-ui/src/views/device/activation-code/index.vue`
- `ruoyi-ui/src/views/device/device/index.vue`

### Android（Ruoyi-Android-App）
- `app/src/main/java/com/ruoyi/app/activity/LoginActivity.kt`（改造）
- `app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt`（新增）
- `app/src/main/java/com/ruoyi/app/fragment/SyncProgressFragment.kt`（新增）
- `app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt`（新增）
- `app/src/main/java/com/ruoyi/app/data/sync/SyncService.kt`（新增）
- `app/src/main/java/com/ruoyi/app/api/service/ActivationService.kt`（新增）
- `app/src/main/java/com/ruoyi/app/api/service/SyncService.kt`（新增）

---

## 八、实施计划

### 阶段一：后端基础（预计 2-3 天）
1. 创建数据库表（sys_activation_code、sys_device）
2. 实现 Domain、Mapper、Service
3. 实现 Controller 接口

### 阶段二：后端管理页面（预计 2 天）
1. Vue 页面开发（激活码列表、设备列表）
2. 生成/复制/详情对话框
3. 有效期倒计时组件

### 阶段三：Android 激活功能（预计 2 天）
1. 激活页面 UI
2. 激活码验证 API 调用
3. 设备 ID 生成与存储

### 阶段四：Android 登录改造（预计 3 天）
1. SQLite 数据库集成
2. 本地 bcrypt 验证
3. 同步进度条 UI

### 阶段五：数据同步功能（预计 3-4 天）
1. 全量/增量同步逻辑
2. 后台同步服务
3. 同步队列管理

### 阶段六：模拟数据（预计 1 天）
1. 编写初始化 SQL 脚本（sys_activation_code 默认激活码）
2. 编写初始化 SQL 脚本（sys_device 空表结构）
3. 生成默认模拟数据（用于开发测试）
   - 默认激活码：TEST0001 ~ TEST0010（有效期 365 天）
   - 测试设备数据（可选）

### 阶段七：SQL 脚本导出（预计 0.5 天）
1. 创建数据库初始化脚本目录 `RuoYi-Vue/sql/`
2. 导出表结构到 `device_auth_init.sql`
3. 导出默认数据到 `device_auth_data.sql`
4. 更新 README 部署文档，说明执行顺序

### 阶段八：测试与优化（预计 2 天）
1. 单元测试
2. 集成测试
3. 离线场景测试
4. SQL 脚本验证（全新环境部署测试）

---

## 九、风险与依赖

### 技术风险
| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| bcrypt 库兼容性 | 前后端哈希结果不一致 | 使用相同 salt 长度和 rounds 参数 |
| SQLite 性能 | 大量数据同步慢 | 使用事务批量插入 |
| 后台同步耗电 | 影响 App 续航 | 使用 WorkManager 优化调度 |
| 数据冲突 | 离线期间数据冲突 | 设计冲突解决策略（最后更新优先） |

### 外部依赖
- 无第三方依赖，全部使用项目现有技术栈

---

## 十、验收标准

- [ ] 后端激活码管理页面可正常使用（生成、复制、查看详情）
- [ ] Android 端可输入激活码完成设备激活
- [ ] 登录页面打开时自动同步用户数据
- [ ] 离线环境下可正常登录（bcrypt 验证）
- [ ] 登录成功后显示同步进度条，支持跳过
- [ ] 首次登录全量同步、后续登录增量同步
- [ ] 后台静默同步服务正常工作
- [ ] 所有数据库表结构与 MySQL 一致
- [ ] 默认模拟数据已插入（10 个测试激活码）
- [ ] SQL 初始化脚本已导出到 `RuoYi-Vue/sql/device_auth_init.sql`
- [ ] SQL 数据脚本已导出到 `RuoYi-Vue/sql/device_auth_data.sql`
- [ ] 全新环境可通过 SQL 脚本完成初始化部署
