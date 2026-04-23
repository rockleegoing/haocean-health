# 数据同步策略与登录改造设计文档

**作者**：Claude Code
**日期**：2026-04-24
**版本**：v1.0
**状态**：草稿

---

## 一、目标

为 Ruoyi-Android-App 构建完整的离线优先数据同步体系，确保：

- 用户在无网络环境下仍能使用 App 核心功能
- 登录后全量同步所有必要数据，用户必须等待完成才能使用 App
- 进入 App 后后台静默增量更新，用户无感知
- 覆盖安装时重置设备激活状态，重新全量同步

---

## 二、整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                    阶段一：LoginActivity                       │
│                                                             │
│  进入登录页时：                                              │
│  ├── 检查设备激活状态                                         │
│  │   └── 未激活 → 跳转到激活页面                              │
│  ├── 检查覆盖安装标志                                         │
│  │   └── 覆盖安装 → 清空本地数据 + 重激活设备                  │
│  ├── 初始化 Room DB                                          │
│  ├── 调用 /getInfo 拉取登录验证数据                           │
│  │   └── 成功 → 存入本地                                      │
│  │   └── 失败 → 不阻塞，继续登录表单（本地上次数据兜底）       │
│  └── 展示登录表单                                            │
└─────────────────────────────────────────────────────────────┘
                          ↓ 登录成功
┌─────────────────────────────────────────────────────────────┐
│              阶段二：SyncWaitActivity（阻塞式进度条）           │
│                                                             │
│  同步内容（全量）：                                           │
│  └── 用户权限（sys_user_role）                               │
│                                                             │
│  预留同步方法（待后端开发后逐步实现）：                         │
│  ├── 行业分类（industry_category）                           │
│  ├── 法律法规库（law_* 表）                                   │
│  ├── 规范用语库（phrase_* 表）                               │
│  ├── 监管事项库（supervision_* 表）                          │
│  ├── 文书模板（document_template）                           │
│  └── 媒体文件（media_file）                                  │
│                                                             │
│  网络策略：只验证有网                                         │
│  失败处理：重试 3 次（指数退避）→ 失败 → 提示后回到登录页      │
│  进度条：百分比 + 当前模块 + 已下载/总大小 + 预估剩余时间      │
└─────────────────────────────────────────────────────────────┘
                          ↓ 全部完成
┌─────────────────────────────────────────────────────────────┐
│         阶段三：WorkManager（后台增量同步，30分钟轮询）         │
│                                                             │
│  增量同步（lastModified 字段，预留给后端实现）：               │
│  └── 预留模块（待后端开发后逐步实现）：                         │
│      ├── 行业分类                                             │
│      ├── 法律法规库                                            │
│      ├── 规范用语库                                            │
│      ├── 监管事项库                                            │
│      ├── 文书模板                                              │
│      └── 媒体文件                                              │
│                                                             │
│  暂不纳入（功能未完成）：                                     │
│  ├── 单位信息（执法对象）                                    │
│  └── sys_dept 部门表                                         │
│                                                             │
│  失败 → Notification 提示，点击重试                          │
│  完成 → Notification 提示（无需跳转）                        │
└─────────────────────────────────────────────────────────────┘
```

---

## 三、数据表结构

### 3.1 已有表（验证通过，使用现有实体）

| 表名 | 实体类 | 同步阶段 | 说明 |
|------|--------|----------|------|
| `sys_user` | `UserEntity` | 阶段一 | 用户表 |
| `sys_role` | `RoleEntity` | 阶段二 | 角色表 |
| `sys_industry_category` | `IndustryCategoryEntity` | 阶段二 | 行业分类表 |
| `sys_activation_code` | `ActivationCodeEntity` | 阶段一 | 激活码表 |
| `sys_device` | `DeviceEntity` | 阶段一 | 设备表 |
| `sys_data_version` | `DataVersionEntity` | 阶段二/三 | 数据版本表 |
| `sys_sync_queue` | `SyncQueueEntity` | 阶段三 | 同步队列表（暂不使用） |

### 3.2 新增表（阶段二/三）

| 表名 | 实体类 | 同步阶段 | 说明 |
|------|--------|----------|------|
| `law_category` | `LawCategoryEntity` | 阶段二/三 | 法规分类表 |
| `law_regulation` | `LawRegulationEntity` | 阶段二/三 | 法规正文表 |
| `phrase_category` | `PhraseCategoryEntity` | 阶段二/三 | 用语分类表 |
| `phrase_library` | `PhraseLibraryEntity` | 阶段二/三 | 用语正文表 |
| `supervision_category` | `SupervisionCategoryEntity` | 阶段二/三 | 监管分类表 |
| `supervision_item` | `SupervisionItemEntity` | 阶段二/三 | 监管事项表 |
| `document_template` | `DocumentTemplateEntity` | 阶段二/三 | 文书模板表 |
| `media_file` | `MediaFileEntity` | 阶段二/三 | 媒体文件表 |

### 3.3 预留表（阶段三，待后端开发）

| 表名 | 实体类 | 说明 |
|------|--------|------|
| `law_*` | `LawEntity` | 预留扩展 |
| `phrase_*` | `PhraseEntity` | 预留扩展 |
| `supervision_*` | `SupervisionEntity` | 预留扩展 |

---

## 四、阶段一：登录页数据预加载

### 4.1 流程

```
LoginActivity.onCreate()
├── 1. 检查设备激活状态
│   └── 未激活 → TheRouter.build(Constant.activationRoute).navigation(); finish();
├── 2. 检查覆盖安装标志
│   └── 覆盖安装 → 清空本地数据 + 重置激活状态 + 跳转激活页
├── 3. 初始化 Room DB（快速，后台执行）
├── 4. 调用 /getInfo 拉取登录验证数据
│   ├── 成功 → 存入本地 SQLite（后台异步）
│   └── 失败 → 不阻塞，继续展示登录表单（本地上次数据兜底）
└── 5. 展示登录表单
```

### 4.2 覆盖安装检测

- 在 Application 中记录首次安装版本号（SharedPreferences）
- 如果当前版本号 > 记录版本号，则判定为覆盖安装
- 覆盖安装时执行：`Room.databaseBuilder.clearAllTables()` + 激活状态重置

### 4.3 网络处理

- 调用 /getInfo 时使用 `scopeNetLife`（协程作用域）
- 网络异常不阻塞，登录表单继续展示
- 失败时提示用户"数据同步失败，请检查网络"

---

## 五、阶段二：全量阻塞式同步

### 5.1 流程

```
登录成功 → SyncWaitActivity
├── 启动 Foreground Service（SyncService）确保后台存活
├── 显示全屏进度页面
│   ├── 总体进度条（百分比）
│   ├── 当前同步模块名称
│   ├── 已下载大小 / 总大小
│   └── 预估剩余时间
├── 按序同步各模块数据
│   ├── 模块 1：用户权限（已有逻辑，扩展存储到本地）
│   └── 预留模块（待后端开发后逐步实现）：
│       ├── 模块 2：行业分类
│       ├── 模块 3：法律法规库
│       ├── 模块 4：规范用语库
│       ├── 模块 5：监管事项库
│       ├── 模块 6：文书模板
│       └── 模块 7：媒体文件
├── 同步完成后 → MainActivity
└── 停止 Foreground Service
```

### 5.2 同步模块详细说明

| 模块 | 数据来源 | 单文件限制 | 说明 |
|------|----------|------------|------|
| 用户权限 | /getInfo | 无 | 已有逻辑，扩展存储到本地 |
| 行业分类 | 后端 API | - | **预留，待后端开发** |
| 法律法规库 | 后端 API | 20MB | **预留，待后端开发** |
| 规范用语库 | 后端 API | 20MB | **预留，待后端开发** |
| 监管事项库 | 后端 API | 20MB | **预留，待后端开发** |
| 文书模板 | 后端 API | 20MB | **预留，待后端开发** |
| 媒体文件 | 后端 API | 20MB | **预留，待后端开发** |

### 5.3 网络策略

- 只验证有网络连接（`ConnectivityManager` 检查 `TYPE_MOBILE` 或 `TYPE_WIFI`）
- 不限制 Wi-Fi，不检查充电状态
- 设备为 PAD，无移动网络限制

### 5.4 失败处理

```
同步失败 → 重试 3 次（指数退避：1s → 4s → 16s）
         ↓ 3 次仍失败
    AlertDialog 提示："数据同步失败，请检查网络后重试"
    点击确定 → 回到 LoginActivity
```

### 5.5 进度计算

- 总大小 = 各模块数据量预估之和（阶段二时无法精确获取，先用模块数估算）
- 每个模块同步完成后更新进度百分比
- 媒体文件下载时实时更新已下载大小

---

## 六、阶段三：后台增量同步

### 6.1 流程

```
MainActivity 启动
├── 调度 WorkManager PeriodicWorkRequest（30 分钟间隔）
│   ├── 检查网络状态（有网才执行）
│   ├── 调用各模块增量接口（lastModified）
│   │   ├── 有变更 → Foreground Service 后台下载
│   │   └── 无变更 → 空跑不下载
│   └── 更新 DataVersionEntity.lastSyncTime
└── 用户正常使用 App（P0 数据已满足基础功能）
```

### 6.2 WorkManager 配置

```kotlin
val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(30, TimeUnit.MINUTES)
    .setConstraints(Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build())
    .build()
```

### 6.3 增量同步接口（预留，后端实现后补充）

各模块需支持：

```
GET /{module}/list?lastModified={timestamp}
Response: {
    data: [...],
    lastModified: timestamp
}
```

### 6.4 Notification 设计

| 场景 | Notification 内容 | 点击行为 |
|------|-------------------|----------|
| 同步中 | "正在同步数据... X%" | 无（静默） |
| 同步完成 | "数据同步完成" | 无跳转，仅提示 |
| 同步失败 | "数据同步失败，点击重试" | 重试（触发 WorkManager 立即执行） |

### 6.5 失败处理

- 重试 3 次（指数退避）
- 3 次仍失败 → Notification 提示失败，用户点击重试
- 网络断开 → 暂停，恢复后自动继续

---

## 七、数据库迁移策略

### 7.1 开发阶段

```kotlin
Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
    .fallbackToDestructiveMigration()
    .build()
```

表结构变更时自动删除重建。

### 7.2 正式上线后

手动编写 `Migration` 脚本，按版本逐步迁移。

### 7.3 覆盖安装处理

```kotlin
// 检测覆盖安装
val prefs = context.getSharedPreferences("app_info", Context.MODE_PRIVATE)
val lastVersion = prefs.getInt("last_version", 0)
val currentVersion = getPackageManager().getPackageInfo(packageName, 0).versionCode

if (currentVersion > lastVersion) {
    // 覆盖安装：清空数据 + 重激活
    AppDatabase.getInstance(context).clearAllTables()
    prefs.edit().putInt("last_version", currentVersion).apply()
    // 重置激活状态
    MMKV.defaultMMKV().removeValueForKey("activation_status")
}
```

---

## 八、关键组件设计

### 8.1 SyncManager（同步管理器）

```kotlin
class SyncManager {
    // 阶段二：全量同步
    suspend fun syncAll(onProgress: (SyncProgress) -> Unit): SyncResult

    // 阶段三：增量同步
    suspend fun syncIncremental(): SyncResult

    // 获取同步进度
    fun getProgress(): SyncProgress

    // 取消同步
    fun cancel()
}
```

### 8.2 SyncService（Foreground Service）

- 保证同步在后台持续运行（即使 Activity 被销毁）
- 显示前台 Notification（同步进度）
- 与 SyncManager 配合，管理同步生命周期

### 8.3 SyncWorker（WorkManager）

- 30 分钟定时调度
- 检查网络状态
- 调用 SyncManager.syncIncremental()
- 处理失败重试

### 8.4 DataVersionManager（版本管理）

- 记录各模块 lastSyncTime
- 比较服务端 lastModified 判断是否有更新
- 管理 DataVersionEntity 表

---

## 九、API 接口需求（后端需实现）

### 9.1 阶段一：登录验证数据

```
GET /getInfo
Response: {
    user: {...},
    roles: [...],
    permissions: [...]
}
```

### 9.2 预留模块数据接口（待后端开发后逐步实现）

| 模块 | 接口路径 | 参数 | 说明 |
|------|----------|------|------|
| 行业分类 | GET /industry/category/list | lastModified（可选） | 支持增量 |
| 法律法规 | GET /law/regulation/list | lastModified（可选） | 支持增量 |
| 法规分类 | GET /law/category/list | lastModified（可选） | 支持增量 |
| 规范用语 | GET /phrase/library/list | lastModified（可选） | 支持增量 |
| 用语分类 | GET /phrase/category/list | lastModified（可选） | 支持增量 |
| 监管事项 | GET /supervision/item/list | lastModified（可选） | 支持增量 |
| 监管分类 | GET /supervision/category/list | lastModified（可选） | 支持增量 |
| 文书模板 | GET /document/template/list | lastModified（可选） | 支持增量 |
| 媒体文件 | GET /media/file/list | lastModified（可选） | 支持增量 |
| 媒体文件 | GET /media/file/{id}/download | - | 文件下载 |

**所有预留接口统一要求**：
- 支持 `lastModified` 参数用于增量查询
- 响应包含 `lastModified` 字段供客户端记录版本
- 单文件下载限制 20MB

### 9.3 增量同步响应格式

```json
{
    "code": 200,
    "msg": "success",
    "data": [...],
    "lastModified": 1713004800000
}
```

---

## 十、文件存储策略

### 10.1 存储位置

- 数据库：Room（SQLite）
- 图片/视频/文档：App 内部存储（`context.filesDir`）
- 临时文件：缓存目录（`context.cacheDir`）

### 10.2 文件命名

```
files/
├── law/
│   └── {id}.pdf
├── media/
│   └── {id}.jpg
├── template/
│   └── {id}.docx
└── ...
```

### 10.3 媒体文件下载（预留，待后端开发）

- 单文件限制：20MB（待后端确认）
- 下载时显示进度（支持大文件断点续传）
- 下载完成后 MD5 校验
- 下载失败自动重试 3 次

---

## 十一、错误处理汇总

| 场景 | 处理方式 |
|------|----------|
| 阶段一 /getInfo 失败 | 不阻塞，用本地数据兜底，提示用户 |
| 阶段二同步失败 | 重试 3 次（指数退避），失败后弹窗回登录页 |
| 阶段三增量同步失败 | 重试 3 次，失败后 Notification 提示用户点击重试 |
| 网络断开 | 暂停，恢复后自动继续 |
| 存储空间不足 | 提示用户清理存储（暂不实现） |
| 文件下载 MD5 校验失败 | 删除错误文件，重试下载 |

---

## 十二、测试要点

1. **阶段一**
   - [ ] 首次安装流程（DB 初始化 + 数据拉取）
   - [ ] 覆盖安装流程（清空数据 + 重激活）
   - [ ] /getInfo 失败时仍能展示登录表单

2. **阶段二**
   - [ ] 全量同步进度条正确显示
   - [ ] 网络断开时重试逻辑
   - [ ] 3 次重试失败后回到登录页
   - [ ] 各模块数据正确存储到本地

3. **阶段三**
   - [ ] WorkManager 30 分钟调度正确
   - [ ] 增量同步只拉取有变更的数据
   - [ ] Notification 显示正确
   - [ ] 点击重试后正确重新执行

4. **边界情况**
   - [ ] 同步过程中 App 被杀，重启后继续
   - [ ] 同步过程中屏幕旋转，进度条保持
   - [ ] 多模块同时下载时内存占用合理

---

## 十三、待后端开发项

1. 各模块数据接口（详见第九章）
2. `lastModified` 字段支持
3. 增量查询接口
4. 媒体文件下载接口（含分片下载支持）

---

## 十四、实现优先级

| 优先级 | 内容 | 依赖 | 状态 |
|--------|------|------|------|
| P0 | 阶段一：登录页数据预加载（含覆盖安装检测） | - | 待开发 |
| P0 | 阶段二：全量阻塞式同步（仅用户权限） | 后端 /getInfo | 待开发 |
| P0 | 覆盖安装：清空数据 + 重激活设备 | - | 待开发 |
| P1 | 阶段三：WorkManager 后台同步框架 | - | 待开发 |
| P2 | 预留模块同步方法（待后端开发后逐步实现） | 后端接口 | 待后端 |
| P2 | 媒体文件分片下载 + MD5 校验 | 后端接口 | 待后端 |
| P3 | 新增表实体和 DAO（为预留模块准备） | - | 待开发 |

---

**设计完成，等待实现计划。**