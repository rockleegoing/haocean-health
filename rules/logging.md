# 日志规范

## 日志框架配置

### 后端日志 (Logback + SLF4J)

项目使用 Spring Boot 默认的 Logback 日志框架，通过 SLF4J 接口编程。

#### logback.xml 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 日志输出格式 -->
    <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>

    <!-- 应用名称 -->
    <property name="APP_NAME" value="ruoyi-admin"/>

    <!-- 日志存储路径 -->
    <property name="LOG_PATH" value="/data/ruoyi/logs"/>

    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- INFO 级别文件输出 -->
    <appender name="INFO_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/${APP_NAME}-info.log</file>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/${APP_NAME}-info.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>2GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- ERROR 级别文件输出 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/${APP_NAME}-error.log</file>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/${APP_NAME}-error.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>90</maxHistory>
            <totalSizeCap>5GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 异步日志（可选，提升性能） -->
    <appender name="ASYNC_INFO" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="INFO_FILE"/>
        <queueSize>512</queueSize>
        <discardingThreshold>0</discardingThreshold>
    </appender>

    <appender name="ASYNC_ERROR" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="ERROR_FILE"/>
        <queueSize>512</queueSize>
        <discardingThreshold>0</discardingThreshold>
    </appender>

    <!-- 开发环境配置 -->
    <springProfile name="dev">
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
        </root>
        <logger name="com.ruoyi" level="DEBUG"/>
    </springProfile>

    <!-- 生产环境配置 -->
    <springProfile name="prod">
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
            <appender-ref ref="INFO_FILE"/>
            <appender-ref ref="ERROR_FILE"/>
        </root>
        <logger name="com.ruoyi" level="INFO"/>
    </springProfile>
</configuration>
```

---

## 日志级别使用规范

### 日志级别定义

| 级别 | 使用场景 | 示例 |
|------|---------|------|
| `ERROR` | 系统错误，需要立即处理 | 数据库连接失败、空指针异常 |
| `WARN` | 警告信息，不影响系统运行 | 参数校验失败、降级处理 |
| `INFO` | 关键业务流程、系统状态 | 用户登录、订单创建、定时任务执行 |
| `DEBUG` | 调试信息，开发环境使用 | 方法入参出参、SQL 执行详情 |
| `TRACE` | 详细追踪信息 | 循环迭代、条件判断细节 |

### 各级别使用示例

```java
@Service
public class UserServiceImpl implements ISysUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public SysUser selectUserById(Long userId) {
        // DEBUG: 方法入参
        log.debug("查询用户信息，userId = {}", userId);

        if (userId == null || userId <= 0) {
            // WARN: 参数不合法
            log.warn("用户 ID 不合法：userId = {}", userId);
            return null;
        }

        try {
            SysUser user = userMapper.selectById(userId);
            if (user == null) {
                // INFO: 查询结果
                log.info("用户不存在：userId = {}", userId);
                return null;
            }

            // DEBUG: 方法出参
            log.debug("查询用户成功：userId = {}, userName = {}", userId, user.getUserName());
            return user;

        } catch (DataAccessException e) {
            // ERROR: 数据库异常，需要处理
            log.error("查询用户失败，数据库异常：userId = {}", userId, e);
            throw new ServiceException("查询用户失败");
        } catch (Exception e) {
            // ERROR: 未知异常
            log.error("查询用户失败，未知异常：userId = {}", userId, e);
            throw new ServiceException("系统内部错误");
        }
    }

    @Override
    @Transactional
    public int insertUser(SysUser user) {
        // INFO: 关键业务操作
        log.info("开始新增用户：userName = {}", user.getUserName());

        // 检查用户名是否存在
        if (checkUserNameUnique(user.getUserName())) {
            // WARN: 业务校验不通过
            log.warn("用户账号已存在：userName = {}", user.getUserName());
            throw new ServiceException("新增用户'" + user.getUserName() + "'失败，账号已存在");
        }

        try {
            int rows = userMapper.insertUser(user);
            // INFO: 业务操作成功
            log.info("新增用户成功：userId = {}, userName = {}", user.getUserId(), user.getUserName());
            return rows;

        } catch (DuplicateKeyException e) {
            // ERROR: 主键/唯一索引冲突
            log.error("新增用户失败，唯一键冲突：userName = {}", user.getUserName(), e);
            throw new ServiceException("数据库唯一约束冲突");
        } catch (DataIntegrityViolationException e) {
            // ERROR: 数据完整性 violation
            log.error("新增用户失败，数据完整性检查失败：userName = {}", user.getUserName(), e);
            throw new ServiceException("数据完整性检查失败");
        }
    }

    @Override
    public int updateUser(SysUser user) {
        log.info("开始修改用户：userId = {}", user.getUserId());

        // 检查用户是否存在
        SysUser existingUser = userMapper.selectById(user.getUserId());
        if (existingUser == null) {
            log.warn("用户不存在，无法修改：userId = {}", user.getUserId());
            throw new ServiceException("用户不存在");
        }

        // 检查用户名是否重复
        if (checkUserNameUnique(user.getUserName()) &&
            !user.getUserName().equals(existingUser.getUserName())) {
            log.warn("用户账号已存在：userName = {}", user.getUserName());
            throw new ServiceException("用户账号已存在");
        }

        try {
            int rows = userMapper.updateUser(user);
            log.info("修改用户成功：userId = {}, userName = {}", user.getUserId(), user.getUserName());
            return rows;
        } catch (Exception e) {
            log.error("修改用户失败：userId = {}", user.getUserId(), e);
            throw new ServiceException("修改用户失败");
        }
    }

    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds) {
        log.info("开始批量删除用户：userIds = {}", Arrays.toString(userIds));

        if (userIds == null || userIds.length == 0) {
            log.warn("批量删除用户 ID 为空");
            return 0;
        }

        try {
            for (Long userId : userIds) {
                SysUser user = userMapper.selectById(userId);
                if (user != null) {
                    log.info("删除用户：userId = {}, userName = {}", userId, user.getUserName());
                    userMapper.deleteById(userId);
                } else {
                    log.warn("用户不存在，跳过删除：userId = {}", userId);
                }
            }
            log.info("批量删除用户完成：共 {} 个用户", userIds.length);
            return userIds.length;
        } catch (Exception e) {
            log.error("批量删除用户失败：userIds = {}", Arrays.toString(userIds), e);
            throw new ServiceException("批量删除失败");
        }
    }
}
```

---

## 日志输出规范

### 1. 使用占位符，禁止字符串拼接

```java
// ✅ 推荐：使用占位符
log.info("用户登录成功：userName = {}", userName);
log.error("查询失败：userId = {}, error = {}", userId, e.getMessage(), e);

// ❌ 不推荐：字符串拼接
log.info("用户登录成功：userName = " + userName);
log.error("查询失败：userId = " + userId + ", error = " + e.getMessage(), e);
```

### 2. 异常日志必须打印堆栈

```java
// ✅ 推荐：打印完整堆栈
try {
    // ...
} catch (Exception e) {
    log.error("操作失败", e);  // e 作为最后一个参数
}

// ❌ 不推荐：只打印异常消息
try {
    // ...
} catch (Exception e) {
    log.error("操作失败：" + e.getMessage());  // 丢失堆栈信息
}
```

### 3. 避免在循环中打印大量日志

```java
// ✅ 推荐：汇总打印
List<String> errors = new ArrayList<>();
for (Item item : items) {
    if (!validate(item)) {
        errors.add(item.getId());
    }
}
if (!errors.isEmpty()) {
    log.warn("校验失败的商品 ID：{}", String.join(",", errors));
}

// ❌ 不推荐：循环打印
for (Item item : items) {
    if (!validate(item)) {
        log.warn("校验失败：itemId = {}", item.getId());
    }
}
```

### 4. 敏感信息脱敏

```java
// ✅ 推荐：敏感信息脱敏
log.info("用户登录：userName = {}, phone = {}", userName, desensitizePhone(phone));
log.info("密码修改：userName = {}, password = ******", userName);

// ❌ 不推荐：打印明文密码
log.info("用户登录：userName = {}, password = {}", userName, password);
```

### 5. 日志内容结构化

```java
// ✅ 推荐：结构化日志，便于日志分析
log.info("订单创建：orderId = {}, userId = {}, amount = {}, status = {}",
    orderId, userId, amount, status);

// ❌ 不推荐：非结构化日志
log.info("订单创建成功，订单 ID 为 " + orderId + "，用户 ID 为 " + userId);
```

---

## 日志文件管理

### 日志目录结构

```
/data/ruoyi/logs/
├── ruoyi-admin-info.log          # INFO 级别日志
├── ruoyi-admin-error.log         # ERROR 级别日志
├── ruoyi-admin-info.2024-01-01.log
├── ruoyi-admin-error.2024-01-01.log
└── ...
```

### 日志保留策略

| 日志类型 | 单文件大小 | 保留天数 | 总大小限制 |
|---------|-----------|---------|-----------|
| INFO | 100MB | 30 天 | 2GB |
| ERROR | 100MB | 90 天 | 5GB |
| DEBUG | 100MB | 7 天 | 1GB |

### 日志清理脚本

```bash
#!/bin/bash
# clean_logs.sh - 日志清理脚本

LOG_DIR="/data/ruoyi/logs"
RETENTION_DAYS=30

# 清理 30 天前的日志
find "$LOG_DIR" -name "*.log.*" -mtime +$RETENTION_DAYS -delete

# 压缩 7 天前的日志
find "$LOG_DIR" -name "*.log.*" -mtime +7 -name "*.log" -exec gzip {} \;

echo "$(date): 日志清理完成" >> "$LOG_DIR/clean.log"
```

---

## 业务日志规范

### 使用 @Log 注解记录操作日志

```java
/**
 * 用户管理 - 操作日志记录
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    /**
     * 新增用户 - 记录操作日志
     */
    @PostMapping
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    public AjaxResult add(@RequestBody SysUser user) {
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户 - 记录操作日志
     */
    @PutMapping
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public AjaxResult edit(@RequestBody SysUser user) {
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户 - 记录操作日志
     */
    @DeleteMapping("/{userIds}")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 导入用户 - 记录操作日志
     */
    @PostMapping("/importData")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    public AjaxResult importData(@RequestParam("file") MultipartFile file) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String message = userService.importUser(userList);
        return success(message);
    }
}
```

### 登录日志

```java
/**
 * 登录日志记录
 */
@Service
public class SysLoginService {

    @Autowired
    private SysLogininforMapper logininforMapper;

    public void recordLoginInfo(String username, String status, String message) {
        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setUserName(username);
        logininfor.setIpaddr(ServletUtils.getClientIP());
        logininfor.setLoginLocation(AddressUtils.getRealAddressByIP(logininfor.getIpaddr()));
        logininfor.setBrowser(ServletUtils.getBrowser());
        logininfor.setOs(ServletUtils.getOS());
        logininfor.setLoginTime(new Date());
        logininfor.setStatus(status);  // "0" 成功，"1" 失败
        logininfor.setMsg(message);
        logininforMapper.insertLogininfor(logininfor);

        // 同时输出应用日志
        if ("0".equals(status)) {
            log.info("用户登录成功：userName = {}, ip = {}", username, logininfor.getIpaddr());
        } else {
            log.warn("用户登录失败：userName = {}, reason = {}, ip = {}", username, message, logininfor.getIpaddr());
        }
    }
}
```

---

## 前端日志规范

### 日志工具类

```javascript
// src/utils/logger.js

const LogLevel = {
  DEBUG: 0,
  INFO: 1,
  WARN: 2,
  ERROR: 3
}

const CURRENT_LEVEL = process.env.NODE_ENV === 'development' ? LogLevel.DEBUG : LogLevel.INFO

const logger = {
  debug(module, message, ...args) {
    if (CURRENT_LEVEL <= LogLevel.DEBUG) {
      console.debug(`[${new Date().toISOString()}] [DEBUG] [${module}] ${message}`, ...args)
    }
  },

  info(module, message, ...args) {
    if (CURRENT_LEVEL <= LogLevel.INFO) {
      console.info(`[${new Date().toISOString()}] [INFO] [${module}] ${message}`, ...args)
    }
  },

  warn(module, message, ...args) {
    if (CURRENT_LEVEL <= LogLevel.WARN) {
      console.warn(`[${new Date().toISOString()}] [WARN] [${module}] ${message}`, ...args)
    }
  },

  error(module, message, error, ...args) {
    if (CURRENT_LEVEL <= LogLevel.ERROR) {
      console.error(`[${new Date().toISOString()}] [ERROR] [${module}] ${message}`, error, ...args)
    }
  }
}

export default logger
```

### 使用示例

```javascript
// src/views/system/user/index.vue

import logger from '@/utils/logger'

export default {
  methods: {
    /** 查询用户列表 */
    getList() {
      logger.info('User', '开始查询用户列表', this.queryParams)
      listUser(this.queryParams).then(response => {
        logger.debug('User', '用户列表查询完成', response)
        this.userList = response.rows
        this.total = response.total
      }).catch(error => {
        logger.error('User', '用户列表查询失败', error)
        this.$modal.msgError('查询用户列表失败')
      })
    },

    /** 新增用户 */
    handleAdd() {
      logger.info('User', '打开新增用户对话框')
      this.reset()
      this.open = true
      this.title = '添加用户'
    },

    /** 提交表单 */
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          logger.info('User', '提交用户表单', this.form)
          addUser(this.form).then(response => {
            logger.info('User', '用户新增成功')
            this.$modal.msgSuccess('新增成功')
            this.close()
            this.getList()
          }).catch(error => {
            logger.error('User', '用户新增失败', error)
            this.$modal.msgError('新增失败')
          })
        } else {
          logger.warn('User', '表单校验失败')
        }
      })
    }
  }
}
```

---

## Android 日志规范

### 日志工具类

```kotlin
// app/utils/Logger.kt
package com.ruoyi.utils

import android.util.Log
import com.ruoyi.BuildConfig

object Logger {
    private const val TAG_PREFIX = "RuoYi-"

    fun d(tag: String, message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            val fullTag = getTag(tag)
            Log.d(fullTag, message, throwable)
        }
    }

    fun i(tag: String, message: String, throwable: Throwable? = null) {
        val fullTag = getTag(tag)
        Log.i(fullTag, message, throwable)
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        val fullTag = getTag(tag)
        Log.w(fullTag, message, throwable)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        val fullTag = getTag(tag)
        Log.e(fullTag, message, throwable)
    }

    private fun getTag(tag: String): String {
        return "$TAG_PREFIX$tag"
    }
}

// 扩展函数
inline fun <reified T> T.logger(): LoggerDelegate {
    return LoggerDelegate(T::class.java.simpleName)
}

class LoggerDelegate(private val tag: String) {
    fun d(message: String, throwable: Throwable? = null) = Logger.d(tag, message, throwable)
    fun i(message: String, throwable: Throwable? = null) = Logger.i(tag, message, throwable)
    fun w(message: String, throwable: Throwable? = null) = Logger.w(tag, message, throwable)
    fun e(message: String, throwable: Throwable? = null) = Logger.e(tag, message, throwable)
}
```

### 使用示例

```kotlin
// app/ui/activity/LoginActivity.kt

class LoginActivity : AppCompatActivity() {

    private val logger by lazy { logger<LoginActivity>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.i("onCreate - 登录页面初始化")
        setContentView(R.layout.activity_login)
    }

    private fun login(username: String, password: String) {
        logger.d("login - 开始登录，username = $username")

        if (username.isBlank() || password.isBlank()) {
            logger.w("login - 用户名或密码为空")
            ToastUtils.showShort("用户名或密码不能为空")
            return
        }

        lifecycleScope.launch {
            try {
                logger.d("login - 调用登录接口")
                val result = userRepository.login(username, password)
                logger.i("login - 登录成功，userId = ${result.id}")

                // 保存登录信息
                saveLoginInfo(result)

                // 跳转首页
                navigateToHome()

            } catch (e: ApiException) {
                logger.e("login - 登录失败，api 异常", e)
                ToastUtils.showShort(e.message ?: "登录失败")
            } catch (e: Exception) {
                logger.e("login - 登录失败，未知异常", e)
                ToastUtils.showShort("网络异常，请稍后重试")
            }
        }
    }
}
```

### 日志持久化规范

```kotlin
// app/utils/FileLogger.kt
package com.ruoyi.utils

import android.content.Context
import kotlinx.coroutines.*
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class FileLogger(private val context: Context) {

    private val logDir = File(context.externalCacheDir, "logs")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)
    private val fileDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
    }

    /**
     * 写入日志文件（仅 ERROR 级别）
     */
    fun writeToFile(level: String, tag: String, message: String, throwable: Throwable? = null) {
        if (level != "ERROR") return  // 仅持久化错误日志

        ioScope.launch {
            try {
                val logFile = getLogFile()
                val logEntry = buildString {
                    append("[${dateFormat.format(Date)}]")
                    append(" [$level]")
                    append(" [$tag]")
                    append(" $message")
                    if (throwable != null) {
                        append("\n${getStackTraceString(throwable)}")
                    }
                }

                FileWriter(logFile, true).use { writer ->
                    writer.appendLine(logEntry)
                }

                // 清理旧日志（保留 7 天）
                cleanupOldLogs()
            } catch (e: Exception) {
                Log.e("FileLogger", "写入日志文件失败", e)
            }
        }
    }

    private fun getLogFile(): File {
        val dateStr = fileDateFormat.format(Date())
        return File(logDir, "error-$dateStr.log")
    }

    private fun cleanupOldLogs() {
        val sevenDaysAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
        logDir.listFiles()?.forEach { file ->
            if (file.lastModified() < sevenDaysAgo) {
                file.delete()
            }
        }
    }

    private fun getStackTraceString(throwable: Throwable): String {
        return android.util.Log.getStackTraceString(throwable)
    }

    fun close() {
        ioScope.cancel()
    }
}
```

### 同步模块日志

```kotlin
// app/data/sync/SyncLogger.kt
package com.ruoyi.data.sync

import com.ruoyi.utils.Logger
import com.ruoyi.utils.logger

object SyncLogger {
    private val logger by lazy { logger<SyncLogger>() }

    fun i(message: String) {
        Logger.i("Sync", message)
    }

    fun d(message: String) {
        Logger.d("Sync", message)
    }

    fun w(message: String) {
        Logger.w("Sync", message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Logger.e("Sync", message, throwable)
    }

    fun logSyncStart() {
        i("=== 同步任务开始 ===")
    }

    fun logSyncEnd(durationMs: Long, successCount: Int, failCount: Int) {
        i("=== 同步任务结束：耗时 ${durationMs}ms, 成功 $successCount, 失败 $failCount ===")
    }

    fun logItemSync(type: String, tableName: String, id: Long, success: Boolean) {
        d("[$type] $tableName#$id - ${if (success) "成功" else "失败"}")
    }

    fun logConflict(tableName: String, id: Long, strategy: String) {
        w("检测到冲突：$tableName#$id, 解决策略：$strategy")
    }
}
```

### 蓝牙打印日志

```kotlin
// app/hardware/print/PrintLogger.kt
package com.ruoyi.hardware.print

import com.ruoyi.utils.Logger

object PrintLogger {
    private const val TAG = "BluetoothPrint"

    fun d(message: String) {
        Logger.d(TAG, message)
    }

    fun i(message: String) {
        Logger.i(TAG, message)
    }

    fun w(message: String) {
        Logger.w(TAG, message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        Logger.e(TAG, message, throwable)
    }

    fun logDeviceFound(address: String, name: String) {
        i("发现设备：$address ($name)")
    }

    fun logPrintStart(documentName: String) {
        i("开始打印：$documentName")
    }

    fun logPrintSuccess() {
        i("打印成功")
    }

    fun logPrintError(error: String) {
        e("打印失败：$error")
    }
}
```

### 日志级别使用场景

| 级别 | 使用场景 | 示例 |
|------|---------|------|
| `Log.v()` | 最详细信息 | 循环迭代、条件判断 |
| `Log.d()` | 调试信息 | 方法入参出参、API 请求响应 |
| `Log.i()` | 关键业务节点 | 页面加载完成、操作成功 |
| `Log.w()` | 警告但不影响运行 | 参数不合法、降级处理 |
| `Log.e()` | 错误，需要修复 | 网络异常、数据库异常、崩溃捕获 |

### 日志安全规范

```kotlin
// ❌ 禁止：打印敏感信息
Log.d("Login", "密码：$password")  // 明文密码
Log.d("User", "token: $token")    // 认证 token

// ✅ 推荐：敏感信息脱敏
Log.d("Login", "密码：******")
Log.d("User", "token: ${token?.take(10)}...")

// ✅ 推荐：敏感操作仅 DEBUG 模式输出
if (BuildConfig.DEBUG) {
    Log.d("Network", "请求头：$headers")
}
```

---

## 检查清单

### 后端日志检查

- [ ] 使用 SLF4J 接口，不使用具体日志实现
- [ ] 日志级别使用正确
- [ ] 异常日志打印完整堆栈
- [ ] 敏感信息已脱敏
- [ ] 日志格式统一（使用占位符）
- [ ] 日志文件滚动策略配置正确
- [ ] 生产环境日志级别合理

### 前端日志检查

- [ ] 使用统一的日志工具
- [ ] 生产环境屏蔽 DEBUG 日志
- [ ] 日志包含模块信息
- [ ] 错误日志包含完整错误信息

### Android 日志检查

- [ ] 使用统一日志工具类
- [ ] Debug 构建才输出 DEBUG 日志
- [ ] Tag 统一命名规范
- [ ] 敏感信息不输出到日志
