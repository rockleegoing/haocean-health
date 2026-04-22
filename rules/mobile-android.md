# Android 移动端开发规范

## 1. 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Kotlin | 1.8+ |
| 架构 | MVVM + DataBinding | - |
| AndroidX | androidx.core | 1.9.0+ |
| 网络 | Retrofit + OkHttp | 2.9.0+ |
| 路由 | TheRouter | 1.2.0+ |
| 图片 | Glide | 4.15.0+ |
| 数据库 | Room | 2.5.0+ |
| JSON | FastJSON / Gson | - |

## 2. 项目结构规范

```
app/
├── src/main/java/com/ruoyi/mobile/
│   ├── app/                    # Application 类
│   ├── common/                 # 公共模块
│   │   ├── api/                # API 接口定义
│   │   ├── constant/           # 常量定义
│   │   ├── domain/             # 数据模型
│   │   ├── repository/         # 数据仓库
│   │   └── utils/              # 工具类
│   ├── feature/                # 功能模块
│   │   ├── login/              # 登录模块
│   │   ├── lawenforcement/     # 便捷执法模块
│   │   ├── unit/               # 执法单位模块
│   │   ├── document/           # 文书制作模块
│   │   ├── knowledge/          # 知识库模块
│   │   └── office/             # 日常办公模块
│   └── ui/                     # UI 组件
│       ├── activity/           # Activity
│       ├── fragment/           # Fragment
│       ├── adapter/            # RecyclerView 适配器
│       └── widget/             # 自定义控件
├── src/main/res/
│   ├── layout/                 # 布局文件
│   ├── values/                 # 资源值
│   ├── drawable/               # 可绘制对象
│   └── menu/                   # 菜单
└── build.gradle
```

## 3. SQLite 本地数据库规范

### 3.1 表命名规范

```sql
-- 格式：t_模块前缀_业务名
t_law_record          -- 执法记录表
t_unit_info           -- 单位信息表
t_template            -- 文书模板表
t_sync_queue          -- 同步队列表
t_user_cache          -- 用户缓存表
```

### 3.2 字段命名规范

```sql
-- 通用字段规范
id              BIGINT PRIMARY KEY AUTOINCREMENT  -- 主键
create_by       VARCHAR(64) DEFAULT ''            -- 创建者
create_time     DATETIME DEFAULT NULL             -- 创建时间
update_by       VARCHAR(64) DEFAULT ''            -- 更新者
update_time     DATETIME DEFAULT NULL             -- 更新时间
remark          VARCHAR(500) DEFAULT NULL         -- 备注
del_flag        CHAR(2) DEFAULT '0'               -- 删除标志 (0 正常 1 删除)
```

### 3.3 索引规范

```sql
-- 外键字段建索引
CREATE INDEX idx_unit_id ON t_law_record(unit_id);

-- 查询条件建索引
CREATE INDEX idx_create_time ON t_law_record(create_time);

-- 唯一索引
CREATE UNIQUE INDEX idx_unit_code ON t_unit_info(unit_code);
```

### 3.4 Room Entity 示例

```kotlin
@Entity(tableName = "t_law_record")
data class LawRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "unit_id")
    val unitId: Long,

    @ColumnInfo(name = "record_type")
    val recordType: String,  // photo/audio/video

    @ColumnInfo(name = "file_path")
    val filePath: String,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "PENDING",  // PENDING/SUCCESS/FAILED

    @ColumnInfo(name = "create_time")
    val createTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "del_flag")
    val delFlag: String = "0"
)
```

## 4. 离线同步机制

### 4.1 同步队列表设计

```sql
CREATE TABLE t_sync_queue (
    id              BIGINT PRIMARY KEY AUTOINCREMENT,
    table_name      VARCHAR(64) NOT NULL,     -- 表名
    record_id       BIGINT NOT NULL,          -- 记录 ID
    sync_type       VARCHAR(16) NOT NULL,     -- CREATE/UPDATE/DELETE
    sync_status     VARCHAR(16) DEFAULT 'PENDING',
    retry_count     INT DEFAULT 0,
    last_retry_time DATETIME,
    request_data    TEXT,                     -- JSON 格式请求数据
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 4.2 同步类型定义

```kotlin
enum class SyncType {
    CREATE,     // 新增
    UPDATE,     // 更新
    DELETE,     // 删除
    DOWNLOAD    // 下载 (后台数据下行)
}
```

### 4.3 同步状态定义

```kotlin
enum class SyncStatus {
    PENDING,    // 待同步
    SUCCESS,    // 同步成功
    FAILED,     // 同步失败
    CONFLICT    // 数据冲突
}
```

### 4.4 同步优先级

| 数据类型 | 优先级 | 说明 |
|---------|-------|------|
| 执法记录 | 高 | 现场采集数据优先上传 |
| 单位信息变更 | 中 | 单位信息变更 |
| 用户信息 | 低 | 个人资料变更 |
| 法律法规 | 下行 | 后台数据下行 |

### 4.5 重试机制

```kotlin
// 指数退避策略
val retryDelay = min(2 ^ retryCount * 1000, 30000)  // 最大 30 秒
```

## 5. 网络层规范

### 5.1 API 接口定义

```kotlin
interface LawEnforcementApi {

    @POST("/api/law/save")
    suspend fun saveLawRecord(@Body request: SaveLawRecordRequest): Response<BaseResult<LawRecordVO>>

    @GET("/api/law/list")
    suspend fun getLawRecordList(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("unitId") unitId: Long?
    ): Response<BaseResult<PageResult<LawRecordVO>>>

    @Multipart
    @POST("/api/law/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): Response<BaseResult<FileUploadVO>>
}
```

### 5.2 请求拦截器

```kotlin
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${TokenManager.getToken()}")
            .addHeader("Device-Id", getDeviceId())
            .build()
        return chain.proceed(request)
    }
}
```

### 5.3 错误处理

```kotlin
sealed class ApiError {
    object NetworkError : ApiError()          // 网络错误
    object TimeoutError : ApiError()          // 超时
    object AuthError : ApiError()             // 认证失败
    data class ServerError(val code: Int, val msg: String) : ApiError()
    data class UnknownError(val e: Throwable) : ApiError()
}

// 错误码映射
object ErrorCodeMapper {
    fun map(code: Int): String = when (code) {
        401 -> "登录已过期，请重新登录"
        403 -> "无权限访问"
        404 -> "资源不存在"
        500 -> "服务器内部错误"
        else -> "请求失败，错误码：$code"
    }
}
```

## 6. UI 开发规范

### 6.1 Fragment 使用规范

```kotlin
class LawEnforcementFragment : Fragment(R.layout.fragment_law_enforcement) {

    private val viewModel: LawEnforcementViewModel by viewModels()
    private lateinit var binding: FragmentLawEnforcementBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLawEnforcementBinding.bind(view)
        setupObserver()
        setupClickListener()
    }

    private fun setupObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            // 更新 UI
        }
    }
}
```

### 6.2 RecyclerView 适配器规范

```kotlin
class LawRecordAdapter(
    private val onItemClick: (LawRecord) -> Unit
) : ListAdapter<LawRecord, LawRecordAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LawRecord>() {
            override fun areItemsTheSame(oldItem: LawRecord, newItem: LawRecord): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: LawRecord, newItem: LawRecord): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: LawRecord) {
            // 数据绑定
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_law_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
```

### 6.3 DataBinding 规范

```xml
<!-- layout 文件 -->
<layout>
    <data>
        <variable
            name="viewModel"
            type="com.ruoyi.mobile.feature.lawenforcement.LawEnforcementViewModel" />
    </data>

    <LinearLayout>
        <TextView
            android:text="@{viewModel.unitName}"
            android:visibility="@{viewModel.isVisible ? View.VISIBLE : View.GONE}" />
    </LinearLayout>
</layout>
```

## 7. 蓝牙打印规范

### 7.1 设备配对流程

```kotlin
// 1. 扫描设备
fun scanBluetoothDevices(): List<BluetoothDevice> {
    // 实现设备扫描
}

// 2. 配对连接
fun pairDevice(device: BluetoothDevice): Boolean {
    // 实现配对
}

// 3. 打印数据
fun printDocument(document: PrintDocument): Boolean {
    // 实现打印
}
```

### 7.2 打印数据格式

```kotlin
data class PrintDocument(
    val title: String,           // 文书标题
    val content: String,         // 文书内容
    val signatures: List<String>, // 电子签名图片路径
    val footer: String           // 页脚信息
)
```

### 7.3 错误处理

```kotlin
sealed class BluetoothError {
    object NotEnabled : BluetoothError()           // 蓝牙未启用
    object NotPaired : BluetoothError()            // 未配对
    object ConnectionFailed : BluetoothError()     // 连接失败
    object PrintFailed : BluetoothError()          // 打印失败
}
```

## 8. 文件上传规范

### 8.1 文件类型限制

| 类型 | 扩展名 | 最大大小 |
|------|-------|---------|
| 图片 | .jpg, .png | 5MB |
| 音频 | .mp3, .wav | 10MB |
| 视频 | .mp4 | 50MB |

### 8.2 文件命名规范

```
{module}_{timestamp}_{uuid}.{ext}
例：law_20240101120000_a1b2c3d4.jpg
```

## 9. 安全规范

### 9.1 数据加密

```kotlin
// 敏感数据加密存储
@TypeConverters(EncryptedTypeConverter::class)
@ColumnInfo(name = "encrypted_password")
val encryptedPassword: String
```

### 9.2 Token 管理

```kotlin
// Token 存储使用 EncryptedSharedPreferences
object TokenManager {
    private val encryptedPrefs by lazy {
        EncryptedSharedPreferences.create(...)
    }

    fun saveToken(token: String) {
        encryptedPrefs.edit().putString("access_token", token).apply()
    }

    fun getToken(): String? {
        return encryptedPrefs.getString("access_token", null)
    }
}
```
