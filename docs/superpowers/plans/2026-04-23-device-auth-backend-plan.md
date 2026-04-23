# 设备与认证模块后端增强实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 完善设备与认证模块后端功能，实现远程清除、心跳机制、一码多设备支持。

**Architecture:** 在现有 Controller/Service/Mapper 架构上扩展，新增 API 端点和数据库字段，保持与现有代码风格一致。

**Tech Stack:** Spring Boot 4.x, MyBatis, MySQL, Quartz 定时任务

---

### Task 1: 数据库变更 - 执行 SQL 脚本

**Files:**
- Create: `RuoYi-Vue/sql/V1.0.4__device_auth_enhancement.sql`

- [ ] **Step 1: 创建 SQL 脚本文件**

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

- [ ] **Step 2: 提交 SQL 脚本**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/sql/V1.0.4__device_auth_enhancement.sql
git commit -m "feat(sql): 添加设备与认证模块功能增强 SQL"
```

---

### Task 2: 激活码实体类新增字段

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysActivationCode.java`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysActivationCode.java
```

- [ ] **Step 2: 在类中添加新字段**

在 `bindUserId` 字段后添加：

```java
/** 最大允许设备数 */
private Integer maxDeviceCount;

/** 已激活设备数 */
private Integer activatedCount;
```

- [ ] **Step 3: 添加 Getter 和 Setter 方法**

在 `setBindUserId()` 方法后添加：

```java
public Integer getMaxDeviceCount() { return maxDeviceCount; }
public void setMaxDeviceCount(Integer maxDeviceCount) { this.maxDeviceCount = maxDeviceCount; }

public Integer getActivatedCount() { return activatedCount; }
public void setActivatedCount(Integer activatedCount) { this.activatedCount = activatedCount; }
```

- [ ] **Step 4: 验证文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysActivationCode.java
```

确认包含以下字段和方法：
- `maxDeviceCount` 字段及 getter/setter
- `activatedCount` 字段及 getter/setter

- [ ] **Step 5: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysActivationCode.java
git commit -m "feat(domain): 激活码实体添加多设备支持字段"
```

---

### Task 3: 激活码 Mapper 接口新增方法

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysActivationCodeMapper.java`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysActivationCodeMapper.java
```

- [ ] **Step 2: 确认 Mapper 接口已包含的方法**

当前已有方法：
- `selectSysActivationCodeById`
- `selectSysActivationCodeByValue`
- `selectSysActivationCodeList`
- `insertSysActivationCode`
- `batchInsertSysActivationCode`
- `updateSysActivationCode`
- `deleteSysActivationCodeById`
- `deleteSysActivationCodeByIds`

无需新增方法，现有方法已支持新字段的 CRUD 操作。

- [ ] **Step 3: 提交（如需修改）**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git status
```

如无修改，跳过提交。

---

### Task 4: 激活码 Mapper XML 配置新字段映射

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysActivationCodeMapper.xml`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysActivationCodeMapper.xml
```

- [ ] **Step 2: 修改 resultMap**

在 `<resultMap>` 中 `bind_user_id` 后添加：

```xml
<result property="maxDeviceCount"    column="max_device_count"   />
<result property="activatedCount"    column="activated_count"   />
```

- [ ] **Step 3: 修改 selectSysActivationCodeVo**

在 `select` 语句的字段列表中添加：
```xml
max_device_count, activated_count,
```

- [ ] **Step 4: 修改 insertSysActivationCode**

在 `<insert id="insertSysActivationCode">` 的 trim 中添加：

```xml
<if test="maxDeviceCount != null">max_device_count,</if>
<if test="activatedCount != null">activated_count,</if>
```

values 部分添加：
```xml
<if test="maxDeviceCount != null">#{maxDeviceCount},</if>
<if test="activatedCount != null">#{activatedCount},</if>
```

- [ ] **Step 5: 修改 batchInsertSysActivationCode**

修改批量插入 SQL：
```xml
<insert id="batchInsertSysActivationCode" parameterType="java.util.List">
    insert into sys_activation_code (code_value, status, expire_time, bind_device_id, bind_user_id, max_device_count, activated_count, create_by, create_time, remark)
    values
    <foreach item="item" collection="list" separator=",">
        (#{item.codeValue}, #{item.status}, #{item.expireTime}, #{item.bindDeviceId}, #{item.bindUserId},
         #{item.maxDeviceCount}, #{item.activatedCount}, #{item.createBy}, sysdate(), #{item.remark})
    </foreach>
</insert>
```

- [ ] **Step 6: 修改 updateSysActivationCode**

在 `<update id="updateSysActivationCode">` 的 set 中添加：

```xml
<if test="maxDeviceCount != null">max_device_count = #{maxDeviceCount},</if>
<if test="activatedCount != null">activated_count = #{activatedCount},</if>
```

- [ ] **Step 7: 验证文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysActivationCodeMapper.xml
```

- [ ] **Step 8: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysActivationCodeMapper.xml
git commit -m "feat(mapper): 激活码 Mapper XML 添加新字段映射"
```

---

### Task 5: 设备 Mapper 新增心跳相关方法

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDeviceMapper.java`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDeviceMapper.java
```

- [ ] **Step 2: 新增心跳更新方法**

在接口末尾添加：

```java
/**
 * 更新设备心跳
 * @param deviceUuid 设备 UUID
 * @param status 状态 (0 离线 1 在线)
 * @return 结果
 */
int updateHeartbeat(String deviceUuid, String status);
```

- [ ] **Step 3: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDeviceMapper.java
git commit -m "feat(mapper): 设备 Mapper 添加心跳更新方法"
```

---

### Task 6: 设备 Mapper XML 实现心跳 SQL

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDeviceMapper.xml`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDeviceMapper.xml
```

- [ ] **Step 2: 修改 resultMap**

在 `<resultMap>` 中 `last_sync_time` 后添加：

```xml
<result property="heartbeatTime"     column="heartbeat_time"     />
<result property="heartbeatInterval" column="heartbeat_interval" />
```

- [ ] **Step 3: 修改 selectSysDeviceVo**

在 `select` 语句的字段列表中添加：
```xml
heartbeat_time, heartbeat_interval,
```

- [ ] **Step 4: 添加 updateHeartbeat 方法**

在 Mapper XML 末尾添加：

```xml
<update id="updateHeartbeat" parameterType="java.util.Map">
    update sys_device
    set heartbeat_time = sysdate(),
        status = #{param2},
        last_login_time = sysdate()
    where device_uuid = #{param1}
</update>
```

- [ ] **Step 5: 修改 insertSysDevice**

在 `<insert id="insertSysDevice">` 的 trim 中添加：

```xml
<if test="heartbeatTime != null">heartbeat_time,</if>
<if test="heartbeatInterval != null">heartbeat_interval,</if>
```

values 部分添加：
```xml
<if test="heartbeatTime != null">#{heartbeatTime},</if>
<if test="heartbeatInterval != null">#{heartbeatInterval},</if>
```

- [ ] **Step 6: 修改 updateSysDevice**

在 `<update id="updateSysDevice">` 的 set 中添加：

```xml
<if test="heartbeatTime != null">heartbeat_time = #{heartbeatTime},</if>
<if test="heartbeatInterval != null">heartbeat_interval = #{heartbeatInterval},</if>
```

- [ ] **Step 7: 验证文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDeviceMapper.xml
```

- [ ] **Step 8: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDeviceMapper.xml
git commit -m "feat(mapper): 设备 Mapper XML 添加心跳字段和 SQL"
```

---

### Task 7: 设备实体类新增心跳字段

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDevice.java`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDevice.java
```

- [ ] **Step 2: 在类中添加新字段**

在 `lastSyncTime` 字段后添加：

```java
/** 最后心跳时间 */
private Date heartbeatTime;

/** 心跳间隔（秒） */
private Integer heartbeatInterval;
```

- [ ] **Step 3: 添加 Getter 和 Setter 方法**

在 `setLastSyncTime()` 方法后添加：

```java
public Date getHeartbeatTime() { return heartbeatTime; }
public void setHeartbeatTime(Date heartbeatTime) { this.heartbeatTime = heartbeatTime; }

public Integer getHeartbeatInterval() { return heartbeatInterval; }
public void setHeartbeatInterval(Integer heartbeatInterval) { this.heartbeatInterval = heartbeatInterval; }
```

- [ ] **Step 4: 验证文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDevice.java
```

- [ ] **Step 5: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDevice.java
git commit -m "feat(domain): 设备实体添加心跳字段"
```

---

### Task 8: 设备 Service 接口新增方法

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDeviceService.java`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDeviceService.java
```

- [ ] **Step 2: 在接口末尾添加新方法**

```java
/**
 * 更新设备心跳
 *
 * @param deviceUuid 设备 UUID
 * @param status 状态 (0 离线 1 在线)
 */
void updateHeartbeat(String deviceUuid, String status);

/**
 * 检测并更新离线设备
 */
void checkOfflineDevices();

/**
 * 创建远程清除指令
 *
 * @param deviceUuid 设备 UUID
 */
void createRemoteWipeCommand(String deviceUuid);
```

- [ ] **Step 3: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDeviceService.java
git commit -m "feat(service): 设备 Service 接口添加心跳和远程清除方法"
```

---

### Task 9: 设备 Service 实现类实现心跳和远程清除

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDeviceServiceImpl.java`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDeviceServiceImpl.java
```

- [ ] **Step 2: 添加必要的导入**

在文件顶部添加：

```java
import com.ruoyi.common.utils.DateUtils;
import org.springframework.scheduling.annotation.Scheduled;
```

- [ ] **Step 3: 实现 updateHeartbeat 方法**

在类中添加：

```java
@Override
public int updateHeartbeat(String deviceUuid, String status) {
    return deviceMapper.updateHeartbeat(deviceUuid, status);
}
```

- [ ] **Step 4: 实现 checkOfflineDevices 方法**

在类中添加：

```java
@Override
@Scheduled(cron = "0 */5 * * * ?") // 每 5 分钟执行一次
public void checkOfflineDevices() {
    // 查询超过 5 分钟未心跳的设备，标记为离线
    List<SysDevice> devices = deviceMapper.selectSysDeviceList(new SysDevice());
    long timeout = 5 * 60 * 1000; // 5 分钟
    long now = System.currentTimeMillis();

    for (SysDevice device : devices) {
        if (device.getHeartbeatTime() != null) {
            long lastHeartbeat = device.getHeartbeatTime().getTime();
            if (now - lastHeartbeat > timeout && "1".equals(device.getStatus())) {
                device.setStatus("0");
                deviceMapper.updateSysDevice(device);
            }
        }
    }
}
```

- [ ] **Step 5: 实现 createRemoteWipeCommand 方法**

在类中添加：

```java
@Override
public void createRemoteWipeCommand(String deviceUuid) {
    // 创建远程清除指令记录
    // 后续可扩展：推送到消息队列或 Redis，供设备轮询
    SysDevice device = deviceMapper.selectSysDeviceByUuid(deviceUuid);
    if (device != null) {
        // 在 remark 中记录清除指令时间
        device.setRemark("REMOTE_WIPE:" + System.currentTimeMillis());
        device.setUpdateTime(DateUtils.getNowDate());
        deviceMapper.updateSysDevice(device);
    }
}
```

- [ ] **Step 6: 验证文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDeviceServiceImpl.java
```

- [ ] **Step 7: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDeviceServiceImpl.java
git commit -m "feat(service): 实现设备心跳、离线检测和远程清除功能"
```

---

### Task 10: 激活码 Service 实现一码多设备验证逻辑

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysActivationCodeServiceImpl.java`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysActivationCodeServiceImpl.java
```

- [ ] **Step 2: 修改 validateCode 方法 - 添加设备数检查**

找到 `validateCode` 方法中的状态检查部分，在检查过期前添加：

```java
// 检查是否已达到最大设备数
if (code.getMaxDeviceCount() != null && code.getActivatedCount() >= code.getMaxDeviceCount()) {
    result.put("success", false);
    result.put("message", "该激活码已达到最大设备数限制");
    return result;
}
```

- [ ] **Step 3: 修改 validateCode 方法 - 增加已激活设备数**

找到激活成功后更新激活码的部分，添加：

```java
// 增加已激活设备数
if (code.getActivatedCount() == null) {
    code.setActivatedCount(0);
}
code.setActivatedCount(code.getActivatedCount() + 1);
```

- [ ] **Step 4: 修改返回结果**

找到返回结果部分，添加：

```java
result.put("activatedCount", code.getActivatedCount());
result.put("maxDeviceCount", code.getMaxDeviceCount());
```

- [ ] **Step 5: 验证修改后的 validateCode 方法**

```bash
grep -A 80 "public Map<String, Object> validateCode" RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysActivationCodeServiceImpl.java
```

- [ ] **Step 6: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysActivationCodeServiceImpl.java
git commit -m "feat(service): 激活码验证支持一码多设备配置"
```

---

### Task 11: 设备 Controller 新增心跳和远程清除 API

**Files:**
- Modify: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/device/SysDeviceController.java`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/device/SysDeviceController.java
```

- [ ] **Step 2: 修改 clearData 方法为 remoteWipe**

找到 `clearData` 方法，修改为：

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

- [ ] **Step 3: 添加心跳 API 方法**

在 Controller 末尾添加：

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

- [ ] **Step 4: 验证文件内容**

```bash
cat RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/device/SysDeviceController.java
```

- [ ] **Step 5: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/device/SysDeviceController.java
git commit -m "feat(controller): 设备 Controller 添加心跳和远程清除 API"
```

---

### Task 12: 添加定时任务配置

**Files:**
- Modify: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/RuoYiApplication.java`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/RuoYiApplication.java
```

- [ ] **Step 2: 确认是否已启用定时任务**

检查是否有 `@EnableScheduling` 注解。

如没有，添加：

```java
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RuoYiApplication {
    // ...
}
```

- [ ] **Step 3: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/RuoYiApplication.java
git commit -m "feat(config): 启用 Spring 定时任务支持"
```

---

### Task 13: 编译测试后端代码

**Files:**
- Test: Maven 编译

- [ ] **Step 1: 编译后端项目**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue
mvn clean compile -DskipTests
```

预期输出：`BUILD SUCCESS`

- [ ] **Step 2: 运行单元测试（如有）**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue
mvn test
```

---

## 验证

执行完所有任务后，应验证：
- [ ] SQL 脚本执行成功，数据库表结构正确
- [ ] 后端编译成功，无语法错误
- [ ] 心跳 API 可正常调用
- [ ] 远程清除 API 可正常调用
- [ ] 一码多设备配置生效

---

## 后续任务

后端增强完成后，继续执行：
1. `2026-04-23-device-auth-android-plan.md` - Android 端存储完善
2. `2026-04-23-device-auth-frontend-plan.md` - 前端体验优化
