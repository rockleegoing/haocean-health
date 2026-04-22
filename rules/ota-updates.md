# OTA 在线升级管理规范

## 1. 概述

本规范定义卫生执法系统 Android App 的 OTA（Over-The-Air）在线升级功能。

### 1.1 升级类型

| 类型 | 说明 | 用户交互 |
|------|------|---------|
| **强制更新** | 必须更新才能继续使用 App | 不可取消，下载完成后强制安装 |
| **建议更新** | 推荐更新，但不影响使用 | 可取消，可稍后提醒 |
| **静默更新** | 后台自动下载并安装 | 无需用户干预 |

### 1.2 升级流程

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ 检查更新    │ →  │ 版本对比    │ →  │ 显示更新    │
│ App 启动/手动  │    │ 版本号比较   │    │ 弹窗提示    │
└─────────────┘    └─────────────┘    └─────────────┘
                          │
         ┌────────────────┼────────────────┐
         ▼                ▼                ▼
   ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
   │ 无需更新    │ │ 建议更新    │ │ 强制更新    │
   │ 提示最新版  │ │ 可取消      │ │ 不可取消    │
   └─────────────┘ └─────────────┘ └─────────────┘
                          │                │
                         └────────────────┘
                                │
                                ▼
                       ┌─────────────┐
                       │ 下载安装包  │
                       │ 后台下载    │
                       └─────────────┘
                                │
                                ▼
                       ┌─────────────┐
                       │ 校验完整性  │
                       │ MD5 校验      │
                       └─────────────┘
                                │
                                ▼
                       ┌─────────────┐
                       │ 安装更新    │
                       │ 引导安装    │
                       └─────────────┘
```

---

## 2. 版本管理

### 2.1 版本号规范

```
版本格式：MAJOR.MINOR.PATCH (语义化版本)

MAJOR（主版本号）：不兼容的 API 变更
MINOR（次版本号）：向后兼容的功能性新增
PATCH（修订号）：向后兼容的问题修复

示例：
1.0.0  - 初始版本
1.0.1  - Bug 修复
1.1.0  - 新增功能
2.0.0  - 重大变更
```

### 2.2 版本配置

**App 端版本信息**（build.gradle）：
```groovy
android {
    defaultConfig {
        applicationId "com.ruoyi.lawenforcement"
        versionCode 1        // 整数，用于版本对比
        versionName "1.0.0"  // 字符串，用于显示
        minSdkVersion 24     // 最低 Android 版本
        targetSdkVersion 33  // 目标 Android 版本
    }
}
```

**后端版本配置**：
```java
@Data
public class AppVersion {
    private Long versionId;
    private Integer versionCode;     // 1, 2, 3...
    private String versionName;      // "1.0.0"
    private String platform;         // "android"
    private String downloadUrl;      // APK 下载地址
    private String releaseNotes;     // 更新说明
    private Integer minSdk;          // 最低 SDK 要求
    private Integer isForce;         // 0=建议，1=强制
    private Date publishTime;        // 发布时间
    private Integer isActive;        // 0=禁用，1=启用
}
```

---

## 3. 数据库设计

### 3.1 版本信息表（后端）

```sql
-- 应用版本表
CREATE TABLE t_app_version (
    version_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    version_code    INT NOT NULL COMMENT '版本号（整数）',
    version_name    VARCHAR(32) NOT NULL COMMENT '版本名称',
    platform        VARCHAR(16) NOT NULL COMMENT '平台：android/web',
    download_url    VARCHAR(512) NOT NULL COMMENT '下载地址',
    file_size       BIGINT COMMENT '文件大小（字节）',
    md5_hash        VARCHAR(64) COMMENT 'MD5 校验值',
    release_notes   TEXT COMMENT '更新说明',
    min_sdk         INT COMMENT '最低 SDK 要求',
    is_force        TINYINT DEFAULT 0 COMMENT '是否强制更新：0=建议，1=强制',
    publish_time    DATETIME COMMENT '发布时间',
    publish_by      VARCHAR(64) COMMENT '发布人',
    is_active       TINYINT DEFAULT 1 COMMENT '是否启用：0=禁用，1=启用',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_platform_code (platform, version_code),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用版本表';
```

### 3.2 版本信息表（Android 本地）

```kotlin
@Entity(tableName = "t_local_version")
data class LocalVersion(
    @PrimaryKey
    val id: Long = 0,

    @ColumnInfo(name = "version_code")
    val versionCode: Int,           // 本地版本号

    @ColumnInfo(name = "version_name")
    val versionName: String,        // 本地版本名称

    @ColumnInfo(name = "last_check_time")
    val lastCheckTime: Long = 0,    // 上次检查时间

    @ColumnInfo(name = "download_progress")
    val downloadProgress: Int = 0,  // 下载进度（0-100）

    @ColumnInfo(name = "has_update")
    val hasUpdate: Boolean = false, // 是否有更新

    @ColumnInfo(name = "update_info")
    val updateInfo: String? = null  // 更新信息（JSON）
)
```

### 3.3 升级配置表

```sql
-- 升级配置表
CREATE TABLE t_upgrade_config (
    config_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key      VARCHAR(64) NOT NULL UNIQUE,
    config_value    VARCHAR(256) NOT NULL,
    config_desc     VARCHAR(256),
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='升级配置表';

-- 初始化配置
INSERT INTO t_upgrade_config (config_key, config_value, config_desc) VALUES
('min_version_code', '1', '最低允许版本号，低于此版本强制升级'),
('latest_version_code', '1', '最新版本号'),
('force_upgrade_enable', '1', '是否启用强制升级：0=禁用，1=启用'),
('upgrade_check_interval', '86400', '检查更新间隔（秒），默认 24 小时'),
('download_timeout', '300', '下载超时时间（秒），默认 5 分钟'),
('retry_max_times', '3', '下载失败最大重试次数');
```

---

## 4. API 接口

### 4.1 检查更新

```kotlin
interface UpdateApi {
    /**
     * 检查更新
     * @param currentVersionCode 当前版本号
     * @param platform 平台：android
     * @param deviceId 设备 ID
     */
    @GET("/api/app/update/check")
    suspend fun checkUpdate(
        @Query("currentVersionCode") currentVersionCode: Int,
        @Query("platform") platform: String = "android",
        @Query("deviceId") deviceId: String
    ): Response<UpdateCheckResponse>
}

// 响应数据
data class UpdateCheckResponse(
    val code: Int = 200,
    val message: String = "",
    val data: UpdateInfo? = null
)

data class UpdateInfo(
    val hasUpdate: Boolean,           // 是否有更新
    val versionCode: Int,             // 最新版本号
    val versionName: String,          // 最新版本名称
    val downloadUrl: String,          // 下载地址
    val fileSize: Long,               // 文件大小
    val md5Hash: String,              // MD5 校验值
    val releaseNotes: String,         // 更新说明
    val minSdk: Int?,                 // 最低 SDK 要求
    val isForce: Boolean,             // 是否强制更新
    val publishTime: String           // 发布时间
)
```

### 4.2 下载 APK

```kotlin
interface DownloadApi {
    /**
     * 下载 APK 文件
     */
    @Streaming
    @GET
    suspend fun downloadApk(
        @Url downloadUrl: String
    ): Response<ResponseBody>
}
```

### 4.3 上报升级结果

```kotlin
interface ReportApi {
    /**
     * 上报升级结果
     */
    @POST("/api/app/upgrade/report")
    suspend fun reportUpgrade(
        @Body request: UpgradeReportRequest
    ): Response<BaseResult<Unit>>
}

data class UpgradeReportRequest(
    val deviceId: String,             // 设备 ID
    val fromVersionCode: Int,         // 原版本号
    val toVersionCode: Int,           // 新版本号
    val upgradeResult: String,        // SUCCESS/FAILED
    val failedReason: String?,        // 失败原因
    val upgradeTime: Long             // 升级时间
)
```

---

## 5. Android 端实现

### 5.1 更新管理器

```kotlin
@AndroidEntryPoint
class OtaUpdateManager @Inject constructor(
    private val context: Context,
    private val updateApi: UpdateApi,
    private val downloadApi: DownloadApi,
    private val reportApi: ReportApi,
    private val localVersionDao: LocalVersionDao,
    private val preferences: EncryptedSharedPreferences
) {
    companion object {
        private const val TAG = "OtaUpdateManager"
        private const val DOWNLOAD_DIR = "app_update"
        private const val APK_FILE_NAME = "ruoyi_law_enforcement.apk"
    }

    private val downloadManager by lazy {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    // 检查更新
    suspend fun checkUpdate(showDialog: Boolean = true): UpdateInfo? {
        val currentVersionCode = getCurrentVersionCode()
        val deviceId = getDeviceId()

        return try {
            val response = updateApi.checkUpdate(
                currentVersionCode = currentVersionCode,
                platform = "android",
                deviceId = deviceId
            )

            if (response.isSuccessful && response.body()?.code == 200) {
                val updateInfo = response.body()?.data
                updateInfo?.let {
                    saveUpdateInfo(it)
                    if (showDialog && it.hasUpdate) {
                        showUpdateDialog(it)
                    }
                }
                updateInfo
            } else {
                Log.e(TAG, "检查更新失败：${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "检查更新异常", e)
            null
        }
    }

    // 下载更新
    suspend fun downloadUpdate(updateInfo: UpdateInfo): Flow<DownloadProgress> = flow {
        val downloadDir = getDownloadDir()
        val apkFile = File(downloadDir, APK_FILE_NAME)

        // 检查文件是否已存在
        if (apkFile.exists() && verifyMd5(apkFile, updateInfo.md5Hash)) {
            Log.i(TAG, "APK 已存在，跳过下载")
            emit(DownloadProgress.Complete(apkFile))
            return@flow
        }

        // 开始下载
        val request = DownloadManager.Request(
            Uri.parse(updateInfo.downloadUrl)
        )
            .setTitle("卫生执法系统")
            .setDescription("正在下载更新...")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setAllowedOverMetered(true)  // 允许使用移动网络
            .setAllowedOverRoaming(true)  // 允许漫游
            .setMimeType("application/vnd.android.package-archive")
            .setDestinationUri(Uri.fromFile(apkFile))

        val downloadId = downloadManager.enqueue(request)
        Log.i(TAG, "下载任务已启动，downloadId: $downloadId")

        // 轮询下载进度
        while (true) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                )
                val progress = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED)
                )
                val total = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                )

                when (status) {
                    DownloadManager.STATUS_RUNNING -> {
                        val percent = (progress * 100L / total).toInt()
                        emit(DownloadProgress.Progress(percent))
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        // 验证 MD5
                        if (verifyMd5(apkFile, updateInfo.md5Hash)) {
                            emit(DownloadProgress.Complete(apkFile))
                        } else {
                            emit(DownloadProgress.Error("MD5 校验失败"))
                        }
                        break
                    }
                    DownloadManager.STATUS_FAILED -> {
                        val reason = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)
                        )
                        emit(DownloadProgress.Error("下载失败：${getErrorMessage(reason)}"))
                        break
                    }
                }
            }
            cursor.close()
            delay(500)  // 每 500ms 轮询一次
        }
    }

    // 安装 APK
    fun installApk(apkFile: File) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            setDataAndType(
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    apkFile
                ),
                "application/vnd.android.package-archive"
            )
        }
        context.startActivity(intent)
    }

    // 上报升级结果
    suspend fun reportUpgradeResult(
        fromVersion: Int,
        toVersion: Int,
        success: Boolean,
        failedReason: String? = null
    ) {
        try {
            reportApi.reportUpgrade(
                UpgradeReportRequest(
                    deviceId = getDeviceId(),
                    fromVersionCode = fromVersion,
                    toVersionCode = toVersion,
                    upgradeResult = if (success) "SUCCESS" else "FAILED",
                    failedReason = failedReason,
                    upgradeTime = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "上报升级结果失败", e)
        }
    }

    // 获取当前版本号
    private fun getCurrentVersionCode(): Int {
        return context.packageManager
            .getPackageInfo(context.packageName, 0)
            .versionCode
    }

    // 获取设备 ID
    private fun getDeviceId(): String {
        return preferences.getString("device_id", "")
            ?: Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
    }

    // 获取下载目录
    private fun getDownloadDir(): File {
        val dir = File(context.getExternalFilesDir(null), DOWNLOAD_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    // 验证 MD5
    private fun verifyMd5(file: File, expectedMd5: String): Boolean {
        return try {
            val actualMd5 = file.calculateMd5()
            actualMd5.equals(expectedMd5, ignoreCase = true)
        } catch (e: Exception) {
            false
        }
    }

    // 显示更新对话框
    private fun showUpdateDialog(updateInfo: UpdateInfo) {
        // 使用 AlertDialog 或自定义 Dialog
        // 强制更新时不可取消
    }

    // 保存更新信息
    private fun saveUpdateInfo(updateInfo: UpdateInfo) {
        runBlocking {
            localVersionDao.update(
                LocalVersion(
                    id = 1,
                    versionCode = updateInfo.versionCode,
                    versionName = updateInfo.versionName,
                    lastCheckTime = System.currentTimeMillis(),
                    hasUpdate = updateInfo.hasUpdate,
                    updateInfo = Json.encodeToString(updateInfo)
                )
            )
        }
    }
}

// 下载进度密封类
sealed class DownloadProgress {
    data class Progress(val percent: Int) : DownloadProgress()
    data class Complete(val apkFile: File) : DownloadProgress()
    data class Error(val message: String) : DownloadProgress()
}
```

### 5.2 MD5 工具类

```kotlin
object FileUtil {
    fun File.calculateMd5(): String {
        val md = MessageDigest.getInstance("MD5")
        FileInputStream(this).use { fis ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                md.update(buffer, 0, bytesRead)
            }
        }
        return md.digest().joinToString("") { "%02x".format(it) }
    }
}
```

### 5.3 更新对话框

```kotlin
@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { if (!updateInfo.isForce) onDismiss() },
        title = { Text("发现新版本") },
        text = {
            Column {
                Text(
                    text = "v${updateInfo.versionName}",
                    style = MaterialTheme.typography.subtitle1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = updateInfo.releaseNotes,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
                if (updateInfo.fileSize > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "安装包大小：${formatFileSize(updateInfo.fileSize)}",
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDownload) {
                Text(if (updateInfo.isForce) "立即更新" else "下载更新")
            }
        },
        dismissButton = {
            if (!updateInfo.isForce) {
                TextButton(onClick = onDismiss) {
                    Text("稍后再说")
                }
            }
        },
        dismissOnBackPress = !updateInfo.isForce,
        modifier = modifier
    )
}

private fun formatFileSize(size: Long): String {
    return when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${size / 1024} KB"
        else -> String.format("%.2f MB", size / (1024.0 * 1024.0))
    }
}
```

---

## 6. 后端实现

### 6.1 版本检查服务

```java
@Service
public class AppUpdateService {

    @Autowired
    private AppVersionMapper appVersionMapper;

    /**
     * 检查更新
     */
    public UpdateInfo checkUpdate(Integer currentVersionCode, String platform) {
        // 获取最新版本
        AppVersion latestVersion = appVersionMapper.selectLatestVersion(platform);

        if (latestVersion == null || !latestVersion.getIsActive()) {
            return null;
        }

        boolean hasUpdate = latestVersion.getVersionCode() > currentVersionCode;

        if (!hasUpdate) {
            return null;
        }

        // 检查最低 SDK 要求
        if (latestVersion.getMinSdk() != null) {
            // 可选：根据设备 SDK 判断兼容性
        }

        UpdateInfo updateInfo = new UpdateInfo();
        updateInfo.setHasUpdate(hasUpdate);
        updateInfo.setVersionCode(latestVersion.getVersionCode());
        updateInfo.setVersionName(latestVersion.getVersionName());
        updateInfo.setDownloadUrl(latestVersion.getDownloadUrl());
        updateInfo.setFileSize(latestVersion.getFileSize());
        updateInfo.setMd5Hash(latestVersion.getMd5Hash());
        updateInfo.setReleaseNotes(latestVersion.getReleaseNotes());
        updateInfo.setMinSdk(latestVersion.getMinSdk());
        updateInfo.setIsForce(latestVersion.getIsForce() == 1);
        updateInfo.setPublishTime(DateUtils.formatDate(latestVersion.getPublishTime()));

        return updateInfo;
    }

    /**
     * 发布新版本
     */
    @Transactional
    public void publishVersion(AppVersionDTO versionDTO) {
        // 1. 上传 APK 文件
        String filePath = fileUploadService.uploadApk(versionDTO.getFile());

        // 2. 计算 MD5
        String md5Hash = FileUtil.calculateMd5(versionDTO.getFile());

        // 3. 保存版本信息
        AppVersion version = new AppVersion();
        version.setVersionCode(versionDTO.getVersionCode());
        version.setVersionName(versionDTO.getVersionName());
        version.setPlatform(versionDTO.getPlatform());
        version.setDownloadUrl(filePath);
        version.setFileSize(versionDTO.getFile().getSize());
        version.setMd5Hash(md5Hash);
        version.setReleaseNotes(versionDTO.getReleaseNotes());
        version.setMinSdk(versionDTO.getMinSdk());
        version.setIsForce(versionDTO.getIsForce() ? 1 : 0);
        version.setIsActive(1);
        version.setPublishTime(new Date());
        version.setPublishBy(SecurityUtils.getUsername());

        appVersionMapper.insert(version);

        // 4. 更新配置表
        updateConfig(versionDTO.getVersionCode());
    }

    private void updateConfig(Integer versionCode) {
        UpgradeConfig config = upgradeConfigMapper.selectByKey("latest_version_code");
        if (config == null) {
            config = new UpgradeConfig();
            config.setConfigKey("latest_version_code");
            config.setConfigValue(versionCode.toString());
            upgradeConfigMapper.insert(config);
        } else {
            config.setConfigValue(versionCode.toString());
            upgradeConfigMapper.update(config);
        }
    }
}
```

### 6.2 版本发布 Controller

```java
@RestController
@RequestMapping("/api/admin/app-version")
@PreAuthorize("@ss.hasPermi('system:update:publish')")
public class AppVersionController {

    @Autowired
    private AppUpdateService appUpdateService;

    /**
     * 发布新版本
     */
    @PostMapping("/publish")
    public AjaxResult publishVersion(@RequestBody AppVersionDTO versionDTO) {
        try {
            appUpdateService.publishVersion(versionDTO);
            return AjaxResult.success("发布成功");
        } catch (Exception e) {
            return AjaxResult.error("发布失败：" + e.getMessage());
        }
    }

    /**
     * 获取版本列表
     */
    @GetMapping("/list")
    public TableDataInfo list(AppVersion appVersion) {
        PageHelper.startPage();
        List<AppVersion> list = appUpdateService.selectAppVersionList(appVersion);
        return getDataTable(list);
    }

    /**
     * 获取版本详情
     */
    @GetMapping("/{versionId}")
    public AjaxResult getInfo(@PathVariable Long versionId) {
        return success(appUpdateService.selectAppVersionById(versionId));
    }

    /**
     * 删除版本
     */
    @DeleteMapping("/{versionId}")
    public AjaxResult remove(@PathVariable Long versionId) {
        return toAjax(appUpdateService.deleteAppVersionById(versionId));
    }

    /**
     * 修改版本状态
     */
    @PutMapping("/{versionId}/status")
    public AjaxResult changeStatus(
        @PathVariable Long versionId,
        @RequestParam Integer isActive
    ) {
        return toAjax(appUpdateService.changeAppVersionStatus(versionId, isActive));
    }
}
```

---

## 7. 升级策略

### 7.1 检查频率

| 场景 | 检查频率 | 说明 |
|------|---------|------|
| App 启动 | 每次启动检查一次 | 静默检查，不频繁打扰用户 |
| 手动检查 | 用户主动点击 | 设置页面"检查更新"按钮 |
| 定时检查 | 每 24 小时一次 | 后台任务，仅在有更新时通知 |

### 7.2 下载策略

```kotlin
object DownloadStrategy {
    // WiFi 优先
    const val PREFER_WIFI = true

    // 移动网络限制（超过 100MB 需用户确认）
    const val MOBILE_DATA_LIMIT = 100 * 1024 * 1024L

    // 断点续传
    const val SUPPORT_RESUME = true

    // 最大重试次数
    const val MAX_RETRY = 3

    // 重试间隔（秒）
    const val RETRY_INTERVAL = 5
}
```

### 7.3 强制更新触发条件

| 条件 | 说明 |
|------|------|
| 重大安全漏洞 | 必须立即修复的安全问题 |
| 法律法规变更 | 执法依据发生重大变化 |
| 核心功能缺陷 | 影响正常执法的 Bug |
| 后端接口变更 | 旧版本无法正常使用 |

---

## 8. 错误处理

### 8.1 错误类型

```kotlin
sealed class UpdateError {
    object NETWORK_ERROR : UpdateError()           // 网络错误
    object DOWNLOAD_FAILED : UpdateError()         // 下载失败
    object MD5_MISMATCH : UpdateError()            // MD5 不匹配
    object STORAGE_FULL : UpdateError()            // 存储空间不足
    object INSTALL_FAILED : UpdateError()          // 安装失败
    object VERSION_TOO_LOW : UpdateError()         // 版本过低
    object PLATFORM_NOT_SUPPORTED : UpdateError()  // 平台不支持
}
```

### 8.2 错误码定义

| 错误码 | 说明 | 处理建议 |
|-------|------|---------|
| 1001 | 网络不可用 | 提示用户检查网络 |
| 1002 | 下载超时 | 自动重试，最多 3 次 |
| 1003 | MD5 校验失败 | 重新下载 |
| 1004 | 存储空间不足 | 提示清理空间 |
| 1005 | 安装包损坏 | 重新下载 |
| 1006 | 安装被拒绝 | 引导用户开启未知来源安装权限 |
| 1007 | 版本不兼容 | 提示用户设备系统版本过低 |

---

## 9. 安全考虑

### 9.1 下载安全

- **HTTPS 下载**：APK 下载地址必须使用 HTTPS
- **MD5 校验**：下载完成后验证文件完整性
- **域名白名单**：仅允许从指定域名下载

### 9.2 安装安全

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
<uses-permission android:name="android.permission.UPDATE_PACKAGES_WITHOUT_USER_ACTION" />

<!-- Android 11+ 需要声明 -->
<queries>
    <intent>
        <action android:name="android.intent.action.INSTALL_PACKAGE" />
    </intent>
</queries>
```

### 9.3 防篡改

```kotlin
object SecurityUtil {
    /**
     * 验证 APK 签名
     */
    fun verifyApkSignature(context: Context, apkPath: String): Boolean {
        return try {
            val packageManager = context.packageManager
            val signatures = packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            ).signatures

            // 验证签名是否与当前 App 一致
            signatures.any { it.hashCode() == getExpectedSignatureHash() }
        } catch (e: Exception) {
            false
        }
    }
}
```

---

## 10. 测试用例

### 10.1 单元测试

```kotlin
class OtaUpdateManagerTest {

    @Test
    fun testCheckUpdate_NoUpdate() = runTest {
        // Given: 当前是最新版本
        every { updateApi.checkUpdate(any(), any(), any()) }
            returns Response.success(UpdateCheckResponse(data = null))

        // When
        val result = otaUpdateManager.checkUpdate(showDialog = false)

        // Then
        assertNull(result)
    }

    @Test
    fun testCheckUpdate_HasUpdate() = runTest {
        // Given: 有新版本
        val updateInfo = UpdateInfo(
            hasUpdate = true,
            versionCode = 2,
            versionName = "1.1.0",
            downloadUrl = "https://example.com/app.apk",
            isForce = false
        )
        every { updateApi.checkUpdate(any(), any(), any()) }
            returns Response.success(UpdateCheckResponse(data = updateInfo))

        // When
        val result = otaUpdateManager.checkUpdate(showDialog = false)

        // Then
        assertNotNull(result)
        assertTrue(result.hasUpdate)
        assertEquals("1.1.0", result.versionName)
    }

    @Test
    fun testMd5Verification_Success() {
        // Given
        val testFile = createTempFile()
        val expectedMd5 = "d41d8cd98f00b204e9800998ecf8427e"

        // When
        val isValid = verifyMd5(testFile, expectedMd5)

        // Then
        assertTrue(isValid)
    }

    @Test
    fun testMd5Verification_Failure() {
        // Given
        val testFile = createTempFile()
        val wrongMd5 = "0000000000000000000000000000000"

        // When
        val isValid = verifyMd5(testFile, wrongMd5)

        // Then
        assertFalse(isValid)
    }
}
```

### 10.2 集成测试

```kotlin
@Test
fun testDownloadAndInstall_ForceUpdate() = runTest {
    // Given: 强制更新
    val updateInfo = UpdateInfo(
        hasUpdate = true,
        versionCode = 2,
        isForce = true,
        downloadUrl = testServer.url("/app.apk").toString(),
        md5Hash = "expected_md5_hash"
    )

    // When: 开始下载
    val progressFlow = otaUpdateManager.downloadUpdate(updateInfo)

    // Then: 验证下载流程
    progressFlow.collect { progress ->
        when (progress) {
            is DownloadProgress.Progress -> {
                // 验证进度
                assertTrue(progress.percent in 0..100)
            }
            is DownloadProgress.Complete -> {
                // 验证文件下载完成
                assertTrue(progress.apkFile.exists())
            }
            is DownloadProgress.Error -> {
                fail("下载失败：${progress.message}")
            }
        }
    }
}
```

---

## 11. 运维支持

### 11.1 升级统计

```sql
-- 升级记录表
CREATE TABLE t_upgrade_record (
    record_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_id     VARCHAR(64) NOT NULL,
    from_version  INT NOT NULL,
    to_version    INT NOT NULL,
    upgrade_time  DATETIME NOT NULL,
    upgrade_result VARCHAR(16) NOT NULL,
    failed_reason TEXT,
    INDEX idx_device (device_id),
    INDEX idx_time (upgrade_time)
);
```

### 11.2 统计查询

```java
/**
 * 升级统计
 */
@GetMapping("/statistics")
public Map<String, Object> statistics(
    @RequestParam(required = false) String startDate,
    @RequestParam(required = false) String endDate
) {
    Map<String, Object> result = new HashMap<>();

    // 升级成功率
    result.put("successRate", appVersionMapper.calculateSuccessRate(startDate, endDate));

    // 升级总数
    result.put("totalCount", appVersionMapper.countUpgrades(startDate, endDate));

    // 按版本统计
    result.put("byVersion", appVersionMapper.countByVersion(startDate, endDate));

    // 失败原因分析
    result.put("failureReasons", appVersionMapper.countFailureReasons(startDate, endDate));

    return result;
}
```

---

## 12. 附录

### 12.1 FileProvider 配置

```xml
<!-- res/xml/file_paths.xml -->
<paths>
    <external-files-path
        name="app_update"
        path="app_update" />
</paths>

<!-- AndroidManifest.xml -->
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

### 12.2 网络权限

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

---

**文档版本**: v1.0
**最后更新**: 2026-04-22
