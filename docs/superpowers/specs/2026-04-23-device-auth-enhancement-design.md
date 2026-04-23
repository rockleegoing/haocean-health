# 设备与认证模块完善设计文档

**文档版本**: v1.0
**创建日期**: 2026-04-23
**状态**: 待实施

---

## 一、目标

在现有代码基础上完善设备与认证模块，实现：
1. Android 端使用 Room 数据库存储激活信息
2. 后端远程清除设备数据功能
3. 设备心跳机制与在线状态管理
4. 一码多设备配置化支持
5. 前端体验优化（到期提醒、定时刷新）

---

## 二、现有代码状态

### 2.1 已完成的工作

| 模块 | 文件 | 状态 |
|------|------|------|
| **Android Room 数据库** | `AppDatabase.kt` | ✅ 已配置 |
| **激活码 DAO** | `ActivationCodeDao.kt` | ✅ 已实现 |
| **设备 DAO** | `DeviceDao.kt` | ✅ 已实现 |
| **激活码实体** | `ActivationCodeEntity.kt` | ✅ 已实现 |
| **设备实体** | `DeviceEntity.kt` | ✅ 已实现 |
| **后端 Controller** | 两个 Controller | ✅ 已实现 |
| **后端 Service** | 两个 Service 实现 | ✅ 已实现 |
| **后端 Mapper** | XML 映射文件 | ✅ 已实现 |
| **前端页面** | 两个管理页面 | ✅ 已实现 |
| **数据库表** | SQL 脚本 | ✅ 已创建 |

### 2.2 需要完善的部分

| 优先级 | 模块 | 说明 |
|--------|------|------|
| P0 | Android 激活逻辑 | `ActivationActivity` 中 `saveActivationInfo()` 使用 MMKV 而非 Room |
| P0 | 后端远程清除 | `clearData` 接口仅记录，无实际清除逻辑 |
| P1 | 后端心跳机制 | 缺少心跳 API 和定时任务 |
| P1 | 后端一码多设备 | `maxDeviceCount` 字段未添加 |
| P2 | 前端体验优化 | 缺少到期提醒、定时刷新 |

---

## 三、架构设计

### 3.1 整体架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                        设备与认证模块完善                            │
├─────────────────────────────────────────────────────────────────────┤
│  模块 1: Android 端存储完善          │  模块 2: 后端功能增强           │
│  ┌─────────────────────────────┐    │  ┌──────────────────────────┐  │
│  │ • ActivationRepository      │    │  │ • 远程清除设备数据        │  │
│  │ • Room 数据存储              │    │  │ • 设备心跳机制            │  │
│  │ • 离线模式支持               │    │  │ • 一码多设备配置化        │  │
│  │ • 网络同步逻辑               │    │  │ • 离线设备检测            │  │
│  └─────────────────────────────┘    │  └──────────────────────────┘  │
├─────────────────────────────────────────────────────────────────────┤
│  模块 3: 前端体验优化                │  模块 4: 数据库变更              │
│  ┌─────────────────────────────┐    │  ┌──────────────────────────┐  │
│  │ • 设备状态定时刷新 (30s)     │    │  │ • 激活码表新增字段         │  │
│  │ • 激活码到期提醒             │    │  │ • 设备表新增字段           │  │
│  │ • 批量生成进度展示           │    │  │ • SQL 脚本执行            │  │
│  └─────────────────────────────┘    │  └──────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.2 实施顺序

```
第 1 阶段：数据库变更 (V1.0.4__device_auth_enhancement.sql)
         ↓
第 2 阶段：Android 端 Room 存储完善 (模块 1)
         ↓
第 3 阶段：后端功能增强 (模块 2)
         ↓
第 4 阶段：前端体验优化 (模块 3)
         ↓
第 5 阶段：联调测试与部署
```

---

## 四、详细设计

### 4.1 模块 1: Android 端存储完善

#### 4.1.1 新增 ActivationRepository

**文件**: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/api/repository/ActivationRepository.kt`

**职责**:
- 激活码验证并保存到 Room
- 离线数据缓存
- 网络同步逻辑

**核心接口**:
```kotlin
class ActivationRepository(
    private var context: Context
) {
    /**
     * 验证激活码并保存到 Room
     * @param codeValue 激活码值
     * @param deviceUuid 设备 UUID
     * @return 验证结果
     */
    suspend fun validateAndSave(codeValue: String, deviceInfo: DeviceInfo): Result<ActivationResponse>

    /**
     * 获取本地激活状态
     */
    fun getActivationStatus(): Flow<ActivationStatus>

    /**
     * 同步离线数据到后端
     */
    suspend fun syncOfflineData(): List<ActivationCodeEntity>
}
```

#### 4.1.2 修改 ActivationActivity

**文件**: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt`

**改动**:
```kotlin
// 原代码 (使用 MMKV)
private fun saveActivationInfo(response: ActivationCodeValidationResponse, activationCode: String) {
    MMKV.defaultMMKV().encode("activation_code_id", response.activationCodeId)
    MMKV.defaultMMKV().encode("activation_code_value", activationCode)
    // ...
}

// 新代码 (使用 Room)
private fun saveActivationInfo(response: ActivationCodeValidationResponse, activationCode: String) {
    val codeEntity = ActivationCodeEntity(
        codeId = response.activationCodeId,
        codeValue = activationCode,
        status = "1", // 已使用
        expireTime = response.expiryTime,
        bindDeviceId = deviceUuid,
        bindUserId = null,
        remark = "",
        createBy = "system",
        createTime = System.currentTimeMillis(),
        updateBy = null,
        updateTime = null
    )
    // 保存到 Room 数据库
    CoroutineScope(Dispatchers.IO).launch {
        AppDatabase.getDatabase(applicationContext)
            .activationCodeDao()
            .insertActivationCode(codeEntity)
    }
}
```

---

### 4.2 模块 2: 后端功能增强

#### 4.2.1 实现远程清除设备数据

**修改文件**:
- `SysDeviceController.java`
- `ISysDeviceService.java`
- `SysDeviceServiceImpl.java`

**Controller 新增方法**:
```java
/**
 * 远程清除设备数据
 */
@PreAuthorize("@ss.hasPermi('device:device:clearData')")
@Log(title = "设备管理", businessType = BusinessType.CLEAN)
@PostMapping("/remoteWipe")
public AjaxResult remoteWipe(@RequestBody Map<String, String> params) {
    String deviceUuid = params.get("deviceUuid");
    if (deviceUuid == null || deviceUuid.isEmpty()) {
        return error("设备标识不能为空");
    }

    SysDevice device = deviceService.selectSysDeviceByUuid(deviceUuid);
    if (device == null) {
        return error("设备不存在");
    }

    // 创建清除指令，推送到设备
    deviceService.createRemoteWipeCommand(deviceUuid);

    return success("已发送清除设备数据指令，设备上线后将执行");
}
```

#### 4.2.2 添加设备心跳机制

**新增数据库字段** `sys_device`:
- `heartbeat_time` DATETIME - 最后心跳时间
- `heartbeat_interval` INT DEFAULT 300 - 心跳间隔（秒）

**新增 API**:
```java
/**
 * 设备心跳上报 (App 调用)
 */
@Anonymous
@Log(title = "设备心跳", businessType = BusinessType.OTHER)
@PostMapping("/heartbeat")
public AjaxResult heartbeat(@RequestBody Map<String, String> params) {
    String deviceUuid = params.get("deviceUuid");
    String status = params.get("status"); // 0-离线，1-在线

    if (deviceUuid == null || deviceUuid.isEmpty()) {
        return error("设备标识不能为空");
    }

    deviceService.updateHeartbeat(deviceUuid, status);
    return success();
}
```

**Service 接口**:
```java
/**
 * 更新设备心跳
 */
void updateHeartbeat(String deviceUuid, String status);

/**
 * 检测并更新离线设备
 */
@Scheduled(cron = "0 */5 * * * ?") // 每 5 分钟执行一次
void checkOfflineDevices();
```

#### 4.2.3 支持一码多设备配置

**修改 `SysActivationCode.java`**:
```java
/** 最大允许设备数 */
private Integer maxDeviceCount;

/** 已激活设备数 */
private Integer activatedCount;
```

**修改验证逻辑** `SysActivationCodeServiceImpl.validateCode()`:
```java
// 检查是否已达到最大设备数
if (code.getActivatedCount() >= code.getMaxDeviceCount()) {
    result.put("success", false);
    result.put("message", "该激活码已达到最大设备数限制");
    return result;
}

// 激活成功后增加已激活设备数
code.setActivatedCount(code.getActivatedCount() + 1);
activationCodeMapper.updateSysActivationCode(code);
```

---

### 4.3 模块 3: 前端体验优化

#### 4.3.1 设备状态定时刷新

**修改文件**: `ruoyi-ui/src/views/device/device/index.vue`

```javascript
created() {
  this.getList()
  this.timer = setInterval(() => {
    this.getList()
  }, 30000) // 30 秒刷新
},
beforeDestroy() {
  clearInterval(this.timer)
}
```

#### 4.3.2 激活码到期提醒组件

**新增文件**: `ruoyi-ui/src/components/ExpireWarning.vue`

```vue
<template>
  <el-tag :type="warningType" size="small" v-if="showWarning">
    <i class="el-icon-warning"></i>
    {{ warningText }}
  </el-tag>
</template>

<script>
export default {
  props: {
    expireTime: {
      type: [Number, String],
      required: true
    }
  },
  computed: {
    showWarning() {
      const remainDays = this.getRemainDays()
      return remainDays >= 0 && remainDays <= 7
    },
    warningType() {
      const remainDays = this.getRemainDays()
      if (remainDays <= 1) return 'danger'
      if (remainDays <= 3) return 'warning'
      return ''
    },
    warningText() {
      const remainDays = this.getRemainDays()
      if (remainDays < 0) return '已过期'
      if (remainDays === 0) return '今日到期'
      return `剩${remainDays}天到期`
    }
  },
  methods: {
    getRemainDays() {
      const now = new Date().getTime()
      const expire = new Date(this.expireTime).getTime()
      return Math.floor((expire - now) / (1000 * 60 * 60 * 24))
    }
  }
}
</script>
```

---

### 4.4 模块 4: 数据库变更

**SQL 脚本**: `RuoYi-Vue/sql/V1.0.4__device_auth_enhancement.sql`

```sql
-- ============================================================
-- 设备与认证模块 - 功能增强
-- 版本：V1.0.4
-- 日期：2026-04-23
-- 说明：添加心跳机制、一码多设备支持
-- ============================================================

-- 1. 激活码表新增字段
ALTER TABLE sys_activation_code
ADD COLUMN max_device_count INT DEFAULT 1 COMMENT '最大允许设备数' AFTER bind_user_id,
ADD COLUMN activated_count INT DEFAULT 0 COMMENT '已激活设备数' AFTER max_device_count;

-- 2. 设备表新增字段
ALTER TABLE sys_device
ADD COLUMN heartbeat_time DATETIME COMMENT '最后心跳时间' AFTER last_sync_time,
ADD COLUMN heartbeat_interval INT DEFAULT 300 COMMENT '心跳间隔 (秒)' AFTER heartbeat_time;

-- 3. 更新现有数据（如果有）
UPDATE sys_activation_code SET max_device_count = 1, activated_count = 0 WHERE max_device_count IS NULL;
UPDATE sys_device SET heartbeat_interval = 300 WHERE heartbeat_interval IS NULL;
```

---

## 五、完整文件清单

| 序号 | 文件路径 | 操作 | 模块 |
|------|---------|------|------|
| 1 | `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/api/repository/ActivationRepository.kt` | 新增 | 模块 1 |
| 2 | `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt` | 修改 | 模块 1 |
| 3 | `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/service/DeviceSyncService.kt` | 新增 (可选) | 模块 1 |
| 4 | `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDeviceMapper.java` | 修改 | 模块 2 |
| 5 | `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysActivationCodeMapper.java` | 修改 | 模块 2 |
| 6 | `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDeviceMapper.xml` | 修改 | 模块 2 |
| 7 | `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysActivationCodeMapper.xml` | 修改 | 模块 2 |
| 8 | `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/device/SysDeviceController.java` | 修改 | 模块 2 |
| 9 | `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDeviceService.java` | 修改 | 模块 2 |
| 10 | `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDeviceServiceImpl.java` | 修改 | 模块 2 |
| 11 | `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysActivationCode.java` | 修改 | 模块 2 |
| 12 | `RuoYi-Vue/ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/DeviceStatusTask.java` | 新增 | 模块 2 |
| 13 | `RuoYi-Vue/sql/V1.0.4__device_auth_enhancement.sql` | 新增 | 模块 4 |
| 14 | `RuoYi-Vue/ruoyi-ui/src/views/device/device/index.vue` | 修改 | 模块 3 |
| 15 | `RuoYi-Vue/ruoyi-ui/src/views/device/activation-code/index.vue` | 修改 | 模块 3 |
| 16 | `RuoYi-Vue/ruoyi-ui/src/components/ExpireWarning.vue` | 新增 | 模块 3 |

---

## 六、验收标准

### 模块 1: Android 端存储完善
- [ ] 激活信息成功存入 Room 数据库
- [ ] 离线模式下可正常使用
- [ ] 网络恢复后自动同步到后端

### 模块 2: 后端功能增强
- [ ] 远程清除指令下发成功
- [ ] 设备心跳正常更新在线状态
- [ ] 离线设备检测定时任务正常运行
- [ ] 一码多设备配置生效

### 模块 3: 前端体验优化
- [ ] 前端设备状态 30 秒内自动更新
- [ ] 到期激活码正确显示警告（7 天/3 天/1 天）
- [ ] 批量生成激活码有进度反馈

### 模块 4: 数据库变更
- [ ] SQL 脚本执行无错误
- [ ] 新增字段正确添加到表中
- [ ] 现有数据迁移成功

---

## 七、风险与注意事项

1. **数据库变更风险**: 执行 SQL 前务必备份数据
2. **Android 端兼容性**: Room 数据库升级需测试旧版本数据迁移
3. **心跳机制性能**: 定时任务频率需根据实际设备数量调整
4. **一码多设备**: 需考虑并发激活场景的锁机制

---

## 八、后续扩展

1. **设备分组管理**: 支持按部门/用户分组管理设备
2. **激活码批量导入**: 支持 Excel 批量导入预生成激活码
3. **设备使用统计**: 统计设备活跃度、使用时长等数据
4. **WebSocket 实时推送**: 替代轮询，实现实时状态更新
