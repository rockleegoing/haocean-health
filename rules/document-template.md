# 文书模板管理规范

## 1. 概述

本规范定义卫生执法系统中文书模板的管理、编辑、生成和打印流程。

### 1.1 文书类型

| 类型 | 说明 | 示例 |
|------|------|------|
| 现场笔录类 | 现场检查记录 | 现场笔录、勘验笔录 |
| 询问笔录类 | 当事人询问记录 | 询问笔录、调查笔录 |
| 行政强制类 | 强制措施文书 | 先行登记保存决定书 |
| 行政处罚类 | 处罚决定文书 | 行政处罚决定书 |
| 其他文书 | 程序性文书 | 责令改正通知书 |

### 1.2 文件流程

```
后台配置模板 → App 下载模板 → 填写填空项 → 生成文书 → 打印/导出
```

## 2. 模板格式规范

### 2.1 文件格式

- **源文件格式**: `.docx` (Office Open XML)
- **导出格式**: `.docx`, `.pdf`
- **编码**: UTF-8

### 2.2 填空项标记

```
格式：${变量名}

示例：
- ${unitName}         -- 单位名称
- ${inspectionDate}   -- 检查日期
- ${lawOfficerName}   -- 执法人员姓名
- ${recordContent}    -- 记录内容
```

### 2.3 变量命名规范

```kotlin
// 命名规则：驼峰命名，语义清晰
data class DocumentVariables(
    // 基本信息
    val unitName: String,           // 单位名称
    val unitAddress: String,        // 单位地址
    val legalPerson: String,        // 法人代表
    val socialCreditCode: String,   // 统一社会信用代码

    // 人员信息
    val lawOfficerName: String,     // 执法人员姓名
    val lawOfficerNo: String,       // 执法证号

    // 时间信息
    val inspectionDate: String,     // 检查日期
    val recordTime: String,         // 记录时间

    // 文书内容
    val inspectionContent: String,  // 检查内容
    val problemDescription: String, // 问题描述
    val handlingOpinion: String     // 处理意见
)
```

### 2.4 变量类型定义

```kotlin
sealed class VariableType {
    object TEXT : VariableType()           // 文本输入
    object DATE : VariableType()           // 日期选择
    object DATETIME : VariableType()       // 日期时间
    object SELECT : VariableType()         // 下拉选择
    object RADIO : VariableType()          // 单选框
    object CHECKBOX : VariableType()       // 复选框
    object SIGNATURE : VariableType()      // 电子签名
    object PHOTO : VariableType()          // 照片
    object RICH_TEXT : VariableType()      // 富文本
}
```

## 3. 模板数据结构

### 3.1 数据库表设计

```sql
-- 文书模板表
CREATE TABLE t_document_template (
    id              BIGINT PRIMARY KEY AUTOINCREMENT,
    template_code   VARCHAR(64) NOT NULL,     -- 模板编码
    template_name   VARCHAR(128) NOT NULL,    -- 模板名称
    template_type   VARCHAR(32),              -- 模板类型
    category        VARCHAR(32),              -- 分类
    file_path       VARCHAR(256),             -- 文件路径
    file_url        VARCHAR(256),             -- 文件 URL
    version         INT DEFAULT 1,            -- 版本号
    variables       TEXT,                     -- 变量定义 (JSON)
    is_active       CHAR(1) DEFAULT '1',      -- 是否启用
    create_time     DATETIME,
    update_time     DATETIME
);

-- 变量定义表
CREATE TABLE t_template_variable (
    id              BIGINT PRIMARY KEY AUTOINCREMENT,
    template_id     BIGINT NOT NULL,
    variable_name   VARCHAR(64) NOT NULL,     -- 变量名
    variable_label  VARCHAR(128),             -- 变量标签
    variable_type   VARCHAR(32),              -- 变量类型
    required        CHAR(1) DEFAULT '1',      -- 是否必填
    default_value   VARCHAR(256),             -- 默认值
    options         TEXT,                     -- 选项 (JSON, 用于下拉框)
    sort_order      INT DEFAULT 0,
    FOREIGN KEY (template_id) REFERENCES t_document_template(id)
);
```

### 3.2 变量定义 JSON 格式

```json
{
  "variables": [
    {
      "name": "unitName",
      "label": "单位名称",
      "type": "TEXT",
      "required": true,
      "maxLength": 200
    },
    {
      "name": "inspectionDate",
      "label": "检查日期",
      "type": "DATE",
      "required": true
    },
    {
      "name": "problemType",
      "label": "问题类型",
      "type": "SELECT",
      "required": true,
      "options": [
        {"value": "1", "label": "卫生许可"},
        {"value": "2", "label": "卫生管理"},
        {"value": "3", "label": "从业人员健康"}
      ]
    },
    {
      "name": "signature",
      "label": "当事人签名",
      "type": "SIGNATURE",
      "required": true
    }
  ]
}
```

## 4. 模板编辑器规范

### 4.1 编辑器功能

```kotlin
interface TemplateEditor {

    /**
     * 加载模板
     */
    suspend fun loadTemplate(templatePath: String): Document

    /**
     * 解析模板变量
     */
    fun parseVariables(document: Document): List<TemplateVariable>

    /**
     * 绑定变量
     */
    fun bindVariable(document: Document, variableName: String, value: String)

    /**
     * 插入图片 (签名、照片)
     */
    fun insertImage(document: Document, bookmarkName: String, imagePath: String)

    /**
     * 保存模板
     */
    suspend fun saveTemplate(document: Document, outputPath: String)
}
```

### 4.2 Spire.WordJS 使用示例

```kotlin
// 注意：Spire.WordJS 是 Node.js 库，Android 端使用 Java 版本或通过后端处理

// 后端 Node.js 示例
const { Document, ShapeObjectType } = require('spire.doc');

async function processTemplate(templatePath, variables, outputPath) {
    const doc = new Document();
    await doc.loadFromFile(templatePath);

    // 替换变量
    for (const [key, value] of Object.entries(variables)) {
        doc.replace(new RegExp(`\\$\\{${key}\\}`, 'g'), value, false, false);
    }

    // 保存
    await doc.saveToFile(outputPath, 'DOCX');
    doc.close();
}
```

### 4.3 Android 端变量填写界面

```kotlin
@AndroidEntryPoint
class DocumentFillActivity : AppCompatActivity() {

    private val viewModel: DocumentFillViewModel by viewModels()
    private lateinit var binding: ActivityDocumentFillBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentFillBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeVariables()
        setupSubmitButton()
    }

    private fun observeVariables() {
        viewModel.variables.observe(this) { variables ->
            renderForm(variables)
        }
    }

    private fun renderForm(variables: List<TemplateVariable>) {
        variables.forEach { variable ->
            val view = when (variable.type) {
                VariableType.TEXT -> createTextInput(variable)
                VariableType.DATE -> createDatePicker(variable)
                VariableType.SELECT -> createSpinner(variable)
                VariableType.SIGNATURE -> createSignaturePad(variable)
                else -> null
            }
            view?.let { binding.formContainer.addView(it) }
        }
    }
}
```

## 5. 文书生成流程

### 5.1 生成流程图

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ 选择模板    │───►│ 填写变量    │───►│ 预览文书    │
└─────────────┘    └─────────────┘    └─────────────┘
                                           │
                    ┌──────────────────────┼──────────────────────┐
                    ▼                      ▼                      ▼
            ┌─────────────┐        ┌─────────────┐        ┌─────────────┐
            │ 生成 Word   │        │ 生成 PDF    │        │ 蓝牙打印    │
            └─────────────┘        └─────────────┘        └─────────────┘
```

### 5.2 生成 API

```kotlin
interface DocumentApi {

    /**
     * 获取模板列表
     */
    @GET("/api/document/template/list")
    suspend fun getTemplateList(
        @Query("category") category: String?
    ): Response<BaseResult<List<TemplateVO>>>

    /**
     * 下载模板
     */
    @GET("/api/document/template/download/{templateId}")
    suspend fun downloadTemplate(
        @Path("templateId") templateId: Long
    ): Response<ResponseBody>

    /**
     * 生成文书
     */
    @POST("/api/document/generate")
    suspend fun generateDocument(
        @Body request: GenerateDocumentRequest
    ): Response<BaseResult<DocumentVO>>

    /**
     * 转换为 PDF
     */
    @POST("/api/document/convert-pdf")
    suspend fun convertToPdf(
        @Body request: ConvertToPdfRequest
    ): Response<ResponseBody>
}
```

### 5.3 生成请求数据

```kotlin
data class GenerateDocumentRequest(
    val templateId: Long,
    val variables: Map<String, String>,
    val lawRecordId: Long?,           // 关联执法记录 ID
    val unitId: Long?,                // 关联单位 ID
    val generateType: String = "WORD" // WORD/PDF
)
```

## 6. PDF 转换规范

### 6.1 转换服务

```kotlin
interface PdfConvertService {

    /**
     * Word 转 PDF
     */
    suspend fun wordToPdf(wordPath: String, pdfPath: String): Boolean

    /**
     * 添加电子签名
     */
    suspend fun addSignature(
        pdfPath: String,
        signaturePath: String,
        position: SignaturePosition
    ): Boolean
}
```

### 6.2 iText 7 使用示例

```kotlin
// 后端 Java 示例 (使用 iText 7)
public class PdfConverter {

    public static void convertToPdf(String wordPath, String pdfPath) throws IOException {
        // 使用 Aspose.Words 或其他库转换
        Document doc = new Document(wordPath);
        doc.save(pdfPath, SaveFormat.PDF);
    }

    public static void addSignature(String pdfPath, String signaturePath, float x, float y) {
        PdfReader reader = new PdfReader(pdfPath);
        PdfWriter writer = new PdfWriter(pdfPath.replace(".pdf", "_signed.pdf"));
        PdfDocument pdf = new PdfDocument(reader, writer);

        ImageData image = ImageDataFactory.create(signaturePath);
        Image img = new Image(image);
        img.setFixedPosition(1, x, y);
        img.scaleToFit(150, 50);

        pdf.getFirstPage().addXObject(img, new PdfXObject(img));
        pdf.close();
    }
}
```

## 7. 打印规范

### 7.1 蓝牙打印流程

```kotlin
class BluetoothPrintService @Inject constructor(
    private val bluetoothManager: BluetoothManager
) {

    /**
     * 打印文书
     */
    suspend fun printDocument(document: PrintDocument): PrintResult {
        // 1. 检查蓝牙连接
        if (!bluetoothManager.isConnected()) {
            return PrintResult.ERROR_NOT_CONNECTED
        }

        // 2. 转换为打印数据
        val printData = convertToPrintData(document)

        // 3. 发送打印命令
        return try {
            bluetoothManager.write(printData)
            PrintResult.SUCCESS
        } catch (e: Exception) {
            PrintResult.ERROR_PRINT_FAILED(e.message)
        }
    }

    /**
     * 转换为打印机指令
     */
    private fun convertToPrintData(document: PrintDocument): ByteArray {
        val builder = StringBuilder()

        // ESC/POS 指令
        builder.append(ESC + "@")           // 初始化打印机
        builder.append(ESC + "a" + 1)       // 居中对齐
        builder.append(document.title)      // 标题
        builder.append(LF + LF)             // 换行

        builder.append(ESC + "a" + 0)       // 左对齐
        builder.append(document.content)    // 内容
        builder.append(LF + LF)

        // 签名图片 (如果有)
        document.signatures.forEach { path ->
            builder.append(printImage(path))
        }

        builder.append(ESC + "d" + 2)       // 走纸 2 行
        builder.append(ESC + "i")           // 切纸

        return builder.toString().toByteArray()
    }
}
```

### 7.2 打印数据格式

```kotlin
data class PrintDocument(
    val title: String,                    // 文书标题
    val content: String,                  // 文书内容
    val signatures: List<String> = emptyList(), // 签名图片路径
    val footer: String = "",              // 页脚
    val copies: Int = 1                   // 打印份数
)
```

### 7.3 打印预览

```kotlin
@Composable
fun PrintPreview(document: PrintDocument) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 标题居中
        Text(
            text = document.title,
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 内容
        Text(
            text = document.content,
            style = MaterialTheme.typography.body1
        )

        // 签名区域
        document.signatures.forEach { path ->
            Image(
                painter = painterResource(path),
                contentDescription = "签名",
                modifier = Modifier
                    .size(150.dp, 50.dp)
                    .border(1.dp, Color.Gray)
            )
        }
    }
}
```

## 8. 电子签名规范

### 8.1 签名采集

```kotlin
class SignaturePad @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val path = Path()
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }

    fun getSignatureImage(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawPath(path, paint)
        return bitmap
    }

    fun saveSignature(path: String) {
        val bitmap = getSignatureImage()
        FileOutputStream(path).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }
}
```

### 8.2 签名合成

```kotlin
// 将签名合成到 PDF 指定位置
suspend fun合成 Signature(
    pdfPath: String,
    signatureBitmap: Bitmap,
    pageNumber: Int,
    x: Float,
    y: Float,
    width: Float,
    height: Float
): String {
    // 使用 iText 或 PdfBox 合成
    // 返回合成后的文件路径
}
```

## 9. 文件管理

### 9.1 文件存储路径

```kotlin
object DocumentStorage {

    // 模板目录
    val templateDir: File
        get() = File(getExternalDocsDir(), "templates")

    // 生成的文书目录
    val documentDir: File
        get() = File(getExternalDocsDir(), "documents")

    // PDF 导出目录
    val pdfExportDir: File
        get() = File(getExternalDocsDir(), "pdf_export")

    // 签名图片目录
    val signatureDir: File
        get() = File(getExternalDocsDir(), "signatures")

    private fun getExternalDocsDir(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            .resolve("ruoyi-law-enforcement")
    }
}
```

### 9.2 文件清理

```kotlin
// 定期清理临时文件
@Scheduled(cron = "0 0 3 * * ?")  // 每天凌晨 3 点
fun cleanupTempFiles() {
    val tempDir = DocumentStorage.documentDir.resolve("temp")
    val sevenDaysAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000

    tempDir.listFiles()?.forEach { file ->
        if (file.lastModified() < sevenDaysAgo) {
            file.delete()
        }
    }
}
```

## 10. 错误处理

### 10.1 错误类型

```kotlin
sealed class DocumentError {
    object TEMPLATE_NOT_FOUND : DocumentError()     // 模板不存在
    object VARIABLE_MISSING : DocumentError()       // 变量缺失
    object GENERATE_FAILED : DocumentError()        // 生成失败
    object CONVERT_FAILED : DocumentError()         // 转换失败
    object PRINT_FAILED : DocumentError()           // 打印失败
    object SIGNATURE_INVALID : DocumentError()      // 签名无效
}
```

### 10.2 错误处理示例

```kotlin
try {
    val document = documentGenerator.generate(templateId, variables)
    showSuccess("文书生成成功")
} catch (e: TemplateNotFoundException) {
    showError("模板不存在，请更新模板")
} catch (e: VariableMissingException) {
    showError("请填写所有必填项")
} catch (e: Exception) {
    showError("文书生成失败：${e.message}")
}
```
