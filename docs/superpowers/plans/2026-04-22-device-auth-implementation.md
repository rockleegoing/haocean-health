# 设备与认证模块实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现设备激活、离线登录、数据同步功能，支持执法人员在无网络环境下正常执法

**Architecture:** 后端新增激活码和设备管理模块，Android 端使用 Room 数据库实现本地数据存储，采用三阶段同步策略（登录前同步用户数据→登录后阻塞同步 P0 数据→后台静默同步 P1 数据）

**Tech Stack:** Spring Boot 4.x, MyBatis, Vue 2.6, Element UI, Kotlin, Room, Retrofit, bcrypt

---

## 文件结构总览

### 后端（RuoYi-Vue）

**新增文件：**
```
RuoYi-Vue/
├── ruoyi-admin/src/main/java/com/ruoyi/web/controller/device/
│   ├── SysActivationCodeController.java
│   └── SysDeviceController.java
├── ruoyi-system/src/main/java/com/ruoyi/system/
│   ├── domain/
│   │   ├── SysActivationCode.java
│   │   └── SysDevice.java
│   ├── mapper/
│   │   ├── SysActivationCodeMapper.java
│   │   └── SysDeviceMapper.java
│   └── service/
│       ├── ISysActivationCodeService.java
│       ├── ISysDeviceService.java
│       └── impl/
│           ├── SysActivationCodeServiceImpl.java
│           └── SysDeviceServiceImpl.java
├── ruoyi-ui/src/views/device/
│   ├── activation-code/
│   │   ├── index.vue
│   │   └── CreateDialog.vue
│   └── device/
│       └── index.vue
├── ruoyi-ui/src/components/
│   └── CountdownTimer.vue          # 有效期倒计时组件
└── sql/
    ├── device_auth_init.sql        # 表结构初始化
    └── device_auth_data.sql        # 默认数据
```

**修改文件：**
```
RuoYi-Vue/
├── ruoyi-admin/src/main/java/com/ruoyi/web/controller/common/
│   └── CommonController.java       # 新增同步接口
└── ruoyi-ui/src/router/index.js    # 新增设备管理路由
```

### Android（Ruoyi-Android-App）

**新增文件：**
```
Ruoyi-Android-App/
├── app/src/main/java/com/ruoyi/app/
│   ├── activity/
│   │   └── ActivationActivity.kt
│   ├── fragment/
│   │   └── SyncProgressFragment.kt
│   ├── data/
│   │   ├── database/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── UserDao.kt
│   │   │   │   ├── RoleDao.kt
│   │   │   │   ├── DeptDao.kt
│   │   │   │   ├── IndustryCategoryDao.kt
│   │   │   │   ├── ActivationCodeDao.kt
│   │   │   │   ├── DeviceDao.kt
│   │   │   │   ├── SyncQueueDao.kt
│   │   │   │   └── DataVersionDao.kt
│   │   │   └── entity/
│   │   │       ├── UserEntity.kt
│   │   │       ├── RoleEntity.kt
│   │   │       ├── DeptEntity.kt
│   │   │       ├── IndustryCategoryEntity.kt
│   │   │       ├── ActivationCodeEntity.kt
│   │   │       ├── DeviceEntity.kt
│   │   │       ├── SyncQueueEntity.kt
│   │   │       └── DataVersionEntity.kt
│   │   ├── repository/
│   │   │   ├── UserRepository.kt
│   │   │   ├── SyncRepository.kt
│   │   │   └── ActivationRepository.kt
│   │   └── sync/
│   │       ├── SyncService.kt
│   │       ├── SyncManager.kt
│   │       └── SyncProgress.kt
│   ├── api/
│   │   └── service/
│   │       ├── ActivationService.kt
│   │       └── SyncService.kt
│   └── utils/
│       ├── DeviceUtils.kt
│       └── PasswordUtils.kt
└── app/build.gradle                # 添加 Room 依赖
```

**修改文件：**
```
Ruoyi-Android-App/
├── app/src/main/java/com/ruoyi/app/activity/LoginActivity.kt
└── app/src/main/java/com/ruoyi/app/App.kt
```

---

## 第一部分：后端基础（阶段一）

### Task 1: 创建数据库表

**Files:**
- Create: `RuoYi-Vue/sql/device_auth_init.sql`
- Create: `RuoYi-Vue/sql/device_auth_data.sql`

- [ ] **Step 1: 创建激活码表和设备表 SQL 脚本**

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

- [ ] **Step 2: 创建默认数据 SQL 脚本**

```sql
-- 默认测试激活码（10 个，有效期 365 天）
INSERT INTO sys_activation_code (code_value, status, expire_time, remark, create_by) VALUES
('TEST0001', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 1', 'admin'),
('TEST0002', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 2', 'admin'),
('TEST0003', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 3', 'admin'),
('TEST0004', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 4', 'admin'),
('TEST0005', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 5', 'admin'),
('TEST0006', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 6', 'admin'),
('TEST0007', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 7', 'admin'),
('TEST0008', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 8', 'admin'),
('TEST0009', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 9', 'admin'),
('TEST0010', '0', DATE_ADD(NOW(), INTERVAL 365 DAY), '测试激活码 10', 'admin');
```

- [ ] **Step 3: 提交**

```bash
cd RuoYi-Vue
git add sql/device_auth_init.sql sql/device_auth_data.sql
git commit -m "feat(db): 新增激活码和设备表结构及默认数据"
```

---

### Task 2: 实现 Domain 实体

**Files:**
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/domain/SysActivationCode.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDevice.java`

- [ ] **Step 1: 创建 SysActivationCode 实体**

```java
package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 激活码对象 sys_activation_code
 */
public class SysActivationCode extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    private Long codeId;

    /** 激活码值 */
    private String codeValue;

    /** 状态（0 未使用 1 已使用 2 已过期 3 已禁用） */
    private String status;

    /** 有效期 */
    private Date expireTime;

    /** 绑定设备 ID */
    private String bindDeviceId;

    /** 绑定用户 ID */
    private Long bindUserId;

    /** 备注 */
    private String remark;

    // Getters and Setters
    public Long getCodeId() { return codeId; }
    public void setCodeId(Long codeId) { this.codeId = codeId; }

    public String getCodeValue() { return codeValue; }
    public void setCodeValue(String codeValue) { this.codeValue = codeValue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }

    public String getBindDeviceId() { return bindDeviceId; }
    public void setBindDeviceId(String bindDeviceId) { this.bindDeviceId = bindDeviceId; }

    public Long getBindUserId() { return bindUserId; }
    public void setBindUserId(Long bindUserId) { this.bindUserId = bindUserId; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    /** 状态文本（用于前端显示） */
    public String getStatusText() {
        switch (this.status) {
            case "0": return "未使用";
            case "1": return "已使用";
            case "2": return "已过期";
            case "3": return "已禁用";
            default: return "未知";
        }
    }
}
```

- [ ] **Step 2: 创建 SysDevice 实体**

```java
package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import java.util.Date;

/**
 * 设备对象 sys_device
 */
public class SysDevice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    private Long deviceId;

    /** 设备唯一标识 */
    private String deviceUuid;

    /** 设备名称 */
    private String deviceName;

    /** 设备型号 */
    private String deviceModel;

    /** 操作系统 */
    private String deviceOs;

    /** App 版本 */
    private String appVersion;

    /** 当前登录用户 ID */
    private Long currentUserId;

    /** 当前登录用户名（冗余字段，便于查询） */
    private String currentUserName;

    /** 激活码 ID */
    private Long activationCodeId;

    /** 最后同步时间 */
    private Date lastSyncTime;

    /** 最后登录时间 */
    private Date lastLoginTime;

    /** 最后登录 IP */
    private String lastLoginIp;

    /** 状态（0 离线 1 在线） */
    private String status;

    /** 备注 */
    private String remark;

    // Getters and Setters
    public Long getDeviceId() { return deviceId; }
    public void setDeviceId(Long deviceId) { this.deviceId = deviceId; }

    public String getDeviceUuid() { return deviceUuid; }
    public void setDeviceUuid(String deviceUuid) { this.deviceUuid = deviceUuid; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getDeviceOs() { return deviceOs; }
    public void setDeviceOs(String deviceOs) { this.deviceOs = deviceOs; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }

    public Long getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(Long currentUserId) { this.currentUserId = currentUserId; }

    public String getCurrentUserName() { return currentUserName; }
    public void setCurrentUserName(String currentUserName) { this.currentUserName = currentUserName; }

    public Long getActivationCodeId() { return activationCodeId; }
    public void setActivationCodeId(Long activationCodeId) { this.activationCodeId = activationCodeId; }

    public Date getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(Date lastSyncTime) { this.lastSyncTime = lastSyncTime; }

    public Date getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(Date lastLoginTime) { this.lastLoginTime = lastLoginTime; }

    public String getLastLoginIp() { return lastLoginIp; }
    public void setLastLoginIp(String lastLoginIp) { this.lastLoginIp = lastLoginIp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    /** 状态文本（用于前端显示） */
    public String getStatusText() {
        switch (this.status) {
            case "0": return "离线";
            case "1": return "在线";
            default: return "未知";
        }
    }
}
```

- [ ] **Step 3: 提交**

```bash
cd RuoYi-Vue
git add ruoyi-system/src/main/java/com/ruoyi/system/domain/*.java
git commit -m "feat(domain): 新增激活码和设备实体类"
```

---

### Task 3: 实现 Mapper 接口

**Files:**
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysActivationCodeMapper.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDeviceMapper.java`

- [ ] **Step 1: 创建 SysActivationCodeMapper 接口**

```java
package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysActivationCode;
import java.util.List;

/**
 * 激活码 Mapper 数据层
 */
public interface SysActivationCodeMapper {

    /**
     * 查询激活码
     * @param codeId 激活码主键
     * @return 激活码
     */
    SysActivationCode selectSysActivationCodeById(Long codeId);

    /**
     * 根据激活码值查询
     * @param codeValue 激活码值
     * @return 激活码
     */
    SysActivationCode selectSysActivationCodeByValue(String codeValue);

    /**
     * 查询激活码列表
     * @param sysActivationCode 激活码
     * @return 激活码集合
     */
    List<SysActivationCode> selectSysActivationCodeList(SysActivationCode sysActivationCode);

    /**
     * 新增激活码
     * @param sysActivationCode 激活码
     * @return 结果
     */
    int insertSysActivationCode(SysActivationCode sysActivationCode);

    /**
     * 批量新增激活码
     * @param sysActivationCodeList 激活码列表
     * @return 结果
     */
    int batchInsertSysActivationCode(List<SysActivationCode> sysActivationCodeList);

    /**
     * 修改激活码
     * @param sysActivationCode 激活码
     * @return 结果
     */
    int updateSysActivationCode(SysActivationCode sysActivationCode);

    /**
     * 删除激活码
     * @param codeId 激活码主键
     * @return 结果
     */
    int deleteSysActivationCodeById(Long codeId);

    /**
     * 批量删除激活码
     * @param codeIds 需要删除的数据主键
     * @return 结果
     */
    int deleteSysActivationCodeByIds(Long[] codeIds);
}
```

- [ ] **Step 2: 创建 SysDeviceMapper 接口**

```java
package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysDevice;
import java.util.List;

/**
 * 设备 Mapper 数据层
 */
public interface SysDeviceMapper {

    /**
     * 查询设备
     * @param deviceId 设备主键
     * @return 设备
     */
    SysDevice selectSysDeviceById(Long deviceId);

    /**
     * 根据设备 UUID 查询
     * @param deviceUuid 设备 UUID
     * @return 设备
     */
    SysDevice selectSysDeviceByUuid(String deviceUuid);

    /**
     * 查询设备列表
     * @param sysDevice 设备
     * @return 设备集合
     */
    List<SysDevice> selectSysDeviceList(SysDevice sysDevice);

    /**
     * 新增设备
     * @param sysDevice 设备
     * @return 结果
     */
    int insertSysDevice(SysDevice sysDevice);

    /**
     * 修改设备
     * @param sysDevice 设备
     * @return 结果
     */
    int updateSysDevice(SysDevice sysDevice);

    /**
     * 删除设备
     * @param deviceId 设备主键
     * @return 结果
     */
    int deleteSysDeviceById(Long deviceId);

    /**
     * 批量删除设备
     * @param deviceIds 需要删除的数据主键
     * @return 结果
     */
    int deleteSysDeviceByIds(Long[] deviceIds);

    /**
     * 更新设备当前用户
     * @param deviceUuid 设备 UUID
     * @param currentUserId 当前用户 ID
     * @param currentUserName 当前用户名
     * @return 结果
     */
    int updateDeviceCurrentUser(String deviceUuid, Long currentUserId, String currentUserName);
}
```

- [ ] **Step 3: 创建 MyBatis XML 映射文件**

创建 `ruoyi-system/src/main/resources/mapper/system/SysActivationCodeMapper.xml`:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.system.mapper.SysActivationCodeMapper">

    <resultMap type="SysActivationCode" id="SysActivationCodeResult">
        <result property="codeId"    column="code_id"    />
        <result property="codeValue"    column="code_value"    />
        <result property="status"    column="status"    />
        <result property="expireTime"    column="expire_time"    />
        <result property="bindDeviceId"    column="bind_device_id"    />
        <result property="bindUserId"    column="bind_user_id"    />
        <result property="remark"    column="remark"    />
        <result property="createBy"    column="create_by"    />
        <result property="createTime"    column="create_time"    />
        <result property="updateBy"    column="update_by"    />
        <result property="updateTime"    column="update_time"    />
    </resultMap>

    <sql id="selectSysActivationCodeVo">
        select code_id, code_value, status, expire_time, bind_device_id, bind_user_id, remark, create_by, create_time, update_by, update_time from sys_activation_code
    </sql>

    <select id="selectSysActivationCodeList" parameterType="SysActivationCode" resultMap="SysActivationCodeResult">
        <include refid="selectSysActivationCodeVo"/>
        <where>
            <if test="codeValue != null  and codeValue != ''">
                and code_value like concat('%', #{codeValue}, '%')
            </if>
            <if test="status != null  and status != ''">
                and status = #{status}
            </if>
            <if test="expireTime != null">
                and expire_time &gt;= #{expireTime}
            </if>
            <if test="params.beginTime != null and params.endTime != null">
                and create_time between #{params.beginTime} and #{params.endTime}
            </if>
        </where>
    </select>

    <select id="selectSysActivationCodeById" parameterType="Long" resultMap="SysActivationCodeResult">
        <include refid="selectSysActivationCodeVo"/>
        where code_id = #{codeId}
    </select>

    <select id="selectSysActivationCodeByValue" parameterType="String" resultMap="SysActivationCodeResult">
        <include refid="selectSysActivationCodeVo"/>
        where code_value = #{codeValue}
    </select>

    <insert id="insertSysActivationCode" parameterType="SysActivationCode" useGeneratedKeys="true" keyProperty="codeId">
        insert into sys_activation_code
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="codeValue != null and codeValue != ''">code_value,</if>
            <if test="status != null">status,</if>
            <if test="expireTime != null">expire_time,</if>
            <if test="bindDeviceId != null">bind_device_id,</if>
            <if test="bindUserId != null">bind_user_id,</if>
            <if test="remark != null">remark,</if>
            <if test="createBy != null">create_by,</if>
         </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="codeValue != null and codeValue != ''">#{codeValue},</if>
            <if test="status != null">#{status},</if>
            <if test="expireTime != null">#{expireTime},</if>
            <if test="bindDeviceId != null">#{bindDeviceId},</if>
            <if test="bindUserId != null">#{bindUserId},</if>
            <if test="remark != null">#{remark},</if>
            <if test="createBy != null">#{createBy},</if>
         </trim>
    </insert>

    <insert id="batchInsertSysActivationCode">
        insert into sys_activation_code (code_value, status, expire_time, remark, create_by) values
        <foreach item="item" collection="list" separator=",">
            (#{item.codeValue}, #{item.status}, #{item.expireTime}, #{item.remark}, #{item.createBy})
        </foreach>
    </insert>

    <update id="updateSysActivationCode" parameterType="SysActivationCode">
        update sys_activation_code
        <trim prefix="set" suffixOverrides=",">
            <if test="status != null">status = #{status},</if>
            <if test="expireTime != null">expire_time = #{expireTime},</if>
            <if test="bindDeviceId != null">bind_device_id = #{bindDeviceId},</if>
            <if test="bindUserId != null">bind_user_id = #{bindUserId},</if>
            <if test="remark != null">remark = #{remark},</if>
            <if test="updateBy != null">update_by = #{updateBy},</if>
        </trim>
        where code_id = #{codeId}
    </update>

    <delete id="deleteSysActivationCodeById" parameterType="Long">
        delete from sys_activation_code where code_id = #{codeId}
    </delete>

    <delete id="deleteSysActivationCodeByIds" parameterType="String">
        delete from sys_activation_code where code_id in
        <foreach item="codeId" collection="array" open="(" separator="," close=")">
            #{codeId}
        </foreach>
    </delete>
</mapper>
```

创建 `ruoyi-system/src/main/resources/mapper/system/SysDeviceMapper.xml`:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.system.mapper.SysDeviceMapper">

    <resultMap type="SysDevice" id="SysDeviceResult">
        <result property="deviceId"    column="device_id"    />
        <result property="deviceUuid"    column="device_uuid"    />
        <result property="deviceName"    column="device_name"    />
        <result property="deviceModel"    column="device_model"    />
        <result property="deviceOs"    column="device_os"    />
        <result property="appVersion"    column="app_version"    />
        <result property="currentUserId"    column="current_user_id"    />
        <result property="currentUserName"    column="current_user_name"    />
        <result property="activationCodeId"    column="activation_code_id"    />
        <result property="lastSyncTime"    column="last_sync_time"    />
        <result property="lastLoginTime"    column="last_login_time"    />
        <result property="lastLoginIp"    column="last_login_ip"    />
        <result property="status"    column="status"    />
        <result property="remark"    column="remark"    />
        <result property="createBy"    column="create_by"    />
        <result property="createTime"    column="create_time"    />
        <result property="updateBy"    column="update_by"    />
        <result property="updateTime"    column="update_time"    />
    </resultMap>

    <sql id="selectSysDeviceVo">
        select device_id, device_uuid, device_name, device_model, device_os, app_version, current_user_id, current_user_name, activation_code_id, last_sync_time, last_login_time, last_login_ip, status, remark, create_by, create_time, update_by, update_time from sys_device
    </sql>

    <select id="selectSysDeviceList" parameterType="SysDevice" resultMap="SysDeviceResult">
        <include refid="selectSysDeviceVo"/>
        <where>
            <if test="deviceUuid != null  and deviceUuid != ''">
                and device_uuid like concat('%', #{deviceUuid}, '%')
            </if>
            <if test="status != null  and status != ''">
                and status = #{status}
            </if>
            <if test="currentUserId != null">
                and current_user_id = #{currentUserId}
            </if>
        </where>
    </select>

    <select id="selectSysDeviceById" parameterType="Long" resultMap="SysDeviceResult">
        <include refid="selectSysDeviceVo"/>
        where device_id = #{deviceId}
    </select>

    <select id="selectSysDeviceByUuid" parameterType="String" resultMap="SysDeviceResult">
        <include refid="selectSysDeviceVo"/>
        where device_uuid = #{deviceUuid}
    </select>

    <insert id="insertSysDevice" parameterType="SysDevice" useGeneratedKeys="true" keyProperty="deviceId">
        insert into sys_device
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="deviceUuid != null and deviceUuid != ''">device_uuid,</if>
            <if test="deviceName != null">device_name,</if>
            <if test="deviceModel != null">device_model,</if>
            <if test="deviceOs != null">device_os,</if>
            <if test="appVersion != null">app_version,</if>
            <if test="currentUserId != null">current_user_id,</if>
            <if test="currentUserName != null">current_user_name,</if>
            <if test="activationCodeId != null">activation_code_id,</if>
            <if test="status != null">status,</if>
            <if test="remark != null">remark,</if>
            <if test="createBy != null">create_by,</if>
         </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="deviceUuid != null and deviceUuid != ''">#{deviceUuid},</if>
            <if test="deviceName != null">#{deviceName},</if>
            <if test="deviceModel != null">#{deviceModel},</if>
            <if test="deviceOs != null">#{deviceOs},</if>
            <if test="appVersion != null">#{appVersion},</if>
            <if test="currentUserId != null">#{currentUserId},</if>
            <if test="currentUserName != null">#{currentUserName},</if>
            <if test="activationCodeId != null">#{activationCodeId},</if>
            <if test="status != null">#{status},</if>
            <if test="remark != null">#{remark},</if>
            <if test="createBy != null">#{createBy},</if>
         </trim>
    </insert>

    <update id="updateSysDevice" parameterType="SysDevice">
        update sys_device
        <trim prefix="set" suffixOverrides=",">
            <if test="deviceName != null">device_name = #{deviceName},</if>
            <if test="deviceModel != null">device_model = #{deviceModel},</if>
            <if test="deviceOs != null">device_os = #{deviceOs},</if>
            <if test="appVersion != null">app_version = #{appVersion},</if>
            <if test="currentUserId != null">current_user_id = #{currentUserId},</if>
            <if test="currentUserName != null">current_user_name = #{currentUserName},</if>
            <if test="activationCodeId != null">activation_code_id = #{activationCodeId},</if>
            <if test="lastSyncTime != null">last_sync_time = #{lastSyncTime},</if>
            <if test="lastLoginTime != null">last_login_time = #{lastLoginTime},</if>
            <if test="lastLoginIp != null">last_login_ip = #{lastLoginIp},</if>
            <if test="status != null">status = #{status},</if>
            <if test="remark != null">remark = #{remark},</if>
            <if test="updateBy != null">update_by = #{updateBy},</if>
        </trim>
        where device_id = #{deviceId}
    </update>

    <update id="updateDeviceCurrentUser">
        update sys_device
        set current_user_id = #{currentUserId},
            current_user_name = #{currentUserName},
            last_login_time = NOW()
        where device_uuid = #{deviceUuid}
    </update>

    <delete id="deleteSysDeviceById" parameterType="Long">
        delete from sys_device where device_id = #{deviceId}
    </delete>

    <delete id="deleteSysDeviceByIds" parameterType="String">
        delete from sys_device where device_id in
        <foreach item="deviceId" collection="array" open="(" separator="," close=")">
            #{deviceId}
        </foreach>
    </delete>
</mapper>
```

- [ ] **Step 4: 提交**

```bash
cd RuoYi-Vue
git add ruoyi-system/src/main/java/com/ruoyi/system/mapper/*.java
git add ruoyi-system/src/main/resources/mapper/system/*.xml
git commit -m "feat(mapper): 新增激活码和设备 Mapper 接口及 XML"
```

---

### Task 4: 实现 Service 接口和实现类

**Files:**
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/ISysActivationCodeService.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDeviceService.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysActivationCodeServiceImpl.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDeviceServiceImpl.java`

- [ ] **Step 1: 创建 ISysActivationCodeService 接口**

```java
package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysActivationCode;
import java.util.List;

/**
 * 激活码 Service 接口
 */
public interface ISysActivationCodeService {

    /**
     * 查询激活码
     * @param codeId 激活码主键
     * @return 激活码
     */
    SysActivationCode selectSysActivationCodeById(Long codeId);

    /**
     * 根据激活码值查询
     * @param codeValue 激活码值
     * @return 激活码
     */
    SysActivationCode selectSysActivationCodeByValue(String codeValue);

    /**
     * 查询激活码列表
     * @param sysActivationCode 激活码
     * @return 激活码集合
     */
    List<SysActivationCode> selectSysActivationCodeList(SysActivationCode sysActivationCode);

    /**
     * 新增激活码
     * @param sysActivationCode 激活码
     * @return 结果
     */
    int insertSysActivationCode(SysActivationCode sysActivationCode);

    /**
     * 批量新增激活码
     * @param count 数量
     * @param expireDays 有效期天数
     * @param remark 备注
     * @param createBy 创建人
     * @return 激活码列表
     */
    List<SysActivationCode> batchGenerateCodes(int count, int expireDays, String remark, String createBy);

    /**
     * 批量新增激活码
     * @param sysActivationCodeList 激活码列表
     * @return 结果
     */
    int batchInsertSysActivationCode(List<SysActivationCode> sysActivationCodeList);

    /**
     * 修改激活码
     * @param sysActivationCode 激活码
     * @return 结果
     */
    int updateSysActivationCode(SysActivationCode sysActivationCode);

    /**
     * 批量删除激活码
     * @param codeIds 需要删除的激活码主键
     * @return 结果
     */
    int deleteSysActivationCodeByIds(Long[] codeIds);

    /**
     * 删除激活码
     * @param codeId 激活码主键
     * @return 结果
     */
    int deleteSysActivationCodeById(Long codeId);

    /**
     * 验证激活码
     * @param codeValue 激活码值
     * @param deviceUuid 设备 UUID
     * @return 验证结果 {valid: true/false, message: 消息}
     */
    Map<String, Object> validateCode(String codeValue, String deviceUuid);
}
```

- [ ] **Step 2: 创建 SysActivationCodeServiceImpl 实现类**

```java
package com.ruoyi.system.service.impl;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.text.CharsetKit;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.security.SecureUtil;
import com.ruoyi.system.domain.SysActivationCode;
import com.ruoyi.system.mapper.SysActivationCodeMapper;
import com.ruoyi.system.service.ISysActivationCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;

/**
 * 激活码 Service 业务层处理
 */
@Service
public class SysActivationCodeServiceImpl implements ISysActivationCodeService {

    private static final Logger log = LoggerFactory.getLogger(SysActivationCodeServiceImpl.class);

    @Autowired
    private SysActivationCodeMapper sysActivationCodeMapper;

    /**
     * 生成激活码字符集（排除易混淆字符 I/O/0/1）
     */
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    /**
     * 激活码长度
     */
    private static final int CODE_LENGTH = 8;

    /**
     * 查询激活码
     * @param codeId 激活码主键
     * @return 激活码
     */
    @Override
    public SysActivationCode selectSysActivationCodeById(Long codeId) {
        return sysActivationCodeMapper.selectSysActivationCodeById(codeId);
    }

    /**
     * 根据激活码值查询
     * @param codeValue 激活码值
     * @return 激活码
     */
    @Override
    public SysActivationCode selectSysActivationCodeByValue(String codeValue) {
        return sysActivationCodeMapper.selectSysActivationCodeByValue(codeValue);
    }

    /**
     * 查询激活码列表
     * @param sysActivationCode 激活码
     * @return 激活码
     */
    @Override
    public List<SysActivationCode> selectSysActivationCodeList(SysActivationCode sysActivationCode) {
        return sysActivationCodeMapper.selectSysActivationCodeList(sysActivationCode);
    }

    /**
     * 新增激活码
     * @param sysActivationCode 激活码
     * @return 结果
     */
    @Override
    public int insertSysActivationCode(SysActivationCode sysActivationCode) {
        sysActivationCode.setCreateTime(DateUtils.getNowDate());
        return sysActivationCodeMapper.insertSysActivationCode(sysActivationCode);
    }

    /**
     * 批量生成激活码
     * @param count 数量
     * @param expireDays 有效期天数
     * @param remark 备注
     * @param createBy 创建人
     * @return 激活码列表
     */
    @Override
    @Transactional
    public List<SysActivationCode> batchGenerateCodes(int count, int expireDays, String remark, String createBy) {
        if (count <= 0 || count > 50) {
            throw new ServiceException("批量生成数量必须在 1-50 之间");
        }

        List<SysActivationCode> codeList = new ArrayList<>();
        Set<String> generatedValues = new HashSet<>();

        // 计算有效期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, expireDays);
        Date expireTime = calendar.getTime();

        // 生成激活码
        for (int i = 0; i < count; i++) {
            SysActivationCode code = new SysActivationCode();
            String codeValue = generateCodeValue();

            // 确保唯一性
            while (generatedValues.contains(codeValue) || sysActivationCodeMapper.selectSysActivationCodeByValue(codeValue) != null) {
                codeValue = generateCodeValue();
            }

            generatedValues.add(codeValue);
            code.setCodeValue(codeValue);
            code.setStatus("0"); // 未使用
            code.setExpireTime(expireTime);
            code.setRemark(remark);
            code.setCreateBy(createBy);
            codeList.add(code);
        }

        // 批量插入
        batchInsertSysActivationCode(codeList);
        log.info("批量生成激活码 {} 个，创建人：{}", count, createBy);

        return codeList;
    }

    /**
     * 批量新增激活码
     * @param sysActivationCodeList 激活码列表
     * @return 结果
     */
    @Override
    @Transactional
    public int batchInsertSysActivationCode(List<SysActivationCode> sysActivationCodeList) {
        if (sysActivationCodeList == null || sysActivationCodeList.isEmpty()) {
            return 0;
        }
        return sysActivationCodeMapper.batchInsertSysActivationCode(sysActivationCodeList);
    }

    /**
     * 修改激活码
     * @param sysActivationCode 激活码
     * @return 结果
     */
    @Override
    public int updateSysActivationCode(SysActivationCode sysActivationCode) {
        sysActivationCode.setUpdateTime(DateUtils.getNowDate());
        return sysActivationCodeMapper.updateSysActivationCode(sysActivationCode);
    }

    /**
     * 批量删除激活码
     * @param codeIds 需要删除的激活码
     * @return 结果
     */
    @Override
    public int deleteSysActivationCodeByIds(Long[] codeIds) {
        return sysActivationCodeMapper.deleteSysActivationCodeByIds(codeIds);
    }

    /**
     * 删除激活码
     * @param codeId 激活码主键
     * @return 结果
     */
    @Override
    public int deleteSysActivationCodeById(Long codeId) {
        return sysActivationCodeMapper.deleteSysActivationCodeById(codeId);
    }

    /**
     * 验证激活码
     * @param codeValue 激活码值
     * @param deviceUuid 设备 UUID
     * @return 验证结果
     */
    @Override
    @Transactional
    public Map<String, Object> validateCode(String codeValue, String deviceUuid) {
        Map<String, Object> result = new HashMap<>();

        // 查询激活码
        SysActivationCode code = sysActivationCodeMapper.selectSysActivationCodeByValue(codeValue);
        if (code == null) {
            result.put("valid", false);
            result.put("message", "激活码不存在");
            return result;
        }

        // 检查状态
        if ("1".equals(code.getStatus())) {
            result.put("valid", false);
            result.put("message", "激活码已被使用");
            return result;
        }

        if ("3".equals(code.getStatus())) {
            result.put("valid", false);
            result.put("message", "激活码已被禁用");
            return result;
        }

        // 检查有效期
        if (code.getExpireTime() != null && code.getExpireTime().before(new Date())) {
            result.put("valid", false);
            result.put("message", "激活码已过期");
            // 更新状态为已过期
            code.setStatus("2");
            sysActivationCodeMapper.updateSysActivationCode(code);
            return result;
        }

        // 验证通过
        result.put("valid", true);
        result.put("message", "激活码有效");
        result.put("code", code);

        // 更新激活码状态为已使用
        code.setStatus("1");
        code.setBindDeviceId(deviceUuid);
        sysActivationCodeMapper.updateSysActivationCode(code);

        return result;
    }

    /**
     * 生成激活码值
     * @return 8 位大写字母 + 数字组合
     */
    private String generateCodeValue() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return code.toString();
    }
}
```

- [ ] **Step 3: 创建 ISysDeviceService 接口**

```java
package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysDevice;
import java.util.List;

/**
 * 设备 Service 接口
 */
public interface ISysDeviceService {

    /**
     * 查询设备
     * @param deviceId 设备主键
     * @return 设备
     */
    SysDevice selectSysDeviceById(Long deviceId);

    /**
     * 根据设备 UUID 查询
     * @param deviceUuid 设备 UUID
     * @return 设备
     */
    SysDevice selectSysDeviceByUuid(String deviceUuid);

    /**
     * 查询设备列表
     * @param sysDevice 设备
     * @return 设备集合
     */
    List<SysDevice> selectSysDeviceList(SysDevice sysDevice);

    /**
     * 新增设备
     * @param sysDevice 设备
     * @return 结果
     */
    int insertSysDevice(SysDevice sysDevice);

    /**
     * 修改设备
     * @param sysDevice 设备
     * @return 结果
     */
    int updateSysDevice(SysDevice sysDevice);

    /**
     * 批量删除设备
     * @param deviceIds 需要删除的设备主键
     * @return 结果
     */
    int deleteSysDeviceByIds(Long[] deviceIds);

    /**
     * 删除设备
     * @param deviceId 设备主键
     * @return 结果
     */
    int deleteSysDeviceById(Long deviceId);

    /**
     * 设备解绑
     * @param deviceId 设备主键
     * @return 结果
     */
    int unbindDevice(Long deviceId);

    /**
     * 更新设备当前用户
     * @param deviceUuid 设备 UUID
     * @param currentUserId 当前用户 ID
     * @param currentUserName 当前用户名
     * @return 结果
     */
    int updateDeviceCurrentUser(String deviceUuid, Long currentUserId, String currentUserName);
}
```

- [ ] **Step 4: 创建 SysDeviceServiceImpl 实现类**

```java
package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.mapper.SysDeviceMapper;
import com.ruoyi.system.service.ISysDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备 Service 业务层处理
 */
@Service
public class SysDeviceServiceImpl implements ISysDeviceService {

    private static final Logger log = LoggerFactory.getLogger(SysDeviceServiceImpl.class);

    @Autowired
    private SysDeviceMapper sysDeviceMapper;

    /**
     * 查询设备
     * @param deviceId 设备主键
     * @return 设备
     */
    @Override
    public SysDevice selectSysDeviceById(Long deviceId) {
        return sysDeviceMapper.selectSysDeviceById(deviceId);
    }

    /**
     * 根据设备 UUID 查询
     * @param deviceUuid 设备 UUID
     * @return 设备
     */
    @Override
    public SysDevice selectSysDeviceByUuid(String deviceUuid) {
        return sysDeviceMapper.selectSysDeviceByUuid(deviceUuid);
    }

    /**
     * 查询设备列表
     * @param sysDevice 设备
     * @return 设备
     */
    @Override
    public List<SysDevice> selectSysDeviceList(SysDevice sysDevice) {
        return sysDeviceMapper.selectSysDeviceList(sysDevice);
    }

    /**
     * 新增设备
     * @param sysDevice 设备
     * @return 结果
     */
    @Override
    public int insertSysDevice(SysDevice sysDevice) {
        sysDevice.setCreateTime(DateUtils.getNowDate());
        return sysDeviceMapper.insertSysDevice(sysDevice);
    }

    /**
     * 修改设备
     * @param sysDevice 设备
     * @return 结果
     */
    @Override
    public int updateSysDevice(SysDevice sysDevice) {
        sysDevice.setUpdateTime(DateUtils.getNowDate());
        return sysDeviceMapper.updateSysDevice(sysDevice);
    }

    /**
     * 批量删除设备
     * @param deviceIds 需要删除的设备主键
     * @return 结果
     */
    @Override
    public int deleteSysDeviceByIds(Long[] deviceIds) {
        return sysDeviceMapper.deleteSysDeviceByIds(deviceIds);
    }

    /**
     * 删除设备
     * @param deviceId 设备主键
     * @return 结果
     */
    @Override
    public int deleteSysDeviceById(Long deviceId) {
        return sysDeviceMapper.deleteSysDeviceById(deviceId);
    }

    /**
     * 设备解绑
     * @param deviceId 设备主键
     * @return 结果
     */
    @Override
    public int unbindDevice(Long deviceId) {
        SysDevice device = sysDeviceMapper.selectSysDeviceById(deviceId);
        if (device == null) {
            throw new ServiceException("设备不存在");
        }

        // 清空当前用户信息
        device.setCurrentUserId(null);
        device.setCurrentUserName(null);
        device.setStatus("0"); // 设置为离线
        device.setUpdateTime(DateUtils.getNowDate());

        return sysDeviceMapper.updateSysDevice(device);
    }

    /**
     * 更新设备当前用户
     * @param deviceUuid 设备 UUID
     * @param currentUserId 当前用户 ID
     * @param currentUserName 当前用户名
     * @return 结果
     */
    @Override
    public int updateDeviceCurrentUser(String deviceUuid, Long currentUserId, String currentUserName) {
        return sysDeviceMapper.updateDeviceCurrentUser(deviceUuid, currentUserId, currentUserName);
    }
}
```

- [ ] **Step 5: 提交**

```bash
cd RuoYi-Vue
git add ruoyi-system/src/main/java/com/ruoyi/system/service/*.java
git add ruoyi-system/src/main/java/com/ruoyi/system/service/impl/*.java
git commit -m "feat(service): 新增激活码和设备 Service 接口及实现"
```

---

### Task 5: 实现 Controller 接口

**Files:**
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/device/SysActivationCodeController.java`
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/device/SysDeviceController.java`

- [ ] **Step 1: 创建 SysActivationCodeController**

```java
package com.ruoyi.web.controller.device;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysActivationCode;
import com.ruoyi.system.service.ISysActivationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 激活码管理
 */
@RestController
@RequestMapping("/device/activationCode")
public class SysActivationCodeController extends BaseController {

    @Autowired
    private ISysActivationCodeService sysActivationCodeService;

    /**
     * 查询激活码列表
     */
    @PreAuthorize("@ss.hasPermi('device:activationCode:list')")
    @PostMapping("/list")
    public TableDataInfo list(SysActivationCode sysActivationCode) {
        startPage();
        List<SysActivationCode> list = sysActivationCodeService.selectSysActivationCodeList(sysActivationCode);
        return getDataTable(list);
    }

    /**
     * 查询激活码详情
     */
    @PreAuthorize("@ss.hasPermi('device:activationCode:query')")
    @GetMapping("/{codeId}")
    public AjaxResult getInfo(@PathVariable("codeId") Long codeId) {
        return success(sysActivationCodeService.selectSysActivationCodeById(codeId));
    }

    /**
     * 生成激活码
     */
    @PreAuthorize("@ss.hasPermi('device:activationCode:add')")
    @Log(title = "生成激活码", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> params) {
        Integer count = (Integer) params.get("count");
        Integer expireDays = (Integer) params.get("expireDays");
        String remark = (String) params.get("remark");

        if (count == null || count <= 0 || count > 50) {
            return error("生成数量必须在 1-50 之间");
        }
        if (expireDays == null || expireDays <= 0) {
            expireDays = 30; // 默认 30 天
        }

        List<SysActivationCode> codes = sysActivationCodeService.batchGenerateCodes(
                count, expireDays, remark, getUsername());

        // 返回生成的激活码列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (SysActivationCode code : codes) {
            Map<String, Object> result = new HashMap<>();
            result.put("codeValue", code.getCodeValue());
            result.put("expireTime", code.getExpireTime());
            resultList.add(result);
        }

        return success(resultList);
    }

    /**
     * 修改激活码
     */
    @PreAuthorize("@ss.hasPermi('device:activationCode:edit')")
    @Log(title = "激活码管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysActivationCode sysActivationCode) {
        sysActivationCode.setUpdateTime(DateUtils.getNowDate());
        return toAjax(sysActivationCodeService.updateSysActivationCode(sysActivationCode));
    }

    /**
     * 删除激活码
     */
    @PreAuthorize("@ss.hasPermi('device:activationCode:remove')")
    @Log(title = "激活码管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{codeIds}")
    public AjaxResult remove(@PathVariable Long[] codeIds) {
        return toAjax(sysActivationCodeService.deleteSysActivationCodeByIds(codeIds));
    }

    /**
     * 验证激活码（App 调用）
     */
    @Log(title = "验证激活码", businessType = BusinessType.OTHER)
    @PostMapping("/validate")
    public AjaxResult validate(@RequestBody Map<String, String> params) {
        String codeValue = params.get("codeValue");
        String deviceUuid = params.get("deviceUuid");

        if (StringUtils.isEmpty(codeValue) || StringUtils.isEmpty(deviceUuid)) {
            return error("参数不能为空");
        }

        Map<String, Object> result = sysActivationCodeService.validateCode(codeValue, deviceUuid);
        return success(result);
    }
}
```

- [ ] **Step 2: 创建 SysDeviceController**

```java
package com.ruoyi.web.controller.device;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.service.ISysDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备管理
 */
@RestController
@RequestMapping("/device/device")
public class SysDeviceController extends BaseController {

    @Autowired
    private ISysDeviceService sysDeviceService;

    /**
     * 查询设备列表
     */
    @PreAuthorize("@ss.hasPermi('device:device:list')")
    @PostMapping("/list")
    public TableDataInfo list(SysDevice sysDevice) {
        startPage();
        List<SysDevice> list = sysDeviceService.selectSysDeviceList(sysDevice);
        return getDataTable(list);
    }

    /**
     * 查询设备详情
     */
    @PreAuthorize("@ss.hasPermi('device:device:query')")
    @GetMapping("/{deviceId}")
    public AjaxResult getInfo(@PathVariable("deviceId") Long deviceId) {
        return success(sysDeviceService.selectSysDeviceById(deviceId));
    }

    /**
     * 设备解绑
     */
    @PreAuthorize("@ss.hasPermi('device:device:unbind')")
    @Log(title = "设备解绑", businessType = BusinessType.UPDATE)
    @PutMapping("/unbind")
    public AjaxResult unbind(@RequestBody Map<String, Long> params) {
        Long deviceId = params.get("deviceId");
        if (deviceId == null) {
            return error("设备 ID 不能为空");
        }
        return toAjax(sysDeviceService.unbindDevice(deviceId));
    }

    /**
     * 远程清除设备数据
     */
    @PreAuthorize("@ss.hasPermi('device:device:clearData')")
    @Log(title = "清除设备数据", businessType = BusinessType.CLEAN)
    @PostMapping("/clearData")
    public AjaxResult clearData(@RequestBody Map<String, Object> params) {
        Long deviceId = (Long) params.get("deviceId");
        String reason = (String) params.get("reason");

        if (deviceId == null) {
            return error("设备 ID 不能为空");
        }

        // TODO: 实现远程清除逻辑（推送消息到设备）
        log.info("请求清除设备数据，设备 ID: {}, 原因：{}", deviceId, reason);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "清除指令已发送，设备将在下次同步时清除数据");

        return success(result);
    }
}
```

- [ ] **Step 3: 提交**

```bash
cd RuoYi-Vue
git add ruoyi-admin/src/main/java/com/ruoyi/web/controller/device/*.java
git commit -m "feat(controller): 新增激活码和设备 Controller 接口"
```

---

## 第二部分：后端管理页面（阶段二）

### Task 6: 创建 Vue 页面组件

**Files:**
- Create: `ruoyi-ui/src/views/device/activation-code/index.vue`
- Create: `ruoyi-ui/src/views/device/activation-code/CreateDialog.vue`
- Create: `ruoyi-ui/src/views/device/device/index.vue`
- Create: `ruoyi-ui/src/components/CountdownTimer.vue`

- [ ] **Step 1: 创建激活码列表页面**

```vue
<template>
  <div class="app-container">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="激活码">
          <el-input
            v-model="queryParams.codeValue"
            placeholder="请输入激活码"
            clearable
            style="width: 200px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="未使用" value="0" />
            <el-option label="已使用" value="1" />
            <el-option label="已过期" value="2" />
            <el-option label="已禁用" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <el-row :gutter="10" style="margin-bottom: 10px">
        <el-col :span="1.5">
          <el-button
            type="primary"
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['device:activationCode:add']"
          >
            生成激活码
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['device:activationCode:remove']"
          >
            删除
          </el-button>
        </el-col>
      </el-row>

      <!-- 列表 -->
      <el-table
        v-loading="loading"
        :data="codeList"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="激活码" prop="codeValue" width="120" :show-overflow-tooltip="true">
          <template #default="{ row }">
            <el-tag size="small" effect="plain" copyable>
              {{ row.codeValue }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="绑定用户" prop="currentUserName" width="150" :show-overflow-tooltip="true" />
        <el-table-column label="有效期" width="200">
          <template #default="{ row }">
            <countdown-timer v-if="row.expireTime" :time="row.expireTime" />
            <span v-else>永久有效</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" :show-overflow-tooltip="true" />
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              icon="CopyDocument"
              @click="handleCopy(row)"
              v-hasPermi="['device:activationCode:query']"
            >
              复制
            </el-button>
            <el-button
              link
              type="primary"
              icon="View"
              @click="handleDetail(row)"
              v-hasPermi="['device:activationCode:query']"
            >
              详情
            </el-button>
            <el-button
              link
              type="danger"
              icon="Delete"
              @click="handleDelete(row)"
              v-hasPermi="['device:activationCode:remove']"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 生成激活码对话框 -->
    <create-dialog v-model="visible" @success="getList" />

    <!-- 激活码详情对话框 -->
    <el-dialog v-model="detailVisible" title="激活码详情" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="激活码">{{ detail.codeValue }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detail.status)">
            {{ getStatusText(detail.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="绑定设备">{{ detail.bindDeviceId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="绑定用户">{{ detail.currentUserName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="有效期">
          <countdown-timer v-if="detail.expireTime" :time="detail.expireTime" />
          <span v-else>永久有效</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ detail.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="activationCode">
import { list, add, del, getInfo } from "@/api/device/activationCode";
import CreateDialog from "./CreateDialog";
import CountdownTimer from "@/components/CountdownTimer";

const { proxy } = getCurrentInstance();

const codeList = ref([]);
const loading = ref(true);
const total = ref(0);
const multiple = ref(true);
const ids = ref([]);
const visible = ref(false);
const detailVisible = ref(false);
const detail = ref({});

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  codeValue: undefined,
  status: undefined
});

// 查询
function getList() {
  loading.value = true;
  list(queryParams.value).then(response => {
    codeList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 搜索
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

// 重置
function resetQuery() {
  proxy.resetForm("queryParams");
  handleQuery();
}

// 多选
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.codeId);
  multiple.value = !selection.length;
}

// 生成激活码
function handleAdd() {
  visible.value = true;
}

// 复制
function handleCopy(row) {
  navigator.clipboard.writeText(row.codeValue);
  proxy.$modal.msgSuccess("激活码已复制到剪贴板");
}

// 详情
function handleDetail(row) {
  getInfo(row.codeId).then(response => {
    detail.value = response.data;
    detailVisible.value = true;
  });
}

// 删除
function handleDelete(row) {
  const codeIds = row.codeId || ids.value;
  proxy.$modal.confirm('是否确认删除激活码编号为"' + codeIds + '"的数据项？').then(function() {
    return del(codeIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

// 状态文本
function getStatusText(status) {
  const map = { '0': '未使用', '1': '已使用', '2': '已过期', '3': '已禁用' };
  return map[status] || '未知';
}

// 状态类型
function getStatusType(status) {
  const map = { '0': 'success', '1': 'info', '2': 'warning', '3': 'danger' };
  return map[status] || '';
}

getList();
</script>
```

- [ ] **Step 2: 创建生成激活码对话框**

```vue
<template>
  <el-dialog v-model="visible" title="生成激活码" width="500px" @close="reset">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="数量" prop="count">
        <el-input-number v-model="form.count" :min="1" :max="50" style="width: 200px" />
        <span class="form-tip">单次最多生成 50 个</span>
      </el-form-item>
      <el-form-item label="有效期" prop="expireDays">
        <el-input-number v-model="form.expireDays" :min="1" :max="365" style="width: 200px" />
        <span class="form-tip">单位：天（默认 30 天）</span>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="选填" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">生成</el-button>
    </template>

    <!-- 生成结果对话框 -->
    <el-dialog v-model="resultVisible" title="生成的激活码" width="500px" append-to-body>
      <el-table :data="generatedCodes" size="small">
        <el-table-column label="激活码" prop="codeValue" />
        <el-table-column label="有效期" prop="expireTime" width="180" />
        <el-table-column label="操作" width="80" fixed="right">
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
  </el-dialog>
</template>

<script setup>
import { add } from "@/api/device/activationCode";

const props = defineProps({
  modelValue: Boolean
});

const emit = defineEmits(['update:modelValue', 'success']);

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
});

const formRef = ref(null);
const loading = ref(false);
const resultVisible = ref(false);
const generatedCodes = ref([]);

const form = ref({
  count: 1,
  expireDays: 30,
  remark: ''
});

const rules = {
  count: [{ required: true, message: '请输入生成数量', trigger: 'blur' }],
  expireDays: [{ required: true, message: '请输入有效期', trigger: 'blur' }]
};

// 提交
function handleSubmit() {
  formRef.value.validate(valid => {
    if (valid) {
      loading.value = true;
      add(form.value).then(response => {
        generatedCodes.value = response.data;
        resultVisible.value = true;
        loading.value = false;
        emit('success');
      }).catch(() => {
        loading.value = false;
      });
    }
  });
}

// 复制单个
function copyCode(code) {
  navigator.clipboard.writeText(code);
  proxy.$modal.msgSuccess("已复制");
}

// 全部复制
function copyAll() {
  const allCodes = generatedCodes.value.map(item => item.codeValue).join('\n');
  navigator.clipboard.writeText(allCodes);
  proxy.$modal.msgSuccess("全部已复制");
}

// 重置
function reset() {
  form.value = { count: 1, expireDays: 30, remark: '' };
  generatedCodes.value = [];
  resultVisible.value = false;
}
</script>

<style scoped>
.form-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #999;
}
</style>
```

- [ ] **Step 3: 创建设备列表页面**

```vue
<template>
  <div class="app-container">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :model="queryParams" :inline="true">
        <el-form-item label="设备 UUID">
          <el-input
            v-model="queryParams.deviceUuid"
            placeholder="请输入设备 UUID"
            clearable
            style="width: 200px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="离线" value="0" />
            <el-option label="在线" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <el-row :gutter="10" style="margin-bottom: 10px">
        <el-col :span="1.5">
          <el-button
            type="danger"
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['device:device:remove']"
          >
            删除
          </el-button>
        </el-col>
      </el-row>

      <!-- 列表 -->
      <el-table
        v-loading="loading"
        :data="deviceList"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="设备名称" prop="deviceName" :show-overflow-tooltip="true" />
        <el-table-column label="设备型号" prop="deviceModel" :show-overflow-tooltip="true" />
        <el-table-column label="操作系统" prop="deviceOs" width="100" />
        <el-table-column label="App 版本" prop="appVersion" width="100" />
        <el-table-column label="当前用户" prop="currentUserName" width="150" :show-overflow-tooltip="true" />
        <el-table-column label="最后同步时间" prop="lastSyncTime" width="180" />
        <el-table-column label="状态" prop="status" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : 'info'">
              {{ row.status === '1' ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              icon="View"
              @click="handleDetail(row)"
              v-hasPermi="['device:device:query']"
            >
              详情
            </el-button>
            <el-button
              link
              type="warning"
              icon="Unlock"
              @click="handleUnbind(row)"
              v-hasPermi="['device:device:unbind']"
            >
              解绑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        v-model:total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script setup name="device">
import { list, del, unbind } from "@/api/device/device";

const { proxy } = getCurrentInstance();

const deviceList = ref([]);
const loading = ref(true);
const total = ref(0);
const multiple = ref(true);
const ids = ref([]);

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  deviceUuid: undefined,
  status: undefined
});

// 查询
function getList() {
  loading.value = true;
  list(queryParams.value).then(response => {
    deviceList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 搜索
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

// 重置
function resetQuery() {
  proxy.resetForm("queryParams");
  handleQuery();
}

// 多选
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.deviceId);
  multiple.value = !selection.length;
}

// 删除
function handleDelete(row) {
  const deviceIds = row.deviceId || ids.value;
  proxy.$modal.confirm('是否确认删除设备编号为"' + deviceIds + '"的数据项？').then(function() {
    return del(deviceIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

// 解绑
function handleUnbind(row) {
  proxy.$modal.confirm('是否确认解绑设备"' + row.deviceName + '"？').then(function() {
    return unbind({ deviceId: row.deviceId });
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("解绑成功");
  }).catch(() => {});
}

// 详情
function handleDetail(row) {
  proxy.$modal.msg('功能开发中');
}

getList();
</script>
```

- [ ] **Step 4: 创建倒计时组件**

```vue
<template>
  <span :class="className" :style="{ color: timeColor }">
    {{ displayText }}
  </span>
</template>

<script setup>
const props = defineProps({
  time: {
    type: [String, Number],
    required: true
  },
  className: {
    type: String,
    default: ''
  }
});

const displayText = ref('');
const timeColor = ref('#67c23a');

// 计算剩余时间
function updateDisplay() {
  const targetTime = new Date(props.time).getTime();
  const now = Date.now();
  const diff = targetTime - now;

  if (diff <= 0) {
    displayText.value = '已过期';
    timeColor.value = '#f56c6c';
    return;
  }

  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));

  displayText.value = `剩余 ${days}天 ${hours}小时 ${minutes}分`;

  // 根据剩余时间改变颜色
  if (days < 3) {
    timeColor.value = '#f56c6c'; // 红色警告
  } else if (days < 7) {
    timeColor.value = '#e6a23c'; // 橙色警告
  } else {
    timeColor.value = '#67c23a'; // 绿色正常
  }
}

onMounted(() => {
  updateDisplay();
  // 每分钟更新一次
  const timer = setInterval(updateDisplay, 60000);
  onUnmounted(() => clearInterval(timer));
});
</script>
```

- [ ] **Step 5: 创建 API 文件**

创建 `ruoyi-ui/src/api/device/activationCode.js`:

```javascript
import request from '@/utils/request'

// 查询激活码列表
export function list(query) {
  return request({
    url: '/device/activationCode/list',
    method: 'post',
    params: query
  })
}

// 查询激活码详情
export function getInfo(codeId) {
  return request({
    url: '/device/activationCode/' + codeId,
    method: 'get'
  })
}

// 生成激活码
export function add(data) {
  return request({
    url: '/device/activationCode',
    method: 'post',
    data: data
  })
}

// 修改激活码
export function edit(data) {
  return request({
    url: '/device/activationCode',
    method: 'put',
    data: data
  })
}

// 删除激活码
export function del(codeIds) {
  return request({
    url: '/device/activationCode/' + codeIds,
    method: 'delete'
  })
}

// 验证激活码
export function validate(data) {
  return request({
    url: '/device/activationCode/validate',
    method: 'post',
    data: data
  })
}
```

创建 `ruoyi-ui/src/api/device/device.js`:

```javascript
import request from '@/utils/request'

// 查询设备列表
export function list(query) {
  return request({
    url: '/device/device/list',
    method: 'post',
    params: query
  })
}

// 查询设备详情
export function getInfo(deviceId) {
  return request({
    url: '/device/device/' + deviceId,
    method: 'get'
  })
}

// 设备解绑
export function unbind(data) {
  return request({
    url: '/device/device/unbind',
    method: 'put',
    data: data
  })
}

// 删除设备
export function del(deviceIds) {
  return request({
    url: '/device/device/' + deviceIds,
    method: 'delete'
  })
}

// 远程清除设备数据
export function clearData(data) {
  return request({
    url: '/device/device/clearData',
    method: 'post',
    data: data
  })
}
```

- [ ] **Step 6: 提交**

```bash
cd RuoYi-Vue/ruoyi-ui
git add src/views/device/ src/components/CountdownTimer.vue src/api/device/
git commit -m "feat(ui): 新增激活码和设备管理页面"
```

---

## 第三部分：Android 端（阶段三、四、五）

> 由于 Android 端代码量较大，以下为任务框架，实际实施时每个 Task 需要展开为详细步骤。

### Task 7: Android Room 数据库集成

**Files:**
- Create: `Ruoyi-Android-App/app/build.gradle` (modify - add Room dependencies)
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/*.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/*.kt`

- [ ] **Step 1: 添加 Room 依赖到 build.gradle**

```gradle
dependencies {
    // ... existing dependencies ...

    // Room
    implementation "androidx.room:room-runtime:2.6.1"
    implementation "androidx.room:room-ktx:2.6.1"
    annotationProcessor "androidx.room:room-compiler:2.6.1"
    kapt "androidx.room:room-compiler:2.6.1"

    // bcrypt
    implementation "org.mindrot:jbcrypt:0.4"
}
```

- [ ] **Step 2: 创建数据库实体和 DAO**

（参考设计文档中的表结构创建对应的 Entity 和 DAO）

- [ ] **Step 3: 提交**

```bash
cd Ruoyi-Android-App
git add app/src/main/java/com/ruoyi/app/data/database/
git add app/build.gradle
git commit -m "feat(android): 集成 Room 数据库"
```

---

### Task 8: Android 激活功能

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/ActivationActivity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/api/service/ActivationService.kt`

- [ ] **Step 1: 创建激活页面 UI**
- [ ] **Step 2: 实现激活码验证 API 调用**
- [ ] **Step 3: 实现设备 ID 生成与存储**
- [ ] **Step 4: 提交**

---

### Task 9: Android 登录改造

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/activity/LoginActivity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/utils/PasswordUtils.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/fragment/SyncProgressFragment.kt`

- [ ] **Step 1: 实现本地 bcrypt 密码验证**
- [ ] **Step 2: 改造登录页面支持离线登录**
- [ ] **Step 3: 创建同步进度条对话框**
- [ ] **Step 4: 提交**

---

### Task 10: Android 数据同步功能

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/sync/SyncService.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/sync/SyncManager.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/api/service/SyncService.kt`

- [ ] **Step 1: 实现全量/增量同步逻辑**
- [ ] **Step 2: 实现后台同步服务**
- [ ] **Step 3: 实现同步队列管理**
- [ ] **Step 4: 提交**

---

## 验证

### 后端测试
```bash
cd RuoYi-Vue
# 执行 SQL 脚本初始化数据库
mysql -u root -p ruoyi < sql/device_auth_init.sql
mysql -u root -p ruoyi < sql/device_auth_data.sql

# 启动后端
mvn spring-boot:run -pl ruoyi-admin

# 测试激活码生成 API
curl -X POST http://localhost:8080/device/activationCode \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"count": 5, "expireDays": 30}'
```

### Android 测试
```bash
cd Ruoyi-Android-App
./gradlew assembleDebug
# 安装到模拟器/真机测试
adb install -r app/build/outputs/apk/debug/ruoyi-android-mobile.apk
```

---

## 验收标准

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
