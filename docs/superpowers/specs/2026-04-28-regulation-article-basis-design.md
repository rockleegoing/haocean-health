# 法律法规详情页 - 章节关联依据功能 设计方案

## 一、需求概述

在 Android 法律法规详情页中，章节下显示条目，条目关联定性依据和处理依据。点击条目上的"定性依据 N"或"处理依据 N"可跳转到对应依据列表页，再点击跳转到详情页。详情页每个字段可复制。

## 二、系统架构

```
┌─────────────────────────────────────────────────────────────────┐
│ 后端 RuoYi-Vue (Spring Boot)                                     │
│  ├── 系统管理                                                    │
│  │   └── 法律法规                                                │
│  │       ├── 法律法规管理                                         │
│  │       ├── 章节管理                                             │
│  │       ├── 条款管理                                             │
│  │       ├── 定性依据管理                                         │
│  │       ├── 处理依据管理 🆕                                       │
│  │       └── 依据关联管理 🆕                                       │
├─────────────────────────────────────────────────────────────────┤
│ 数据库 MySQL                                                     │
│  ├── sys_processing_basis（处理依据表）🆕                         │
│  └── sys_basis_chapter_link（依据章节关联表）🆕                    │
├─────────────────────────────────────────────────────────────────┤
│ 前端 RuoYi-Vue (Vue)                                            │
│  ├── 处理依据管理页面 🆕                                           │
│  └── 依据关联管理页面 🆕                                           │
├─────────────────────────────────────────────────────────────────┤
│ Android App                                                     │
│  ├── 法规详情页（目录+章节+条目+关联依据徽章）                      │
│  ├── 定性依据列表页 🆕                                            │
│  ├── 定性依据详情页 🆕                                            │
│  ├── 处理依据列表页 🆕                                            │
│  ├── 处理依据详情页 🆕                                            │
│  └── 数据同步 🆕                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## 三、交互流程

```
法规详情页 → 点击"定性依据 N" → 定性依据列表页 → 点击 → 定性依据详情页（字段可复制）
                        ↓
             点击"处理依据 N" → 处理依据列表页 → 点击 → 处理依据详情页（字段可复制）
```

## 四、数据库设计

### 4.1 sys_processing_basis 表（处理依据）

```sql
CREATE TABLE sys_processing_basis (
    basis_id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    basis_no             VARCHAR(20) COMMENT '编号',
    title                VARCHAR(255) NOT NULL COMMENT '标题',
    violation_type       VARCHAR(100) COMMENT '违法类型',
    issuing_authority    VARCHAR(100) COMMENT '颁发机构',
    effective_date       VARCHAR(20) COMMENT '实施时间',
    legal_level          VARCHAR(20) COMMENT '法律层级',
    clauses              TEXT COMMENT '条款内容(JSON数组)',
    legal_liability      TEXT COMMENT '法律责任',
    discretion_standard  TEXT COMMENT '裁量标准',
    regulation_id        BIGINT COMMENT '关联法规ID',
    status               CHAR(1) DEFAULT '0' COMMENT '状态',
    del_flag             CHAR(1) DEFAULT '0' COMMENT '删除标志',
    create_by            VARCHAR(64),
    create_time          DATETIME,
    update_by            VARCHAR(64),
    update_time          DATETIME,
    PRIMARY KEY (basis_id),
    INDEX idx_regulation_id (regulation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处理依据表';
```

### 4.2 sys_basis_chapter_link 表（关联表）

```sql
CREATE TABLE sys_basis_chapter_link (
    link_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    basis_type   VARCHAR(20) NOT NULL COMMENT '关联类型：legal/processing',
    basis_id     BIGINT NOT NULL COMMENT '依据ID',
    chapter_id   BIGINT NOT NULL COMMENT '章节ID',
    article_id   BIGINT COMMENT '条款ID',
    create_time  DATETIME,
    UNIQUE KEY uk_basis_chapter_article (basis_type, basis_id, chapter_id, article_id),
    INDEX idx_chapter (chapter_id),
    INDEX idx_article (article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='依据章节关联表';
```

### 4.3 菜单SQL

```sql
-- 处理依据管理（作为法律法规的子菜单）
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES ('处理依据管理',
    (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='法律法规') t),
    6, 'processingBasis', 'system/processingBasis/index', 'C', '0', '0',
    'system:processingBasis:list', 'form', 'admin', sysdate());

-- 依据关联管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES ('依据关联管理',
    (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name='法律法规') t),
    7, 'basisLink', 'system/basisLink/index', 'C', '0', '0',
    'system:basisLink:list', 'link', 'admin', sysdate());
```

## 五、后端API设计

### 5.1 获取章节下条目的关联依据统计

```
GET /system/regulation/articleBasisCount/{regulationId}

Response:
{
  "code": 200,
  "data": [
    {
      "chapterId": 1,
      "chapterNo": "第一章",
      "chapterTitle": "总则",
      "articles": [
        {
          "articleId": 1,
          "articleNo": "第一条",
          "legalBasisCount": 1,
          "processingBasisCount": 0
        },
        {
          "articleId": 2,
          "articleNo": "第二条",
          "legalBasisCount": 2,
          "processingBasisCount": 1
        }
      ]
    }
  ]
}
```

### 5.2 获取某条目关联的定性依据列表

```
GET /system/regulation/{regulationId}/legalBasisByArticle/{articleId}

Response:
{
  "code": 200,
  "rows": [
    { "basisId": 1, "basisNo": "001", "title": "超出资质认可范围从事诊疗活动" }
  ],
  "total": 3
}
```

### 5.3 处理依据CRUD

```
GET    /system/processingBasis/list        -- 分页列表
GET    /system/processingBasis/{basisId}  -- 详情
POST   /system/processingBasis             -- 新增
PUT    /system/processingBasis             -- 修改
DELETE /system/processingBasis/{basisId}   -- 删除
```

### 5.4 依据关联管理

```
GET    /system/basisLink/list                    -- 关联列表
POST   /system/basisLink                       -- 新增关联
DELETE /system/basisLink/{linkId}               -- 删除关联
GET    /system/basisLink/byArticle/{articleId} -- 按条款查关联
```

### 5.5 定性依据/处理依据详情

```
GET /system/legalBasis/{basisId}
GET /system/processingBasis/{basisId}

Response:
{
  "code": 200,
  "data": {
    "basisId": 1,
    "basisNo": "001",
    "title": "超出资质认可范围从事诊疗活动",
    "violationType": "超出资质认可范围",
    "issuingAuthority": "卫健委",
    "effectiveDate": "2020-06-01",
    "legalLevel": "法律",
    "clauses": "[\"基本医疗卫生与健康促进法第三十八条\"]",
    "legalLiability": "由县级以上人民政府卫生健康主管部门责令停止执业...",
    "discretionStandard": "轻微：...一般：...严重：...",
    "linkedChapters": [
      {"chapterId": 1, "chapterNo": "第一章", "chapterTitle": "总则"}
    ]
  }
}
```

## 六、前端实现（Vue）

### 6.1 API接口

```javascript
// src/api/system/processingBasis.js
import request from '@/utils/request'
export function listProcessingBasis(query) {
  return request({ url: '/system/processingBasis/list', method: 'get', params: query })
}
export function getProcessingBasis(basisId) {
  return request({ url: '/system/processingBasis/' + basisId, method: 'get' })
}
export function addProcessingBasis(data) {
  return request({ url: '/system/processingBasis', method: 'post', data })
}
export function updateProcessingBasis(data) {
  return request({ url: '/system/processingBasis', method: 'put', data })
}
export function delProcessingBasis(basisId) {
  return request({ url: '/system/processingBasis/' + basisId, method: 'delete' })
}
```

### 6.2 页面

**处理依据管理页面** `src/views/system/processingBasis/index.vue`
- 表格展示处理依据列表
- 新增/编辑弹窗表单
- 删除确认
- 分页查询

**依据关联管理页面** `src/views/system/basisLink/index.vue`
- 左侧树：选择法规 → 章节 → 条款
- 右侧Tab：定性依据列表 / 处理依据列表
- 新增关联：选择依据绑定到当前条款

## 七、Android端实现

### 7.1 数据模型

```kotlin
sealed class ChapterTreeItem {
    data class Chapter(
        val chapterId: Long,
        val chapterNo: String?,
        val chapterTitle: String?,
        val articles: List<Article> = emptyList()
    ) : ChapterTreeItem()
}

data class Article(
    val articleId: Long,
    val chapterId: Long?,
    val articleNo: String?,
    val content: String?,
    val legalBasisCount: Int = 0,
    val processingBasisCount: Int = 0
)
```

### 7.2 本地数据库

```kotlin
// ProcessingBasisEntity.kt
@Entity(tableName = "sys_processing_basis")
data class ProcessingBasisEntity(
    @PrimaryKey val basisId: Long,
    val basisNo: String?,
    val title: String,
    val violationType: String?,
    val issuingAuthority: String?,
    val effectiveDate: String?,
    val legalLevel: String?,
    val clauses: String?,
    val legalLiability: String?,
    val discretionStandard: String?,
    val regulationId: Long?,
    val status: String,
    val delFlag: String,
    val syncStatus: String = "SYNCED"
)

// BasisChapterLinkEntity.kt
@Entity(tableName = "sys_basis_chapter_link")
data class BasisChapterLinkEntity(
    @PrimaryKey val linkId: Long,
    val basisType: String,  // 'legal' 或 'processing'
    val basisId: Long,
    val chapterId: Long,
    val articleId: Long?,
    val syncStatus: String = "SYNCED"
)
```

### 7.3 DAO方法

```kotlin
@Dao interface BasisChapterLinkDao {
    @Query("SELECT COUNT(*) FROM sys_basis_chapter_link WHERE article_id = :articleId AND basis_type = 'legal'")
    suspend fun getLegalBasisCountByArticle(articleId: Long): Int

    @Query("SELECT COUNT(*) FROM sys_basis_chapter_link WHERE article_id = :articleId AND basis_type = 'processing'")
    suspend fun getProcessingBasisCountByArticle(articleId: Long): Int

    @Query("SELECT lb.* FROM sys_legal_basis lb INNER JOIN sys_basis_chapter_link l ON lb.basis_id = l.basis_id WHERE l.article_id = :articleId AND l.basis_type = 'legal'")
    fun getLegalBasisesByArticle(articleId: Long): Flow<List<LegalBasisEntity>>

    @Query("SELECT pb.* FROM sys_processing_basis pb INNER JOIN sys_basis_chapter_link l ON pb.basis_id = l.basis_id WHERE l.article_id = :articleId AND l.basis_type = 'processing'")
    fun getProcessingBasisesByArticle(articleId: Long): Flow<List<ProcessingBasisEntity>>
}
```

### 7.4 路由常量

```kotlin
const val legalBasisListRoute = "/law/legalBasis/list"
const val legalBasisDetailRoute = "/law/legalBasis/detail"
const val processingBasisListRoute = "/law/processingBasis/list"
const val processingBasisDetailRoute = "/law/processingBasis/detail"
```

### 7.5 编号格式

```kotlin
// 编号格式：001 -> 1.
fun formatBasisNo(basisNo: String?): String {
    if (basisNo.isNullOrBlank()) return ""
    val num = basisNo.toLongOrNull() ?: return basisNo
    return "$num."
}
```

### 7.6 复制功能

```kotlin
fun copyToClipboard(text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("依据内容", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show()
}
```

### 7.7 数据同步

**同步时机：**
- APP启动时全量同步
- 下拉刷新手动同步
- 增量同步（按 updateTimeFrom）

**同步数据：**
- sys_processing_basis（处理依据表）
- sys_basis_chapter_link（关联表）

## 八、UI原型

### 8.1 法规详情页

```
┌────────────────────────────────────┐
│ 中华人民共和国基本医疗卫生与健康促进法 │
│ [法律] 发布日期: 2019-12-28        │
├────────────────────────────────────┤
│ 目 录                              │
│ 第一章 总则                         │
│ 第二章 基本医疗卫生服务              │
├────────────────────────────────────┤
│ 第一章 总则                        │
│ ┌────────────────────────────────┐ │
│ │第一条          [定性依据1][处理依据0]│
│ │为了发展医疗卫生与健康事业...      │ │
│ └────────────────────────────────┘ │
│ ┌────────────────────────────────┐ │
│ │第二条          [定性依据2][处理依据1]│
│ │国家和社会尊重，保护公民的健康权...│ │
│ └────────────────────────────────┘ │
└────────────────────────────────────┘
```

### 8.2 定性依据列表页

```
┌────────────────────────────────────┐
│ 定性依据 - 第一条                    │
│ 关联条目：3 条                      │
├────────────────────────────────────┤
│ 1. 超出资质认可范围从事诊疗活动  [▶]│
│ 2. 医疗卫生机构等未按规定报告...  [▶]│
│ 3. 医疗机构使用非卫生技术人员...  [▶]│
└────────────────────────────────────┘
```

### 8.3 定性依据详情页

```
┌────────────────────────────────────┐
│ 超出资质认可范围从事诊疗活动          │
│ [001] [超出资质认可范围] [2020-06-01]│
├────────────────────────────────────┤
│ 基本信息                            │
│ 编号            001          [📋复制]│
│ 标题      超出资...       [📋复制]│
│ 违法类型  超出资...       [📋复制]│
│ 颁发机构  卫健委          [📋复制]│
├────────────────────────────────────┤
│ 裁量信息                            │
│ 条款内容   基本医疗卫生... [📋复制]│
│ 法律责任  由县级以上...   [📋复制]│
│ 裁量标准  轻微：...     [📋复制]│
└────────────────────────────────────┘
```

## 九、完整实现步骤

| 阶段 | 模块 | 实现内容 |
|------|------|----------|
| 1. 后端 | 数据库 | 新建 sys_processing_basis、sys_basis_chapter_link 表 |
| 1. 后端 | 菜单 | 在"法律法规"下添加处理依据管理、依据关联管理菜单 |
| 1. 后端 | Service/Mapper | ProcessingBasisService、ProcessingBasisMapper、BasisLinkService、BasisLinkMapper |
| 1. 后端 | Controller | ProcessingBasisController（CRUD）、BasisLinkController（关联管理）、统计接口 |
| 2. 前端 Vue | API | 新建 processingBasis.js、basisLink.js |
| 2. 前端 Vue | 页面 | processingBasis/index.vue（处理依据CRUD）、basisLink/index.vue（关联管理） |
| 2. 前端 Vue | 路由 | 配置 processingBasis、basisLink 路由 |
| 3. Android | 数据层 | ProcessingBasisEntity、BasisChapterLinkEntity、DAO、同步逻辑 |
| 3. Android | Repository | 新增 getLegalBasisCountByArticle、getProcessingBasisCountByArticle 等方法 |
| 3. Android | UI | 法规详情页添加目录、条目关联徽章；新增依据列表页、详情页（含复制功能） |
| 3. Android | 同步 | SyncManager 添加 processingBasis、basis_chapter_link 同步 |
